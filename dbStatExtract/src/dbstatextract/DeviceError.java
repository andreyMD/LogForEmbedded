/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dbstatextract;

/**
 *
 * @author a20057
 */
public class DeviceError {
    
    private Integer err_cnt;
    private String  err_string;

    public Integer getErr_cnt() {
        return err_cnt;
    }

    public void setErr_cnt(Integer err_cnt) {
        this.err_cnt = err_cnt;
    }

    public String getErr_string() {
        return err_string;
    }

    public void setErr_string(String err_string) {
        this.err_string = err_string;
    }
    
    
    
}
