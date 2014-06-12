package org.bitbucket.dyatlov.crawler;

import java.io.IOException;
import java.io.Reader;

/**
 * Created by Dyatlov on 12/06/2014.
 */
public abstract class Parser {
    public abstract String nextToken(Reader reader) throws IOException;
}
