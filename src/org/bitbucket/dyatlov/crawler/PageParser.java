package org.bitbucket.dyatlov.crawler;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import java.io.IOException;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Parses page in order to obtain links, page title and other useful information
 */
public class PageParser {

    private static URL makeAbsolute(URL base, String link) {
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

    /**
     * Returns information parsed from the page content
     *
     * @param pageUrl  URL of the document to fetch
     * @param reader   document reader
     * @param encoding original encoding of the document, UTF-8 if encoding == "" or null
     * @return absolute URL strings
     * @throws java.lang.IllegalArgumentException if reader or pageUrl are null
     * @see UrlNormalizer#normalizeURL(java.net.URL, String) normalizeURL
     */
    public Set<String> parse(URL pageUrl, Reader reader, String encoding) throws IOException {
        if (pageUrl == null || reader == null) {
            throw new IllegalArgumentException("reader an pageUrl shouldn't be null");
        }
        MyHTMLEditorKit kit = new MyHTMLEditorKit();
        HTMLEditorKit.Parser parser = kit.getParser();
        PageParserCallback callback = new PageParserCallback();
        parser.parse(reader, callback, true);

        HashSet<String> links = new HashSet<>(callback.getLinks().size());
        // According to http://www.w3.org/TR/html51/document-metadata.html#the-base-element
        // it is possible to have relative URL in href attribute of <base> tag.
        URL baseUrl = makeAbsolute(pageUrl, callback.getBaseLink());
        for (String link : callback.getLinks()) {
            URL url = makeAbsolute(baseUrl, link);
            // Collect only http links
            if (url != null) {
                if (!url.getProtocol().equals("http") && !url.getProtocol().equals("https")) {
                    continue;
                }
                url = UrlNormalizer.normalizeURL(url, encoding);
            }
            if (url != null) {
                links.add(url.toString());
            }
            // TODO(Dyatlov): add statistics
        }

        return links;
    }
}

// TODO(Dyatlov): Use some third-party parser in the production.
// Using this swing parser is not a good idea in general as it's old and can produce stack overflow.
// It is here just because of the requirements which restrict to use anything outside of SDK.
class PageParserCallback extends HTMLEditorKit.ParserCallback {
    private static final HashSet<HTML.Tag> CONSIDERED_TAGS;

    static {
        CONSIDERED_TAGS = new HashSet<>();
        CONSIDERED_TAGS.add(HTML.Tag.BASE);
        CONSIDERED_TAGS.add(HTML.Tag.A);
    }
    private String baseLink;
    private final ArrayList<String> links = new ArrayList<>();

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
        } else if (t.equals(HTML.Tag.BASE)) {
            handleBaseTag(a);
        }
    }

    @Override
    public void handleSimpleTag(HTML.Tag t, MutableAttributeSet a, int pos) {
        if (a.isDefined(HTML.Attribute.ENDTAG)) {
            return;
        }
        if (t.equals(HTML.Tag.BASE)) {
            handleBaseTag(a);
        }
    }

    private void handleBaseTag(MutableAttributeSet a) {
        // Use only first <base> tag
        if (baseLink == null) {
            baseLink = (String) a.getAttribute(HTML.Attribute.HREF);
        }
    }

    public String getBaseLink() {
        return baseLink;
    }

    public ArrayList<String> getLinks() {
        return links;
    }
}


// Just a trick to obtain the parser
class MyHTMLEditorKit extends HTMLEditorKit {
    public HTMLEditorKit.Parser getParser() {
        return super.getParser();
    }
}