package org.bitbucket.dyatlov.crawler;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.net.URL;

import static org.testng.Assert.assertEquals;

public class UrlNormalizerTest {
    @DataProvider
    public Object[][] normalizeData() {
        return new Object[][] {
                // Converting the scheme and host to lower case
                {"HTTP://www.Example.com/",                     "http://www.example.com/"},
                // Capitalizing letters in escape sequences.
                {"http://www.example.com/a%5db",                "http://www.example.com/a%5Db"},
                // Decoding percent-encoded octets of unreserved characters (~)
                {"http://www.example.com/%7Eusername/",         "http://www.example.com/~username/"},
                // Removing the default port
                {"http://www.example.com:80/bar.html",          "http://www.example.com/bar.html"},
                {"https://www.example.com:443/bar.html",        "https://www.example.com/bar.html"},
                // Removing the fragment
                {"http://www.example.com/bar.html#section1",    "http://www.example.com/bar.html"},
                // Replace spaces with %20
                {"http://www.example.com/a b?c d=e f",          "http://www.example.com/a%20b?c%20d=e%20f"},
                // Replace "+" with %20
                {"http://www.example.com/a+b?c+d=e+f",          "http://www.example.com/a%20b?c%20d=e%20f"},
        };
    }

    @Test(dataProvider = "normalizeData")
    public void testNormalizeUrl(String input, String expected) throws Exception {
        URL url = UrlNormalizer.normalizeURL(new URL(input), null);
        assertEquals(url.toString(), expected);
    }
}
