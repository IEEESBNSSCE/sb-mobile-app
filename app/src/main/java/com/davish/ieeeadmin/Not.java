package com.davish.ieeeadmin;

public class Not {
    private String Body,ImageUrl,Title,hash;


    public Not() {}

    public Not(String body, String imageUrl, String title, String hash) {
        Body = body;
        ImageUrl = imageUrl;
        Title = title;
        this.hash = hash;
    }

    public String getBody() {
        return Body;
    }

    public void setBody(String body) {
        Body = body;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}