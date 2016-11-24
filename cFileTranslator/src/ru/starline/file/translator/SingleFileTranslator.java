/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.starline.file.translator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ru.starline.entity.LogEntry;
import ru.starline.entity.LogLevel;

/**
 *
 * @author user
 */
public class SingleFileTranslator {

    private File fFile = null;
    private String patternRegex = "LOG_TEST\\(|LOG_BLOCK\\(|LOG_WARN\\(|LOG_INFO\\(|LOG_DEBUG\\(|LOG_TRACE\\(|LOG_ERR\\(";
    private String patternEndOfLogLine = ");";
    private Integer currentLineNumber;
    private String currentFile;
    // compile default pattern
    private Pattern pattern = Pattern.compile(patternRegex);
    private Boolean multiLineRecord;
    private Matcher matcher = null;
    private List<LogEntry> logEntryList = null;
    private final static int MAX_LOG_ENTRY = 0xEDFE;

    private static void log(Object aObject) {
        System.out.println(String.valueOf(aObject));
    }

    public void setfFile(File fFile) {
        this.fFile = fFile;
    }

    private String quote(String aText) {
        String QUOTE = "'";
        return QUOTE + aText + QUOTE;
    }

    public SingleFileTranslator() {
    }

    public List<LogEntry> getLogEntryList() {
        return logEntryList;
    }

    public void setLogEntryList(List<LogEntry> logEntryList) {
        this.logEntryList = logEntryList;
    }

    public SingleFileTranslator(File fFile) {
        this.fFile = fFile;
    }

    public void setPatternRegex(String patternRegex) {
        this.patternRegex = patternRegex;
        pattern = Pattern.compile(patternRegex);
    }

