/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmjo.hackbug.usb;


import java.util.ArrayList;
import javax.swing.tree.DefaultMutableTreeNode;
import org.usb4java.ConfigDescriptor;
import org.usb4java.Context;
import org.usb4java.Device;
import org.usb4java.DeviceDescriptor;
import org.usb4java.DeviceList;
import org.usb4java.EndpointDescriptor;
import org.usb4java.Interface;
import org.usb4java.InterfaceDescriptor;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;

/**
 *
 * @author bijum
 */
public class USBHelper {
      static  String [] devClassName = {"0-Device","1-Audio","2-CDC","3-HID","5-Physical","6-Image","7-Printer","8-MSD","9-HUB","10-Data","11-SmartCard","13-ContentSecurity","14-Video","15-Personal HealthCare","16-Audio Video",
        "17-Billboard","220- Diagnostic","224-Wireless","239-Misc","254-application","255-VendorSpecific"};

   public static ArrayList<String> fillDeviceList()
    {
        ArrayList<String> devList = new ArrayList<String>();
         Context context = new Context();
            int result = LibUsb.init(context);
            if (result != LibUsb.SUCCESS) {
                throw new LibUsbException("Unable to initialize libusb.", result);
            }

            DeviceList deviceList = new DeviceList();
            result = LibUsb.getDeviceList(context, deviceList);
            if (result < 0) {
                throw new LibUsbException("Unable to get device list", result);
            }
                
       
            for (Device device: deviceList){
                   DeviceDescriptor descriptor = new DeviceDescriptor();
                   result = LibUsb.getDeviceDescriptor(device, descriptor);

                    if (result != LibUsb.SUCCESS){
                         LibUsb.freeDeviceList(deviceList, true);
                    }
              
                String devDesc=new String();// = descriptor.idVendor()+":"+ descriptor.idProduct()+":"+ descriptor.bDeviceClass();
                devDesc= devDesc.format("%x:%x:",descriptor.idVendor(),descriptor.idProduct());
                int devClassIdnum = descriptor.bDeviceClass();
                String className = getClassName(devClassIdnum);
                if(className!=null)
                    devDesc+=className;
                else 
                    devDesc+=descriptor.bDeviceClass();
                devList.add(devDesc);
               
            }
            LibUsb.freeDeviceList(deviceList, true);
           return devList;
    }

