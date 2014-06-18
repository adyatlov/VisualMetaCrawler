package org.bitbucket.dyatlov.crawler;

/**
 * Thrown when the content type of the web-document is not supported
 */
public class UnsupportedContentTypeException extends Exception {
    public UnsupportedContentTypeException(String s) {
        super(s);
    }
}
