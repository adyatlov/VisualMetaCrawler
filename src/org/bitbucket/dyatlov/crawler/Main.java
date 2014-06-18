package org.bitbucket.dyatlov.crawler;

import java.net.MalformedURLException;
import java.net.URL;

public class Main {
    public static void main(String[] args) {
        Crawler crawler;
        try {
            crawler = new Crawler(new URL("http://scils.de"), 1000, new Fetcher(), new PageParser());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            System.exit(1);
            return;
        }
        crawler.crawl();
    }
}