    public final void processLineByLine() throws FileNotFoundException {

        String line;
        BufferedReader b = new BufferedReader(new FileReader(fFile), 1024);

        Boolean messageFlag = Boolean.FALSE;
        Boolean findEnd = Boolean.FALSE;
        Boolean multiLine = Boolean.FALSE;
        Boolean haveStart = Boolean.FALSE;


        Integer begin;
        Integer end;
        char charAt;
        int logEntryCnt = 0;

        StringBuilder sbFirst = new StringBuilder();
        StringBuilder sbSecond = new StringBuilder();
        LogEntry le = new LogEntry();

        currentFile = fFile.getName();

        currentFile = currentFile.substring(0, currentFile.length() - 2);
        currentFile = currentFile.toUpperCase();
        currentFile = currentFile.concat("_");


        currentLineNumber = 1;
        try {
            while ((line = b.readLine()) != null) {
                
                matcher = pattern.matcher(line);

                if (matcher.find() && !multiLine) {

                    if(matcher.start()>=2)
                       if(line.charAt(matcher.start()-1)=='/' 
                               && line.charAt(matcher.start()-1)=='/')

                       {
                           currentLineNumber++;
                           continue;
                       }
                       
                    le = new LogEntry();
                    if (++logEntryCnt > MAX_LOG_ENTRY) {

                        System.out.println("Overflow UID number");
                        System.out.println("Abnormal Exit");
                        System.exit(1);
                    }
                    String levelName = line.substring(matcher.start(), matcher.end() - 1);
                    le.logLevel = new LogLevel(0, levelName);
                    le.setFilename(currentFile);
                        le.setLineNum(currentLineNumber);
                    begin = matcher.end();
                    end = line.indexOf(");");

                    // If end symbol in this line
                    if (end != -1) {
                        // Copy string to LogEntry
                             /*message pattern
                         * LOG(LOG_LEVEL_xxx, "message", ...)
                         */

                        if (begin > end) {
                            System.out.print("ОШИБКА! файл: " + currentFile.toLowerCase().substring(0, currentFile.length() - 1) + ".c" + " строка: " + currentLineNumber);
                            System.out.println("  - сообщение не разобрано, используйте для лога отдельную строку!");                            
                            continue;
                        }

                        // parse message string
                        findEnd = Boolean.TRUE;
                        StringTokenizer st = new StringTokenizer(line.substring(begin, end), " , \t\r\n");
                        String str;
                        sbSecond = new StringBuilder();

                        while (st.hasMoreTokens()) {
                            str = st.nextToken();

                            if (str.startsWith("LOG_LEVEL")) {
                                le.logLevel.setLevelName(str);
                            }

                            if (str.charAt(0) == '"') {
                                messageFlag = Boolean.TRUE;
                                haveStart = Boolean.TRUE;
                            }

                            if (messageFlag) {
                                sbSecond.append(str);
                                sbSecond.append(' ');
                            }

                            if (str.charAt(str.length() - 1) == '"') {
                                messageFlag = Boolean.FALSE;
                                le.setMessage(sbSecond.toString());
                                parseMessage(le.getMessage(), le);
                            }


                            // System.out.println(str);

                        }
                        multiLine = Boolean.FALSE;
                        logEntryList.add(le);
                    } else {
                        //Copy current string and go to next line
                        sbFirst = new StringBuilder();
                        sbFirst.append(line.substring(begin, (line.length() - 1)));
                        multiLine = Boolean.TRUE;
                    }
                } else if (multiLine) {

                    end = line.indexOf(");");

                    // If end symbol in this line
                    if (end != -1) {
                        // parse message string
                        findEnd = Boolean.TRUE;
                        sbFirst.append(line.substring(0, end));

                        StringTokenizer st = new StringTokenizer(sbFirst.toString(), " , \t\r\n");
                        String str;
                        sbSecond = new StringBuilder();
                        while (st.hasMoreTokens()) {
                            str = st.nextToken();

                            if (str.startsWith("LOG_LEVEL")) {
                                le.logLevel.setLevelName(str);
                            }

                            if (str.charAt(0) == '"' || messageFlag) {
                                messageFlag = Boolean.TRUE;
                            }

                            if (messageFlag) {
                                sbSecond.append(str);
                                sbSecond.append(' ');
                            }

                            if (str.charAt(str.length() - 1) == '"') {
                                messageFlag = Boolean.FALSE;
                                le.setMessage(sbSecond.toString());
                                parseMessage(le.getMessage(), le);
                            }
                            //  System.out.println(str);
                        }
                        multiLine = Boolean.FALSE;
                        logEntryList.add(le);
                    } else {
                        //Copy current string and go to next line
                        sbFirst.append(line);
                        multiLine = Boolean.TRUE;
                    }
                }
                currentLineNumber++;
            }
        } catch (IOException ex) {
            Logger.getLogger(SingleFileTranslator.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (b != null) {
                    b.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

    private void parseMessage(String message, LogEntry le) {
        // StringTokenizer st = new StringTokenizer(message, " ");
        List<Integer> paramList = new ArrayList(16);
        Integer cnt = 0;
        Integer curPos = 0;
        Character nextCharacter;
        String str;
        paramList.add(cnt);
        int i = message.indexOf('%');
        if (i == -1) {
            return;
        }


        while (i < message.length()) {

            Integer sizeParam = 0xFF;
            nextCharacter = message.charAt(i);
            if (nextCharacter == '%') {
                while (++i < message.length()) {
                    nextCharacter = message.charAt(i);

                    if (nextCharacter != '%') {


                        switch (nextCharacter) {

                            case 's':
                                sizeParam = 0;
                                break;

                            case 'd':
                            case 'i':
                            case 'u':
                            case 'o':
                            case 'x':
                            case 'X':
                            case 'p':
                            case 'H':
                                sizeParam = 4;
                                break;

                            case 'e':
                            case 'E':
                            case 'g':
                            case 'G':
                            case 'f':
                            case 'F':
                            case 'a':
                            case 'A':
                                sizeParam = 5;
                                break;

                            case 'c':
                                sizeParam = 1;
                                break;

                        }
                        if (sizeParam != 0xFF) {
                            cnt++;
                            paramList.set(0, cnt);
                            paramList.add(sizeParam);
                            break;
                        }
                    }
                }

            }
            i++;
        }
        le.setParametrList(paramList);
    }
}