   public static DefaultMutableTreeNode getDeviceTree(int vid, int pid, DefaultMutableTreeNode devRoot)
   {
        
        try {
            Context context = new Context();
           
            int result = LibUsb.init(context);
            if (result != LibUsb.SUCCESS) {
                throw new LibUsbException("Unable to initialize libusb.", result);
            }

            DeviceList list = new DeviceList();
            result = LibUsb.getDeviceList(context, list);
            if (result < 0) {
                throw new LibUsbException("Unable to get device list", result);
            }

            // Iterate over all devices and scan for the right one
            for (Device device : list) {
                DeviceDescriptor descriptor = new DeviceDescriptor();
                result = LibUsb.getDeviceDescriptor(device, descriptor);
                if (result != LibUsb.SUCCESS) {
                    throw new LibUsbException("Unable to read device descriptor", result);
                }
                if(descriptor.idVendor()!=vid) continue;
              
                //devRoot = new DefaultMutableTreeNode(descriptor.idVendor()+":"+descriptor.idProduct());
                devRoot.removeAllChildren();
                ConfigDescriptor cd = new ConfigDescriptor();
                result = LibUsb.getConfigDescriptor(device, (byte) 0, cd);
                if (result == LibUsb.SUCCESS) {
                    
                   // deviceInfo+=desc1;
                    
                    for(int m=0;m<cd.bNumInterfaces();++m)
                    {
                    Interface iface =  cd.iface()[m];
                    if(iface.numAltsetting()==0){
                        final InterfaceDescriptor ifaceDescriptor = iface
                                .altsetting()[0];
                      
                     
                       String interfaceName = "Interface:"+ifaceDescriptor.bInterfaceNumber();
                       DefaultMutableTreeNode interfaceNode = new DefaultMutableTreeNode(interfaceName);//
                       DefaultMutableTreeNode alterSet = new DefaultMutableTreeNode("Alternate Setting:"+ifaceDescriptor.bAlternateSetting());
                       interfaceNode.add(alterSet);
                       DefaultMutableTreeNode className = new DefaultMutableTreeNode("Class:"+getClassName(ifaceDescriptor.bInterfaceClass()));
                       interfaceNode.add(className);
                       DefaultMutableTreeNode endPoints = new DefaultMutableTreeNode("EndPoints");
                       interfaceNode.add(endPoints);
                        for(int l =0;l<ifaceDescriptor.bNumEndpoints();++l)
                            {
                                System.out.println("End Point ->"+ ifaceDescriptor.endpoint()[0]
                                    .bEndpointAddress());
                                
                                 System.out.println("---------INTERFACE------------");
                             
                                EndpointDescriptor ep = ifaceDescriptor.endpoint()[l];
                               String epType = "OUT";
                               if((ep.bEndpointAddress()&0x80)==0x80) epType="IN";
                               String endPoint = String.format("Addr:0x%x %s" ,  ep.bEndpointAddress(),epType);//"End Point Addr" + ep.bEndpointAddress()+ " "+ ep.bDescriptorType();
                               DefaultMutableTreeNode endPointNode = new DefaultMutableTreeNode(endPoint);
                               endPoints.add(endPointNode);
                            }
                       
                       devRoot.add(interfaceNode);
                    }else{
                     for (int k = 0; k < iface.numAltsetting(); k++)
                        {
                            
                            final InterfaceDescriptor ifaceDescriptor = iface
                                .altsetting()[k];
                            
                       String interfaceName = "Interface:"+ifaceDescriptor.bInterfaceNumber();
                       DefaultMutableTreeNode interfaceNode = new DefaultMutableTreeNode(interfaceName);//
                       DefaultMutableTreeNode alterSet = new DefaultMutableTreeNode("Alternate Setting:"+ifaceDescriptor.bAlternateSetting());
                       interfaceNode.add(alterSet);
                       DefaultMutableTreeNode className = new DefaultMutableTreeNode("Class:"+getClassName(ifaceDescriptor.bInterfaceClass()));
                       interfaceNode.add(className);
                       DefaultMutableTreeNode endPoints = new DefaultMutableTreeNode("EndPoints");
                       interfaceNode.add(endPoints);
                             
                           for(int l =0;l<ifaceDescriptor.bNumEndpoints();++l)
                            {
                              
                               EndpointDescriptor ep = ifaceDescriptor.endpoint()[l];
                               String epType = "OUT";
                               if((ep.bEndpointAddress()&0x80)==0x80) epType="IN";
                               String endPoint = String.format("Addr:0x%x %s" ,  ep.bEndpointAddress(),epType);//"End Point Addr" + ep.bEndpointAddress()+ " "+ ep.bDescriptorType();

                               DefaultMutableTreeNode endPointNode = new DefaultMutableTreeNode(endPoint);
                               endPoints.add(endPointNode);
                            }
                           devRoot.add(interfaceNode);
                        }
                    }
                   
                }
            }
                return devRoot;
            }
             
        } catch (Exception exp) {
            System.out.println(exp);
        } finally {
            // Ensure the allocated device list is freed
            // LibUsb.freeDeviceList(list, true);
        }
      return null;
   }
   public static String getDeiceInfo(int vid, int pid){
        String deviceInfo="<HTML>";
       try {
            Context context = new Context();
           
            int result = LibUsb.init(context);
            if (result != LibUsb.SUCCESS) {
                throw new LibUsbException("Unable to initialize libusb.", result);
            }

            DeviceList list = new DeviceList();
            result = LibUsb.getDeviceList(context, list);
            if (result < 0) {
                throw new LibUsbException("Unable to get device list", result);
            }

            // Iterate over all devices and scan for the right one
            for (Device device : list) {
                DeviceDescriptor descriptor = new DeviceDescriptor();
                result = LibUsb.getDeviceDescriptor(device, descriptor);
                if (result != LibUsb.SUCCESS) {
                    throw new LibUsbException("Unable to read device descriptor", result);
                }
                if(descriptor.idVendor()!=vid) continue;
                System.out.println("vendorID ->" + descriptor.idVendor() + " Product Id ->" + descriptor.idProduct() + " Class->" + descriptor.bDeviceClass());
                String desc1 = descriptor.dump();
                System.out.println(desc1);
                deviceInfo = desc1;
                
                ConfigDescriptor cd = new ConfigDescriptor();
                result = LibUsb.getConfigDescriptor(device, (byte) 0, cd);
                if (result == LibUsb.SUCCESS) {
                    deviceInfo+="\r\n------------CONFIG DESC-----------\r\n";
                    deviceInfo+="Number of Interfaces Available-->"+cd.bNumInterfaces()+"\r\n\r\n";
                    System.out.println("CONFIG DESC");
                    desc1 =cd.dump();
                    System.out.println(desc1);
                   // deviceInfo+=desc1;
                    
                    for(int m=0;m<cd.bNumInterfaces();++m)
                    {
                    Interface iface =  cd.iface()[m];
                    deviceInfo+="\r\n---------INTERFACE------------\r\n";
                    deviceInfo+="INTERFACE Number--->"+m+"\r\n";
                    
                    deviceInfo+="Number of Alternate Settings Available --->"+iface.numAltsetting()+"\r\n\r\n";
                    if(iface.numAltsetting()==0){
                        final InterfaceDescriptor ifaceDescriptor = iface
                                .altsetting()[0];
                       deviceInfo+="\r\n----------ALTERNATE SETTINGS-----------\r\n";
                       deviceInfo+="Alternate Settings Number-->"+0+"\r\n\r\n";
                       deviceInfo+= ifaceDescriptor.dump();
                    }else{
                     for (int k = 0; k < iface.numAltsetting(); k++)
                        {
                            deviceInfo+="\r\n----------ALTERNATE SETTINGS-----------\r\n";
                            deviceInfo+="Alternate Settings Number-->"+k+"\r\n\r\n";
                            final InterfaceDescriptor ifaceDescriptor = iface
                                .altsetting()[k];
                            deviceInfo+= ifaceDescriptor.dump();
                        /*    for(int l =0;l<ifaceDescriptor.bNumEndpoints();++l)
                            {
                                System.out.println("End Point ->"+ ifaceDescriptor.endpoint()[0]
                                    .bEndpointAddress());
                                
                                 System.out.println("---------INTERFACE------------");
                             
                               desc1= ifaceDescriptor.endpoint()[l].dump();
                                System.out.println(desc1);
                               deviceInfo+=desc1;
                            }*/
                        }
                    }
                   
                }
            }
            }
        } catch (Exception exp) {
            System.out.println(exp);
        } finally {
            // Ensure the allocated device list is freed
            // LibUsb.freeDeviceList(list, true);
        }
       return deviceInfo;
   }
   
    public  static String getClassName(int classId){
        String classNum = String.format("%d", classId&0x00ff);
        for(String className : devClassName){
            if(className.contains(classNum+"-")){
                return className;
            }
        }
        return null;
    }
}
