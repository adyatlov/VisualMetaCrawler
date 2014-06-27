package org.bitbucket.dyatlov.crawler;

import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

/**
 * The Crawler. It crawls WEB starting with the single URL using PageFetcher and PageParser.
 * TODO(Dyatlov): extract interfaces of PageFetcher and PageParser for testing and flexibility.
 * TODO(Dyatlov): make the class thread-safe to obtain URLs simultaneously from multiple threads.
 */
public class Crawler {
    private final int maxToObtain;
    private final Set<String> obtained = new HashSet<>();
    private final Deque<String> toProcess = new ArrayDeque<>();
    private final PageFetcher fetcher;
    private final PageParser parser;

    /**
     * Constructs crawler
     *
     * @param startUrl    starting URL
     * @param maxToObtain maximum pages to obtain
     * @param fetcher     obtains reader and encoding for a page
     * @param parser      parses pages extracting page information
     */
    public Crawler(URL startUrl, int maxToObtain, PageFetcher fetcher, PageParser parser) {
        if (startUrl == null) {
            throw new IllegalArgumentException("startUrl shouldn't be null");
        }
        if (fetcher == null) {
            throw new IllegalArgumentException("fetcher shouldn't be null");
        }
        if (parser == null) {
            throw new IllegalArgumentException("parser shouldn't be null");
        }
        toProcess.add(startUrl.toString());
        this.maxToObtain = maxToObtain;
        this.fetcher = fetcher;
        this.parser = parser;
    }

    /**
     * Starts crawling process. Stops when maxToObtain amount of unique pages addresses obtained of when the processed
     * pages don't contain more links.
     */
    public void crawl() {
        while (!toProcess.isEmpty()) {
            String url = toProcess.pollFirst();
            URL pageUrl;
            Reader reader;
            PageFetcher.Result result;
            try {
                pageUrl = new URL(url);
                result = fetcher.fetch(pageUrl);
                reader = result.getReader();
            } catch (IOException | UnsupportedContentTypeException e) {
                // Do nothing
                continue;
            }

            Set<String> links;
            try {
                links = parser.parse(pageUrl, reader, result.getEncoding());
            } catch (IOException e) {
                // Do nothing
                continue;
            }
            for (String link : links) {
                if (obtained.size() >= maxToObtain) {
                    return;
                }
                if (obtained.add(link)) {
                    toProcess.addLast(link);
                }
            }
        }
    }

    /**
     * @return obtained pages information
     */
    public Set<String> getLinks() {
        return obtained;
    }

}
