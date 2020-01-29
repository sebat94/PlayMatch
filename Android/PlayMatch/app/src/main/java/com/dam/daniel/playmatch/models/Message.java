package com.dam.daniel.playmatch.models;

public class Message {

    private int id;
    private int chat;
    private int userSender;
    private int userReceiver;
    private String message;
    private String date;

    public Message(){ }

    public Message(int id, int chat, int userSender, int userReceiver, String message, String date) {
        this.id = id;
        this.chat = chat;
        this.userSender = userSender;
        this.userReceiver = userReceiver;
        this.message = message;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getChat() {
        return chat;
    }

    public void setChat(int chat) {
        this.chat = chat;
    }

    public int getUserSender() {
        return userSender;
    }

    public void setUserSender(int userSender) {
        this.userSender = userSender;
    }

    public int getUserReceiver() {
        return userReceiver;
    }

    public void setUserReceiver(int userReceiver) {
        this.userReceiver = userReceiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", chat=" + chat +
                ", userSender=" + userSender +
                ", userReceiver=" + userReceiver +
                ", message='" + message + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
