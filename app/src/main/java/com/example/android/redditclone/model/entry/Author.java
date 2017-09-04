package com.example.android.redditclone.model.entry;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.io.Serializable;

/**
 * Created by shash on 8/12/2017.
 */

@Root(name = "author",strict = false)
public class Author implements Serializable {

    @Element(name = "name")
    private String name;

    @Element(required = false,name = "uri")
    private String uri;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return uri;
    }

    public void setUrl(String url) {
        this.uri = url;
    }

    @Override
    public String toString() {
        return "Author{" +
                "name='" + name + '\'' +
                ", url='" + uri + '\'' +
                '}';
    }
}
