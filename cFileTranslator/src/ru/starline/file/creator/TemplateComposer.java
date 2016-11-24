/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.starline.file.creator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import ru.starline.entity.LogEntry;
import ru.starline.entity.LogLevel;
import utils.CrcCalc;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import utils.CmdExec;

/**
 *
 * @author user
 */
public class TemplateComposer {

    public static void createUidEnumFiles(List<LogEntry> recordList, String outputDest) {

        if (recordList.isEmpty()) {
            return;
        }
        boolean new_list = true;
        int cnt = 0;
        int last_cnt = 0;
        String fileName = null;
        LogEntry le = null;

        List<LogEntry> nl = new ArrayList<>(50);

        for (LogEntry logEntry : recordList) {
            if (!nl.isEmpty()) {
                if (le.getFilename().equals(logEntry.getFilename())) {
                    le = logEntry;
                    nl.add(logEntry);
                } else {

                    createUidEnum(nl, fileName, last_cnt);
                    new_list = true;
                }
            }

            if (new_list) {
                nl = new ArrayList<>(50);
                new_list = false;
                fileName = outputDest + "log_"
                        + logEntry.getFilename().toLowerCase().substring(0, logEntry.getFilename().length() - 1)
                        + ".h";
                le = logEntry;
                nl.add(logEntry);
                last_cnt = cnt;
            }
            cnt++;
        }
        // для последнего элемента
        createUidEnum(nl, fileName, last_cnt);
    }

