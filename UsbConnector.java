/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmjo.hackbug.usb;
import org.usb4java.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
/**
 *
 * @author bijum
 */
public class UsbConnector {
   public enum EMsgType {
    PASS, FAIL, INFO, WARNING, NULL
};
    private static final int DEFAULT_INTERFACE = 0;
    private static final int DEFAULT_HOMEBREW_CONFIGURATION = 1;

    private static final short RCM_VID = 0x0403;
    private static final short RCM_PID = 0x6001;

    private static final short HOMEBREW_VID = 0x057E;
    private static final short HOMEBREW_PID = 0x3000;

    // private static final short TEST_VID = 0x1a86;
    // private static final short TEST_PID = 0x7523;

    private Context contextNS;
    private DeviceHandle handlerNS;
    private Device deviceNS;

   static  String [] devClassName = {"0-Device","1-Audio","2-CDC","3-HID","5-Physical","6-Image","7-Printer","8-MSD","9-HUB","10-Data","11-SmartCard","13-ContentSecurity"+"14-Video","15-Personal HealthCare","16-Audio Video",
        "17-Billboard","220- Diagnostic","224-Wireless","239-Misc","254-application","255-Both"};
    

    private boolean connected; // TODO: replace to 'connectionFailure' and invert requests everywhere

    private short VENDOR_ID;
    private short PRODUCT_ID;

    private int returningValue;
    private DeviceList deviceList;

    public static UsbConnector connectUSB(short vid,short pid){
        UsbConnector usbConnect = new UsbConnector();
        usbConnect.VENDOR_ID = vid;// RCM_VID;
        usbConnect.PRODUCT_ID = pid;//RCM_PID;
        try{
            usbConnect.createContextAndInitLibUSB();
            usbConnect.getDeviceList();
            usbConnect.findDevice();
            usbConnect.openDevice();
            usbConnect.freeDeviceList();
            usbConnect.setAutoDetachKernelDriver();
            //this.resetDevice();
            usbConnect.claimInterface();
            usbConnect.connected = true;
        }
        catch (Exception e){
            System.out.print(e.getMessage());
            usbConnect.close();
        }

        return usbConnect;
    }
    
  public  static String getClassName(int classId){
        for(String className : devClassName){
            if(className.contains(classId+"-")){
                return className;
            }
        }
        return null;
    }
    public static UsbConnector InitUSB(){
        UsbConnector usbConnect = new UsbConnector();
       
        try {
            usbConnect.createContextAndInitLibUSB();
          
        }
        catch (Exception e){
            System.out.print(e.getMessage());
            usbConnect.close();
        }
        return usbConnect;
    }

    private UsbConnector(){
    this.connected = false;
    }

  

    private void createContextAndInitLibUSB() throws Exception{
        // Creating Context required by libusb. Optional? Consider removing.
        contextNS = new Context();

        returningValue = LibUsb.init(contextNS);
        if (returningValue != LibUsb.SUCCESS)
            throw new Exception("LibUSB initialization failed: "+UsbErrorCodes.getErrCode(returningValue));
    }

    private void getDeviceList() throws Exception{
        deviceList = new DeviceList();
        returningValue = LibUsb.getDeviceList(contextNS, deviceList);
        if (returningValue < 0)
            throw new Exception("Can't get device list: "+UsbErrorCodes.getErrCode(returningValue));
    }
    
     public String [] getDeviceListStr() throws Exception{
        deviceList = new DeviceList();
        returningValue = LibUsb.getDeviceList(contextNS, deviceList);
        if (returningValue < 0)
            throw new Exception("Can't get device list: "+UsbErrorCodes.getErrCode(returningValue));
        
         DeviceDescriptor descriptor;
        String [] devListNams = new String[deviceList.getSize()];
        int index=0;
        for (Device device: deviceList){
            descriptor = getDeviceDescriptor(device);
            String devDesc = descriptor.idVendor()+":"+ descriptor.idProduct()+":"+ descriptor.bDeviceClass();
            devListNams[index]=devDesc;
            ++index;
        }
       
        return devListNams;
    }
    
     private ConfigDescriptor getConfigDesc(int config) throws Exception{
       ConfigDescriptor cd = new ConfigDescriptor();
       returningValue =LibUsb.getConfigDescriptor(deviceNS, (byte) config, cd);
       if (returningValue < 0)
            throw new Exception("Can't get device list: "+UsbErrorCodes.getErrCode(returningValue));
       cd.dump();
       return cd;
    }

