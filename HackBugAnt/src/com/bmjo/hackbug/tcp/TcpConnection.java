/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmjo.hackbug.tcp;

import com.bmjo.hackbug.serial.*;
import com.bmjo.hackbug.core.*;
import com.fazecast.jSerialComm.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bijum
 */
public class TcpConnection implements IConnection {

    public static final int PORT_NOT_CONNECTED = 100;
    public static final String PORT_NOT_CONNECTED_MESG = "PORT Not in connected state";

    int lastError = 0;
    String lastErrorMesg = "";

    boolean running;
    Thread threadReader;
    OutputStream out;
    InputStream in;
    Socket con;
    String conName;

    @Override
    public String getConnectionName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static class ConParams {

        public String serverIP;
        public int port;

    }

    public TcpConnection() {

    }

    @Override
    public boolean connect(Object conInfo) {
        ConParams conParam = (ConParams) conInfo;
        try {
            con = new Socket();
            con.connect(new InetSocketAddress(conParam.serverIP, conParam.port), 30000);
            MainControler.fireConEvent(MainControler.ConEvents.Connected, "Connected to: "+conParam.serverIP+ "Port: "+conParam.port+"\r\n");
             
            out = (OutputStream) con.getOutputStream();
            in = (InputStream) con.getInputStream();
            threadReader = new Thread(new ReadSockData());
            threadReader.start();
            ++CommonDataArea.conCount;
            conName = conParam.serverIP+":"+conParam.port+":"+CommonDataArea.conCount;
            return true;
        } catch (Exception exp) {
             MainControler.fireConEvent(MainControler.ConEvents.ConFailed, exp.getMessage()+"\r\n");

            return false;
        }
    }

    @Override
    public String getErrorString() {
        return lastErrorMesg;
    }

    @Override
    public int getErrorValue() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean send(byte[] data) {
        try {
            out.write(data);
            return true;
        } catch (IOException ex) {
            Logger.getLogger(TcpConnection.class.getName()).log(Level.SEVERE, null, ex);
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
        try {
            con.close();
            MainControler.fireConEvent(MainControler.ConEvents.ConClosed,"Connnection Closed \r\n");

            return true;
        } catch (Exception exp) {
            return false;
        }
    }

    byte[] readData;
    boolean closeIt = false;

    class ReadSockData implements Runnable {

        @Override
        public void run() {
            readData = new byte[512];
            try {
                while (!closeIt) {
                    int numBytes = in.read(readData);
                    if(numBytes==-1) break;
                    MainControler.onReceive(readData, numBytes, TcpConnection.this, null);
                }
                MainControler.fireConEvent(MainControler.ConEvents.ConClosed, "Connnection Closed \r\n");
            } catch (Exception exp) {

            }
        }
    }
}
