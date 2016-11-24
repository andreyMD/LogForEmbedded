/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.starline.entity;

import java.io.Serializable;

/**
 *
 * @author user
 */
public class LogLevel implements Serializable{
    private Integer levelNumber;
    private String  levelName;

    public LogLevel(Integer levelNumber, String levelName) {
        this.levelNumber = levelNumber;
        this.levelName = levelName;
    }   

    public Integer getLevelNumber() {
        return levelNumber;
    }

    public void setLevelNumber(Integer levelNumber) {
        this.levelNumber = levelNumber;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(this == obj) return true;
        if(getClass() != obj.getClass())
        return false;
        LogLevel other = (LogLevel) obj;
        
        return levelName.equals(other.levelName)
                && levelNumber == other.levelNumber;        
    }
    
    
    
    
}
