/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmjo.hackbug.utils;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author bijum
 */
public class AlertBox {
    public static void Alert( String mesg){
         JFrame f=new JFrame();  
         JOptionPane.showMessageDialog(f, mesg);
    }
}
