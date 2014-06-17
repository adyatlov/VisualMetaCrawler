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
     * @param baseUrl URL of the document to fetch
     * @param encoding original encoding of the document, UTF-8 if encoding == "" or null
     * @return PageInfo object containing information about the page
     */
    public PageInfo parse(URL baseUrl, Reader reader, String encoding) throws IOException {
        HashSet<String> links = new HashSet<>();
        MyHTMLEditorKit kit = new MyHTMLEditorKit();
        HTMLEditorKit.Parser parser =  kit.getParser();
        PageInfoCallback pageInfoCallback = new PageInfoCallback();
        parser.parse(reader,pageInfoCallback, true);
        //TODO(Dyatlov): Implement tomorrow
        PageInfo pageInfo = new PageInfo();
        return pageInfo;
    }
}

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

class MyHTMLEditorKit extends HTMLEditorKit {
    public HTMLEditorKit.Parser getParser() {
        return super.getParser();
    }
}