package org.bitbucket.dyatlov.crawler;

import java.net.MalformedURLException;
import java.net.URL;

public class Main {
    public static void main(String[] args) {
        try {
            Crawler crawler = new Crawler(new URL("http://bruker.com"), 1000);
            crawler.crawl();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
