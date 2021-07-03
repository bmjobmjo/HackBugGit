/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmjo.hackbug.usb;

import com.bmjo.hackbug.core.CommonDataArea;
import com.bmjo.hackbug.core.IConnection;
import com.bmjo.hackbug.core.MainControler;
import com.bmjo.hackbug.serial.SerialPortConection;
import com.bmjo.hackbug.utils.LogWriter;

/**
 *
 * @author bijum
 */
public class UsbControler implements IConnection{
    USBHelper usbHelper ;
    boolean running;
    Thread threadReader;
    String conName;
    @Override
    public boolean connect(Object conInfo) {
        usbHelper = new USBHelper();
        if(usbHelper.Open((UsbConParams)conInfo)) {
               ++CommonDataArea.conCount;
                conName = ((UsbConParams)conInfo).vid+":"+ ((UsbConParams)conInfo).pid+":"+CommonDataArea.conCount;
                MainControler.fireConEvent(MainControler.ConEvents.Connected, "USB Device "+conName+ " Opened\r\n",this);

        }else{
              MainControler.fireConEvent(MainControler.ConEvents.ConFailed,"Failed to Open usb device ",this);
                return false;

        }
        running = true;
        threadReader = new Thread(new ReadUsbData());
        threadReader.start();
        return true;
    }

    @Override
    public boolean send(byte[] data) {
        usbHelper.writeUsb(data);
        return true;
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
    public String getErrorString() {
        return usbHelper.getError();
    }

    @Override
    public int getErrorValue() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean close() {
        try {
            usbHelper.close();
            running = false;
            
            threadReader.join(1000);
            MainControler.fireConEvent(MainControler.ConEvents.ConClosed, "USB device closed", this);

            return true;
        } catch (Exception exp) {
            return false;
        }
    }

    @Override
    public String getConnectionName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

       void fireReadComplete(byte[] data, int numBytes){
       MainControler.onReceive(data, numBytes,this,null);
    }
    
    class ReadUsbData implements Runnable{
        @Override
        public void run() {
          
           while(running){
              try{
              byte [] block = usbHelper.readUsb();
               
               if((block!=null)&&(block.length>0)){
               int len =block.length;
               fireReadComplete(block, len);
               }
              }catch(Exception exp){
                  LogWriter.WriteLog("USB READ", exp.getMessage());
              }
           }
        }
        
    }
      
   
}
