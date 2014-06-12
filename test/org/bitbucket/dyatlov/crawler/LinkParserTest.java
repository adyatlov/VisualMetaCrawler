package org.bitbucket.dyatlov.crawler;

import static org.testng.Assert.*;
import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;

import java.util.Collection;

public class LinkParserTest {

    @DataProvider
    public Object[][] data() {
        return new Object[][] {
                {"<a href='http://google.com'>", "http://google.com"},
                {"<a href = 'http://google.com'>", "http://google.com"},
                {"<a href=\"http://google.com\">", "http://google.com"},
                {"<a href = \"http://google.com\">", "http://google.com"},
                {"blah blah<a style='blah' href = \"http://google.com\">blah blah<br>", "http://google.com"},
        };
    }

    @Test(dataProvider = "data")
    public void testParse(String input, String expected) throws Exception {
        LinkParser parser = new LinkParser();
        Collection<String> links = parser.parse(input);
        assertEquals(1, links.size());
        assertEquals(expected, links.iterator().next());
    }
}