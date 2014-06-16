package org.bitbucket.dyatlov.crawler;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;



public class LinkParserTest {

    @DataProvider
    public String[][] data() {
        return new String[][] {
                {"http://example.com",      "href<a HREF='http://example.com/#test'>Example</a>",
                        "http://example.com/"},
                {"http://example.com",      "<a hreF = 'http://example.com'> Example </a>",
                        "http://example.com"},
                {"http://example.com",      "<a href = \"http://example.com\">gle</a>",
                        "http://example.com"},
                {"http://example.com",      "blah blah<a style='blah' href = \"http://example.com\">blah blah</a><br>",
                        "http://example.com"},
                {"http://example.com",      "blah blah<a style=blah href = http://example.com>blah blah</a><br>",
                        "http://example.com"},

                {"http://example.com/path/", "<a href=\"relative path\">Example</a>",
                        "http://example.com/path/relative%20path"}   ,
                {"http://example.com/path/rel", "<a href=\"relative path\">Example</a>",
                        "http://example.com/path/relative%20path"}   ,
                {"http://example.com/path/", "<a href=\"/absolute path\">Example</a>",
                        "http://example.com/absolute%20path"},

                {"http://example.com",      "<a href='http://wikipedia.org'>Example</a>",
                        "http://wikipedia.org"},
        };
    }

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
        LinkParser parser = new LinkParser();
        URL url = parser.normalizeURL(new URL(input), null);
        assertEquals(url.toString(), expected);
    }

    @Test(dataProvider = "data")
    public void testParseSingle(String baseUrl, String input, String expected) throws Exception {
        LinkParser parser = new LinkParser();
        Set<String> links = parser.parse(new URL(baseUrl), input, null);
        assertEquals(links.size(), 1);
        assertEquals(links.iterator().next(), expected);
    }

    @Test
    public void testParseMultiple() throws Exception {
        LinkParser parser = new LinkParser();
        Object[][] inputArr = data();
        StringBuilder input = new StringBuilder();
        for (Object[] obj: inputArr) {
            input.append((String)obj[1]);
        }
        Set<String> links = parser.parse(new URL("http://example.com"), input, null);
        System.out.println(Arrays.toString(links.toArray()));
        Set<String> expected = new HashSet<>();
        expected.add("http://example.com");
        expected.add("http://example.com/absolute%20path");
        expected.add("http://example.com/");
        expected.add("http://example.com/relative%20path");
        expected.add("http://wikipedia.org");
        assertEquals(links, expected);
    }

}