/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.starline.translator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import ru.starline.entity.LogEntry;
import ru.starline.file.creator.TemplateComposer;
import ru.starline.file.translator.SingleFileTranslator;
import ru.starline.file.finder.DirectoryScanner;

/**
 *
 * @author user
 */
public class main {

    private static String PROJECT_SRC_DIRECTORY_NAME;
    private static String OUTPUT_UID_ENUM;
    private static String OUTPUT_PARAMETR_TABLE;
    private static String COMPILE_OUT_DIRECTORY;
    private static String OUTPUT_MESSAGE_TABLE;
    private static String LOGGER_NAME_REGEX;
    private static String OUTPUT_MODULES;
    private static String OUTPUT_MODULES_NAME;
    private static String OUTPUT_LOG_ERR_CNT;
    private static String EXCLUDE_DIRECTORY = "";
    private static String OUTPUT_GIT_HASH;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {

        long startTime;
        List<File> cFileList = null;
        List<LogEntry> recordList = new LinkedList();

        SingleFileTranslator sft = new SingleFileTranslator();
        // Find all *.c files
        DirectoryScanner ds = new DirectoryScanner();

        sft.setLogEntryList(recordList);
        startTime = System.currentTimeMillis();

        Properties pr = new Properties();
        try {
            pr.load(new FileReader("translator.properties"));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        }
        PROJECT_SRC_DIRECTORY_NAME = pr.getProperty("PROJECT_SRC_DIRECTORY_NAME");
        OUTPUT_UID_ENUM = pr.getProperty("OUTPUT_UID_ENUM");
        OUTPUT_MODULES = pr.getProperty("OUTPUT_MODULES");
        OUTPUT_MODULES_NAME = pr.getProperty("OUTPUT_MODULES_NAME");
        OUTPUT_PARAMETR_TABLE = pr.getProperty("OUTPUT_PARAMETR_TABLE");
        OUTPUT_MESSAGE_TABLE = pr.getProperty("OUTPUT_MESSAGE_TABLE");
        COMPILE_OUT_DIRECTORY = pr.getProperty("COMPILE_OUT_DIRECTORY");
        LOGGER_NAME_REGEX = pr.getProperty("LOGGER_NAME_REGEX");
        OUTPUT_LOG_ERR_CNT = pr.getProperty("OUTPUT_LOG_ERR_CNT");
        OUTPUT_GIT_HASH = pr.getProperty("OUTPUT_GIT_HASH");


        //EXCLUDE_DIRECTORY = pr.getProperty("EXCLUDE_DIRECTORY");
        

        // sft.setPatternRegex(LOGGER_NAME_REGEX);
        ds.setExcludeDirectories( new ArrayList<String>(Arrays.asList(args)));
        try {
            cFileList = ds.findFiles(PROJECT_SRC_DIRECTORY_NAME, ".*\\.c$");
        } catch (Exception ex) {
            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("-----------------------------------------");
        System.out.println("number of *.c files processed: " + cFileList.size());

        for (File file : cFileList) {
            sft.setfFile(file);
            sft.processLineByLine();
        }
        // Название файлов будем создавать на основе hash
        String md5 = TemplateComposer.md5(recordList.toString().getBytes());

        TemplateComposer.createUidEnumFiles(recordList, OUTPUT_UID_ENUM);

        TemplateComposer.createModules(recordList, OUTPUT_MODULES);
        TemplateComposer.createModulesName(recordList, OUTPUT_MODULES_NAME);

        TemplateComposer.createLogErrCnt(recordList, OUTPUT_LOG_ERR_CNT);

        TemplateComposer.createUidTableForC(recordList, OUTPUT_MESSAGE_TABLE, COMPILE_OUT_DIRECTORY, md5);

        TemplateComposer.createParamTable(recordList, OUTPUT_PARAMETR_TABLE, md5);
        
        TemplateComposer.createGitHash(OUTPUT_GIT_HASH);

        TemplateComposer.serializeLogEntrys(recordList, COMPILE_OUT_DIRECTORY, md5);

        //  TemplateComposer.createUidTableJava(recordList, COMPILE_OUT_DIRECTORY);

        //  TemplateComposer.createParamTableJava(recordList, COMPILE_OUT_DIRECTORY);
        
        System.out.println("search time: " + (System.currentTimeMillis() - startTime) + " msec");
        System.out.println("-----------------------------------------");      
    }
}
