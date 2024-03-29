/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmjo.hackbug.ui;

//import java.lang.System.Logger;
import java.util.logging.Level;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author BMJO <bmjo@iOrbit>
 */
public class HexView extends javax.swing.JPanel {

    int row=0,col=0;
    DefaultTableModel dtm ;
    DefaultTableModel dtm2 ;
    /**
     * Creates new form HexView
     */
    public HexView() {
        initComponents();
       
       dtm = new DefaultTableModel(0, 0);
       dtm2 = new DefaultTableModel(0, 0);
       String header[] = new String[18];
       for(int i=0;i<18;++i){
           header[i]= ""+(i+1);
       }
       dtm.setColumnIdentifiers(header);
       dtm2.setColumnIdentifiers(header);
       
       jTable1.setModel(dtm);
       jTable3.setModel(dtm2);
       
        String [] vals = new String[16];
        dtm.addRow(vals);
        dtm2.addRow(vals);
        
      
    }
    
    public void ClearContents()
    {
         dtm = new DefaultTableModel(0, 0);
       dtm2 = new DefaultTableModel(0, 0);
       String header[] = new String[18];
       for(int i=0;i<18;++i){
           header[i]= ""+(i+1);
       }
       dtm.setColumnIdentifiers(header);
       dtm2.setColumnIdentifiers(header);
       
       jTable1.setModel(dtm);
       jTable3.setModel(dtm2);
       
        String [] vals = new String[16];
        dtm.addRow(vals);
        dtm2.addRow(vals);
        row=0;
        col=0;
    }

    public void appendData(byte[] data, int numBytes) {
           try{
             String s = new String(data);
             for(int i=0;i<numBytes;++i){
             String valHex = String.format("%x", data[i]);
             String valAscii = String.format("%c", data[i]);
             
              if(dtm.getRowCount()<=row){
                     String [] vals = new String[16];
                     dtm.addRow(vals);
                     dtm2.addRow(vals);
                 }
             dtm.setValueAt(valHex, row, col);
             dtm2.setValueAt(valAscii, row, col);
             if(++col>=16) {
                 col=0;
                 ++row;
                
             }
             }
             if(row%20==0){
             jTable3.scrollRectToVisible(jTable3.getCellRect(dtm2.getRowCount()-1, 0, true));
             jTable1.scrollRectToVisible(jTable1.getCellRect(dtm.getRowCount()-1, 0, true));
             }
           }
           catch(Exception exp){
              java.util.logging.Logger.getLogger(HexView.class.getName()).log(Level.SEVERE, null, exp);
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

        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();

        jScrollPane3.setAlignmentX(1.0F);
        jScrollPane3.setAlignmentY(1.0F);
        jScrollPane3.setAutoscrolls(true);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "1", "2", "3", "4", "5", "6", " 7", "8", "9", "10", "11", "12", "13", "14", "15", "16"
            }
        ));
        jScrollPane3.setViewportView(jTable1);

        jSplitPane1.setLeftComponent(jScrollPane3);

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "1", "2", "3", "4", "5", "6", " 7", "8", "9", "10", "11", "12", "13", "14", "15", "16"
            }
        ));
        jScrollPane5.setViewportView(jTable3);

        jSplitPane1.setRightComponent(jScrollPane5);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 924, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 392, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable3;
    // End of variables declaration//GEN-END:variables
}
