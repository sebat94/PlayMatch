package com.dam.daniel.playmatch.models;

import com.google.gson.annotations.Expose;

public class Chat {

    @Expose private int id;
    @Expose private int matchUser1;
    @Expose private int matchUser2;

    public Chat() {}

    public Chat(int id, int matchUser1, int matchUser2) {
        this.id = id;
        this.matchUser1 = matchUser1;
        this.matchUser2 = matchUser2;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMatchUser1() {
        return matchUser1;
    }

    public void setMatchUser1(int matchUser1) {
        this.matchUser1 = matchUser1;
    }

    public int getMatchUser2() {
        return matchUser2;
    }

    public void setMatchUser2(int matchUser2) {
        this.matchUser2 = matchUser2;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "id=" + id +
                ", matchUser1=" + matchUser1 +
                ", matchUser2=" + matchUser2 +
                '}';
    }
}
