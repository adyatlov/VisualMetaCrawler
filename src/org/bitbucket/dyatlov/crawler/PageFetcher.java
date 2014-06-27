package org.bitbucket.dyatlov.crawler;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Obtains page content reader and encoding. Supports only text-based mime-types.
 */
public class PageFetcher {
    /**
     * Returns content reader and encoding
     * @param url ULR of the page
     * @return page information and encoding pair
     * @throws IOException when something went wrong with the connection of so
     * @throws UnsupportedContentTypeException when the mime-types of the addressed document is not text-based .
     */
    public Result fetch(URL url) throws IOException, UnsupportedContentTypeException {
        URLConnection connection = url.openConnection();
        connection.connect();
        String encoding = connection.getContentEncoding();
        if (encoding == null || encoding.isEmpty()) {
            encoding = "UTF-8";
        }
        MimeType mimeType;
        try {
            String contentType = connection.getContentType();
            if (contentType == null) {
                throw new UnsupportedContentTypeException("Content type is null");
            }
            mimeType = new MimeType(contentType);
        } catch (MimeTypeParseException e) {
            throw new UnsupportedContentTypeException("Cannot fetch mime type string");
        }
        if (!"text".equalsIgnoreCase(mimeType.getPrimaryType())) {
            throw new UnsupportedContentTypeException("Mime type " + mimeType.toString() + " is not supported");
        }
        return new Result(new BufferedReader(new InputStreamReader(connection.getInputStream())), encoding);
    }

    /**
     * Simple container for which contains page content reader and page encoding
     */
    public static class Result {
        private final Reader reader;
        private final String encoding;

        public Result(Reader reader, String encoding) {
            this.reader = reader;
            this.encoding = encoding;
        }

        public Reader getReader() {
            return reader;
        }

        public String getEncoding() {
            return encoding;
        }
    }
}
