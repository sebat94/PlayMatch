package com.dam.daniel.playmatch.models;

public class GenderPreference {

    private int user;
    private int gender;

    public GenderPreference() {
    }

    public GenderPreference(int user, int gender) {
        this.user = user;
        this.gender = gender;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "GenderPreference{" +
                "user=" + user +
                ", gender=" + gender +
                '}';
    }
}