    private void findDevice() throws Exception{
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
            throw new Exception("NS not found in connected USB devices");
        }
    }

    private DeviceDescriptor getDeviceDescriptor(Device device) throws Exception{
        DeviceDescriptor descriptor = new DeviceDescriptor();

        returningValue = LibUsb.getDeviceDescriptor(device, descriptor);

        if (returningValue != LibUsb.SUCCESS){
            this.freeDeviceList();
            throw new Exception("Get USB device descriptor failure: "+UsbErrorCodes.getErrCode(returningValue));
        }
        return descriptor;
    }

    private void openDevice() throws Exception{
        // Handle NS device
        handlerNS = new DeviceHandle();
        returningValue = LibUsb.open(deviceNS, handlerNS);

        if (returningValue == LibUsb.SUCCESS)
            return;

        handlerNS = null;  // Avoid issue on close();
        if (returningValue == LibUsb.ERROR_ACCESS) {
            throw new Exception(String.format(
                    "Can't open NS USB device: %s\n" +
                    "Double check that you have administrator privileges (you're 'root') or check 'udev' rules set for this user (linux only)!\n\n" +
                    "Steps to set 'udev' rules:\n" +
                    "root # vim /etc/udev/rules.d/99-NS" + ((RCM_VID == VENDOR_ID) ? "RCM" : "") + ".rules\n" +
                    "SUBSYSTEM==\"usb\", ATTRS{idVendor}==\"%04x\", ATTRS{idProduct}==\"%04x\", GROUP=\"plugdev\"\n" +
                    "root # udevadm control --reload-rules && udevadm trigger\n", UsbErrorCodes.getErrCode(returningValue), VENDOR_ID, PRODUCT_ID));
        }
        else
            throw new Exception("Can't open NS USB device: "+UsbErrorCodes.getErrCode(returningValue));
    }

    private void freeDeviceList(){
        LibUsb.freeDeviceList(deviceList, true);
    }

    private void setAutoDetachKernelDriver(){
        // Actually, there are no drivers in Linux kernel which uses this device.
        returningValue = LibUsb.setAutoDetachKernelDriver(handlerNS, true);
        if (returningValue != LibUsb.SUCCESS)
            System.out.print("Skip kernel driver attach & detach ("+UsbErrorCodes.getErrCode(returningValue)+")");
    }

    /*
    private void resetDevice(){
        result = LibUsb.resetDevice(handlerNS);
        if (returningValue != LibUsb.SUCCESS)
            throw new Exception("Reset device\n         Returned: "+UsbErrorCodes.getErrCode(returningValue));
    }
     */
    private void setConfiguration(int configuration) throws Exception{
        returningValue = LibUsb.setConfiguration(handlerNS, configuration);
        if (returningValue != LibUsb.SUCCESS)
            throw new Exception("Unable to set active configuration on device: "+UsbErrorCodes.getErrCode(returningValue));
    }
    private void claimInterface() throws Exception{
        // Claim interface
        returningValue = LibUsb.claimInterface(handlerNS, DEFAULT_INTERFACE);
        if (returningValue != LibUsb.SUCCESS)
            throw new Exception("Claim interface failure: "+UsbErrorCodes.getErrCode(returningValue));
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
    public void close(){
        // Close handler in the end
        if (handlerNS != null) {
            // Try to release interface
            returningValue = LibUsb.releaseInterface(handlerNS, DEFAULT_INTERFACE);

            if (returningValue != LibUsb.SUCCESS) {
                System.out.print("Release interface failure: " +
                        UsbErrorCodes.getErrCode(returningValue) +
                        " (sometimes it's not an issue)");
            }

            LibUsb.close(handlerNS);
        }
        // Close context in the end
        if (contextNS != null)
            LibUsb.exit(contextNS);
    }
    
     public byte[] readUsb() throws Exception{
        ByteBuffer readBuffer = ByteBuffer.allocateDirect(512);
        // We can limit it to 32 bytes, but there is a non-zero chance to got OVERFLOW from libusb.
        IntBuffer readBufTransferred = IntBuffer.allocate(1);
        int result;
        while (true) {
            result = LibUsb.bulkTransfer(handlerNS, (byte) 0x81, readBuffer, readBufTransferred, 1000);  // last one is TIMEOUT. 0 stands for unlimited. Endpoint IN = 0x81

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
            /*
            if (varVar != 0)
                logPrinter.print("writeUsb() retry cnt: "+varVar, EMsgType.INFO); //NOTE: DEBUG
            varVar++;
            */
            result = LibUsb.bulkTransfer(handlerNS, (byte) 0x02, writeBuffer, writeBufTransferred, 5050);  // last one is TIMEOUT. 0 stands for unlimited. Endpoint OUT = 0x01

            switch (result){
                case LibUsb.SUCCESS:
                    if (writeBufTransferred.get() == message.length)
                        return false;
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
}
        
