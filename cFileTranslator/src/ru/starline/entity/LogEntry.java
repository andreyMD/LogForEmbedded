/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.starline.entity;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author user
 */
public class LogEntry implements Serializable{
    
    private String      message;
    private String      printf;
    private String      filename;
    private Integer     lineNum;
    List<Integer>       parametrList; 
    
    public  LogLevel    logLevel;

    
    public String getFilename() {
        return filename;
    }

    public Integer getLineNum() {
        return lineNum;
    }

    public List<Integer> getParametrList() {
        return parametrList;
    }

    public void setLineNum(Integer lineNum) {
        this.lineNum = lineNum;
    }
    

    public void setFilename(String filename) {
        this.filename = filename;
    }    

    public void setParametrList(List<Integer> parametrList) {
        this.parametrList = parametrList;
    }
    
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }   

    public LogLevel getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
    }

    @Override
    public String toString() {
        return message + filename + lineNum ;
    }
    
    
}
