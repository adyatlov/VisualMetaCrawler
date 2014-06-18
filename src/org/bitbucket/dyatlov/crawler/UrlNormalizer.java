package org.bitbucket.dyatlov.crawler;

import java.io.UnsupportedEncodingException;
import java.net.*;

/**
 * Created by Dyatlov on 18/06/2014.
 */
public class UrlNormalizer {
    /**
     * Tries to convert URL to a normalized (unify) format cutting
     * its fragment part and properly encode path and query parts.
     * @param url URL to convert
     * @param encoding original encoding of the document, UTF-8 if encoding == "" or null
     * @return normalized URL or null if the normalization wasn't successful
     */
    public static URL normalizeURL(URL url, String encoding) {
        if (url == null) {
            throw new IllegalArgumentException("url cannot be null");
        }
        if (encoding == null || encoding.isEmpty()) {
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
}
