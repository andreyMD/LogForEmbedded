package dbstatextract;

import java.io.IOException;
import java.util.TimeZone;
import java.util.logging.LogManager;

public class DbStatExtract {

    public static void main(String[] args) throws Exception {

        
        try {
            LogManager.getLogManager().readConfiguration(
                    DbStatExtract.class.getResourceAsStream("/logging.properties"));
        } catch (IOException e) {
            System.err.println("Could not setup logger configuration: " + e.toString());
        }
        
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Bahrain"));
        
        XMLConfig config = new XMLConfig();

        DBmanager db = new DBmanager();

        Email.send(db.GenerateStatistic(config));
    }

}
