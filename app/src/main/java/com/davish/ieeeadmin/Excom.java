package com.davish.ieeeadmin;

public class Excom {
    private String Name,Position,ImageUrl,hash;

    public Excom() {}


    public Excom(String name, String position, String image, String hash) {
        Name = name;
        Position = position;
        ImageUrl = image;
        this.hash = hash;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPosition() {
        return Position;
    }

    public void setPosition(String position) {
        Position = position;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }
}