/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmjo.hackbug.ui;

import com.bmjo.hackbug.core.CommonDataArea;

import com.bmjo.hackbug.serial.SerialPortConection;
import com.fazecast.jSerialComm.SerialPort;
import static javax.swing.JOptionPane.showMessageDialog;
import com.bmjo.hackbug.core.IConnection;
import com.bmjo.hackbug.core.IConnectionEvents;
import com.bmjo.hackbug.core.MainControler;
import com.bmjo.hackbug.core.MainControler.ConEvents;
import com.bmjo.hackbug.core.MainControler.ConnectionMode;
import com.bmjo.hackbug.tcp.TcpConnection;

/**
 *
 * @author bijum
 */
public class TCPClientConPropView extends javax.swing.JPanel {

    ConnectionMode mode;
    
    /**
     * Creates new form SerialPortConPropSel
     */
    public TCPClientConPropView() {
        initComponents();     
        mode = ConnectionMode.TCPClient;
        MainControler.AddConEventListner(new ConnectionEventss());
    }
    
   

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();

        jLabel1.setText("Server IP");

        jLabel2.setText("Port");

        jButton1.setText("Connect");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jTextField1.setText("127.0.0.1");
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jTextField2.setText("80");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE)
                            .addComponent(jTextField2))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        try{
        if(jButton1.getText()=="Connect"){    
            if(CommonDataArea.connection==null) 
            {
                CommonDataArea.connection = new  TcpConnection();
            }else {
                 CommonDataArea.connection.close();
                 CommonDataArea.connection = new  TcpConnection();
            }
               
            TcpConnection.ConParams params = new TcpConnection.ConParams();
            params.port = Integer.parseInt(jTextField2.getText());
            params.serverIP = jTextField1.getText();
            if(CommonDataArea.connection.connect(params)){
                
            }
            
        }else if(jButton1.getText()=="Close"){
            if(CommonDataArea.connection!=null){
                CommonDataArea.connection.close();
            }
        }
        }catch(Exception exp){
            showMessageDialog(this, "Connection Failed");
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

   
    class ConnectionEventss implements IConnectionEvents{


        @Override
        public void onConnectionModeChange(ConnectionMode mode) {
            }

        @Override
        public void onEvent(MainControler.ConEvents event, Object param, Object source) {
            if(source instanceof TcpConnection){
           if(event == ConEvents.Connected){
               jButton1.setText("Close");
            }else if(event == ConEvents.ConClosed){
                 jButton1.setText("Connect");
            }        
            }
        }

        @Override
        public void onError(MainControler.Error error, Object details) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void onReceive(byte[] data, int numBytes, IConnection eventSource, Object eventInfo) {
             return; 
        }

        @Override
        public void onSelectedConChange(IConnection connection) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables
}