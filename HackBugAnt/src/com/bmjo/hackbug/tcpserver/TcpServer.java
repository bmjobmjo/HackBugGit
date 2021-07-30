/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmjo.hackbug.tcpserver;

import com.bmjo.hackbug.core.CommonDataArea;
import com.bmjo.hackbug.core.IConnection;
import com.bmjo.hackbug.core.MainControler;
import com.bmjo.hackbug.tcp.TcpConnection;
import com.bmjo.hackbug.utils.LogWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author bmjo
 */
public class TcpServer implements IConnection{

    private static ServerSocket server;
     public static class ConParams {
        public String serverIP;
        public int port;
    }
    Thread serverThread ;
    ServerThreadRun  serverThreadRun;
    ArrayList<TcpConnectionContext> conList;

    @Override
    public boolean connect(Object conInfo) {
         ConParams conParam = (ConParams) conInfo;
        try {
         conList = new ArrayList<TcpConnectionContext>();
         server = new ServerSocket(conParam.port);
         serverThreadRun = new ServerThreadRun();
         serverThread = new Thread(serverThreadRun );
         serverThread.start();
         CommonDataArea.conList = conList;
         MainControler.fireConEvent(MainControler.ConEvents.Connected,"Server started..\r\n",this);
        } catch (Exception exp) {
            MainControler.fireConEvent(MainControler.ConEvents.ConFailed, exp.getMessage()+"\r\n",this);
            return false;
        }
        return true;
    }

    @Override
    public boolean send(byte[] data) {
       new Thread(new Runnable() {
           @Override
           public void run() {
              for(String conName: CommonDataArea.selectedCon){
                  for(IConnection con : conList){
                      if(con.getConnectionName()==conName)
                      con.send(data);
                  }
              }
           }
       }).run();
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getErrorValue() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean close() {
        try {
            server.close();
            for (TcpConnectionContext conCon : conList) {
                conCon.close();
            }
            conList.clear();
        } catch (Exception exp) {
            return false;
        }
        return true;
    }

    @Override
    public String getConnectionName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    class ServerThreadRun implements Runnable {
        @Override
        public void run() {
            try {
                
                while (true) {
                    Socket socket = server.accept();
                    TcpConnectionContext conContext = new TcpConnectionContext();
                    conContext.connect(socket);
                    conList.add(conContext);
                }
            } catch (Exception exp) {
                 LogWriter.WriteLog("Exception", exp.getMessage());
            }
        }
    }
}
