package org.bitbucket.dyatlov.crawler;

import java.util.Deque;
import java.util.HashSet;

/**
 * Created by Dyatlov on 13/06/2014.
 */
public class Crawler {
    private HashSet<String> obtained;
    private Deque<String> toProcess;
    private int maxToObtain;
    public boolean addUrl(String url) {
        if (!obtained.add(url)) {
            return obtained.size() < maxToObtain;
        }
        toProcess.addLast(url);
        return obtained.size() < maxToObtain;
    }
    public void crawl() {
//        while (!toProcess.isEmpty()) {
//            String url = toProcess.pop();
//            Fetcher fetcher = new Fetcher();
//            CharSequence html;
//            try {
//                html = fetcher.fetch(new URL(url));
//            } catch (Exception e) {
//                // Do nothing
//                continue;
//            }
//            LinkParser parser = new LinkParser();
//            Collection<String> links = parser.parse(html);
//            for (String link: links) {
//                URI uri;
//                try {
//                    uri = new URI(link);
//                } catch (URISyntaxException e) {
//                    // Do nothing
//                    continue;
//                }
//                // Check if URL
//
//            }
//        }
    }
}
