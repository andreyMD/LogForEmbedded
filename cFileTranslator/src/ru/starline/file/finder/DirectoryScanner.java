/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.starline.file.finder;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;

/**
 *
 * @author user
 */
public class DirectoryScanner {

    private long filesNumber = 0;
    private long directoriesNumber = 0;
    private long totalLength = 0;
    private final int FILES = 0;
    private final int DIRECTORIES = 1;
    private final int ALL = 2;
    private Pattern p = null;
    private Matcher m = null;

    private List<String> excludeDirectories;

    public void setExcludeDirectories(List<String> excludeDirectories) {
        this.excludeDirectories = excludeDirectories;
    }

    public List findAll(String startPath) throws Exception {

        return find(startPath, "", ALL);

    }

    public List findAll(String startPath, String mask)
            throws Exception {

        return find(startPath, mask, ALL);

    }

    public List findFiles(String startPath)
            throws Exception {

        return find(startPath, "", FILES);

    }

    public List findFiles(String startPath, String mask)
            throws Exception {

        return find(startPath, mask, FILES);

    }

    public List findDirectories(String startPath)
            throws Exception {

        return find(startPath, "", DIRECTORIES);

    }

    public List findDirectories(String startPath, String mask)
            throws Exception {

        return find(startPath, mask, DIRECTORIES);

    }

    public long getDirectorySize() {

        return totalLength;

    }

    public long getFilesNumber() {

        return filesNumber;

    }

    public long getDirectoriesNumber() {

        return directoriesNumber;

    }

    private boolean accept(String name) {

        //если регулярное выражение не задано…
        if (p == null) {

            //…значит объект подходит
            return true;

        }

        //создаем Matcher
        m = p.matcher(name);

        //выполняем проверку
        if (m.matches()) {

            return true;

        } else {

            return false;

        }

    }

    private List find(String startPath, String mask, int objectType) throws Exception {

        //проверка параметров 
        
        if (startPath == null || mask == null) {

            throw new Exception("Ошибка: не заданы параметры поиска");

        }

        File topDirectory = new File(startPath);
        
        System.out.println(startPath);

        if (!topDirectory.exists()) {

            throw new Exception("Ошибка: указанный путь не существует" + startPath);

        }
        if (!mask.equals("")) {

            p = Pattern.compile(mask,
                    Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

        }

        //обнуляем все счетчики
        filesNumber = 0;

        directoriesNumber = 0;

        totalLength = 0;

        //создаем список результатов
        ArrayList res = new ArrayList(100);

        //выполняем поиск
        search(topDirectory, res, objectType);

        //присваиваем null шаблону, т.к. при следующем вызове find…
        //регулярное выражение может быть не задано
        p = null;

        //возвращаем результат
        return res;

    }

    private void search(File topDirectory, List res, int objectType) throws IOException {

        //получаем список всех объектов в текущей директории
        File[] list = topDirectory.listFiles();

        //просматриваем все объекты по-очереди
        for (int i = 0; i < list.length; i++) {

            Boolean ignore_flag = false;
            //если это директория (папка)…
            if (list[i].isDirectory()) {

                //…выполняем проверку на соответствие типу объекта
                // и регулярному выражению…
                String dest;
                dest = list[i].getCanonicalPath();

               // System.out.println(dest);

                for (String str : excludeDirectories) {
                    if (dest.contains(str)) {
                        System.out.println("Ignore folder: " + str);
                        ignore_flag = true;
                        break;
                    }
                }

                if (ignore_flag) continue;

                if (objectType != FILES && accept(list[i].getName())) {

                    //…добавляем текущий объект в список результатов,
                    //и обновляем значения счетчиков
                    directoriesNumber++;

                    res.add(list[i]);

                }

                //выполняем поиск во вложенных директориях
                search(list[i], res, objectType);

            } //если это файл
            else {

                //…выполняем проверку на соответствие типу объекта
                // и регулярному выражению…
                if (objectType != DIRECTORIES && accept(list[i].getName())) {

                    //…добавляем текущий объект в список результатов,
                    //и обновляем значения счетчиков
                    filesNumber++;

                    totalLength += list[i].length();

                    res.add(list[i]);

                }

            }

        }
    }
}
