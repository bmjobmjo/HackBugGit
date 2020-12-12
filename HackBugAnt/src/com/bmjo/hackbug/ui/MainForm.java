/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmjo.hackbug.ui;
import com.bmjo.hackbug.core.CommonDataArea;
import com.bmjo.hackbug.core.IConnection;
import com.bmjo.hackbug.core.IConnectionEvents;
import com.bmjo.hackbug.core.MainControler;
import com.bmjo.hackbug.core.MainControler.ConEvents;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URISyntaxException;
//import java.security.Timestamp;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.Timer;
import org.w3c.dom.events.Event;
import org.xml.sax.Attributes;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author bijum
 */
public class MainForm extends javax.swing.JFrame {

    boolean dispPause=false;
    FileOutputStream saveLogFile;
    boolean saveEnabled = false;
    boolean timeStampEnabled = false;
    /**
     * Creates new form MainForm
     */
    public MainForm() {
        MainControler.Init();
        initComponents();
        MainControler.AddConEventListner(new ConEventsHandler());
         loadPerstValues();
         
         addWindowListener(new WindowAdapter() {
             public void windowClosing(WindowEvent e){
                   savePersistValues();
                }
         });
    }

   
     
   public void loadPerstValues() {
        ObjectInputStream objectinputstream = null;
        try {
            String jarPath="excpetion";
            try {
                jarPath = MainForm.class
                        .getProtectionDomain()
                        .getCodeSource()
                        .getLocation()
                        .toURI()
                        .getPath();
            } catch (URISyntaxException ex) {
                Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            String filePath = jarPath;
            filePath=filePath.substring(0, filePath.lastIndexOf("/"))+"/persist.ser";
            FileInputStream streamIn = new FileInputStream(filePath);
            objectinputstream = new ObjectInputStream(streamIn);
           

            for (int i = 0; i < 10; ++i) {
                String compoName = "SendText"+i;
                JTextField sednText = new JTextField();

                for (Component sendTextCom : jPanel1SendArea.getComponents()) {
                    try {
                        if (((JTextField) sendTextCom).getName().contains(compoName)) {
                            sednText = (JTextField) sendTextCom;
                             String persVal = (String) objectinputstream.readObject();
                             sednText.setText(persVal);
                            break;
                        }
                    } catch (java.lang.ClassCastException exp) {

                    }
                }
            }
            if (objectinputstream != null) {
                objectinputstream.close();
            }
        } catch (Exception e) {
             Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, e);
             JOptionPane.showMessageDialog(this, "Error Loading Persist Values.", e.getMessage(),JOptionPane.WARNING_MESSAGE);
        } finally {

        }
    }
public void savePersistValues() {
        FileOutputStream fout = null;
        try {
             String jarPath="excpetion";
            try {
                jarPath = MainForm.class
                        .getProtectionDomain()
                        .getCodeSource()
                        .getLocation()
                        .toURI()
                        .getPath();
            } catch (URISyntaxException ex) {
                Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            String filePath = jarPath;
            
            filePath=filePath.substring(0, filePath.lastIndexOf("/"))+"/persist.ser";
            // JOptionPane.showMessageDialog(this, filePath,"File Ptah",JOptionPane.WARNING_MESSAGE);

            fout = new FileOutputStream(filePath);
            ObjectOutputStream oos;

            oos = new ObjectOutputStream(fout);
            for (int i = 0; i < 10; ++i) {
                String compoName = "SendText"+i;
                JTextField sednText = new JTextField();

                for (Component sendTextCom : jPanel1SendArea.getComponents()) {
                    try {
                        if (((JTextField) sendTextCom).getName().contains(compoName)) {
                            sednText = (JTextField) sendTextCom;
                             oos.writeObject(sednText.getText());
                            break;
                        }
                    } catch (java.lang.ClassCastException exp) {

                    }
                }
            }
           
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
             JOptionPane.showMessageDialog(this, "Error Saving Persist Values.", ex.getMessage(),JOptionPane.WARNING_MESSAGE);
        } catch (IOException ex) {
            Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
             JOptionPane.showMessageDialog(this, "Error Loading Saving Values.", ex.getMessage(),JOptionPane.WARNING_MESSAGE);
        } finally {
            try {
                fout.close();
            } catch (IOException ex) {
                Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
                 JOptionPane.showMessageDialog(this, "Error Saving Persist Values.", ex.getMessage(),JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        buttonGroup3 = new javax.swing.ButtonGroup();
        buttonGroup4 = new javax.swing.ButtonGroup();
        buttonGroup5 = new javax.swing.ButtonGroup();
        buttonGroup6 = new javax.swing.ButtonGroup();
        ConnectionManagerPanel = new javax.swing.JPanel();
        connectionMode1 = new com.bmjo.hackbug.ui.ConnectionModeView();
        ConnectionParamPanel = new javax.swing.JPanel();
        serialPortConPropSel1 = new com.bmjo.hackbug.ui.SerialPortConPropView();
        tCPClientConPropView1 = new com.bmjo.hackbug.ui.TCPClientConPropView();
        WorkAreaPanel = new javax.swing.JPanel();
        GeneralTab = new javax.swing.JPanel();
        jPanel1SendArea = new javax.swing.JPanel();
        SendText1 = new javax.swing.JTextField();
        SendText2 = new javax.swing.JTextField();
        SendText3 = new javax.swing.JTextField();
        SendText4 = new javax.swing.JTextField();
        SendText5 = new javax.swing.JTextField();
        SendText6 = new javax.swing.JTextField();
        SendButton1 = new javax.swing.JButton();
        SendButton2 = new javax.swing.JButton();
        SendButton3 = new javax.swing.JButton();
        SendButton4 = new javax.swing.JButton();
        SendButton5 = new javax.swing.JButton();
        SendButton6 = new javax.swing.JButton();
        HexNormalViewHolder = new javax.swing.JTabbedPane();
        textAreaInputText = new java.awt.TextArea();
        hexView1 = new com.bmjo.hackbug.ui.HexView();
        SaveToFilePanel = new javax.swing.JPanel();
        textSelectedFolder = new javax.swing.JTextField();
        buttonSelectDir = new javax.swing.JButton();
        checkSaveToFile = new javax.swing.JCheckBox();
        checkboxTimeStamp = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
        checkRealtimeDisplay = new javax.swing.JCheckBox();
        checkboxEcho = new javax.swing.JCheckBox();
        buttonClearText = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jTextField1 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        buttonOpenCommandsFile = new javax.swing.JButton();
        buttonSaveCommandsToFile = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("MianFrame");
        setName("MainFrame"); // NOI18N

        ConnectionManagerPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        ConnectionManagerPanel.setName("ConMode"); // NOI18N
        ConnectionManagerPanel.setLayout(new java.awt.BorderLayout());

        connectionMode1.setName("ConnectionProp"); // NOI18N
        ConnectionManagerPanel.add(connectionMode1, java.awt.BorderLayout.PAGE_START);

        ConnectionParamPanel.setLayout(new java.awt.CardLayout());

        serialPortConPropSel1.setName("SerialPort"); // NOI18N
        ConnectionParamPanel.add(serialPortConPropSel1, "card2");

        tCPClientConPropView1.setName("TCPClient"); // NOI18N
        ConnectionParamPanel.add(tCPClientConPropView1, "card3");

        ConnectionManagerPanel.add(ConnectionParamPanel, java.awt.BorderLayout.CENTER);

        getContentPane().add(ConnectionManagerPanel, java.awt.BorderLayout.WEST);

        WorkAreaPanel.setLayout(new java.awt.BorderLayout());

        GeneralTab.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        GeneralTab.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1SendArea.setBorder(javax.swing.BorderFactory.createTitledBorder("Send"));
        jPanel1SendArea.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        SendText1.setName("SendText1"); // NOI18N
        jPanel1SendArea.add(SendText1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 590, 30));

        SendText2.setName("SendText2"); // NOI18N
        jPanel1SendArea.add(SendText2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, 590, 30));

        SendText3.setName("SendText3"); // NOI18N
        SendText3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SendText3ActionPerformed(evt);
            }
        });
        jPanel1SendArea.add(SendText3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, 590, 30));

        SendText4.setName("SendText4"); // NOI18N
        jPanel1SendArea.add(SendText4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, 590, 30));

        SendText5.setName("SendText5"); // NOI18N
        jPanel1SendArea.add(SendText5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 160, 590, 30));

        SendText6.setName("SendText6"); // NOI18N
        jPanel1SendArea.add(SendText6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 190, 590, 30));

        SendButton1.setText("Send");
        SendButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SendButton1ActionPerformed(evt);
            }
        });
        jPanel1SendArea.add(SendButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 40, -1, 30));

        SendButton2.setText("Send");
        SendButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SendButton2ActionPerformed(evt);
            }
        });
        jPanel1SendArea.add(SendButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 70, -1, 30));

        SendButton3.setText("Send");
        SendButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SendButton3ActionPerformed(evt);
            }
        });
        jPanel1SendArea.add(SendButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 100, -1, 30));

        SendButton4.setText("Send");
        SendButton4.setToolTipText("");
        SendButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SendButton4ActionPerformed(evt);
            }
        });
        jPanel1SendArea.add(SendButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 130, -1, 30));

        SendButton5.setText("Send");
        SendButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SendButton5ActionPerformed(evt);
            }
        });
        jPanel1SendArea.add(SendButton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 160, -1, 30));

        SendButton6.setText("Send");
        SendButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SendButton6ActionPerformed(evt);
            }
        });
        jPanel1SendArea.add(SendButton6, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 190, -1, 30));

        GeneralTab.add(jPanel1SendArea, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 560, 710, 230));

        HexNormalViewHolder.setName("HexNormalViews"); // NOI18N
        HexNormalViewHolder.addTab("Ascii", textAreaInputText);
        HexNormalViewHolder.addTab("Hex", hexView1);

        GeneralTab.add(HexNormalViewHolder, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1170, 470));
        HexNormalViewHolder.getAccessibleContext().setAccessibleName("HexView");
        HexNormalViewHolder.getAccessibleContext().setAccessibleDescription("");

        SaveToFilePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("SaveToFile  "));
        SaveToFilePanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        textSelectedFolder.setEditable(false);
        textSelectedFolder.setName("textSaveDir"); // NOI18N
        SaveToFilePanel.add(textSelectedFolder, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 360, -1));

        buttonSelectDir.setText("Select Directory");
        buttonSelectDir.setName("buttonSelectDir"); // NOI18N
        buttonSelectDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSelectDirActionPerformed(evt);
            }
        });
        SaveToFilePanel.add(buttonSelectDir, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 30, -1, -1));

        checkSaveToFile.setText("Enable");
        checkSaveToFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkSaveToFileActionPerformed(evt);
            }
        });
        SaveToFilePanel.add(checkSaveToFile, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 30, -1, -1));

        checkboxTimeStamp.setText("Add TimeStamp");
        checkboxTimeStamp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkboxTimeStampActionPerformed(evt);
            }
        });
        SaveToFilePanel.add(checkboxTimeStamp, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 30, -1, -1));

        GeneralTab.add(SaveToFilePanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 480, 710, 70));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Display  "));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        checkRealtimeDisplay.setSelected(true);
        checkRealtimeDisplay.setText("RealTimeDisplay");
        jPanel1.add(checkRealtimeDisplay, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, -1, -1));

        checkboxEcho.setText("Local Echo");
        jPanel1.add(checkboxEcho, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 30, -1, -1));

        buttonClearText.setText("Clear");
        buttonClearText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClearTextActionPerformed(evt);
            }
        });
        jPanel1.add(buttonClearText, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 30, -1, -1));

        jButton1.setText("Pause");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 30, -1, -1));

        GeneralTab.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 480, 440, 70));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Misc "));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTextField1.setEditable(false);
        jPanel2.add(jTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 240, -1));

        jLabel1.setText("Send File");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, -1, -1));

        jButton2.setText(":::");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 40, -1, -1));

        jButton5.setText("Send File");
        jPanel2.add(jButton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 40, 120, -1));

        buttonOpenCommandsFile.setText("Open Command File");
        buttonOpenCommandsFile.setActionCommand("Open Command File");
        buttonOpenCommandsFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonOpenCommandsFileActionPerformed(evt);
            }
        });
        jPanel2.add(buttonOpenCommandsFile, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, -1, 30));

        buttonSaveCommandsToFile.setText("Save Commands");
        jPanel2.add(buttonSaveCommandsToFile, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, 130, 30));

        GeneralTab.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 560, 440, 230));

        WorkAreaPanel.add(GeneralTab, java.awt.BorderLayout.CENTER);

        getContentPane().add(WorkAreaPanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void SendText3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SendText3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_SendText3ActionPerformed

    private void SendButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SendButton1ActionPerformed
        sendButtonClicked(1);
    }//GEN-LAST:event_SendButton1ActionPerformed

    private void SendButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SendButton2ActionPerformed
        sendButtonClicked(2);
    }//GEN-LAST:event_SendButton2ActionPerformed

    private void SendButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SendButton3ActionPerformed
       sendButtonClicked(3);
    }//GEN-LAST:event_SendButton3ActionPerformed

    private void SendButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SendButton4ActionPerformed
       sendButtonClicked(4);
    }//GEN-LAST:event_SendButton4ActionPerformed

    private void SendButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SendButton5ActionPerformed
       sendButtonClicked(5);
    }//GEN-LAST:event_SendButton5ActionPerformed

    private void SendButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SendButton6ActionPerformed
        sendButtonClicked(6);
    }//GEN-LAST:event_SendButton6ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        if(!dispPause){
            jButton1.setText("Play");
            dispPause=true;
        }
        else {
             jButton1.setText("Pause");
            dispPause=false;
        }
            
    }//GEN-LAST:event_jButton1ActionPerformed

    private void buttonSelectDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSelectDirActionPerformed
        JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int option = fileChooser.showOpenDialog(this);
            if(option == JFileChooser.APPROVE_OPTION){
               File file = fileChooser.getSelectedFile();
               textSelectedFolder.setText(file.getPath());
            }
    }//GEN-LAST:event_buttonSelectDirActionPerformed

    private void checkSaveToFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkSaveToFileActionPerformed
        if(checkSaveToFile.isSelected()){
            if(textSelectedFolder.getText().isEmpty()){
                 JOptionPane.showMessageDialog(this, "Select folder to save file ","File Logging",JOptionPane.WARNING_MESSAGE);
                 checkSaveToFile.setSelected(false);
                 return;
            }
             String fileName = textSelectedFolder.getText()+"/"+ System.currentTimeMillis()+".log";
             File logFile = new File(fileName);
            
            try {
                saveLogFile = new FileOutputStream(logFile,true);
                 saveEnabled=true;
            } catch (IOException ex) {
                Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
                saveLogFile=null;
                saveEnabled=false;
                 JOptionPane.showMessageDialog(this, "Error creating log file->"+ex.getMessage(),"File Logging",JOptionPane.WARNING_MESSAGE);
            }
           JOptionPane.showMessageDialog(this, "Data from/to the connected source will be saved in to the file ","File Logging",JOptionPane.WARNING_MESSAGE);

        }else {
            JOptionPane.showMessageDialog(this, "Data transfer logging disabled","File Logging",JOptionPane.WARNING_MESSAGE);
            if(saveLogFile!=null) try {
                saveLogFile.close();
            } catch (IOException ex) {
                Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_checkSaveToFileActionPerformed

    private void checkboxTimeStampActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkboxTimeStampActionPerformed
        // TODO add your handling code here:
        timeStampEnabled =checkboxTimeStamp.isSelected();
    }//GEN-LAST:event_checkboxTimeStampActionPerformed

    private void buttonClearTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonClearTextActionPerformed
       
        textAreaInputText.setText(null);
        hexView1.removeAll();
         System.gc();
    }//GEN-LAST:event_buttonClearTextActionPerformed

    private void buttonOpenCommandsFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonOpenCommandsFileActionPerformed
         JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            FileFilter docFilter = new FileNameExtensionFilter("Commands file", "ser");
            fileChooser.setFileFilter(docFilter);
            int option = fileChooser.showOpenDialog(this);
            if(option == JFileChooser.APPROVE_OPTION){
               File file = fileChooser.getSelectedFile();
               textSelectedFolder.setText(file.getPath());
            }
    }//GEN-LAST:event_buttonOpenCommandsFileActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed
       enum SendMode{
        Ascii,
        Hex,
        File
    }
    void sendButtonClicked(int buttonNum)
    {
        try{
        SendMode sendMode=SendMode.Ascii;
        String compoName = "SendText"+buttonNum;
        JTextField sednText=new JTextField();
        JPanel sendOpPanel=null;
        for(Component sendTextCom:jPanel1SendArea.getComponents()){
            try{
                if(((JTextField)sendTextCom).getName().contains(compoName))
                {
                    sednText = (JTextField)sendTextCom;
                    break;
                }
            }catch(java.lang.ClassCastException exp){
                
            }
        }
         compoName = "SendOp"+buttonNum;
         for(Component sendOpPan:jPanel1SendArea.getComponents()){
            try{
            if(((JPanel)sendOpPan).getName().contains(compoName))
            {
                sendOpPanel = (JPanel)sendOpPan;
                break;
            }
            }catch(java.lang.ClassCastException exp){
                
            }
        }
         if(sendOpPanel!=null){
            for(Component sendOp:sendOpPanel.getComponents()){
                try{
                    if((((JRadioButton)sendOp).getText().contains("Ascii"))&&(((JRadioButton)sendOp).isSelected())){
                       sendMode = SendMode.Ascii;
                    } else if((((JRadioButton)sendOp).getText().contains("Hex"))&&(((JRadioButton)sendOp).isSelected())){
                       sendMode = SendMode.Hex;
                    } else if((((JRadioButton)sendOp).getText().contains("File"))&&(((JRadioButton)sendOp).isSelected())){
                       sendMode = SendMode.File;
                    }
                }
                catch(java.lang.ClassCastException exp){
            }
           }
         }
         String text = sednText.getText();
         byte[] toSend =convertToAscii(text);
         MainControler.send(toSend);
         logToFile(toSend,false,timeStampEnabled);
         if(checkboxEcho.isSelected())  textAreaInputText.append(text);
    /*     Timer timer = new Timer(1,new ActionListener() {
             int i=0;
            @Override
            public void actionPerformed(ActionEvent e) {
                  byte[] toSend2 =convertToAscii("SendingSendingSendingSendingSendingSendingSendingSendingSendingSendingSendingSendingSendingSendingSendingSendingSendingSendingSending="+i+"\r\n");
                  MainControler.send(toSend2);   
                  ++i;
            }
         });
         timer.start();*/
        /* JOptionPane.showMessageDialog(null, 
                              "Text to send", 
                              text, 
                              JOptionPane.WARNING_MESSAGE);*/
        }catch(Exception exp){
            JOptionPane.showMessageDialog(null, 
                              "Erron in Message sending", 
                              exp.getMessage(), 
                              JOptionPane.WARNING_MESSAGE);
        }
    }
    //all escape charectors to support
    //https://en.wikipedia.org/wiki/Escape_sequences_in_C
    byte[] convertToAscii(String inputString){
        if(inputString.contains("//")){
            inputString = inputString.substring(0, inputString.indexOf("//"));
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        boolean escFound = false;
        boolean hexExtract = false;
        String hexBytes="";
        ArrayList<String> hexVals = new ArrayList<String>();
        try{
        for(int i=0;i<inputString.length();++i){
            if(hexExtract){
                if((inputString.charAt(i)!=' ')&&(inputString.charAt(i)!='\\')){
                    hexBytes+=inputString.charAt(i);
                    if(i%2==1){
                        hexVals.add(hexBytes);
                        hexBytes=new String("");
                    }
                 }else{
                    for(String hexValStr :hexVals ){
                    int hexVal = Integer.parseInt(hexValStr, 16);
                     outputStream.write(hexVal);
                    }
                     hexExtract=false;
                }
                continue;
            }
            if(escFound){
                switch(inputString.charAt(i)){
                    case 'r':
                        outputStream.write(0x0D);
                        break;
                    case 'n':
                        outputStream.write(0x0A);
                        break;
                    case 't':
                        outputStream.write(0x09);
                        break;
                     case '\\':
                        outputStream.write(0x5c);
                        break;
                    case 'x':
                        hexBytes="";
                        hexExtract=true;
                        break;
                }
                escFound=false;
                continue;
            }
            if(inputString.charAt(i)!='\\'){
            outputStream.write(inputString.charAt(i));
            }else{
                escFound=true;
            }
        }
       return outputStream.toByteArray();
        }catch(Exception exp){
             JOptionPane.showMessageDialog(null, 
                              "Erron in input text", 
                              exp.getMessage(), 
                              JOptionPane.WARNING_MESSAGE);
        }
        return null;
    }
    
    void logToFile(byte [] data,boolean fromDevice, boolean addTimeStamp){
         String hint="";
         if(saveEnabled){
                try {
                    if(addTimeStamp){
                        hint="\n";
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSS");
                        Timestamp timestamp =  new Timestamp(System.currentTimeMillis());
                        String times = sdf.format(timestamp);
                        hint += times;
                        if(fromDevice){
                            hint += ": From Device: ";
                        }else {
                            hint += ": To Device: ";
                        }
                        saveLogFile.write(hint.getBytes());
                    }
                    
                    saveLogFile.write(data);
                    saveLogFile.flush();
                } catch (IOException ex) {
                    Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
                    saveEnabled=false;
                    try {
                        saveLogFile.close();
                    } catch (IOException ex1) {
                        Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                }
            }
    }
    
    void logToFile(byte [] data,int numBytes,boolean fromDevice, boolean addTimeStamp){
         String hint="";
         if(saveEnabled){
                try {
                    if(addTimeStamp){
                        hint="\n";
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSS");
                        Timestamp timestamp =  new Timestamp(System.currentTimeMillis());
                        String times = sdf.format(timestamp);
                        hint += times;
                        if(fromDevice){
                            hint += ": From Device: ";
                        }else {
                            hint += ": To Device: ";
                        }
                        saveLogFile.write(hint.getBytes());
                    }
                    
                    saveLogFile.write(data, 0,numBytes);
                    saveLogFile.flush();
                } catch (IOException ex) {
                    Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex);
                    saveEnabled=false;
                    try {
                        saveLogFile.close();
                    } catch (IOException ex1) {
                        Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                }
            }
    }
    /**
     * @param args the command line arguments
     */
    public static void main2(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainForm().setVisible(true);
            }
        });
    }
   
    long lastTime;
    String bufferedString;
    int countTime=0;
    class ConEventsHandler implements IConnectionEvents{
        @Override
        public void onReceive(byte[] data, int numBytes,IConnection eventSource,Object eventInfo) {
            
            if(!dispPause){
            if(HexNormalViewHolder.getSelectedIndex()==0){
              if(checkRealtimeDisplay.isSelected())
              {
                  bufferedString +=  new String(data);
                  textAreaInputText.append(bufferedString);
                  bufferedString ="";
              }else{
              if((System.currentTimeMillis()-lastTime) <20){
                  bufferedString +=  new String(data);
                  ++countTime;
                  if(countTime>5){
                      textAreaInputText.append(bufferedString);
                      bufferedString ="";
                      countTime=0;
                  }
                 
              }
              else{
                  bufferedString +=  new String(data);
                  textAreaInputText.append(bufferedString);
                  bufferedString ="";
              }
              lastTime = System.currentTimeMillis();
              }
              // InputText.append(s);
              // InputText.setCaretPosition(InputText.getDocument().getLength());
              
               textAreaInputText.setCaretPosition(textAreaInputText.getText().length());
            }else if(HexNormalViewHolder.getSelectedIndex()==1){
                hexView1.appendData(data, numBytes);
            }
            }
           
           logToFile(data,numBytes,true,timeStampEnabled);
        }


        @Override
        public void onConnectionModeChange(MainControler.ConnectionMode mode) {
          if(CommonDataArea.connection!=null)CommonDataArea.connection.close();
          if(mode== MainControler.ConnectionMode.Serial){
              CardLayout cardLayout =(CardLayout) ConnectionParamPanel.getLayout();
              cardLayout.show(ConnectionParamPanel, "card2");
          }//TCPClient
          else if(mode== MainControler.ConnectionMode.TCPClient){
              CardLayout cardLayout =(CardLayout) ConnectionParamPanel.getLayout();
              cardLayout.show(ConnectionParamPanel, "card3");
          }
        }

        @Override
        public void onEvent(MainControler.ConEvents event, Object param) {
            if(event == ConEvents.Connected) textAreaInputText.append((String)param);
             else if(event == ConEvents.ConFailed)  textAreaInputText.append((String)param);
             else if(event == ConEvents.ConClosed)  textAreaInputText.append((String)param); 
        }

        @Override
        public void onError(MainControler.Error error, Object details) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void onSelectedConChange(IConnection connection) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
                 
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel ConnectionManagerPanel;
    private javax.swing.JPanel ConnectionParamPanel;
    private javax.swing.JPanel GeneralTab;
    private javax.swing.JTabbedPane HexNormalViewHolder;
    private javax.swing.JPanel SaveToFilePanel;
    private javax.swing.JButton SendButton1;
    private javax.swing.JButton SendButton2;
    private javax.swing.JButton SendButton3;
    private javax.swing.JButton SendButton4;
    private javax.swing.JButton SendButton5;
    private javax.swing.JButton SendButton6;
    private javax.swing.JTextField SendText1;
    private javax.swing.JTextField SendText2;
    private javax.swing.JTextField SendText3;
    private javax.swing.JTextField SendText4;
    private javax.swing.JTextField SendText5;
    private javax.swing.JTextField SendText6;
    private javax.swing.JPanel WorkAreaPanel;
    private javax.swing.JButton buttonClearText;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.ButtonGroup buttonGroup4;
    private javax.swing.ButtonGroup buttonGroup5;
    private javax.swing.ButtonGroup buttonGroup6;
    private javax.swing.JButton buttonOpenCommandsFile;
    private javax.swing.JButton buttonSaveCommandsToFile;
    private javax.swing.JButton buttonSelectDir;
    private javax.swing.JCheckBox checkRealtimeDisplay;
    private javax.swing.JCheckBox checkSaveToFile;
    private javax.swing.JCheckBox checkboxEcho;
    private javax.swing.JCheckBox checkboxTimeStamp;
    private com.bmjo.hackbug.ui.ConnectionModeView connectionMode1;
    private com.bmjo.hackbug.ui.HexView hexView1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel1SendArea;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField jTextField1;
    private com.bmjo.hackbug.ui.SerialPortConPropView serialPortConPropSel1;
    private com.bmjo.hackbug.ui.TCPClientConPropView tCPClientConPropView1;
    private java.awt.TextArea textAreaInputText;
    private javax.swing.JTextField textSelectedFolder;
    // End of variables declaration//GEN-END:variables
}
