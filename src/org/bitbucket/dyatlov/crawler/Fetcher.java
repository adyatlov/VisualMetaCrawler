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
 * Created by Dyatlov on 12/06/2014.
 */
public class Fetcher {
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

    public static class Result {
        private Reader reader;
        private String encoding;

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
