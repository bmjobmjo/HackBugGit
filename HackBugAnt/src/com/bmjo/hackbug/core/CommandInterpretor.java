/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmjo.hackbug.core;

import java.io.ByteArrayOutputStream;
import java.io.Console;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author bmjo
 */
public class CommandInterpretor implements IConnectionEvents {

    /**
     * .
     * WaitSec 100 \r\n WaitMilli 100 \r\n WaitFor \x55aa8820 \r\n WaitFor
     * "hello world" \r\n Label 1 ActonOn \s"ATDP 123345" \s"ATDF" \s "ATMK"
     * "\x6789" \r\n Action 1:\r\n END\r\n Send "\x6677" \r\n Goto 1
     */
    public static final String CMD_WAIT_SEC = "waitsec";
    public static final String CMD_WAIT_MILLIS = "waitmilli";
    public static final String CMD_SEND = "send";
    public static final String CMD_GOTO = "goto";
    public static final String CMD_LABEL = ":label";
    public static final String CMD_WAITFOR = "waitfor";
    public static final String CMD_WAITMANY = "waitmany";
    public static final String CMD_CASE = "case";
    public static final String CMD_END = "end";
    public static final String CMD_DEFAULT = "default";
    public static final String CMD_BUFFER = "recvbuffer";

    private List<CommandExecStatus> listeners = new ArrayList<CommandExecStatus>();
    String script;
    ArrayList<WaitForStringInfo> waitForList = new ArrayList<WaitForStringInfo>();

    boolean waitFlag = false;
    boolean bufferDataFlg;
    ArrayList<byte[]> recvdBytesBuff = new ArrayList<byte[]>();
    Object waitObject = new Object();
    // byte[] recvdArray = new  byte[waitForbufSize]
    Stack<Byte> recvdBytes = new Stack<Byte>();
    WaitForStringInfo foundStr;

    enum ScriptErrorType {
        NoError,
        EndOfFile,
        TokenError
    };
    ScriptErrorType scriptError;
    JSONArray commandsJson;

