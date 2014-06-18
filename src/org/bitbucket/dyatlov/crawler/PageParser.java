package org.bitbucket.dyatlov.crawler;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.*;

/**
 * Created by dyatlov on 12.06.14.
 */
public class PageParser {

    /**
     * Tries to convert URL to a normalize (unify) format cutting
     * its fragment part and properly encode path and query parts.
     * @param url URL to convert
     * @param encoding original encoding of the document, UTF-8 if encoding == "" or null
     * @return normalized URL or null if the normalization wasn't successful
     */
    public URL normalizeURL(URL url, String encoding) {
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

    /**
     * Returns unique normalized URLs
     * @see PageParser#normalizeURL(java.net.URL, String) normalizeURL
     * @param pageUrl URL of the document to fetch
     * @param reader document reader
     * @param encoding original encoding of the document, UTF-8 if encoding == "" or null
     * @return PageInfo object containing information about the page
     * @throws java.lang.IllegalArgumentException if reader or pageUrl are null
     */
    public PageInfo parse(URL pageUrl, Reader reader, String encoding) throws IOException {
        if (pageUrl == null || reader == null) {
            throw new IllegalArgumentException("reader an pageUrl shouldn't be null");
        }
        MyHTMLEditorKit kit = new MyHTMLEditorKit();
        HTMLEditorKit.Parser parser =  kit.getParser();
        PageInfoCallback pageInfoCallback = new PageInfoCallback();
        parser.parse(reader,pageInfoCallback, true);

        HashSet<String> links = new HashSet<>(pageInfoCallback.getLinks().size());
        // According to http://www.w3.org/TR/html51/document-metadata.html#the-base-element
        // it is possible to have relative URL in href attribute of <base> tag.
        URL baseUrl = makeAbsolute(pageUrl, pageInfoCallback.getBaseLink());
        for (String link: pageInfoCallback.getLinks()) {
            URL url =  makeAbsolute(baseUrl, link);
            if (url != null) {
                url = normalizeURL(url, encoding);
            }
            if (url != null) {
                links.add(url.toString());
            }
            // TODO(Dyatlov): add statistics
        }

        return new PageInfo(pageUrl, pageInfoCallback.getTitle(), links);
    }

    private URL makeAbsolute(URL base, String link) {
        if (base == null) {
            throw new IllegalArgumentException("base URL shouldn't be null");
        }
        if (link == null) {
            return base;
        }
        try {
            return new URL(base, link);
        } catch (MalformedURLException e) {
            return null;
        }
    }
}

// TODO(Dyatlov): Use some third-party parser in the production.
// Using this swing parser is not a good idea in general.
// It is here just because of the requirements which restrict to use anything outside of SDK.
class PageInfoCallback extends HTMLEditorKit.ParserCallback {
    private static final HashSet<HTML.Tag> CONSIDERED_TAGS;
    static {
        CONSIDERED_TAGS = new HashSet<>();
        CONSIDERED_TAGS.add(HTML.Tag.BASE);
        CONSIDERED_TAGS.add(HTML.Tag.TITLE);
        CONSIDERED_TAGS.add(HTML.Tag.A);
    }
    private String baseLink;
    private String title;
    private ArrayList<String> links = new ArrayList<>();

    // It supposed to be stack but we have to track only one element with text so far.
    boolean inTitle = false;

    @Override
    public void handleStartTag(HTML.Tag t, MutableAttributeSet a, int pos) {
        if (!CONSIDERED_TAGS.contains(t)) {
            return;
        }

        if (t.equals(HTML.Tag.A)) {
            String link = (String) a.getAttribute(HTML.Attribute.HREF);
            if (link != null) {
                links.add(link);
            }
        } else if (t.equals(HTML.Tag.TITLE)) {
            inTitle = true;
        } else if (t.equals(HTML.Tag.BASE)) {
            handleBaseTag(t, a);
        }
    }

    @Override
    public void handleText(char[] data, int pos) {
        if (inTitle) {
            title = new String(data);
        }
    }

    @Override
    public void handleEndTag(HTML.Tag t, int pos) {
        if (t.equals(HTML.Tag.TITLE)) {
            inTitle = false;
        }
    }

    @Override
    public void handleSimpleTag(HTML.Tag t, MutableAttributeSet a, int pos) {
        if (a.isDefined(HTML.Attribute.ENDTAG)) {
            return;
        }
        if (t.equals(HTML.Tag.BASE)) {
            handleBaseTag(t, a);
        }
    }

    private void handleBaseTag(HTML.Tag t, MutableAttributeSet a) {
        // Use only first <base> tag
        if (baseLink == null) {
            title = (String) a.getAttribute(HTML.Attribute.HREF);
        }
    }

    public String getBaseLink() {
        return baseLink;
    }

    public String getTitle() {
        return title;
    }

    public ArrayList<String> getLinks() {
        return links;
    }
}


// Just a trick to obtain parser
class MyHTMLEditorKit extends HTMLEditorKit {
    public HTMLEditorKit.Parser getParser() {
        return super.getParser();
    }
}