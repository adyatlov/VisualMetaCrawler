package org.bitbucket.dyatlov.crawler;

import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;

/**
 * Created by Dyatlov on 13/06/2014.
 */
public class Crawler {
    private HashSet<String> obtained = new HashSet<>();
    private Deque<String> toProcess =  new ArrayDeque<>();
    private final int maxToObtain;

    public Crawler(URL startUrl, int maxToObtain) {
        URL originalStartUrl = startUrl;
        startUrl = PageParser.normalizeURL(startUrl, null);
        if (startUrl == null) {
            startUrl = originalStartUrl;
        }
        addObtainedUrl(PageParser.normalizeURL(startUrl, null).toString());
        this.maxToObtain = maxToObtain;
    }

    private void addObtainedUrl(String url) {
        System.out.print(url);
        if (!obtained.add(url)) {
            System.out.println(" already exists");
            return;
        }
        toProcess.addLast(url);
        System.out.println(" added #" + obtained.size());
        return;
    }

    public void crawl() {
        while (!toProcess.isEmpty()) {
            String url = toProcess.pollFirst();
            Fetcher fetcher = new Fetcher();
            URL pageUrl;
            Reader reader;
            try {
                pageUrl = new URL(url);
                reader = fetcher.fetch(pageUrl);
            } catch (Exception e) {
                // Do nothing
                continue;
            }
            PageInfo pageInfo;
            try {
                pageInfo = PageParser.parse(pageUrl, reader, null);
            } catch (IOException e) {
                // Do nothing
                continue;
            }
            for (String link: pageInfo.getLinks()) {
                if (obtained.size() > maxToObtain) {
                    return;
                }
                addObtainedUrl(link);
            }
        }
    }
}
