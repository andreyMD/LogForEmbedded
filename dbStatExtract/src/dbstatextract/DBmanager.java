/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dbstatextract;

import com.mysql.jdbc.Statement;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * java -Xmx256m -Xms128m -jar dbStatExtract.jar
 *
 * @author user
 */
public class DBmanager {

    private static Connection con;
    private static DBmanager instance;

    private static final Logger log = Logger.getLogger(DBmanager.class.getName());

    public DBmanager() throws Exception {
        try {
            //TODO property file
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://10.1.14.100:3306/logs";
            con = DriverManager.getConnection(url, "log", "123");            

            log.info("Connect to DB ok, current catalog:" + con.getCatalog());
        } catch (ClassNotFoundException e) {
            throw new Exception(e);
        } catch (SQLException e) {
            throw new Exception(e);
        }
    }

    public Writer GenerateStatistic(XMLConfig config) throws SQLException, IOException, TemplateException {

        Writer ftl_out = new StringWriter();
        PreparedStatement stmt = null;
        Integer id = 0;
        String str;
        Boolean errFound = false;
        ResultSet rs = null;
        ResultSet rsSelect = null;

        Configuration cfg = new Configuration();
        cfg.setClassForTemplateLoading(this.getClass(), "");
        Template template = cfg.getTemplate("log_stat.ftl");
        Map<String, Object> rootMap = new HashMap<>();
        List<DeviceStat> inf_msg = new LinkedList<>();

        Calendar cal = Calendar.getInstance();
        cal.set(2014, Calendar.SEPTEMBER, 10);

        //Date d = cal.getTime();
        Date d = new Date();

        System.out.println(d);
        log.info(d.toString());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String cd = sdf.format(d);

        try {

            stmt = con.prepareStatement("SELECT * FROM imei ");
            rs = stmt.executeQuery();

            while (rs != null && rs.next()) {

                id = rs.getInt(1);
                str = rs.getString(2);

                DeviceStat ds = new DeviceStat();

                // Если imei нет в файле настроек то пропускаем запись
                if (!config.containsImei(str)) {
                    System.out.println(str + " отправка не настроена");
                    continue;
                }

                ds.setImei(config.getNameByImei(str));

                String query = "SELECT  log_data.log_time, log_data.log_module, log_data.log_str "
                        + "FROM log_data INNER JOIN load_date ON log_data.data_id=load_date.data_id "
                        + "WHERE log_data.Im_Id = ? "
                        + "AND log_data.log_level=0 "
                        + "AND load_date.loading_date BETWEEN " + "'" + cd + " 00:00:00' AND " + "'" + cd + " 23:59:59' "
                        + "GROUP BY log_data.log_str;";

                System.out.println(query);

                log.info(query);

                // Поиск всех LOG_ERR за текущую дату
                stmt = con.prepareStatement(query);

                stmt.setInt(1, id);

                rsSelect = stmt.executeQuery();

                //List<String> errMsg = new LinkedList<>();
                StringBuilder sb = new StringBuilder();
                while (rsSelect.next()) {
                    sb.append(rsSelect.getString("log_data.log_time") + "\t\t" + "LOG_ERR :" + rsSelect.getString("log_data.log_module") + "\t:" + rsSelect.getString("log_data.log_str") + "<br>");

                    /*System.out.print(""+rsSelect.getString("load_date.loading_date") + " |");
                     System.out.print(rsSelect.getString("log_data.log_time") + " |");
                     System.out.println(rsSelect.getString("log_data.log_str"));*/
                }

                sb.append("------------------------------------------------------------------------------------------------------------------------------------------<br>");
                
                // Поиск всех LOG_TEST за текущую дату
                stmt = con.prepareStatement(
                        "SELECT  log_data.log_time, log_data.log_module, log_data.log_str "
                        + "FROM log_data INNER JOIN load_date ON log_data.data_id=load_date.data_id "
                        + "WHERE log_data.Im_Id = ? "
                        + "AND log_data.log_level=6 "
                        + "AND load_date.loading_date BETWEEN " + "'" + cd + " 00:00:00' AND " + "'" + cd + " 23:59:59' "
                        + "GROUP BY log_data.log_str;"
                );
                stmt.setInt(1, id);

                rsSelect = stmt.executeQuery();

                while (rsSelect.next()) {
                    sb.append(rsSelect.getString("log_data.log_time") + "\t\t" + "LOG_TEST:" + rsSelect.getString("log_data.log_module") + "\t:" + rsSelect.getString("log_data.log_str") + "<br>");
                }

                ds.setErrStr(sb.toString());

                // Подсчет числа LOG_WARN
                stmt = con.prepareStatement(
                        "SELECT COUNT(*)"
                        + "FROM log_data INNER JOIN load_date ON log_data.data_id=load_date.data_id "
                        + "WHERE log_data.Im_Id = ? "
                        + "AND log_data.log_level=1 "
                        + "AND load_date.loading_date BETWEEN " + "'" + cd + " 00:00:00' AND " + "'" + cd + " 23:59:59';"
                );
                stmt.setInt(1, id);
                rsSelect = stmt.executeQuery();

                if (rsSelect.next()) {
                    ds.setWarnCnt(rsSelect.getInt(1));
                }

                stmt = con.prepareStatement(
                        "SELECT COUNT(*)"
                        + "FROM log_data INNER JOIN load_date ON log_data.data_id=load_date.data_id "
                        + "WHERE log_data.Im_Id = ? "
                        + "AND log_data.log_level=0 "
                        + "AND load_date.loading_date BETWEEN " + "'" + cd + " 00:00:00' AND " + "'" + cd + " 23:59:59';"
                );
                stmt.setInt(1, id);
                rsSelect = stmt.executeQuery();

                if (rsSelect.next()) {
                    ds.setErrCnt(rsSelect.getInt(1));
                }

                if (ds.getErrCnt() != 0 || ds.getWarnCnt() != 0) {
                    inf_msg.add(ds);
                    errFound = true;
                }

                /*
                 // Подсчет статистики стеков
                 stmt = con.prepareStatement(
                 "SELECT load_date.loading_date, log_data.log_time, log_data.log_str"
                 + " FROM log_data INNER JOIN load_date ON log_data.data_id=load_date.data_id"
                 + " WHERE log_data.Im_Id = ?"
                 + " AND log_data.log_level=2"
                 + " AND load_date.loading_date BETWEEN " + "'" + cd + " 00:00:00' AND " + "'" + cd + " 23:59:59'"
                 + " AND log_data.log_str LIKE '%TNPROFILER:%';"
                 );
                 stmt.setInt(1, id);
                 rsSelect = stmt.executeQuery();

                 while (rsSelect.next()) {
                 out.println(rsSelect.getString("log_data.log_str"));
                 }*/
            }

            if (errFound) {
                rootMap.put("cur_date", cd);
            } else {
                rootMap.put("cur_date", "LOG_ERR, LOG_WARN, LOG_TEST c " + cd + " 00:00:00" + " по текущее время в базе данных не обнаружены");
            }

            rootMap.put("imei_msg", inf_msg);

            template.process(rootMap, ftl_out);

        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
        return ftl_out;
    }

}