    public static void createUidEnum(List<LogEntry> recordList, String outputDest, int enumCnt) {

        int cnt = 0;
        String moduleName = null;

        //Freemarker configuration object
        Configuration cfg = new Configuration();
        try {
            //Load template from source folder
            Template template = cfg.getTemplate("src/auto_uid_enum.ftl");

            // Build the data-model
            Map<String, Object> data = new HashMap<String, Object>();
            //  data.put("message", "Hello World!");

            //List parsing 
            List<String> uid = new LinkedList<String>();

            // Создание enum с названием uid для Keil
            for (LogEntry le : recordList) {
                StringBuffer sb = new StringBuffer(le.getFilename());
                sb.append(le.getLineNum());
                if (cnt == 0) {
                    sb.append("=" + enumCnt);
                    moduleName = le.getFilename();
                }
                cnt++;
                if (recordList.size() != cnt) {
                    // sb.append(",");
                }
                uid.add(sb.toString());
            }

            data.put("uid", uid);
            data.put("ifdef", moduleName);

            // Console output
            /*  Writer out = new OutputStreamWriter(System.out);
             template.process(data, out);
             out.flush();*/
            String md5 = md5(uid.toString().getBytes());

            data.put("md5", "FILE_MD5_HASH = " + md5);

            if (varInFileCompare(md5, outputDest, "FILE_MD5_HASH")) {
                // Old data == New data
                return;
            } else {

                // File output
                Writer file = new FileWriter(new File(outputDest));
                template.process(data, file);
                file.flush();
                file.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(outputDest);
        } catch (TemplateException e) {
            e.printStackTrace();
        }
    }

    public static void createModules(List<LogEntry> recordList, String outputDest) {

        int cnt = 0;
        //Freemarker configuration object
        Configuration cfg = new Configuration();
        try {
            //Load template from source folder
            Template template = cfg.getTemplate("src/auto_modules.ftl");

            // Build the data-model
            Map<String, Object> data = new HashMap<String, Object>();
            //  data.put("message", "Hello World!");

            //List parsing 
            Set<String> modules = new LinkedHashSet<String>();

            // Создание enum с названием uid для Keil
            for (LogEntry le : recordList) {
                StringBuffer sb = new StringBuffer(le.getFilename());
                cnt++;
                if (recordList.size() != cnt) {
                    // sb.append(",");
                }
                modules.add(sb.toString());
            }

            data.put("modules", modules);

            // Console output
            /*  Writer out = new OutputStreamWriter(System.out);
             template.process(data, out);
             out.flush();*/
            String md5 = md5(modules.toString().getBytes());

            data.put("md5", "FILE_MD5_HASH = " + md5);

            if (varInFileCompare(md5, outputDest, "FILE_MD5_HASH")) {
                // Old data == New data
                return;
            } else {

                // File output
                Writer file = new FileWriter(new File(outputDest));
                template.process(data, file);
                file.flush();
                file.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
    }

    public static void createModulesName(List<LogEntry> recordList, String outputDest) {

        int cnt = 0;
        //Freemarker configuration object
        Configuration cfg = new Configuration();
        try {
            //Load template from source folder
            Template template = cfg.getTemplate("src/auto_modules_name.ftl");

            // Build the data-model
            Map<String, Object> data = new HashMap<String, Object>();
            //  data.put("message", "Hello World!");

            //List parsing 
            Set<String> modules = new LinkedHashSet<String>();

            // Создание enum с названием uid для Keil
            for (LogEntry le : recordList) {
                String str = le.getFilename().substring(0, le.getFilename().length() - 1).replace("GUARD", "G");
                str = str.replace("STATE", "S");
                str = str.replace("CONTROL", "CTRL");
                str = str.replace("CONNECTOR", "CONN");
                str = str.replace("ENGINE", "ENG");
                str = str.replace("COMMON", "COM");
                str = str.replace("SUPERVISOR", "SV");
                str = str.replace("PREPARE", "PREP");
                str = str.replace("MANAGER", "MNG");
                str = str.replace("ACTIONS", "ACT");
                str = str.replace("MEMORY", "MEM");
                str = str.replace("TASK", "TSK");
                str = str.replace("SYSTEM", "SYS");

                str = str.replace("SENSOR", "SENS");
                str = str.replace("RESET", "RST");
                str = str.replace("ONEWIRE", "OW");
                str = str.replace("SENSOR", "SENS");

                str = str.replace("INTERFACE", "IFACE");
                str = str.replace("PRIORITY", "PRT");
                str = str.replace("LOW", "L");
                str = str.replace("EVENT", "EVT");
                str = str.replace("AUDIO", "AUD");
                str = str.replace("STORAGE", "ST");

                StringBuffer sb = new StringBuffer(String.format("%-9s", str));
                sb.append(": ");
                sb.deleteCharAt(sb.length() - 1);
                sb.append("\"}");
                cnt++;
                //if (recordList.size() != cnt) 
                {
                    sb.append(",");
                }
                modules.add(sb.toString());
            }

            data.put("modules", modules);

            // Console output
            /*  Writer out = new OutputStreamWriter(System.out);
             template.process(data, out);
             out.flush();*/
            String md5 = md5(modules.toString().getBytes());

            data.put("md5", "FILE_MD5_HASH = " + md5);

            if (varInFileCompare(md5, outputDest, "FILE_MD5_HASH")) {
                // Old data == New data
                return;
            } else {

                // File output
                Writer file = new FileWriter(new File(outputDest));
                template.process(data, file);
                file.flush();
                file.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
    }

    public static void createLogErrCnt(List<LogEntry> recordList, String outputDest) {

        Integer cnt = 0;
        //Freemarker configuration object
        Configuration cfg = new Configuration();
        try {
            //Load template from source folder
            Template template = cfg.getTemplate("src/auto_log_err_cnt.ftl");

            // Build the data-model
            Map<String, Object> data = new HashMap<String, Object>();
            //  data.put("message", "Hello World!");

            //List parsing 
            List<String> modules = new ArrayList<String>(150);

            // Создание enum с названием uid для Keil
            for (LogEntry le : recordList) {

                if (le.getLogLevel().equals(new LogLevel(0, "LOG_ERR"))) {
                    modules.add(cnt.toString());
                }

                cnt++;
            }

            data.put("log_err_cnt", modules);

            // Console output
            /*  Writer out = new OutputStreamWriter(System.out);
             template.process(data, out);
             out.flush();*/
            String md5 = md5(modules.toString().getBytes());

            data.put("md5", "FILE_MD5_HASH = " + md5);

            if (varInFileCompare(md5, outputDest, "FILE_MD5_HASH")) {
                // Old data == New data
                return;
            } else {

                // File output
                Writer file = new FileWriter(new File(outputDest));
                template.process(data, file);
                file.flush();
                file.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
    }

    public static void createUidTableForC(List<LogEntry> recordList, String outputDest, String imageDest, String log_hash) {

        int cnt = 0;
        //Freemarker configuration object
        Configuration cfg = new Configuration();
        try {
            //Load template from source folder
            Template templateForC = cfg.getTemplate("src/auto_uid_message_table.ftl");

            // Build the data-model
            Map<String, Object> data = new HashMap<String, Object>();
            //  data.put("message", "Hello World!");

            //List parsing 
            List<String> message = new LinkedList<String>();
            List<String> image = new LinkedList<String>();

            // Создание enum с названием uid для Keil
            for (LogEntry le : recordList) {
                String str = le.getFilename().substring(0, le.getFilename().length() - 1).replace("GUARD", "G");
                str = str.replace("STATE", "S");
                str = str.replace("CONTROL", "CTRL");
                str = str.replace("CONNECTOR", "CONN");
                str = str.replace("ENGINE", "ENG");
                str = str.replace("COMMON", "COM");
                str = str.replace("SUPERVISOR", "SV");
                str = str.replace("PREPARE", "PREP");
                str = str.replace("MANAGER", "MNG");
                str = str.replace("ACTIONS", "ACT");
                str = str.replace("MEMORY", "MEM");
                str = str.replace("TASK", "TSK");
                str = str.replace("SYSTEM", "SYS");

                str = str.replace("SENSOR", "SENS");
                str = str.replace("RESET", "RST");
                str = str.replace("ONEWIRE", "OW");
                str = str.replace("SENSOR", "SENS");

                str = str.replace("INTERFACE", "IFACE");
                str = str.replace("PRIORITY", "PRT");
                str = str.replace("LOW", "L");
                str = str.replace("EVENT", "EVT");
                str = str.replace("AUDIO", "AUD");
                str = str.replace("STORAGE", "ST");

                StringBuffer sb = new StringBuffer(String.format("%-9s", str));

                StringBuffer for_debug_sb = new StringBuffer();

                //sb.append(le.getLineNum());
                sb.append(": ");
                if (le.getMessage() != null) {
                    sb.append(le.getMessage().substring(1).trim());
                    for_debug_sb.append(le.getMessage().substring(1).trim());
                } else {
                    System.out.println("Сообщение не распознано, модуль " + le.getFilename() + " " + le.getLineNum());
                    System.out.println("Логи не обработаны, проверьте наличие символа конца строки ");
                    System.exit(0);
                }
                image.add(sb.deleteCharAt(sb.length() - 1).toString());
                for_debug_sb.deleteCharAt(for_debug_sb.length() - 1);
                for_debug_sb.append("\"}");
                cnt++;
                if (recordList.size() != cnt) {
                    for_debug_sb.append(",");
                }
                message.add(for_debug_sb.toString());
            }

            data.put("message", message);

            createImage(image, imageDest, log_hash);

            // Console output
            /*  Writer out = new OutputStreamWriter(System.out);
             template.process(data, out);
             out.flush();*/
            String md5 = md5(message.toString().getBytes());

            data.put("md5", "FILE_MD5_HASH = " + md5);

            if (varInFileCompare(md5, outputDest, "FILE_MD5_HASH")) {
                // Old data == New data
                return;
            } else {
                // File output
                Writer file = new FileWriter(new File(outputDest));
                templateForC.process(data, file);
                file.flush();
                file.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
    }

    public static void serializeLogEntrys(List<LogEntry> recordList, String outputDest, String md5) {

        try {
            FileOutputStream fos = new FileOutputStream(outputDest + "UID_" + md5 + ".ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(recordList);
            oos.flush();
            oos.close();
            fos.close();
        } catch (IOException ex) {
            System.err.println("Trouble writing display list array list");
        }
    }

    @Deprecated
    public static void createUidTableJava(List<LogEntry> recordList, String outputDest) {

        int cnt = 0;
        //Freemarker configuration object
        Configuration cfg = new Configuration();
        try {
            //Load template from source folder
            Template templateJava = cfg.getTemplate("src/auto_uid_message_table_java.ftl");

            // Build the data-model
            Map<String, Object> data = new HashMap<String, Object>();
            //  data.put("message", "Hello World!");

            //List parsing 
            List<String> message = new LinkedList<String>();

            // Создание enum с названием uid для Keil
            for (LogEntry le : recordList) {
                StringBuffer sb = new StringBuffer(le.getFilename());
                sb.append(le.getLineNum());
                sb.append('_');
                sb.append(le.getMessage().substring(1));
                cnt++;
                if (recordList.size() != cnt) {
                    sb.append(",");
                }

                message.add(sb.toString());
            }

            data.put("message", message);

            FileWriter file = new FileWriter(new File("output/UidMessageTable.java"));
            templateJava.process(data, file);
            file.flush();
            file.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
    }

    public static void createGitHash(String outputDest) {

        String git_hash;

        CmdExec cmd = new CmdExec();
        List<String> answ;

        answ = cmd.run_command("git rev-parse HEAD");

        if (!answ.isEmpty()) {

            git_hash = answ.get(0);

            git_hash = git_hash.substring(0, 10);
            
            System.out.println("Git Hash: " +  git_hash);

            if (git_hash.matches("[0-9a-fA-F]+")) {
             
                //Freemarker configuration object
                Configuration cfg = new Configuration();
                try {
                    //Load template from source folder
                    Template template = cfg.getTemplate("src/git_hash.ftl");
                    // Build the data-model
                    Map<String, Object> data = new HashMap<String, Object>();

                    data.put("git_hash", git_hash);

                    if (varInFileCompare(git_hash, outputDest, "git_hash")) {
                        // Old data == New data
                        return;
                    } else {
                        // File output
                        Writer file = new FileWriter(new File(outputDest));
                        template.process(data, file);
                        file.flush();
                        file.close();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (TemplateException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static void createParamTable(List<LogEntry> recordList, String outputDest, String md5RecordList) {

        int cnt = 0;
        int in_cnt = 0;
        int uid_index = 0;
        List<Integer> paramList;
        //Freemarker configuration object
        Configuration cfg = new Configuration();
        try {
            //Load template from source folder
            Template template = cfg.getTemplate("src/auto_uid_param_table.ftl");
            // Build the data-model
            Map<String, Object> data = new HashMap<String, Object>();

            //List parsing 
            Set<String> msg_set = new LinkedHashSet<String>();

            List<String> param = new LinkedList<String>();

            for (LogEntry le : recordList) {
                StringBuilder param_t = new StringBuilder();
                param_t.append("{");
                paramList = le.getParametrList();
                in_cnt = 0;
                if (paramList != null) {
                    for (Integer p : paramList) {
                        param_t.append(p);
                        in_cnt++;
                        if (in_cnt != paramList.size()) {
                            param_t.append(',');
                        }
                    }
                } else {
                    param_t.append(0);
                }
                param_t.append("}");
                String str = param_t.toString();
                msg_set.add(str);

                List<String> stringsList = new ArrayList<>(msg_set);

                StringBuilder sb = new StringBuilder();
                sb.append(stringsList.indexOf(str));

                /*cnt++;
                 if (recordList.size() != cnt) {
                 sb.append(",");
                 }*/
                param.add(sb.toString());
            }
            Set<String> message = new LinkedHashSet<String>();
            List<String> index_tbl = new LinkedList<String>();
            int i = 0;
            for (String elem : msg_set) {
                String ind_elem;
                ind_elem = "index_" + i + "[] = " + elem;
                message.add(ind_elem);
                index_tbl.add("index_" + i);
                i++;
            }

            data.put("uid_massive", message);
            data.put("uid_index", index_tbl);
            data.put("uid_param", param);
            data.put("hash", md5RecordList);

            String md5 = md5(message.toString().getBytes());

            data.put("md5", "FILE_MD5_HASH = " + md5);

            if (varInFileCompare(md5RecordList, outputDest, "logger_hash")) {
                // Old data == New data
                return;
            } else {
                // File output
                Writer file = new FileWriter(new File(outputDest));
                template.process(data, file);
                file.flush();
                file.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
    }

    @Deprecated
    public static void createParamTableJava(List<LogEntry> recordList, String outputDest) {

        int cnt = 0;
        int in_cnt = 0;
        List<Integer> paramList;
        //Freemarker configuration object
        Configuration cfg = new Configuration();
        try {
            //Load template from source folder
            Template template = cfg.getTemplate("src/auto_uid_param_table_java.ftl");
            // Build the data-model
            Map<String, Object> data = new HashMap<String, Object>();

            //List parsing 
            List<String> message = new LinkedList<String>();

            for (LogEntry le : recordList) {
                StringBuffer sb = new StringBuffer("{");
                paramList = le.getParametrList();
                in_cnt = 0;
                if (paramList != null) {
                    for (Integer p : paramList) {
                        sb.append(p);
                        in_cnt++;
                        if (in_cnt != paramList.size()) {
                            sb.append(',');
                        }
                    }
                } else {
                    sb.append(0);
                }
                sb.append("}");
                cnt++;
                if (recordList.size() != cnt) {
                    sb.append(",");
                }
                message.add(sb.toString());
            }
            data.put("uid_massive", message);

            // File output
            Writer file = new FileWriter(new File(outputDest + "output"));
            template.process(data, file);
            file.flush();
            file.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
    }

    private static Boolean uidFileDiff(String md5New, String fileDest) {

        BufferedReader b = null;
        String line = null;
        String str;

        File f = new File(fileDest);

        if (f.exists()) {
            try {
                b = new BufferedReader(new FileReader(f), 1024);

                while ((line = b.readLine()) != null) {

                    if (line.indexOf("FILE_MD5_HASH") != -1) {
                        StringTokenizer st = new StringTokenizer(line, "=");
                        if (st.hasMoreTokens()) {
                            st.nextToken();
                        }

                        if (st.hasMoreTokens()) {
                            str = st.nextToken().trim();
                            if (str.equals(md5New)) {
                                return Boolean.TRUE;
                            }
                        }
                        return Boolean.FALSE;
                    }
                }

            } catch (FileNotFoundException ex) {
                Logger.getLogger(TemplateComposer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(TemplateComposer.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Boolean.FALSE;
        } else {
            return Boolean.FALSE;
        }

    }

    private static Boolean varInFileCompare(String compared_val, String fileDest, String variable_name) {

        BufferedReader b = null;
        String line = null;
        String str;
        int index;

        File f = new File(fileDest);

        if (f.exists()) {
            try {
                b = new BufferedReader(new FileReader(f), 1024);

                while ((line = b.readLine()) != null) {

                    index = line.indexOf(variable_name);

                    if (index != -1) {
                        StringTokenizer st = new StringTokenizer(line.substring(index), " =\"");
                        if (st.hasMoreTokens()) {
                            st.nextToken();
                        }

                        if (st.hasMoreTokens()) {
                            str = st.nextToken().trim();
                            if (str.equals(compared_val)) {
                                return Boolean.TRUE;
                            }
                        }
                        return Boolean.FALSE;
                    }
                }

            } catch (FileNotFoundException ex) {
                Logger.getLogger(TemplateComposer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(TemplateComposer.class.getName()).log(Level.SEVERE, null, ex);
            }
            return Boolean.FALSE;
        } else {
            return Boolean.FALSE;
        }

    }

    public static String md5(byte[] byteArray) {

        MessageDigest m = null;

        try {

            m = MessageDigest.getInstance("MD5");
            m.reset();
            m.update(byteArray);
            byte[] digest = m.digest();
            BigInteger bigInt = new BigInteger(1, digest);
            String hashtext = bigInt.toString(16);
// Now we need to zero pad it if you actually want the full 32 chars.
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;

        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(TemplateComposer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private static void createImage(List<String> image, String imageDest, String log_hash) throws IOException {

        final byte HEADER_SIZE = 43;
        final byte ELEMENT_SIZE = 8;

        int len = 0;

        File outFile = new File(imageDest + "log_image.bin");
        FileOutputStream oStream = null;
        ByteBuffer headerBuf;

        if (!outFile.exists()) {
            outFile.createNewFile();
        }

        try {
            oStream = new FileOutputStream(outFile);
        } catch (FileNotFoundException ex) {
            System.out.println("Файл не найден");
        }

        // 44 байт заголовок
        len = HEADER_SIZE + image.size() * ELEMENT_SIZE;
        headerBuf = ByteBuffer.allocate(len);
        //headerBuf = ByteBuffer.allocate( 0x40000 );
        headerBuf.order(ByteOrder.LITTLE_ENDIAN);
        // Формирование заголовка
        String str = "LAT";

        byte[] h = str.getBytes();
        int overallFile = 0;
        int addr_cnt = HEADER_SIZE + image.size() * ELEMENT_SIZE;

        for (String msg : image) {
            overallFile += msg.length();
        }

        headerBuf.put(h[0]);                       // 0     // L
        headerBuf.put(h[1]);                       // 1     // A
        headerBuf.put(h[2]);                       // 2     // T
        headerBuf.put((byte) 0);                   // 3     // 0                
        headerBuf.putShort((short) image.size());  // 4 - 5 //Количество записей        
        headerBuf.putInt(overallFile);             // 6 - 9 //Общий размер текста        
        // CRC8
        byte crc = (byte) CrcCalc.crc8(headerBuf.array(), headerBuf.position());
        headerBuf.put(crc);                        // 10 crc8

        headerBuf.put(log_hash.getBytes());        // 11 - 42 

        // Таблица адресов строк и размера
        // На каждую запись 4 байта адрес и 2 байта длина, 2 байта crc
        for (String msg : image) {
            short crc16;
            headerBuf.putInt(addr_cnt);
            addr_cnt += msg.length();
            headerBuf.putShort((short) msg.length());
            crc16 = (short) CrcCalc.crc16(msg.getBytes(), (short) msg.length());
            headerBuf.putShort(crc16);
        }

        oStream.write(headerBuf.array());

        for (String msg : image) {
            byte b[] = msg.getBytes("CP1251");
            oStream.write(b);
            len += b.length;
            //oStream.write(0x00);
        }
        int cnt = 0x30000 - len;

        for (int i = 0; i < cnt; i++) {
            byte a[] = {0};
            oStream.write(a);
        }

        // Загрузка строк смс из json
        JSONParser parser = new JSONParser();

        try {

            Object obj = parser.parse(new FileReader("../utils/sms_parser/str_parse_cmd.json"));

            JSONObject jsonObject = (JSONObject) obj;

            Map<String, String> map = new HashMap<>();

            map = (Map<String, String>) jsonObject;

            List<String> im = new ArrayList<>();

            SortedSet<Integer> keys = new TreeSet<Integer>();

            for (String key : map.keySet()) {
                keys.add(Integer.parseInt(key));
            }

            for (Integer key : keys) {
                String value = map.get(key.toString());
                im.add(value);
                //System.out.println(key);
            }

            // 44 байт заголовок
            len = HEADER_SIZE + im.size() * ELEMENT_SIZE;
            headerBuf = ByteBuffer.allocate(len);
            //headerBuf = ByteBuffer.allocate( 0x40000 );
            headerBuf.order(ByteOrder.LITTLE_ENDIAN);
            // Формирование заголовка
            str = "LAT";

            h = str.getBytes();
            overallFile = 0;
            addr_cnt = HEADER_SIZE + im.size() * ELEMENT_SIZE;

            for (String msg : im) {
                overallFile += msg.length();
            }

            headerBuf.put(h[0]);                       // 0     // L
            headerBuf.put(h[1]);                       // 1     // A
            headerBuf.put(h[2]);                       // 2     // T
            headerBuf.put((byte) 0);                   // 3     // 0                
            headerBuf.putShort((short) im.size());  // 4 - 5 //Количество записей        
            headerBuf.putInt(overallFile);             // 6 - 9 //Общий размер текста        
            // CRC8
            crc = (byte) CrcCalc.crc8(headerBuf.array(), headerBuf.position());
            headerBuf.put(crc);                        // 10 crc8

            headerBuf.put(log_hash.getBytes());        // 11 - 42 

            // Таблица адресов строк и размера
            // На каждую запись 4 байта адрес и 2 байта длина, 2 байта crc
            int i = 0;
            for (String msg : im) {
                short crc16;
                i++;
                headerBuf.putInt(addr_cnt);
                addr_cnt += msg.length();
                headerBuf.putShort((short) msg.length());
                crc16 = (short) CrcCalc.crc16(msg.getBytes(), (short) msg.length());
                headerBuf.putShort(crc16);
            }

            oStream.write(headerBuf.array());
            boolean f = true;
            int cc = 0;
            for (String msg : im) {
                //msg = msg.
                byte b[] = msg.getBytes("CP1251");
                if (f) {
                    f = false;
                    for (int k = 0; k < msg.length(); k++) {
                        System.out.printf("0x%x ", b[k]);
                    }
                    System.out.println(" ");
                    System.out.println(msg);
                }

                oStream.write(b);
                len += b.length;

                cc++;
                //System.out.println(b[0]);
                //oStream.write(0x00);
            }

            /* cnt = 0x3000 - len;

             for (int i = 0; i < cnt; i++) {
             byte a[] = {0};
             oStream.write(a);
             }*/
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            oStream.flush();
            oStream.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
