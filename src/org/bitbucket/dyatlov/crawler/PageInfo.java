package org.bitbucket.dyatlov.crawler;

import java.net.URL;
import java.util.Collections;
import java.util.Set;

/**
 * Page information container
 * TODO(Dyatlov) might be extended with key-words and lists of pages which refer this page
 */
public class PageInfo {
    private URL pageUrl;
    private String pageTitle;
    private Set<String> links;

    /**
     * Constructs PageInfo class
     *
     * @param pageUrl   URL of the page
     * @param pageTitle title of the page (tag <title>)
     * @param links     a set of absolute URLs on the page
     */
    public PageInfo(URL pageUrl, String pageTitle, Set<String> links) {
        if (pageUrl == null) {
            throw new IllegalArgumentException("pageUrl shouldn't be null");
        }
        this.pageUrl = pageUrl;
        this.pageTitle = pageTitle != null ? pageTitle : "";
        this.links = links != null ? links : Collections.emptySet();
    }

    public URL getPageUrl() {
        return pageUrl;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public Set<String> getLinks() {
        return links;
    }
}
