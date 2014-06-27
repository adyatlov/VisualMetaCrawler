package org.bitbucket.dyatlov.crawler;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.StringReader;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.testng.Assert.assertEquals;



public class PageParserTest {

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

    @Test(dataProvider = "data")
    public void testParseSingle(String baseUrl, String input, String expected) throws Exception {
        PageParser parser = new PageParser();
        Set<String> links = parser.parse(new URL(baseUrl), new StringReader(input), null);
        assertEquals(links.size(), 1);
        assertEquals(links.iterator().next(), expected);
    }

    @Test
    public void testParseMultiple() throws Exception {
        Object[][] inputArr = data();
        StringBuilder input = new StringBuilder();
        input.append("<base href='http://example.com'>");
        for (Object[] obj: inputArr) {
            input.append((String)obj[1]);
        }
        PageParser parser = new PageParser();
        Set<String>links = parser.parse(new URL("http://example.com/path/"), new StringReader(input.toString()), null);
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