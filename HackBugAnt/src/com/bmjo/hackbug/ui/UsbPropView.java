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
import com.bmjo.hackbug.serial.SerialPortConection;
import com.bmjo.hackbug.usb.USBHelper;
import com.bmjo.hackbug.usb.UsbConParams;
import com.bmjo.hackbug.usb.UsbControler;
import com.bmjo.hackbug.utils.AlertBox;
import com.bmjo.hackbug.utils.LogWriter;
import java.awt.event.ItemEvent;
/**
 *
 * @author bijum
 */
import java.util.ArrayList;
import javax.swing.JDialog;
import static javax.swing.JOptionPane.showMessageDialog;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
public class UsbPropView extends javax.swing.JPanel {

    /**
     * Creates new form UsbPropView
     */
    public UsbPropView() {
        initComponents();
        jComboBox1.removeAllItems();
        try{
        ArrayList<String> devices= USBHelper.fillDeviceList();
      
        for(String dev : devices){
            jComboBox1.addItem(dev);
        }
        jTreeUsbDev.removeAll();
       
         MainControler.AddConEventListner(new UsbPropView.ConnectionEventss());
        }catch(Exception exp){
            LogWriter.WriteLog("USB", exp.getMessage());
        }
    }
    
    void FillUsbTree()
    {
       
       DefaultTreeModel model = (DefaultTreeModel)jTreeUsbDev.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
        String selectedItem = (String)  jComboBox1.getSelectedItem();
        if(selectedItem==null) return;
        String [] parts = selectedItem.split(":");
        if(parts.length>=2){
        int vid = Integer.parseInt(parts[0],16);
        int pid = Integer.parseInt(parts[1],16);
 
        USBHelper.getDeviceTree(vid, pid, root);
        model.reload(root);
        }
        //new DefaultMutableTreeNode("another_child").
      
               
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jButtonDetails = new javax.swing.JButton();
        jButtonOpen = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTreeUsbDev = new javax.swing.JTree();

        setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox1ItemStateChanged(evt);
            }
        });

        jLabel1.setText("Device");

        jButtonDetails.setText("View Details");
        jButtonDetails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDetailsActionPerformed(evt);
            }
        });

        jButtonOpen.setText("Open");
        jButtonOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOpenActionPerformed(evt);
            }
        });

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("USB Device");
        jTreeUsbDev.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        jScrollPane1.setViewportView(jTreeUsbDev);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButtonDetails, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(jComboBox1, 0, 97, Short.MAX_VALUE))
                    .addComponent(jButtonOpen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonDetails)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonOpen)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonDetailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDetailsActionPerformed
        // TODO add your handling code here:
        String selectedItem = (String)  jComboBox1.getSelectedItem();
        if(selectedItem==null) return;
        String [] parts = selectedItem.split(":");
        if(parts.length>=2){
        int vid = Integer.parseInt(parts[0],16);
        int pid = Integer.parseInt(parts[1],16);
        String devInfo =USBHelper.getDeiceInfo(vid,pid);
        
        UsbDetailsDlg usbDet = new UsbDetailsDlg(null,true);
        usbDet.setUsbDetails(devInfo);
        usbDet.pack();
        usbDet.setVisible(true);
        }
        
    }//GEN-LAST:event_jButtonDetailsActionPerformed

    private void jButtonOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOpenActionPerformed
        // TODO add your handling code here:
      
        if(jTreeUsbDev.getSelectionPath().getPathCount()!=2){
         AlertBox.Alert("Select an Interface to open");
        }
       String selectedItem = (String)  jComboBox1.getSelectedItem();
        if(selectedItem==null) return;
        String [] parts = selectedItem.split(":");
        if(parts.length>=2){
        int vid = Integer.parseInt(parts[0],16);
        int pid = Integer.parseInt(parts[1],16);
        }
      
       DefaultMutableTreeNode selectedNode =  (DefaultMutableTreeNode)jTreeUsbDev.getLastSelectedPathComponent();
       String nodeName = (String)selectedNode.getUserObject();
        UsbConParams params = USBHelper.interfaces.get(nodeName);
       //AlertBox.Alert(nodeName);
       
         if(jButtonOpen.getText()=="Open"){
           
            if(CommonDataArea.connection==null) 
            {
                CommonDataArea.connection =  new UsbControler() ;
            }else {
                 CommonDataArea.connection.close();
                 CommonDataArea.connection = new UsbControler();
            }
           
            if(!CommonDataArea.connection.connect(params)) {
                String error = CommonDataArea.connection.getErrorString();
                 showMessageDialog(this, "Connection Failed");
            }
        }else if(jButtonOpen.getText()=="Close"){
            if(CommonDataArea.connection!=null){
                CommonDataArea.connection.close();
            }
        }
       
    }//GEN-LAST:event_jButtonOpenActionPerformed

    private void jComboBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox1ItemStateChanged
        // TODO add your handling code here:
        if(evt.getStateChange() == ItemEvent.SELECTED)
        {
            FillUsbTree();
        }
    }//GEN-LAST:event_jComboBox1ItemStateChanged

     class ConnectionEventss implements IConnectionEvents{


        @Override
        public void onConnectionModeChange(MainControler.ConnectionMode mode) {
            }

        @Override
        public void onEvent(MainControler.ConEvents event, Object param, Object source) {
           if(source instanceof UsbControler){
           if(event == MainControler.ConEvents.Connected){
               jButtonOpen.setText("Close");
            }else if(event == MainControler.ConEvents.ConClosed){
                 jButtonOpen.setText("Open");
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
    private javax.swing.JButton jButtonDetails;
    private javax.swing.JButton jButtonOpen;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTree jTreeUsbDev;
    // End of variables declaration//GEN-END:variables
}
