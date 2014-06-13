package org.bitbucket.dyatlov.crawler;

import static org.testng.Assert.*;
import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;

import java.util.Collection;

public class LinkParserTest {

    @DataProvider
    public Object[][] data() {
        return new Object[][] {
                {"href<a HREF='http://google.com'>Google</a>", "http://google.com"},
                {"<a hreF = 'http://google.com'> Google </a>", "http://google.com"},
                {"<a HreF=\"google.com\">Go</a>", "google.com"},
                {"<a href = \"http://google.com\">gle</a>", "http://google.com"},
                {"blah blah<a style='blah' href = \"http://google.com\">blah blah</a><br>", "http://google.com"},
                {"blah blah<a style=blah href = http://google.com>blah blah</a><br>", "http://google.com"},
        };
    }

    @Test(dataProvider = "data")
    public void testParseSingle(String input, String expected) throws Exception {
        LinkParser parser = new LinkParser();
        Collection<String> links = parser.parse(input);
        assertEquals(links.size(), 1);
        assertEquals(links.iterator().next(), expected);
    }

    @Test
    public void testParseMultiple() throws Exception {
        LinkParser parser = new LinkParser();
        Object[][] inputArr = data();
        StringBuilder input = new StringBuilder();
        for (Object[] obj: inputArr) {
            input.append((String)obj[0]);
            input.append((String)obj[1]);
        }
        Collection<String> links = parser.parse(input);
        assertEquals(links.size(), inputArr.length);
        int i = 0;
        for (String link: links) {
            assertEquals(link, inputArr[i][1]);
            i++;
        }
    }

}