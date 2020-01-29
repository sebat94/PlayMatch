package com.dam.daniel.playmatch.models;

public class ChatResponse {

    private Chat chat;
    private Message message;
    private User user;

    public ChatResponse() { }

    public ChatResponse(Chat chat, Message message, User user) {
        this.chat = chat;
        this.message = message;
        this.user = user;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "ChatResponse{" +
                "chat=" + chat +
                ", message=" + message +
                ", user=" + user +
                '}';
    }
}
