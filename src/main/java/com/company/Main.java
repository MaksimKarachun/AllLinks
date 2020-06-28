package com.company;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ForkJoinPool;

public class Main {

    public static void main(String[] args) throws IOException {

        //получение настроек из файла конфигурации
        Properties properties = new Properties();
        FileInputStream fis = new FileInputStream("src/main/resources/config.properties");
        properties.load(fis);
        CustomRecursiveTask.setResultPath(properties.getProperty("resultPath"));

        //установка уровня вложенности
        CustomRecursiveTask.setNestingLvl(3);

        //запуск процесса получения ссылок
        new ForkJoinPool().invoke(new CustomRecursiveTask(properties.getProperty("site"), 1));
    }
}
