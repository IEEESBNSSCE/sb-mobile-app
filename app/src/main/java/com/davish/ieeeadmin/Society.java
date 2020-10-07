package com.davish.ieeeadmin;

public class Society {
    private String Name,Imageurl,hash;


    public Society() {}

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Society(String name, String imageurl, String hash) {
        Name = name;
        Imageurl = imageurl;
        this.hash = hash;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImageurl() {
        return Imageurl;
    }

    public void setImageurl(String imageurl) {
        Imageurl = imageurl;
    }
}