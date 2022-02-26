package com.anurag.therabeat;

public class Song {

    private String uri;
    private String name;
    private String artist;
    private String imageUrl;

    public Song(String uri, String name, String artist, String imagesUrl) {
        this.name = name;
        this.uri = uri;
        this.artist = artist;
        this.imageUrl = imagesUrl;
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

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
