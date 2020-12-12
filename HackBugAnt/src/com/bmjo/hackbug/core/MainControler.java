/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmjo.hackbug.core;

import com.bmjo.hackbug.serial.SerialPortConection;
import java.util.ArrayList;
import java.util.ArrayList;
import com.bmjo.hackbug.ui.ConnectionModeView;
/**
 *
 * @author bijum
 */
public class MainControler {
    public static enum Error
    {
        ConnectionLost,
        Timeout
    }
    public static enum ConEvents
    {
        ConnectionModeChange,
        Connecting,
        Connected,
        ConExists,
        ConFailed,
        ConClosed,
        SendCompleted,
        SendFailed,
        ReadFailed,
        
    }
    public static String CONMODE_SERIAL = "Serial";
    public static String CONMODE_TCPSERVER = "TCP Server";
    public static String CONMODE_TCPCLIENT = "TCP Client";
    public static enum ConnectionMode {
        Serial,
        TCPClient,
        TCPServer
      }
    static  ArrayList<IConnectionEvents> eventHandlers = new ArrayList<IConnectionEvents>();
    public static void Init(){
        CommonDataArea.connectionMode = ConnectionMode.Serial;
        CommonDataArea.connection = new SerialPortConection();
    }
  
    public static void send(byte [] data){
        try{
        if(CommonDataArea.connection!=null){
            CommonDataArea.connection.send(data);
        }
        }catch(Exception exp){
            fireErroEvent(Error.ConnectionLost,exp.getMessage());
        }
    }
     public static void fireErroEvent(MainControler.Error type, Object param){
        for(IConnectionEvents conEvent :eventHandlers ){
            conEvent.onError(type,param);
        }
    }
    public static void fireConEvent(ConEvents type, Object param){
        for(IConnectionEvents conEvent :eventHandlers ){
            conEvent.onEvent(type,param);
        }
    }
     public static void fireConModeEvent(ConnectionMode type){
        for(IConnectionEvents conEvent :eventHandlers ){
            conEvent.onConnectionModeChange(type);
        }
    }
    public static void AddConEventListner(IConnectionEvents eventListner){
        if(eventListner!=null) {
          if(!eventHandlers.contains(eventListner))eventHandlers.add(eventListner);
      }
    }
    
   public static void onReceive(byte [] data, int numBytes,IConnection eventSource,Object eventInfo){
        for(IConnectionEvents conEvent :eventHandlers ){
            conEvent.onReceive(data, numBytes,eventSource,eventInfo);
        }
   }

}
