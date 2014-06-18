package org.bitbucket.dyatlov.crawler;

import java.io.Reader;
import java.net.URL;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

public class FetcherTest {

    @Test
    public void testFetch() throws Exception {
        Fetcher fetcher = new Fetcher();
        Reader reader =  fetcher.fetch(new URL("http://example.com"));

        StringBuilder stringBuilder = new StringBuilder();
        char[] buff = new char[8192];
        for (int len = reader.read(buff); len != -1; len = reader.read(buff)) {
            stringBuilder.append(buff, 0, len);
        }
        assertTrue(stringBuilder.toString().contains("example"));
    }
}