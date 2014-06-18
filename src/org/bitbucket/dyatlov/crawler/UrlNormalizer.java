package org.bitbucket.dyatlov.crawler;

import java.io.UnsupportedEncodingException;
import java.net.*;

/**
 * Created by Dyatlov on 18/06/2014.
 */
public class UrlNormalizer {
    /**
     * Normalizes URL
     * <p>
     * Converts the scheme and host to lower case
     * HTTP://www.Example.com/ to http://www.example.com/
     * <p>
     * Capitalizes letters in escape sequences.
     * http://www.example.com/a%5db to http://www.example.com/a%5Db
     * <p>
     * Decodes percent-encoded octets of unreserved characters (~)
     * http://www.example.com/%7Eusername/ to http://www.example.com/~username/
     * <p>
     * Removes the default port
     * http://www.example.com:80/bar.html to http://www.example.com/bar.html
     * https://www.example.com:443/bar.html to https://www.example.com/bar.html
     * <p>
     * Removes the fragment
     * http://www.example.com/bar.html#section1 to http://www.example.com/bar.html
     * <p>
     * Replaces spaces with %20
     * http://www.example.com/a b?c d=e f to http://www.example.com/a%20b?c%20d=e%20f
     * <p>
     * Replaces "+" with %20
     * http://www.example.com/a+b?c+d=e+f to http://www.example.com/a%20b?c%20d=e%20f
     *
     * @param url      URL to convert
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
                    (url.getPath() == null) ? null : URLDecoder.decode(url.getPath(), encoding),
                    (url.getQuery() == null) ? null : URLDecoder.decode(url.getQuery(), encoding),
                    null /*fragment*/);
            return uri.toURL();
        } catch (UnsupportedEncodingException | MalformedURLException | URISyntaxException e) {
            return null;
        }
    }
}
