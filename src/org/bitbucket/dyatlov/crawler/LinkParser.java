package org.bitbucket.dyatlov.crawler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dyatlov on 12.06.14.
 */
public class LinkParser {
    static final Pattern LINK_PATTERN = Pattern.compile("\\s*(?i)href\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))");

    public Collection<String> parse(CharSequence sequence) {
        Matcher matcher = LINK_PATTERN.matcher(sequence);
        ArrayList<String> result = new ArrayList<String>();
        while (matcher.find()) {
            String link = matcher.group(1);
            result.add(link.substring(1, link.length()-1));
        }
        return result;
    }
}
