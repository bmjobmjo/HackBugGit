/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmjo.hackbug.core;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

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
                while (getNextCommandTokens()) {
                    CommandStepInfo nextStep = new CommandStepInfo();
                    nextStep.startPointer = curState.nextPointer;
                    curState = nextStep;
                }
                sendError("Script", "Executing script");
                ExecuteCommand();
            } catch (Exception exp) {
                sendError("Coomand Parsing", exp.getMessage());
            }
        }

        boolean ExecuteCommand() {
            try {
                int instructionPtr = 0;
                CommandStepInfo cmd = null;
                while (commandList.size() > instructionPtr) {
                    if (stopExec) {
                        break;
                    }
                    cmd = commandList.get(instructionPtr);
                    switch (cmd.commandPart) {
                        case CMD_WAIT_SEC:
                            sendStatus(CMD_WAIT_SEC, "Executing");
                            if (cmd.tokens.size() > 1) {
                                break;
                            }
                            long secs = Integer.parseInt(cmd.tokens.get(0));

                            Thread.sleep(secs * 1000);
                            sendStatus(CMD_WAIT_SEC, "Execution completed");
                            break;
                        case CMD_WAIT_MILLIS:
                            sendStatus(CMD_WAIT_MILLIS, "Executing");
                            if (cmd.tokens.size() > 1) {
                                break;
                            }
                            secs = Integer.parseInt(cmd.tokens.get(0));

                            Thread.sleep(secs);
                            sendStatus(CMD_WAIT_MILLIS, "Execution completed");
                            break;
                        case CMD_SEND:
                            sendStatus(CMD_SEND, "Executing");
                            if (cmd.tokens.size() > 1) {
                                sendStatus(CMD_SEND, "paramter count not match");
                                break;
                            }
                            if (CommonDataArea.connection == null) {
                                sendStatus(CMD_SEND, "no valid connection exist");
                            }
                            String toSend = cmd.tokens.get(0);
                            byte[] bytes = toSend.getBytes();
                            CommonDataArea.connection.send(bytes);
                            sendStatus(CMD_SEND, "Execution completed");
                            break;
                        case CMD_GOTO:
                            sendStatus(CMD_WAIT_MILLIS, "Executing");
                            if (cmd.tokens.size() > 1) {
                                break;
                            }
                            String label = cmd.tokens.get(0);
                            int step = 0;
                            for (CommandStepInfo info : commandList) {
                                if (info.commandPart.contains(":label")) {
                                    if (info.tokens.size() == 1) {
                                        if (info.tokens.get(0).contains(cmd.tokens.get(0))) {
                                            instructionPtr = (step - 1);
                                            break;
                                        }
                                    }
                                }
                                ++step;
                            }
                            break;
                        case CMD_WAITFOR:
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
                                   if(ProcessReceivedBytes(storedCopy, storedCopy.length)) {
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
                            break;
                        case CMD_WAITMANY:

                            int nextInstr = ProcessForManyReturn(cmd, instructionPtr);
                            if (nextInstr == -1) {
                                sendStatus(CMD_SEND, "Case not found");
                                break;
                            }
                            instructionPtr = nextInstr;
                            break;
                        case CMD_BUFFER:
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
                            break;

                    }
                    ++instructionPtr;
                }
            } catch (Exception exp) {
                sendStatus(CMD_SEND, exp.getMessage());
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
                waInfo1.waitForString = toWait1.getBytes();
                waInfo1.recvdBytes.setSize(waInfo1.waitForString.length);
                waInfo1.index = i;
                waitForList.add(waInfo1);
                ++i;
            }

            waitFlag = true;
            boolean foundInBuf = false;
            if (recvdBytesBuff.size() > 0) {
                for (byte[] storedCopy : recvdBytesBuff) {
                    foundInBuf=ProcessReceivedBytes(storedCopy, storedCopy.length);
                    if(foundInBuf) break;
                }
                recvdBytesBuff.clear();
            }
            if(!foundInBuf){
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

        boolean splitToTokens(String command) {
            int length = command.length();
            int index = 0;
            while ((command.charAt(index) == '\r') || (command.charAt(index) == '\n') || (command.charAt(index) == ' ')) {
                ++index;
                if (length <= index) {
                    sendError("Line Number -"+lineNumber, "failed to found parameter, end of text reached for command -"+command+"\r\n");
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
            if (indexSpace == -1) {
                curState.commandPart = command.substring(index, length);
                curState.commandPart = curState.commandPart.toLowerCase();
                return true;
            }

            curState.commandPart = command.substring(index, indexSpace);
            curState.commandPart = curState.commandPart.toLowerCase();
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
                        sendError("Line Number-"+lineNumber, "failed to found end of string \r\n");
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

        int lineNumber =0;
        boolean getNextCommandTokens() {
            //curState.startPointer = curState.nextPointer;
            ++lineNumber;
            int endTokenIndex = script.indexOf("\n", curState.startPointer);
            if (endTokenIndex == -1) {
                sendError("Line Number -"+lineNumber, "end of line not found\r\n");
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
                sendError("Line Number -"+lineNumber, "Token generation failed for command -"+command+"\r\n");
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
