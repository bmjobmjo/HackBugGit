/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmjo.hackbug.serial;
import com.bmjo.hackbug.core.*;
import com.bmjo.hackbug.utils.LogWriter;
import com.fazecast.jSerialComm.*;
import java.util.ArrayList;
/**
 *
 * @author bijum
 */
public class SerialPortConection  implements IConnection{

    public static final int PORT_NOT_CONNECTED =100;
    public static final String PORT_NOT_CONNECTED_MESG ="PORT Not in connected state";
     
    int lastError =0;
    String lastErrorMesg="";
   
    boolean running;
    Thread threadReader;
    String conName;

    @Override
    public String getConnectionName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public enum Parity{
       Even,Odd,None
   }
    public static class ConParams{
       public String port;
       public int baudRate;
       public int bits;
       public Parity parity;
   }
    SerialPort port;
    ConParams conParams;
    public SerialPortConection(){
       
    }
   
    @Override
    public boolean connect(Object conInfo) {
         try{
            conParams = (ConParams) conInfo;
            port = SerialPort.getCommPort(conParams.port);
            port.setNumDataBits(conParams.bits);
            port.setBaudRate(conParams.baudRate);
            switch(conParams.parity){
                case Even:
                    port.setParity(SerialPort.EVEN_PARITY);
                    break;
                case Odd:
                    port.setParity(SerialPort.ODD_PARITY);
                    break;
                case None:
                    port.setParity(SerialPort.NO_PARITY);
                    break;
            }
            if(port.isOpen()){
                port.closePort();
            }
            if(port.openPort()){
                port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 100, 100);
                ++CommonDataArea.conCount;
                conName = conParams.port+":"+CommonDataArea.conCount;
                MainControler.fireConEvent(MainControler.ConEvents.Connected, "Serial port "+port.getSystemPortName()+ " Opened\r\n");
                
                 running = true;
                 threadReader = new Thread(new ReadSerialData());
                 threadReader.start();
                 return true;
            }else {
                MainControler.fireConEvent(MainControler.ConEvents.ConFailed,"Failed to Open Serial port "+port.getSystemPortName()+"\r\n");
                return false;
            }
            
        }catch(SerialPortInvalidPortException exp){
             MainControler.fireConEvent(MainControler.ConEvents.ConFailed,"Failed to Open Serial port "+port.getSystemPortName()+"\r\n");
             return false;
        }
    }
    @Override
    public String getErrorString() {
       return "";
    }

    @Override
    public int getErrorValue() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public boolean send(byte[] data) {
        try{
        if(port.isOpen()){
            port.writeBytes(data,data.length);
            return true;
        }else {
            MainControler.fireConEvent(MainControler.ConEvents.ConFailed,"Failed to send on serial port "+port.getSystemPortName()+"\r\n");
            lastError = PORT_NOT_CONNECTED;
            lastErrorMesg = PORT_NOT_CONNECTED_MESG;
            return false;
        }
        }catch(Exception exp){
            LogWriter.WriteLog(conName, exp.getMessage());
            return false;
        }
    }

    @Override
    public boolean sendAsync(byte[] data) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public byte[] receive() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean close() {
        try{
            port.closePort();
            running = false;
            threadReader.join(1000);
            MainControler.fireConEvent(MainControler.ConEvents.ConClosed,"Closed Serial Port "+port.getSystemPortName()+"\r\n");
            return true;
        }catch(Exception exp){
            return false;
        }
    }
    
    
    void fireReadComplete(byte[] data, int numBytes){
       MainControler.onReceive(data, numBytes,this,null);
    }
    
    class ReadSerialData implements Runnable{
        @Override
        public void run() {
          
           while(running){
               byte [] block = new byte[256];
               int len =port.readBytes(block,block.length);
               if(len>0){
                   fireReadComplete(block, len);
               }else {
                   if(!port.isOpen()) return;
               }
        }
        
    }
    }
}
