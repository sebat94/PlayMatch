package com.dam.daniel.playmatch.models;

public class Image {

    private int id;
    private String url;
    private int user;
    private boolean isProfile;
    private String eliminationDate;

    public Image() {}

    public Image(int id, String url, int user, boolean isProfile, String eliminationDate) {
        this.id = id;
        this.url = url;
        this.user = user;
        this.isProfile = isProfile;
        this.eliminationDate = eliminationDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public boolean isProfile() {
        return isProfile;
    }

    public void setProfile(boolean profile) {
        isProfile = profile;
    }

    public String getEliminationDate() {
        return eliminationDate;
    }

    public void setEliminationDate(String eliminationDate) {
        this.eliminationDate = eliminationDate;
    }

    @Override
    public String toString() {
        return "Image{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", user=" + user +
                ", isProfile=" + isProfile +
                ", eliminationDate=" + eliminationDate +
                '}';
    }
}