    @Override
    public void onReceive(byte[] data, int numBytes, IConnection eventSource, Object eventInfo) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        if (!waitFlag) {
            if (bufferDataFlg) {
                byte[] keepCopy = new byte[numBytes];
                System.arraycopy(data, 0, keepCopy, 0, numBytes);
                recvdBytesBuff.add(keepCopy);
            }
        } else {
            if (recvdBytesBuff.size() > 0) {
                for (byte[] storedCopy : recvdBytesBuff) {
                    ProcessReceivedBytes(storedCopy, storedCopy.length);
                }
                recvdBytesBuff.clear();
            }
            ProcessReceivedBytes(data, numBytes);
        }
    }

    boolean ProcessReceivedBytes(byte[] data, int numBytes) {
        for (WaitForStringInfo waInfo : waitForList) {
            for (int j = 0; j < numBytes; ++j) {
                waInfo.recvdBytes.push(data[j]);
                waInfo.recvdBytes.removeElementAt(0);
                if (waInfo.recvdBytes.get(0) != null) {
                    int index = 0;
                    boolean found = true;
                    for (int i = 0; i < waInfo.recvdBytes.size(); ++i) {
                        Byte by = waInfo.recvdBytes.get(i);
                        if (by == null) {
                            found = false;
                            break;
                        }
                        if (by.byteValue() != waInfo.waitForString[i]) {
                            found = false;
                            break;
                        }
                    }
                    if (found) {
                        foundStr = waInfo;
                        synchronized (waitObject) {
                            waitObject.notify();
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void onEvent(MainControler.ConEvents event, Object param, Object source) {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        if (event == event.ConClosed) {
            Stop();
        }
    }

    @Override
    public void onError(MainControler.Error error, Object details) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onConnectionModeChange(MainControler.ConnectionMode mode) {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onSelectedConChange(IConnection connection) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    class CommandStepInfo {

        int startPointer;
        int nextPointer;
        String commandPart;
        ArrayList<String> tokens = new ArrayList<String>();
    }

    class WaitForStringInfo {

        Stack<Byte> recvdBytes = new Stack<Byte>();
        byte[] waitForString;
        int index;
    }
    CommandStepInfo curState;
    ArrayList<CommandStepInfo> commandList;//= ArrayList<CommandStepInfo> ();
    Thread commandInterThread;
    boolean stopExec = false;

    public void addStatusListner(CommandExecStatus listner) {
        listeners.add(listner);
    }

    public void Execute(String script) {
        MainControler.AddConEventListner(this);
        commandList = new ArrayList<CommandStepInfo>();
        curState = new CommandStepInfo();
        this.script = script;
        commandInterThread = new Thread(new ScriptExec());
        commandInterThread.start();
    }

    public void Stop() {
        MainControler.RemoveConEventListner(this);
        stopExec = true;
        if ((commandInterThread != null) && (commandInterThread.isAlive())) {
            commandInterThread.interrupt();
        }
        bufferDataFlg = false;
    }

    class ScriptExec implements Runnable {

        @Override
        public void run() {
            try {
                sendError("Script", "Parsing script");
                CommandListReader();
                while (getNextCommandTokens()) {
                    CommandStepInfo nextStep = new CommandStepInfo();
                    nextStep.startPointer = curState.nextPointer;
                    curState = nextStep;
                }
                if (scriptError == ScriptErrorType.EndOfFile) {
                    sendError("Script", "Executing script");
                    ExecuteCommand();
                } else {
                    sendError("Script", "Error in script, Execution aborted");
                }

            } catch (Exception exp) {
                sendError("Coomand Parsing", exp.getMessage());
            }
        }

        byte[] convertToBytes(String inputString) {
            if (inputString.contains("//")) { //checking for comment
                inputString = inputString.substring(0, inputString.indexOf("//"));
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            boolean escFound = false;
            boolean hexExtract = false;
            String hexBytes = "";
            int hexCount = 0;
            ArrayList<String> hexVals = new ArrayList<String>();
            try {
                for (int i = 0; i < inputString.length(); ++i) {
                    if (hexExtract) {
                        if ((inputString.charAt(i) != ' ') && (inputString.charAt(i) != '\\')) {
                            hexBytes += inputString.charAt(i);
                            if (hexCount % 2 == 1) {
                                hexVals.add(hexBytes);
                                hexBytes = new String("");
                            }
                            hexCount++;
                        } else {
                            for (String hexValStr : hexVals) {
                                int hexVal = Integer.parseInt(hexValStr, 16);
                                outputStream.write(hexVal);
                            }
                            hexExtract = false;
                            hexVals.clear();
                        }
                        continue;
                    }
                    if (escFound) {
                        switch (inputString.charAt(i)) {
                            case 'r':
                                outputStream.write(0x0D);
                                break;
                            case 'n':
                                outputStream.write(0x0A);
                                break;
                            case 't':
                                outputStream.write(0x09);
                                break;
                            case '\\':
                                outputStream.write(0x5c);
                                break;
                            case 'x':
                                hexBytes = "";
                                hexExtract = true;
                                hexCount = 0;
                                break;
                        }
                        escFound = false;
                        continue;
                    }
                    if (inputString.charAt(i) != '\\') {
                        outputStream.write(inputString.charAt(i));
                    } else {
                        escFound = true;
                    }
                }
                if (hexExtract) {
                    for (String hexValStr : hexVals) {
                        int hexVal = Integer.parseInt(hexValStr, 16);
                        outputStream.write(hexVal);
                    }
                    hexExtract = false;
                    hexVals.clear();
                }
                return outputStream.toByteArray();
            } catch (Exception exp) {
                JOptionPane.showMessageDialog(null,
                        "Erron in input text",
                        exp.getMessage(),
                        JOptionPane.WARNING_MESSAGE);
            }
            return null;
        }

        boolean ExecuteCommand() {
            CommandStepInfo cmd = null;
            try {
                int instructionPtr = 0;

                while (commandList.size() > instructionPtr) {
                    if (stopExec) {
                        break;
                    }
                    cmd = commandList.get(instructionPtr);
                    switch (cmd.commandPart) {
                        case CMD_WAIT_SEC:
                            sendStatus("Line Num "+ instructionPtr+":"+CMD_WAIT_SEC, "Executing");
                            if (cmd.tokens.size() > 1) {
                                break;
                            }
                            long secs = Integer.parseInt(cmd.tokens.get(0));

                            Thread.sleep(secs * 1000);
                            sendStatus(CMD_WAIT_SEC, "Execution completed");
                            break;
                        case CMD_WAIT_MILLIS:
                            sendStatus("Line Num "+ CMD_WAIT_MILLIS, "Executing");
                            if (cmd.tokens.size() > 1) {
                                break;
                            }
                            secs = Integer.parseInt(cmd.tokens.get(0));

                            Thread.sleep(secs);
                            sendStatus(CMD_WAIT_MILLIS, "Execution completed");
                            break;
                        case CMD_SEND:
                            sendStatus("Line Num "+ instructionPtr+":"+CMD_SEND, "Executing");
                            if (cmd.tokens.size() > 1) {
                                sendStatus(CMD_SEND, "paramter count not match");
                                break;
                            }
                            if (CommonDataArea.connection == null) {
                                sendStatus(CMD_SEND, "no valid connection exist");
                            }
                            String toSend = cmd.tokens.get(0);
                           // byte[] bytes = toSend.getBytes();
                             byte[] bytes = convertToBytes(toSend);
                            CommonDataArea.connection.send(bytes);
                            sendStatus(CMD_SEND, "Execution completed");
                            break;
                        case CMD_GOTO:
                            sendStatus("Line Num "+ instructionPtr+":"+CMD_GOTO, "Executing");
                            if (cmd.tokens.size() > 1) {
                                break;
                            }
                            boolean labelFound= false;
                            String label = cmd.tokens.get(0);
                            int step = 0;
                            for (CommandStepInfo info : commandList) {
                                if (info.commandPart.contains(":label")) {
                                    if (info.tokens.size() == 1) {
                                        if (info.tokens.get(0).contains(cmd.tokens.get(0))) {
                                            instructionPtr = (step - 1);
                                            sendStatus(CMD_GOTO, "Found Label at -"+instructionPtr);
                                            labelFound=true;
                                            break;
                                        }
                                    }
                                }
                                ++step;
                            }
                            if(!labelFound) sendStatus(CMD_GOTO, "Label not fpound -> "+label);
                            break;
                        case CMD_WAITFOR:
                            sendStatus("Line Num "+ instructionPtr+":"+CMD_WAITFOR, "Executing");
                            if (cmd.tokens.size() > 1) {
                                sendStatus(CMD_SEND, "paramter count not match");
                                break;
                            }
                            if (CommonDataArea.connection == null) {
                                sendStatus(CMD_SEND, "no valid connection exist");
                            }
                            
                            String toWait = cmd.tokens.get(0);
                            WaitForStringInfo waInfo = new WaitForStringInfo();
                            waInfo.waitForString = toWait.getBytes();
                            waInfo.recvdBytes.setSize(waInfo.waitForString.length);

                            waitForList.clear();
                            waitForList.add(waInfo);

                            waitFlag = true;
                            if (recvdBytesBuff.size() > 0) {
                                for (byte[] storedCopy : recvdBytesBuff) {
                                    if (ProcessReceivedBytes(storedCopy, storedCopy.length)) {
                                        waitFlag = false;
                                        break;
                                    }
                                }
                                recvdBytesBuff.clear();
                            }
                            try {
                                synchronized (waitObject) {
                                    waitObject.wait(60000);
                                }
                            } catch (Exception exp) {

                            }
                            waitFlag = false;
                            sendStatus(CMD_WAITFOR, "Execution completed");
                            break;
                        case CMD_WAITMANY:
                            sendStatus("Line Num "+ instructionPtr+":"+CMD_WAITMANY, "Executing");
                            int nextInstr = ProcessForManyReturn(cmd, instructionPtr);
                            if (nextInstr == -1) {
                                sendStatus(CMD_SEND, "Case not found");
                                break;
                            }
                            instructionPtr = nextInstr;
                            sendStatus(CMD_WAITMANY, "Execution completed");
                            break;
                        case CMD_BUFFER:
                            sendStatus("Line Num "+ instructionPtr+":"+CMD_BUFFER, "Executing");
                            if (cmd.tokens.size() > 1) {
                                sendStatus(CMD_SEND, "paramter count not match");
                                break;
                            }
                            if (CommonDataArea.connection == null) {
                                sendStatus(CMD_SEND, "no valid connection exist");
                            }
                            if (cmd.tokens.get(0).equals("enable")) {
                                bufferDataFlg = true;
                            } else {
                                bufferDataFlg = false;
                            }
                            sendStatus(CMD_BUFFER, "Execution completed");
                            break;

                    }
                    ++instructionPtr;
                }
            } catch (Exception exp) {
                sendStatus(cmd.commandPart, exp.getMessage());
            }
            return true;
        }

        int ProcessForManyReturn(CommandStepInfo cmd, int instructionPtr) {

            if (CommonDataArea.connection == null) {
                sendStatus(CMD_SEND, "no valid connection exist");
                return -1;
            }
            waitForList.clear();
            int i = 0;
            for (String toWait1 : cmd.tokens) {
                
                WaitForStringInfo waInfo1 = new WaitForStringInfo();
                waInfo1.waitForString = convertToBytes(toWait1);
                waInfo1.recvdBytes.setSize(waInfo1.waitForString.length);
                waInfo1.index = i;
                waitForList.add(waInfo1);
                ++i;
            }

            waitFlag = true;
            boolean foundInBuf = false;
            if (recvdBytesBuff.size() > 0) {
                for (byte[] storedCopy : recvdBytesBuff) {
                    foundInBuf = ProcessReceivedBytes(storedCopy, storedCopy.length);
                    if (foundInBuf) {
                        break;
                    }
                }
                recvdBytesBuff.clear();
            }
            if (!foundInBuf) {
                try {
                    synchronized (waitObject) {
                        waitObject.wait(60000);
                    }
                } catch (Exception exp) {

                }
            }

            waitFlag = false;

            String label = cmd.tokens.get(0);
            int step = 0;
            for (step = instructionPtr; step < commandList.size(); ++step) {
                CommandStepInfo info = commandList.get(step);
                if (info.commandPart.equals(CMD_CASE)) {
                    if (info.tokens.size() == 1) {
                        String loc = Integer.toString(foundStr.index);
                        if (info.tokens.get(0).equals(loc)) {
                            instructionPtr = (step - 1);
                            return instructionPtr;
                        }
                    }
                }
                if (info.commandPart.equals(CMD_DEFAULT)) {
                    return instructionPtr;
                }
                if (info.commandPart.equals(CMD_END)) {
                    return -1;
                }

            }
            return -1;
        }

        void CommandListReader() {
            JSONParser jsonParser = new JSONParser();
            String fileName = ClassLoader.getSystemResource("res/scriptcommands.json").getFile();
            try (FileReader reader = new FileReader(fileName)) {
                //Read JSON file
                Object obj = jsonParser.parse(reader);

                commandsJson = (JSONArray) obj;
                System.out.println(commandsJson);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (org.json.simple.parser.ParseException ex) {
                Logger.getLogger(CommandInterpretor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        int checkValidCommand(String command) {
            for (Object obj : commandsJson) {
                JSONObject commandInfo = (JSONObject) obj;
                String commandFound = (String) commandInfo.get("cmd");
                if (commandFound.equals(command)) {
                    long paramsL = (long) commandInfo.get("params");
                    // int paramNum = Integer.parseInt(paramsStr);
                    return (int) paramsL;
                }
            }
            return -1;
        }

      /*  int ignoreQuoteWithSlash(String cmd, int index)
        {
            while((index =cmd.indexOf("\\\"",index))!=-1)
        }*/
        boolean splitToTokens(String command) {
            int length = command.length();
            int index = 0;
            while ((command.charAt(index) == '\r') || (command.charAt(index) == '\n') || (command.charAt(index) == ' ')) {
                ++index;
                if (length <= index) {
                    sendError("Line Number -" + lineNumber, "failed to found parameter, end of text reached for command -" + command + "\r\n");
                    return false;
                }
            }

            int indexSpace = command.indexOf(' ', index);
            if (indexSpace == -1) {
                indexSpace = command.indexOf(index, '\r');
            }
            if (indexSpace == -1) {
                indexSpace = command.indexOf(index, '\n');
            }
            if (indexSpace != -1) {
                length = indexSpace - index;
            }

            curState.commandPart = command.substring(index, length);
            curState.commandPart = curState.commandPart.toLowerCase();
            int paramNum = checkValidCommand(curState.commandPart);
            if (paramNum == -1) {
                sendError("Line Number -" + lineNumber, "Invalid command");
                return false;
            }
            if (indexSpace == -1) {
                sendError("Line Number -" + lineNumber, "Parameter not found");
                if (paramNum == 0) {
                    return true;
                } else {
                    return false;
                }
            }
            length = command.length();
            while (true) {
                ++indexSpace;
                if (indexSpace >= length) {
                    return true;
                }
                if (command.charAt(indexSpace) == ' ') {
                    continue;
                } else if (command.charAt(indexSpace) == '"') {
                    int nextEnd = command.indexOf('"', indexSpace + 1);
                    if (nextEnd == -1) {
                        sendError("Line Number-" + lineNumber, "failed to found end of string \r\n");
                        return false;
                    }
                    String token = command.substring(indexSpace + 1, nextEnd);
                    curState.tokens.add(token);
                    indexSpace = nextEnd + 1;
                    if (indexSpace >= length) {
                        return true;
                    }
                } else if ((command.charAt(indexSpace) == '\r') || (command.charAt(indexSpace) == '\n')) {
                    return true;
                } else {
                    int nextEnd = command.indexOf(' ', indexSpace);
                    if (nextEnd == -1) {
                        nextEnd = command.indexOf('\r', indexSpace);
                    }
                    if (nextEnd == -1) {
                        nextEnd = command.indexOf('\n', indexSpace);
                    }
                    if (nextEnd == -1) {
                        nextEnd = length;
                    }

                    String token = command.substring(indexSpace, nextEnd);
                    curState.tokens.add(token);
                    indexSpace = nextEnd + 1;
                    if (indexSpace >= length) {
                        return true;
                    }
                }
            }
        }

        int lineNumber = 0;

        boolean getNextCommandTokens() {
            //curState.startPointer = curState.nextPointer;
            ++lineNumber;
            int endTokenIndex = script.indexOf("\n", curState.startPointer);
            if (endTokenIndex == -1) {
                sendError("Line Number -" + lineNumber, "end of script reached\r\n");
                scriptError = ScriptErrorType.EndOfFile;
                return false;
            }
            String command = script.substring(curState.startPointer, endTokenIndex);
            curState.nextPointer = endTokenIndex + 1;
            if ((command.length() == 0) || command == "") {
                return true;
            }
            if (splitToTokens(command) == true) {
                commandList.add(curState);
                //sendError(curState.commandPart, curState.tokens.toString());
                return true;
            } else {
                sendError("Line Number -" + lineNumber, "Token generation failed for command -" + command + "\r\n");
                scriptError = ScriptErrorType.TokenError;
                return false;
            }
        }

        void sendError(String command, String error) {
            for (CommandExecStatus listner : listeners) {
                listner.CommandParseStatus(command, error);
            }
        }

        void sendStatus(String command, String error) {
            for (CommandExecStatus listner : listeners) {
                listner.CommandExecStatus(command, error);
            }
        }
    }
}
