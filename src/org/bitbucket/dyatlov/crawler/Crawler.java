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
    private Fetcher fetcher;
    private PageParser parser;

    public Crawler(URL startUrl, int maxToObtain, Fetcher fetcher, PageParser parser) {
        if (startUrl == null) {
            throw new IllegalArgumentException("startUrl shouldn't be null");
        }
        if (fetcher == null) {
            throw new IllegalArgumentException("fetcher shouldn't be null");
        }
        if (parser == null) {
            throw new IllegalArgumentException("parser shouldn't be null");
        }
        URL originalStartUrl = startUrl;
        startUrl = UrlNormalizer.normalizeURL(startUrl, null);
        if (startUrl == null) {
            startUrl = originalStartUrl;
        }
        addObtainedUrl(UrlNormalizer.normalizeURL(startUrl, "UTF-8").toString());
        this.maxToObtain = maxToObtain;
        this.fetcher = fetcher;
        this.parser = parser;
    }

    private void addObtainedUrl(String url) {
        if (!obtained.add(url)) {
            return;
        }
        toProcess.addLast(url);
        System.out.println(url + " #" + obtained.size());
        return;
    }

    public void crawl() {
        while (!toProcess.isEmpty()) {
            String url = toProcess.pollFirst();
            URL pageUrl;
            Reader reader;
            Fetcher.Result result;
            try {
                pageUrl = new URL(url);
                result = fetcher.fetch(pageUrl);
                reader = result.getReader();
            } catch (IOException | UnsupportedContentTypeException e) {
                // Do nothing
                continue;
            }
            PageInfo pageInfo;
            try {
                pageInfo = parser.parse(pageUrl, reader, result.getEncoding());
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
