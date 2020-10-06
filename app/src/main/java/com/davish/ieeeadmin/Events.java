package com.davish.ieeeadmin;

public class Events {
    private String Title,hash,Soc;

    public Events() {}

    public Events(String title, String hash, String Soc) {
        Title = title;
        this.hash = hash;
        this.Soc = Soc;
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

    public String getSoc() {
        return Soc;
    }

    public void setSoc(String Soc) {
        this.Soc = Soc;
    }
}