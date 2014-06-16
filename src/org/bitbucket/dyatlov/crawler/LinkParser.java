package org.bitbucket.dyatlov.crawler;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dyatlov on 12.06.14.
 */
public class LinkParser {
    static final Pattern LINK_PATTERN = Pattern.compile("\\s*(?i)href\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))");

    /**
     * Tries to convert URL to a normalize (unify) format cutting
     * its fragment part and properly encode path and query parts.
     * @param url URL to convert
     * @param encoding original encoding of the document, UTF-8 if encoding == "" or null
     * @return normalized URL or null if the normalization wasn't successful
     */
    public URL normalizeURL(URL url, String encoding) {
        if (encoding == null || "".equals(encoding)) {
            encoding = "UTF-8";
        }
        try {
            // Removing the default port
            int port = url.getPort();
            if (port == url.getDefaultPort()) {
                port = -1;
            }

            URI uri = new URI(url.getProtocol(),
                    null /*userInfo*/,
                    url.getHost().toLowerCase(),
                    port,
                    (url.getPath()  == null) ? null : URLDecoder.decode(url.getPath(), encoding),
                    (url.getQuery() == null) ? null : URLDecoder.decode(url.getQuery(), encoding),
                    null /*fragment*/);
            return uri.toURL();
        } catch (UnsupportedEncodingException | MalformedURLException | URISyntaxException e) {
            return null;
        }
    }

    /**
     * Returns unique normalized URLs
     * @see org.bitbucket.dyatlov.crawler.LinkParser#normalizeURL(java.net.URL, String) normalizeURL
     * @param baseUrl URL of the document to parse
     * @param sequence content of the document
     * @param encoding original encoding of the document, UTF-8 if encoding == "" or null
     * @return unique normalized URLs
     */
    public Set<String> parse(URL baseUrl, CharSequence sequence, String encoding) {
        Matcher matcher = LINK_PATTERN.matcher(sequence);
        HashSet<String> links = new HashSet<>();
        while (matcher.find()) {
            String link = matcher.group(1);
            link = link.replaceAll("\"|'", "");
            // Try to make URL
            URL url;
            try {
                url = new URL(baseUrl, link);
            } catch (MalformedURLException e) {
                // Drop current link if it's malformed.
                continue;
            }
            if (!url.getProtocol().equals("http")) {
                // Drop current link if it's not HTTP link.
                continue;
            }
            url = normalizeURL(url, encoding);
            if (url == null) {
                // Drop current link if cannot normalize.
                continue;
            }
            links.add(url.toString());
        }
        return links;
    }
}
