/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dbstatextract;

import java.util.List;

/**
 *
 * @author user
 */
public class DeviceStat {
    
    private String imei;
    private Integer warnCnt;
    private Integer errCnt;
    private String errStr;

    public String getErrStr() {
        return errStr;
    }

    public void setErrStr(String errStr) {
        this.errStr = errStr;
    }
    private List<DeviceError> errList;

    public List<DeviceError> getErrList() {
        return errList;
    }

    public void setErrList(List<DeviceError> errList) {
        this.errList = errList;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public Integer getWarnCnt() {
        return warnCnt;
    }

    public void setWarnCnt(Integer warnCnt) {
        this.warnCnt = warnCnt;
    }

    public Integer getErrCnt() {
        return errCnt;
    }

    public void setErrCnt(Integer errCnt) {
        this.errCnt = errCnt;
    }
    
}
