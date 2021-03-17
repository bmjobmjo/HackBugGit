/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmjo.hackbug.core;
import com.bmjo.hackbug.core.MainControler.ConEvents;
import com.bmjo.hackbug.core.MainControler.ConnectionMode;
import com.bmjo.hackbug.core.MainControler.Error;
/**
 *
 * @author BMJO <bmjo@iOrbit>
 */
public interface IConnectionEvents {
     public void onReceive(byte [] data, int numBytes,IConnection eventSource,Object eventInfo);
     public void onEvent(ConEvents event,Object param,Object source);
     public void onError(Error error, Object details);
     public void onConnectionModeChange(ConnectionMode mode);
     public void onSelectedConChange(IConnection connection);
}
