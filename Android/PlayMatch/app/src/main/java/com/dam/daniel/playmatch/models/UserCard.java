package com.dam.daniel.playmatch.models;

public class UserCard {
    public int id;
    public String name;
    public String city;
    public String url;

    public UserCard(){}

    public UserCard(int id, String name, String city, String url) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.url = url;
    }
}