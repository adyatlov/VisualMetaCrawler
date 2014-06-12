package org.bitbucket.dyatlov.crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Dyatlov on 12/06/2014.
 */
public class Crawler {
    public Crawler(URL url, Parser parser) {
        this.url = url;
        this.parser = parser;
        this.reader = null;
    }

    boolean connect() throws IOException {
        if (isConnected()) {
            return true;
        }
        URLConnection connection = url.openConnection();
        if ("text/html".equalsIgnoreCase(connection.getContentType())) {
            return false;
        }
        reader = new BufferedReader(new InputStreamReader(connection.getInputStream(),
                                                          connection.getContentEncoding()));
        return true;
    }

    public String parseNext() throws IOException {
        if (!isConnected()) {
            throw new IllegalStateException("Not connected");
        }
        return parser.nextToken(reader);
    }

    public boolean isConnected() {
        return reader != null;
    }

    private final URL url;
    private final Parser parser;
    private Reader reader;
}
