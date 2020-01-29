package com.dam.daniel.playmatch.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.dam.daniel.playmatch.models.User;
import com.dam.daniel.playmatch.models.BaseResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class LocalSorage {

    /**
     * Set Shared Preferences - Save Token and User (If it's sended)
     * @param context
     * @param token
     * @param user
     */
    public static void saveDataInSharedPreferences (Context context, String token, User user){
        SharedPreferences myPrefs = context.getSharedPreferences("localStorage", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putString("token", token);
        if(user != null){
            //Gson gson = new Gson();
            Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().create();           // This will serialize NaN and Infinity, but not as strings.
            String userJson = gson.toJson(user);
            editor.putString("user", userJson);
        }
        editor.apply();
        editor.commit();
    }

    /**
     * Get Shared Preferences
     * @param context
     * @return
     */
    public static BaseResponse loadDataFromSharedPreferences(Context context){
        SharedPreferences myPrefs = context.getSharedPreferences("localStorage", Context.MODE_PRIVATE);
        BaseResponse baseResponse = new BaseResponse();
        String tokenString = myPrefs.getString("token", null);
        baseResponse.setToken(tokenString);
        if(myPrefs.contains("user")){
            // To Retrieve Gson
            Gson gson = new Gson();
            String userJsonString = myPrefs.getString("user", null);
            User userJson = gson.fromJson(userJsonString, User.class);
            baseResponse.setUser(userJson);
        }
        return baseResponse;
    }

    /**
     * Delete All Data From Shared Preferences
     * @param context
     */
    public static void deleteDataFromSharedPreferences(Context context){
        SharedPreferences myPrefs = context.getSharedPreferences("localStorage", Context.MODE_PRIVATE);
        myPrefs.edit().clear().apply();
    }

}
