package com.dam.daniel.playmatch.models;

public class Favourite {

    private int user1;
    private int user2;

    public Favourite() {}

    public Favourite(int user1, int user2) {
        this.user1 = user1;
        this.user2 = user2;
    }

    public int getUser1() {
        return user1;
    }

    public void setUser1(int user1) {
        this.user1 = user1;
    }

    public int getUser2() {
        return user2;
    }

    public void setUser2(int user2) {
        this.user2 = user2;
    }
}
