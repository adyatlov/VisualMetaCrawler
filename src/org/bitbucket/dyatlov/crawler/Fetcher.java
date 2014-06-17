package org.bitbucket.dyatlov.crawler;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Dyatlov on 12/06/2014.
 */
public class Fetcher {
    public CharSequence fetch(URL url) throws IOException, UnsupportedContentTypeException {
        URLConnection connection = url.openConnection();
        connection.connect();
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
        Reader reader = new InputStreamReader(connection.getInputStream());
        StringBuilder stringBuilder = new StringBuilder();
        char[] buff = new char[8192];
        for (int len = reader.read(buff); len != -1; len = reader.read(buff)) {
            stringBuilder.append(buff, 0, len);
        }
        return stringBuilder;
    }
}
