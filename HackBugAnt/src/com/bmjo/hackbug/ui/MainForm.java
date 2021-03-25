/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmjo.hackbug.ui;
import com.bmjo.hackbug.core.CommandInterpretor;
import com.bmjo.hackbug.core.CommonDataArea;
import com.bmjo.hackbug.core.IConnection;
import com.bmjo.hackbug.core.IConnectionEvents;
import com.bmjo.hackbug.core.MainControler;
import com.bmjo.hackbug.core.MainControler.ConEvents;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Image;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
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
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
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
         
         String script = "WaitFor \"hello world\" \r\nLabel\r\nwaitsec 100 \r\n Goto 1\r\nLabel 1\r\n ActonOn \"ATDP 123345\" \\x667766 \"ATMK\" \\x6789\r\n" +
"Action 1\r\n" +
"Send \"\\x6677\" \r\n" +
"END \r\n" +
"Action 2\r\n" +
"Send \"\\x66887\" \r\n" +
"Default\r\n" +
"Send \"\\x6996887\" \r\n" +
"END \"hello\" \"biju\" \"mon\" \\x3344a \\xA45B \"Kill\" \r\n"+
"END 2\r\n";
      //  CommandInterpretor cmdInter = new CommandInterpretor();
      //   cmdInter.Execute(script);
         
         addWindowListener(new WindowAdapter() {
             public void windowClosing(WindowEvent e){
                   savePersistValues();
                }
         });
         try{
          BufferedImage img = null;
          String fileName = ClassLoader.getSystemResource("res/HackBug.png").getFile();
          img = ImageIO.read(new File(fileName));
          Image dimg = img.getScaledInstance(logoLabel.getWidth(), logoLabel.getHeight(),
            Image.SCALE_SMOOTH);
          ImageIcon imageIcon = new ImageIcon(dimg);
          logoLabel.setIcon(imageIcon);
         }catch(Exception exp){
             Logger.getLogger(MainForm.class.getName()).log(Level.SEVERE, null, exp);
         }
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

                for (Component sendTextCom : CommandInputContralArrayPanel.getComponents()) {
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

                for (Component sendTextCom : CommandInputContralArrayPanel.getComponents()) {
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
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        buttonGroup3 = new javax.swing.ButtonGroup();
        buttonGroup4 = new javax.swing.ButtonGroup();
        buttonGroup5 = new javax.swing.ButtonGroup();
        buttonGroup6 = new javax.swing.ButtonGroup();
        ConnectionManagerPanel = new javax.swing.JPanel();
        ConnectionModePanel = new javax.swing.JPanel();
        connectionMode1 = new com.bmjo.hackbug.ui.ConnectionModeView();
        ConnectionParamsPanel = new javax.swing.JPanel();
        ConnectionParamPanel = new javax.swing.JPanel();
        serialPortConPropSel1 = new com.bmjo.hackbug.ui.SerialPortConPropView();
        tCPClientConPropView1 = new com.bmjo.hackbug.ui.TCPClientConPropView();
        tCPServerConPropView1 = new com.bmjo.hackbug.ui.TCPServerConPropView();
        LogoPanel = new javax.swing.JPanel();
        logoLabel = new javax.swing.JLabel();
        WorkAreaPanel = new javax.swing.JPanel();
        GeneralTab = new javax.swing.JPanel();
        HexNormalViewHolder = new javax.swing.JTabbedPane();
        textAreaInputText = new java.awt.TextArea();
        hexView1 = new com.bmjo.hackbug.ui.HexView();
        commandProgExecPanel2 = new com.bmjo.hackbug.ui.CommandProgExecPanel();
        SaveNDisplayOptionPanel = new javax.swing.JPanel();
        SaveToFilePanel = new javax.swing.JPanel();
        textSelectedFolder = new javax.swing.JTextField();
        buttonSelectDir = new javax.swing.JButton();
        checkSaveToFile = new javax.swing.JCheckBox();
        checkboxTimeStamp = new javax.swing.JCheckBox();
        DisplayOptionsPanel = new javax.swing.JPanel();
        checkRealtimeDisplay = new javax.swing.JCheckBox();
        checkboxEcho = new javax.swing.JCheckBox();
        buttonClearText = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        CommandInputPanel = new javax.swing.JPanel();
        CommandInputContralArrayPanel = new javax.swing.JPanel();
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
        MiscPanel = new javax.swing.JPanel();
        textSelectedFileForSend = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        btnChooseFileToSend = new javax.swing.JButton();
        btnSendFile = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("HackBug");
        setName("MainFrame"); // NOI18N
        getContentPane().setLayout(new java.awt.GridBagLayout());

        ConnectionManagerPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        ConnectionManagerPanel.setAlignmentY(1.0F);
        ConnectionManagerPanel.setName("ConMode"); // NOI18N
        ConnectionManagerPanel.setPreferredSize(new java.awt.Dimension(250, 104));
        ConnectionManagerPanel.setLayout(new java.awt.BorderLayout(3, 3));

        ConnectionModePanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        ConnectionModePanel.setMinimumSize(new java.awt.Dimension(100, 100));
        ConnectionModePanel.setName(""); // NOI18N
        ConnectionModePanel.setPreferredSize(new java.awt.Dimension(100, 50));
        ConnectionModePanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 5));

        connectionMode1.setBorder(null);
        connectionMode1.setName("ConnectionProp"); // NOI18N
        ConnectionModePanel.add(connectionMode1);

        ConnectionManagerPanel.add(ConnectionModePanel, java.awt.BorderLayout.PAGE_START);

        ConnectionParamsPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        ConnectionParamsPanel.setMaximumSize(new java.awt.Dimension(0, 0));
        ConnectionParamsPanel.setMinimumSize(new java.awt.Dimension(192, 50));
        ConnectionParamsPanel.setPreferredSize(new java.awt.Dimension(192, 211));

        ConnectionParamPanel.setLayout(new java.awt.CardLayout());

        serialPortConPropSel1.setName("SerialPort"); // NOI18N
        ConnectionParamPanel.add(serialPortConPropSel1, "card2");

        tCPClientConPropView1.setName("TCPClient"); // NOI18N
        ConnectionParamPanel.add(tCPClientConPropView1, "card3");

        tCPServerConPropView1.setAlignmentY(1.0F);
        ConnectionParamPanel.add(tCPServerConPropView1, "card4");

        ConnectionParamsPanel.add(ConnectionParamPanel);

        ConnectionManagerPanel.add(ConnectionParamsPanel, java.awt.BorderLayout.CENTER);

        LogoPanel.setMinimumSize(new java.awt.Dimension(100, 100));
        LogoPanel.setPreferredSize(new java.awt.Dimension(100, 200));

        logoLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        logoLabel.setOpaque(true);
        logoLabel.setPreferredSize(new java.awt.Dimension(200, 200));
        logoLabel.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        LogoPanel.add(logoLabel);

        ConnectionManagerPanel.add(LogoPanel, java.awt.BorderLayout.SOUTH);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        getContentPane().add(ConnectionManagerPanel, gridBagConstraints);

        WorkAreaPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        GeneralTab.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        GeneralTab.setLayout(new java.awt.GridBagLayout());

        HexNormalViewHolder.setAlignmentY(0.0F);
        HexNormalViewHolder.setMinimumSize(new java.awt.Dimension(953, 448));
        HexNormalViewHolder.setName("HexNormalViews"); // NOI18N
        HexNormalViewHolder.addTab("Ascii", textAreaInputText);
        HexNormalViewHolder.addTab("Hex", hexView1);
        HexNormalViewHolder.addTab("CommandExec", commandProgExecPanel2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        GeneralTab.add(HexNormalViewHolder, gridBagConstraints);
        HexNormalViewHolder.getAccessibleContext().setAccessibleName("HexView");
        HexNormalViewHolder.getAccessibleContext().setAccessibleDescription("");

        SaveNDisplayOptionPanel.setAutoscrolls(true);
        SaveNDisplayOptionPanel.setPreferredSize(new java.awt.Dimension(1144, 90));
        SaveNDisplayOptionPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        SaveToFilePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("SaveToFile  "));
        SaveToFilePanel.setMinimumSize(new java.awt.Dimension(750, 80));
        SaveToFilePanel.setOpaque(false);
        SaveToFilePanel.setPreferredSize(new java.awt.Dimension(750, 80));
        SaveToFilePanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 2));

        textSelectedFolder.setEditable(false);
        textSelectedFolder.setName("textSaveDir"); // NOI18N
        textSelectedFolder.setPreferredSize(new java.awt.Dimension(320, 22));
        textSelectedFolder.setRequestFocusEnabled(false);
        textSelectedFolder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textSelectedFolderActionPerformed(evt);
            }
        });
        SaveToFilePanel.add(textSelectedFolder);

        buttonSelectDir.setText("Select Directory");
        buttonSelectDir.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        buttonSelectDir.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        buttonSelectDir.setName("buttonSelectDir"); // NOI18N
        buttonSelectDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSelectDirActionPerformed(evt);
            }
        });
        SaveToFilePanel.add(buttonSelectDir);

        checkSaveToFile.setText("Enable");
        checkSaveToFile.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        checkSaveToFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkSaveToFileActionPerformed(evt);
            }
        });
        SaveToFilePanel.add(checkSaveToFile);

        checkboxTimeStamp.setText("Add TimeStamp");
        checkboxTimeStamp.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        checkboxTimeStamp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkboxTimeStampActionPerformed(evt);
            }
        });
        SaveToFilePanel.add(checkboxTimeStamp);

        SaveNDisplayOptionPanel.add(SaveToFilePanel);

        DisplayOptionsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Display  "));

        checkRealtimeDisplay.setSelected(true);
        checkRealtimeDisplay.setText("RealTimeDisplay");
        DisplayOptionsPanel.add(checkRealtimeDisplay);

        checkboxEcho.setText("Local Echo");
        DisplayOptionsPanel.add(checkboxEcho);

        buttonClearText.setText("Clear");
        buttonClearText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonClearTextActionPerformed(evt);
            }
        });
        DisplayOptionsPanel.add(buttonClearText);

        jButton1.setText("Pause");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        DisplayOptionsPanel.add(jButton1);

        SaveNDisplayOptionPanel.add(DisplayOptionsPanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        GeneralTab.add(SaveNDisplayOptionPanel, gridBagConstraints);

        CommandInputPanel.setAlignmentY(1.0F);
        CommandInputPanel.setAutoscrolls(true);
        CommandInputPanel.setPreferredSize(new java.awt.Dimension(1108, 210));
        CommandInputPanel.setRequestFocusEnabled(false);
        CommandInputPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        CommandInputContralArrayPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Send"));
        CommandInputContralArrayPanel.setPreferredSize(new java.awt.Dimension(750, 200));
        CommandInputContralArrayPanel.setLayout(new java.awt.GridBagLayout());

        SendText1.setMinimumSize(new java.awt.Dimension(650, 22));
        SendText1.setName("SendText1"); // NOI18N
        SendText1.setPreferredSize(new java.awt.Dimension(500, 22));
        SendText1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SendText1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        CommandInputContralArrayPanel.add(SendText1, gridBagConstraints);

        SendText2.setName("SendText2"); // NOI18N
        SendText2.setPreferredSize(new java.awt.Dimension(260, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        CommandInputContralArrayPanel.add(SendText2, gridBagConstraints);

        SendText3.setMinimumSize(new java.awt.Dimension(260, 22));
        SendText3.setName("SendText3"); // NOI18N
        SendText3.setPreferredSize(new java.awt.Dimension(700, 22));
        SendText3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SendText3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        CommandInputContralArrayPanel.add(SendText3, gridBagConstraints);

        SendText4.setName("SendText4"); // NOI18N
        SendText4.setPreferredSize(new java.awt.Dimension(260, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        CommandInputContralArrayPanel.add(SendText4, gridBagConstraints);

        SendText5.setName("SendText5"); // NOI18N
        SendText5.setPreferredSize(new java.awt.Dimension(260, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        CommandInputContralArrayPanel.add(SendText5, gridBagConstraints);

        SendText6.setName("SendText6"); // NOI18N
        SendText6.setPreferredSize(new java.awt.Dimension(260, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        CommandInputContralArrayPanel.add(SendText6, gridBagConstraints);

        SendButton1.setText("Send");
        SendButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SendButton1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        CommandInputContralArrayPanel.add(SendButton1, gridBagConstraints);

        SendButton2.setText("Send");
        SendButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SendButton2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        CommandInputContralArrayPanel.add(SendButton2, gridBagConstraints);

        SendButton3.setText("Send");
        SendButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SendButton3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        CommandInputContralArrayPanel.add(SendButton3, gridBagConstraints);

        SendButton4.setText("Send");
        SendButton4.setToolTipText("");
        SendButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SendButton4ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        CommandInputContralArrayPanel.add(SendButton4, gridBagConstraints);

        SendButton5.setText("Send");
        SendButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SendButton5ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        CommandInputContralArrayPanel.add(SendButton5, gridBagConstraints);

        SendButton6.setText("Send");
        SendButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SendButton6ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 10;
        CommandInputContralArrayPanel.add(SendButton6, gridBagConstraints);

        CommandInputPanel.add(CommandInputContralArrayPanel);

        MiscPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Misc "));
        MiscPanel.setMinimumSize(new java.awt.Dimension(370, 200));
        MiscPanel.setPreferredSize(new java.awt.Dimension(379, 200));
        MiscPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        textSelectedFileForSend.setEditable(false);
        textSelectedFileForSend.setEnabled(false);
        MiscPanel.add(textSelectedFileForSend, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 220, -1));

        jLabel1.setText("Send File");
        MiscPanel.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, -1, -1));

        btnChooseFileToSend.setText(":::");
        btnChooseFileToSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChooseFileToSendActionPerformed(evt);
            }
        });
        MiscPanel.add(btnChooseFileToSend, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 40, -1, -1));

        btnSendFile.setText("Send File");
        btnSendFile.setEnabled(false);
        btnSendFile.setPreferredSize(new java.awt.Dimension(60, 25));
        btnSendFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSendFileActionPerformed(evt);
            }
        });
        MiscPanel.add(btnSendFile, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 40, 90, -1));

        CommandInputPanel.add(MiscPanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        GeneralTab.add(CommandInputPanel, gridBagConstraints);

        WorkAreaPanel.add(GeneralTab);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        getContentPane().add(WorkAreaPanel, gridBagConstraints);

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
        hexView1.ClearContents();
         System.gc();
    }//GEN-LAST:event_buttonClearTextActionPerformed

    private void btnChooseFileToSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChooseFileToSendActionPerformed
         JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int option = fileChooser.showOpenDialog(this);
            if(option == JFileChooser.APPROVE_OPTION){
               File file = fileChooser.getSelectedFile();
               textSelectedFileForSend.setText(file.getPath());
               btnSendFile.setEnabled(true);
            }
    }//GEN-LAST:event_btnChooseFileToSendActionPerformed

    private void SendText1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SendText1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_SendText1ActionPerformed

    private void textSelectedFolderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textSelectedFolderActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_textSelectedFolderActionPerformed

    private void btnSendFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSendFileActionPerformed
        // TODO add your handling code here:
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                 byte [] dat = new byte[200];
                 long fileSize = new File(textSelectedFileForSend.getText()).length();
                 int lastPack =(int) fileSize%200;
                 long pos=0;
                 
                 InputStream inputStream = new FileInputStream(textSelectedFileForSend.getText());
                 while(pos<fileSize){
                     textAreaInputText.append("Sending packet - "+pos+"\r\n");
                     inputStream.read(dat);
                     CommonDataArea.connection.send(dat);
                     pos+=200;
                     
                 }
               
                if(lastPack>0){
                    byte [] lastBytes = new byte[lastPack];
                    inputStream.read(lastBytes);
                    CommonDataArea.connection.send(lastBytes);
                }
                
                inputStream.close();
                 
                }catch(Exception exp){
                    
                }
            }
        });
        thread.start();
    }//GEN-LAST:event_btnSendFileActionPerformed
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
        for(Component sendTextCom:CommandInputContralArrayPanel.getComponents()){
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
         for(Component sendOpPan:CommandInputContralArrayPanel.getComponents()){
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
          else if(mode== MainControler.ConnectionMode.TCPServer){
              CardLayout cardLayout =(CardLayout) ConnectionParamPanel.getLayout();
              cardLayout.show(ConnectionParamPanel, "card4");
          }
        }

        @Override
        public void onEvent(MainControler.ConEvents event, Object param, Object source) {
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
    private javax.swing.JPanel CommandInputContralArrayPanel;
    private javax.swing.JPanel CommandInputPanel;
    private javax.swing.JPanel ConnectionManagerPanel;
    private javax.swing.JPanel ConnectionModePanel;
    private javax.swing.JPanel ConnectionParamPanel;
    private javax.swing.JPanel ConnectionParamsPanel;
    private javax.swing.JPanel DisplayOptionsPanel;
    private javax.swing.JPanel GeneralTab;
    private javax.swing.JTabbedPane HexNormalViewHolder;
    private javax.swing.JPanel LogoPanel;
    private javax.swing.JPanel MiscPanel;
    private javax.swing.JPanel SaveNDisplayOptionPanel;
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
    private javax.swing.JButton btnChooseFileToSend;
    private javax.swing.JButton btnSendFile;
    private javax.swing.JButton buttonClearText;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.ButtonGroup buttonGroup4;
    private javax.swing.ButtonGroup buttonGroup5;
    private javax.swing.ButtonGroup buttonGroup6;
    private javax.swing.JButton buttonSelectDir;
    private javax.swing.JCheckBox checkRealtimeDisplay;
    private javax.swing.JCheckBox checkSaveToFile;
    private javax.swing.JCheckBox checkboxEcho;
    private javax.swing.JCheckBox checkboxTimeStamp;
    private com.bmjo.hackbug.ui.CommandProgExecPanel commandProgExecPanel2;
    private com.bmjo.hackbug.ui.ConnectionModeView connectionMode1;
    private com.bmjo.hackbug.ui.HexView hexView1;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel logoLabel;
    private com.bmjo.hackbug.ui.SerialPortConPropView serialPortConPropSel1;
    private com.bmjo.hackbug.ui.TCPClientConPropView tCPClientConPropView1;
    private com.bmjo.hackbug.ui.TCPServerConPropView tCPServerConPropView1;
    private java.awt.TextArea textAreaInputText;
    private javax.swing.JTextField textSelectedFileForSend;
    private javax.swing.JTextField textSelectedFolder;
    // End of variables declaration//GEN-END:variables
}
