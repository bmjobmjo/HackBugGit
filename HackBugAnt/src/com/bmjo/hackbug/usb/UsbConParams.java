/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmjo.hackbug.usb;

/**
 *
 * @author bijum
 */
public class UsbConParams {
    short vid;
    short pid;
    int interfaceNumber;
    int altSetting;
    short epIn;
    short epOut;
    Object epInDesc;
    Object epOutDesc;
}
