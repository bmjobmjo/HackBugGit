/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmjo.hackbug.usb;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import javax.swing.tree.DefaultMutableTreeNode;
import org.usb4java.ConfigDescriptor;
import org.usb4java.Context;
import org.usb4java.Device;
import org.usb4java.DeviceDescriptor;
import org.usb4java.DeviceHandle;
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

    static String[] devClassName = {"0-Device", "1-Audio", "2-CDC", "3-HID", "5-Physical", "6-Image", "7-Printer", "8-MSD", "9-HUB", "10-Data", "11-SmartCard", "13-ContentSecurity", "14-Video", "15-Personal HealthCare", "16-Audio Video",
        "17-Billboard", "220- Diagnostic", "224-Wireless", "239-Misc", "254-application", "255-VendorSpecific"};
   
    
    private Context contextNS;
    private DeviceHandle handlerNS;
    private Device deviceNS;

   

    private boolean connected; // TODO: replace to 'connectionFailure' and invert requests everywhere

    private short VENDOR_ID;
    private short PRODUCT_ID;
    private short epIN;
    private short epOUT;
    

    private int returningValue;
    private DeviceList deviceList;
    int selectedInterface;
    int selectedAltSetings;
    UsbConParams usbConParams;
    boolean opened = true;
    public static Map<String,UsbConParams> interfaces;// = new Dictionary<String, String>();
    public static ArrayList<String> fillDeviceList() {
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

        for (Device device : deviceList) {
            DeviceDescriptor descriptor = new DeviceDescriptor();
            result = LibUsb.getDeviceDescriptor(device, descriptor);

            if (result != LibUsb.SUCCESS) {
                LibUsb.freeDeviceList(deviceList, true);
            }

            String devDesc = new String();// = descriptor.idVendor()+":"+ descriptor.idProduct()+":"+ descriptor.bDeviceClass();
            short pid=descriptor.idProduct();
            short vid =descriptor.idVendor();
            devDesc = devDesc.format("%x:%x:", vid, pid);
            int devClassIdnum = descriptor.bDeviceClass();
            String className = getClassName(devClassIdnum);
            if (className != null) {
                devDesc += className;
            } else {
                devDesc += descriptor.bDeviceClass();
            }
            devList.add(devDesc);

        }
        LibUsb.freeDeviceList(deviceList, true);
        return devList;
    }

    public static DefaultMutableTreeNode getDeviceTree(String devName ,String vid, String pid, DefaultMutableTreeNode devRoot) {

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
                short viddev = descriptor.idVendor() ;
                short piddev = descriptor.idProduct();
                String vidS = String.format("%x", viddev);
                String pidS = String.format("%x", piddev);
                if (!vidS.equals(vid)) {
                    continue;
                }
                if(!pidS.equals(pid)) {
                    continue;
                }

                //devRoot = new DefaultMutableTreeNode(descriptor.idVendor()+":"+descriptor.idProduct());
                
                devRoot.setUserObject(devName);
                devRoot.removeAllChildren();
                interfaces = new HashMap<String, UsbConParams>();
                ConfigDescriptor cd = new ConfigDescriptor();
                result = LibUsb.getConfigDescriptor(device, (byte) 0, cd);
                if (result == LibUsb.SUCCESS) {

                    // deviceInfo+=desc1;
                    for (int m = 0; m < cd.bNumInterfaces(); ++m) {
                        Interface iface = cd.iface()[m];
                        if (iface.numAltsetting() == 0) {
                            final InterfaceDescriptor ifaceDescriptor = iface
                                    .altsetting()[0];
                            UsbConParams conp = new UsbConParams();
                            conp.pid = descriptor.idProduct();
                            conp.vid = descriptor.idVendor();
                            conp.interfaceNumber = ifaceDescriptor.bInterfaceNumber();
                            conp.altSetting =ifaceDescriptor.bAlternateSetting();
                            
                            String parceableInfo = ifaceDescriptor.bInterfaceNumber() + ":";
                            String interfaceName = "Interface:" + ifaceDescriptor.bInterfaceNumber()+"-"+ifaceDescriptor.bAlternateSetting();
                            DefaultMutableTreeNode interfaceNode = new DefaultMutableTreeNode(interfaceName);//
                            DefaultMutableTreeNode alterSet = new DefaultMutableTreeNode("Alternate Setting:" + ifaceDescriptor.bAlternateSetting());
                            parceableInfo += ifaceDescriptor.bAlternateSetting();
                            interfaceNode.add(alterSet);
                            DefaultMutableTreeNode className = new DefaultMutableTreeNode("Class:" + getClassName(ifaceDescriptor.bInterfaceClass()));
                            interfaceNode.add(className);
                            DefaultMutableTreeNode endPoints = new DefaultMutableTreeNode("EndPoints");
                            interfaceNode.add(endPoints);
                            interfaces.put(interfaceName, conp);
                            for (int l = 0; l < ifaceDescriptor.bNumEndpoints(); ++l) {
                                System.out.println("End Point ->" + ifaceDescriptor.endpoint()[0]
                                        .bEndpointAddress());

                                System.out.println("---------INTERFACE------------");

                                EndpointDescriptor ep = ifaceDescriptor.endpoint()[l];
                                String epType = "OUT";
                                if ((ep.bEndpointAddress() & 0x80) == 0x80) {
                                    epType = "IN";
                                    conp.epIn = ep.bEndpointAddress();
                                    conp.epInDesc = ep;
                                }else{
                                    conp.epOut = ep.bEndpointAddress();
                                    conp.epOutDesc = ep;
                                }
                                String endPoint = String.format("Addr:0x%x %s", ep.bEndpointAddress(), epType);//"End Point Addr" + ep.bEndpointAddress()+ " "+ ep.bDescriptorType();
                                DefaultMutableTreeNode endPointNode = new DefaultMutableTreeNode(endPoint);
                                endPoints.add(endPointNode);
                                parceableInfo += ":" + String.format("%x", ep.bEndpointAddress());
                            }
                          /*  ArrayList<String> useO = new ArrayList<String>();
                            useO.add(parceableInfo);
                            interfaceNode.setUserObject(useO);
                            devRoot.add(interfaceNode);*/
                        } else {
                            for (int k = 0; k < iface.numAltsetting(); k++) {

                                final InterfaceDescriptor ifaceDescriptor = iface
                                        .altsetting()[k];
                                String parceableInfo = ifaceDescriptor.bInterfaceNumber() + ":";
                                
                                UsbConParams conp = new UsbConParams();
                                conp.pid = descriptor.idProduct();
                                conp.vid = descriptor.idVendor();
                                conp.interfaceNumber = ifaceDescriptor.bInterfaceNumber();
                                conp.altSetting =ifaceDescriptor.bAlternateSetting();
                                
                                String interfaceName = "Interface:" + ifaceDescriptor.bInterfaceNumber()+"-"+ifaceDescriptor.bAlternateSetting();
                                DefaultMutableTreeNode interfaceNode = new DefaultMutableTreeNode(interfaceName);//
                                DefaultMutableTreeNode alterSet = new DefaultMutableTreeNode("Alternate Setting:" + ifaceDescriptor.bAlternateSetting());
                                parceableInfo += ifaceDescriptor.bAlternateSetting();
                                interfaceNode.add(alterSet);
                                DefaultMutableTreeNode className = new DefaultMutableTreeNode("Class:" + getClassName(ifaceDescriptor.bInterfaceClass()));
                                interfaceNode.add(className);
                                DefaultMutableTreeNode endPoints = new DefaultMutableTreeNode("EndPoints");
                                interfaceNode.add(endPoints);
                                interfaces.put(interfaceName, conp);

                                for (int l = 0; l < ifaceDescriptor.bNumEndpoints(); ++l) {

                                    EndpointDescriptor ep = ifaceDescriptor.endpoint()[l];
                                    String epType = "OUT";
                                    if ((ep.bEndpointAddress() & 0x80) == 0x80) {
                                        epType = "IN";
                                         conp.epIn = ep.bEndpointAddress();
                                          conp.epInDesc = ep;
                                    }else{
                                        conp.epOut = ep.bEndpointAddress();
                                         conp.epOutDesc = ep;
                                    }
                                    String endPoint = String.format("Addr:0x%x %s", ep.bEndpointAddress(), epType);//"End Point Addr" + ep.bEndpointAddress()+ " "+ ep.bDescriptorType();

                                    DefaultMutableTreeNode endPointNode = new DefaultMutableTreeNode(endPoint);
                                    endPoints.add(endPointNode);
                                    parceableInfo += ":" + String.format("%x", ep.bEndpointAddress());
                                }
                          /*  ArrayList<String> useO = new ArrayList<String>();
                            useO.add(parceableInfo);
                            interfaceNode.setUserObject(useO);*/
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

    public static String getDeiceInfo(String vid, String pid) {
        String deviceInfo = "<HTML>";
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
                short viddev = descriptor.idVendor() ;
                short piddev = descriptor.idProduct();
                String vidS = String.format("%x", viddev);
                String pidS = String.format("%x", piddev);
                if (!vidS.equals(vid)) {
                    continue;
                }
                if(!pidS.equals(pid)) {
                    continue;
                }
                System.out.println("vendorID ->" + descriptor.idVendor() + " Product Id ->" + descriptor.idProduct() + " Class->" + descriptor.bDeviceClass());
                String desc1 = descriptor.dump();
                System.out.println(desc1);
                deviceInfo = desc1;

                ConfigDescriptor cd = new ConfigDescriptor();
                result = LibUsb.getConfigDescriptor(device, (byte) 0, cd);
                if (result == LibUsb.SUCCESS) {
                    deviceInfo += "\r\n------------CONFIG DESC-----------\r\n";
                    deviceInfo += "Number of Interfaces Available-->" + cd.bNumInterfaces() + "\r\n\r\n";
                    System.out.println("CONFIG DESC");
                    desc1 = cd.dump();
                    System.out.println(desc1);
                    // deviceInfo+=desc1;

                    for (int m = 0; m < cd.bNumInterfaces(); ++m) {
                        Interface iface = cd.iface()[m];
                        deviceInfo += "\r\n---------INTERFACE------------\r\n";
                        deviceInfo += "INTERFACE Number--->" + m + "\r\n";

                        deviceInfo += "Number of Alternate Settings Available --->" + iface.numAltsetting() + "\r\n\r\n";
                        if (iface.numAltsetting() == 0) {
                            final InterfaceDescriptor ifaceDescriptor = iface
                                    .altsetting()[0];
                            deviceInfo += "\r\n----------ALTERNATE SETTINGS-----------\r\n";
                            deviceInfo += "Alternate Settings Number-->" + 0 + "\r\n\r\n";
                            deviceInfo += ifaceDescriptor.dump();
                        } else {
                            for (int k = 0; k < iface.numAltsetting(); k++) {
                                deviceInfo += "\r\n----------ALTERNATE SETTINGS-----------\r\n";
                                deviceInfo += "Alternate Settings Number-->" + k + "\r\n\r\n";
                                final InterfaceDescriptor ifaceDescriptor = iface
                                        .altsetting()[k];
                                deviceInfo += ifaceDescriptor.dump();
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

    public static String getClassName(int classId) {
        String classNum = String.format("%d", classId & 0x00ff);
        for (String className : devClassName) {
            if (className.contains(classNum + "-")) {
                return className;
            }
        }
        return null;
    }
    
      private boolean createContextAndInitLibUSB() throws Exception{
        // Creating Context required by libusb. Optional? Consider removing.
        contextNS = new Context();

        returningValue = LibUsb.init(contextNS);
        if (returningValue != LibUsb.SUCCESS) return false; else return true;
            
    }

    private boolean getDeviceList() throws Exception{
        deviceList = new DeviceList();
        returningValue = LibUsb.getDeviceList(contextNS, deviceList);
         if (returningValue <=0 ) return false; else return true;
    }
    
     private ConfigDescriptor getConfigDesc(int config) throws Exception{
       ConfigDescriptor cd = new ConfigDescriptor();
       returningValue =LibUsb.getConfigDescriptor(deviceNS, (byte) config, cd);
       if (returningValue != LibUsb.SUCCESS) return null;       
       return cd;
    }

    private boolean findDevice() throws Exception{
        // Searching for NS in devices: looking for NS
        DeviceDescriptor descriptor;

        for (Device device: deviceList){
            descriptor = getDeviceDescriptor(device);

            if ((descriptor.idVendor() == VENDOR_ID) && descriptor.idProduct() == PRODUCT_ID){
                deviceNS = device;
                break;
            }
        }
        if (deviceNS == null) {
            this.freeDeviceList();
            return false;
        }
        return true;
    }

    private DeviceDescriptor getDeviceDescriptor(Device device) throws Exception{
        DeviceDescriptor descriptor = new DeviceDescriptor();

        returningValue = LibUsb.getDeviceDescriptor(device, descriptor);

        if (returningValue != LibUsb.SUCCESS){
            this.freeDeviceList();
            return null;
        }
        return descriptor;
    }

    private boolean openDevice() throws Exception{
        // Handle NS device
        handlerNS = new DeviceHandle();
        returningValue = LibUsb.open(deviceNS, handlerNS);

        if (returningValue == LibUsb.SUCCESS)
            return true;

        handlerNS = null;  // Avoid issue on close();
        return false;
    }

    private void freeDeviceList(){
        LibUsb.freeDeviceList(deviceList, true);
    }

    private boolean setAutoDetachKernelDriver(){
        // Actually, there are no drivers in Linux kernel which uses this device.
        returningValue = LibUsb.setAutoDetachKernelDriver(handlerNS, true);
        if (returningValue != LibUsb.SUCCESS)
            return false;
        else 
            return true;
    }

    /*
    private void resetDevice(){
        result = LibUsb.resetDevice(handlerNS);
        if (returningValue != LibUsb.SUCCESS)
            throw new Exception("Reset device\n         Returned: "+UsbErrorCodes.getErrCode(returningValue));
    }
     */
    private boolean setConfiguration(int configuration) throws Exception{
        returningValue = LibUsb.setConfiguration(handlerNS, configuration);
        if (returningValue != LibUsb.SUCCESS)
            return false;
        else return true;
    }
    private boolean claimInterface() throws Exception{
        if(selectedAltSetings != 0){
           returningValue = LibUsb.setInterfaceAltSetting(handlerNS, selectedInterface, selectedAltSetings);
            if (returningValue != LibUsb.SUCCESS)
            return false;
        }
        // Claim interface
        returningValue = LibUsb.claimInterface(handlerNS, selectedInterface);
        if (returningValue != LibUsb.SUCCESS)
            return false;
        return true;
                   
    }

    /**
     * Get USB status
     * @return status of connection
     */
    public boolean isConnected() { return connected; }
    /**
     * Getter for handler
     * @return DeviceHandle of NS
     */
    public DeviceHandle getNsHandler(){ return handlerNS; }
    /**
     * Getter for 'Bus ID' where NS located found
     */
    public int getNsBus(){
        return LibUsb.getBusNumber(deviceNS);
    }
    /**
     * Getter for 'Device address' where NS located at
     */
    public int getNsAddress(){
        return LibUsb.getDeviceAddress(deviceNS);
    }
    /**
     * Correct exit
     * */
    public boolean close(){
        try{
        // Close handler in the end
        if (handlerNS != null) {
            // Try to release interface
            returningValue = LibUsb.releaseInterface(handlerNS, selectedInterface);

            if (returningValue != LibUsb.SUCCESS) {
              return false;
            }

            LibUsb.close(handlerNS);
        }
        // Close context in the end
        if (contextNS != null)
            LibUsb.exit(contextNS);
        return true;
        }catch(Exception exp){
            return false;
        }
    }
    
    public boolean Open(UsbConParams params){
        try{
        usbConParams= params;
        selectedInterface = params.interfaceNumber;
        selectedAltSetings = params.altSetting;
        VENDOR_ID =(short) params.vid;
        PRODUCT_ID =(short) params.pid;
        epIN = params.epIn;
        epOUT = params.epOut;

         if(!createContextAndInitLibUSB()) return false;
         if(!getDeviceList()) return false;
         if(!findDevice()) return false;
         if(!openDevice()) return false;
         freeDeviceList();
         if(setAutoDetachKernelDriver()) return false;
            //this.resetDevice();
         if(!claimInterface()) return false;
         connected = true;
        
        return true;
        }catch(Exception exp){
            return false;
        }
    }
    
      public byte[] readUsb() throws Exception{
        if(usbConParams.epInDesc==null ) return null;
        EndpointDescriptor ep = (EndpointDescriptor) usbConParams.epInDesc;
        int bufLen = ep.wMaxPacketSize();
        ByteBuffer readBuffer = ByteBuffer.allocateDirect(bufLen);
        // We can limit it to 32 bytes, but there is a non-zero chance to got OVERFLOW from libusb.
        IntBuffer readBufTransferred = IntBuffer.allocate(1);
        int result;
        while (true) {
            if(!opened) return null;
            result = LibUsb.bulkTransfer(handlerNS, (byte) epIN, readBuffer, readBufTransferred, 1000);  // last one is TIMEOUT. 0 stands for unlimited. Endpoint IN = 0x81

            switch (result) {
                case LibUsb.SUCCESS:
                    int trans = readBufTransferred.get();
                    byte[] receivedBytes = new byte[trans];
                    readBuffer.get(receivedBytes);
                    return receivedBytes;
                case LibUsb.ERROR_TIMEOUT:
                    continue;
                default:
                    throw new Exception("TF Data transfer issue [read]" +
                            "\n         Returned: " + UsbErrorCodes.getErrCode(result)+
                            "\n         (execution stopped)");
            }
        }
      //  throw new InterruptedException("TF Execution interrupted");
    }
     
      public boolean writeUsb(byte[] message) {
        ByteBuffer writeBuffer = ByteBuffer.allocateDirect(message.length);   //writeBuffer.order() equals BIG_ENDIAN;
        writeBuffer.put(message);                                             // Don't do writeBuffer.rewind();
        IntBuffer writeBufTransferred = IntBuffer.allocate(message.length);
        int result;
        //int varVar = 0; //todo:remove
        while (true) {
          
            result = LibUsb.bulkTransfer(handlerNS, (byte) epOUT, writeBuffer, writeBufTransferred, 5050);  // last one is TIMEOUT. 0 stands for unlimited. Endpoint OUT = 0x01
            int send = writeBufTransferred.get();
            switch (result){
                case LibUsb.SUCCESS:
                    if (send == message.length)
                    return true;
                    System.out.print("TF Data transfer issue [write]" +
                            "\n         Requested: "+message.length+
                            "\n         Transferred: "+writeBufTransferred.get());
                    return true;
                case LibUsb.ERROR_TIMEOUT:
                    //System.out.println("writeBuffer position: "+writeBuffer.position()+" "+writeBufTransferred.get());
                    //writeBufTransferred.clear();    // MUST BE HERE IF WE 'GET()' IT
                    continue;
                default:
                    System.out.print("TF Data transfer issue [write]" +
                            "\n         Returned: "+ UsbErrorCodes.getErrCode(result) +
                            "\n         (execution stopped)");
                    return true;
            }
     }
    }
      
    String getError(){
       return UsbErrorCodes.getErrCode(returningValue);
    }
        
}
