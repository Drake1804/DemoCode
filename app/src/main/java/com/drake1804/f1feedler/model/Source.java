package com.drake1804.f1feedler.model;

import org.jsoup.nodes.Document;

/**
 * Created by Pavel.Shkaran on 5/17/2016.
 */
public class Source {

    private String name;
    private String url;
    private Document document;

    public Source(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }
}
