/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmjo.hackbug.core;

/**
 *
 * @author bijum
 */
public interface IConnection {
    public boolean connect(Object conInfo);
    public boolean send(byte [] data);
    public boolean sendAsync(byte [] data);
    public byte [] receive();
    public String getErrorString();
    public int getErrorValue();
    public boolean close();
    public String getConnectionName();
    
    
}
