/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmjo.hackbug.core;

import com.bmjo.hackbug.core.MainControler.ConnectionMode;
import com.bmjo.hackbug.tcpserver.TcpConnectionContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.DefaultListModel;



/**
 *
 * @author bijum
 */
public class CommonDataArea {
    public static ConnectionMode connectionMode;
    public static IConnection connection;
    public static int conCount=0;
    public static ArrayList<TcpConnectionContext> conList;
    public static Vector<String> conInfoList;
    public static ArrayList<String> selectedCon;
    
}
