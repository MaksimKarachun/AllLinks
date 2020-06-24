package com.company;

import java.util.concurrent.ForkJoinPool;

public class Main {

    public static void main(String[] args) {
	// write your code here

        String url = "https://skillbox.ru";
        new ForkJoinPool().invoke(new CustomRecursiveTask(url));

    }
}
