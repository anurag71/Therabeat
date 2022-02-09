package com.anurag.therabeat;

public class Song {

    private String uri;
    private String name;

    public Song(String uri, String name) {
        this.name = name;
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
