package org.bitbucket.dyatlov.crawler;

import java.net.URL;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

public class FetcherTest {

    @Test
    public void testFetch() throws Exception {
        Fetcher fetcher = new Fetcher();
        CharSequence sequence =  fetcher.fetch(new URL("http://google.com"));
        assertTrue(sequence.toString().contains("google"));
    }
}