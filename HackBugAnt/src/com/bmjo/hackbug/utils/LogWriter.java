/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmjo.hackbug.utils;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author BMJO <bmjo@iOrbit>
 */
public class LogWriter {

    public static void WriteLog(String tag, String desc) {

        try {
           
            String filePath = System.getProperty("user.home")+"//hackbug//hackbug.txt";
            String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
            String str =timeStamp+":"+ tag + ":" + desc + "\r\n";

            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true));
            writer.write(str);

            writer.close();

        } catch (Exception ex) {
            Logger.getLogger(LogWriter.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
