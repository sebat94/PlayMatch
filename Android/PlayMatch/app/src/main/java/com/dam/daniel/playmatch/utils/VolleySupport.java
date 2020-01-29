package com.dam.daniel.playmatch.utils;

import com.dam.daniel.playmatch.models.ChatResponse;
import com.dam.daniel.playmatch.models.Message;
import com.dam.daniel.playmatch.models.User;

import java.util.List;

/**
 * Volley Interfaces For Support
 *      This callback don't return a value, but can do it.
 *      It's used to call it from our 'Activity', passing the callback as a parameter, and waiting to receive the answer from our service
 * */
public class VolleySupport {


    /**
     * Handles a List of Users
     * */
    public interface ServerCallbackUserList{
        void onSuccess(List<User> result);
    }

    /**
     * Handles a User
     * */
    public interface ServerCallbackUser{
        void onSuccess(User result);
    }

    /**
     * Handles a List of Chats
     */
    public interface ServerCallbackChatList{
        void onSuccess(List<ChatResponse> result);
    }

    /**
     * Handles a List of Chats
     */
    public interface ServerCallbackMessageList{
        void onSuccess(List<Message> result);
    }

    /**
     * Handles errors from server
     * */
    public interface ServerCalbackError{
        void onError(String errorMessage);
    }

    /**
     * Handles Info Message
     */
    public interface ServerCallbackMessageInfo{
        void onSuccess(String infoMessage);
    }

    /**
     * Handles Empty Success Response
     */
    public interface ServerCallbackEmpty{
        void onSuccess();
    }

}
