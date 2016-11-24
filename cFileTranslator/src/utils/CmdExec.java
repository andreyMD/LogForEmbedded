/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author a20057
 */
public class CmdExec {
    
    private static Logger log = Logger.getLogger(CmdExec.class.getName());

    private ProcessBuilder pb;

    public CmdExec() {
        pb = new ProcessBuilder();
        pb.redirectErrorStream(true);
    }

    ;   
   
    
    public List<String> run_command(String cmd) {

        String line;        
        List<String> answer = new LinkedList<>();
        
        pb.command("cmd", "/c", cmd);

        pb.redirectErrorStream(true);

        try {
            
            Process p = pb.start();
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), "Cp866"));            

            while (true) {
                line = br.readLine();
                if (line == null) {
                    break;
                }
                answer.add(line);
            }

        } catch (Exception ex) {
            log.log(Level.SEVERE,"Exception:", ex);
        }
      return answer;
    }

}
