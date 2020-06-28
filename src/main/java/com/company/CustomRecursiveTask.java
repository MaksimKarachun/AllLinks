package com.company;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

public class CustomRecursiveTask extends RecursiveAction {
    
    private String url;
    private int lvl;
    private static ConcurrentHashMap<String, List<String>> allLinks = new ConcurrentHashMap<>();
    private static String initialUrl = "";
    private static String outPutFilePath;
    public static int nestingLvl;

    public CustomRecursiveTask(String url, int lvl) {
        this.url = url;
        this.lvl = lvl;

        if (initialUrl.equals(""))
            initialUrl = url;
    }

    @Override
    protected void compute() {

        try {

            if(!allLinks.containsKey(url))
            allLinks.put(url, new ArrayList<>());

            Document document = Jsoup.connect(url).get();
            Elements links = document.select("a");
            ArrayList<String> sublinks = new ArrayList<>();
            int count = 0;
            //проходим по всем ссылкам
            for (Element link : links) {

                String currentLink = link.absUrl("href");

                    //если находим дочерние ссылки создаем подзадачу
                    if (currentLink.startsWith(url)
                            && currentLink.endsWith("/")
                            && !allLinks.containsKey(currentLink)) {

                        sublinks.add(currentLink);
                        count++;
                    }
            }

            //условие возврата
            if (lvl <= nestingLvl && count != 0)
                ForkJoinTask.invokeAll(createSubtasks(sublinks));
            else{
                printMap(allLinks);
            }

        } catch (IOException | InterruptedException e) {
            e.getMessage();
        }
    }


    private void printMap(Map<String, List<String>> allLinks) {
        try(FileWriter writer = new FileWriter(outPutFilePath, false)) {

            writer.write(initialUrl + '\n');
            for (String iterator : allLinks.get(initialUrl)) {
                printLinks(iterator, 0, writer);
            }
            writer.flush();
        }
        catch (IOException exception){
            exception.getMessage();
        }
    }


    public List<CustomRecursiveTask> createSubtasks(ArrayList<String> links) throws InterruptedException {

        List<CustomRecursiveTask> taskList = new ArrayList<>();
        int currentLvl = lvl + 1;
        for (String currentLink : links) {
            Thread.sleep(150);
            taskList.add(new CustomRecursiveTask(currentLink, currentLvl));
        }
        allLinks.replace(url, links);

        return taskList;
    }

    public void printLinks(String link, int lvl, FileWriter writer) throws IOException {
        lvl++;
        for(int i = 0; i < lvl; i++){
            writer.write('\t');
        }

        writer.write(link + '\n');

        if (allLinks.get(link) !=  null && allLinks.get(link).size() >  0) {
            for (String iter : allLinks.get(link)){
                printLinks(iter, lvl, writer);
            }
        }
    }

    public static void setResultPath(String path){
        outPutFilePath = path;
    }

    public static void setNestingLvl(int nestingLvl) {
        CustomRecursiveTask.nestingLvl = nestingLvl;
    }
}
