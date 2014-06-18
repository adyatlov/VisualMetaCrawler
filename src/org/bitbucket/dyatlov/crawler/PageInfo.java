package org.bitbucket.dyatlov.crawler;

import java.net.URL;
import java.util.Collections;
import java.util.Set;

/**
 * Created by dyatlov on 17.06.14.
 */
public class PageInfo {
    private URL pageUrl;
    private String pageTitle;
    private Set<String> links;

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
