package com.company;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.concurrent.ForkJoinPool;

public class Main {

    public static void main(String[] args) throws IOException {
	// write your code here

        String url = "https://lenta.ru";
        //new ForkJoinPool().invoke(new CustomRecursiveTask(url));


        Document document = Jsoup.connect(url).get();
        org.jsoup.select.Elements links = document.select("a");
        for (Element link : links) {
            String str = link.attr("href");
            System.out.println(str);
        }
    }
}
