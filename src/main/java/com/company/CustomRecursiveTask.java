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
    private static ConcurrentHashMap<String, List<String>> allLinks = new ConcurrentHashMap<>();

    public CustomRecursiveTask(String url) {
        this.url = url;
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

                String currentLink = link.attr("href");

                if (currentLink== null || currentLink.equals("") || currentLink.equals("/"))
                    continue;

                    //если находим очерние ссылки создаем подзадачу
                    if (currentLink.charAt(0) == '/'
                            && !allLinks.containsKey("https://lenta.ru" + currentLink)
                            && ("https://lenta.ru" + currentLink).contains(url)
                            && !("https://lenta.ru" + currentLink).equals(url)) {

                        sublinks.add(currentLink);
                        count++;
                    }

            }

            //условие возврата
            if (count != 0)
                ForkJoinTask.invokeAll(createSubtasks(sublinks));
            else{
                printMap(allLinks);
            }
        } catch (IOException e) {
            e.getMessage();
        }
    }

    private void printMap(Map<String, List<String>> allLinks) {
        try(FileWriter writer = new FileWriter("./result.txt", false)) {

            writer.write("https://lenta.ru" + '\n');
            for (String iterator : allLinks.get("https://lenta.ru")) {
                printLinks(iterator, 0, writer);
            }
            writer.flush();
        }
        catch (IOException exception){
            exception.getMessage();
        }
    }



    public List<CustomRecursiveTask> createSubtasks(ArrayList<String> links) {

        List<CustomRecursiveTask> taskList = new ArrayList<>();
        List<String> subLinks = new ArrayList<>();

        for (String currentLink : links) {

            subLinks.add("https://lenta.ru" + currentLink);
            taskList.add(new CustomRecursiveTask("https://lenta.ru" + currentLink));

        }
        allLinks.replace(url, subLinks);

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

}
