/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmjo.hackbug.core;

/**
 *
 * @author bmjo
 */
public interface CommandExecStatus {
    void CommandExecStatus(String command, String status);
    void CommandParseStatus(String command, String status);
}
