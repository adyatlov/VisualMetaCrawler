package org.bitbucket.dyatlov.crawler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dyatlov on 12.06.14.
 */
public class LinkParser {
    static final Pattern LINK_PATTERN = Pattern.compile("\\s*(?i)href\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))");
    private URL baseURL;
    public Collection<String> parse(CharSequence sequence) {
        Matcher matcher = LINK_PATTERN.matcher(sequence);
        ArrayList<String> links = new ArrayList<String>();
        while (matcher.find()) {
            String link = matcher.group(1);
            link = link.replaceAll("\"|'", "");
            // Try to make URL
            URL url;
            try {
                url = new URL(baseURL, link);
            } catch (MalformedURLException e) {
                // Consider link as relative
                url = new URL();
            }
            links.add(link);
        }
        return result;
    }
}
