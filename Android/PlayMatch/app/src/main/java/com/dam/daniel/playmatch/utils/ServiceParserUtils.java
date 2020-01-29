package com.dam.daniel.playmatch.utils;

import com.dam.daniel.playmatch.models.Chat;
import com.dam.daniel.playmatch.models.ChatResponse;
import com.dam.daniel.playmatch.models.GenderPreference;
import com.dam.daniel.playmatch.models.Image;
import com.dam.daniel.playmatch.models.Message;
import com.dam.daniel.playmatch.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.dam.daniel.playmatch.utils.Constants.IMAGES_URL;
import static com.dam.daniel.playmatch.utils.Constants.SERVER_URL;

public class ServiceParserUtils {

    /*-- ************************************************************************************** --*/
    /*--                                      UserService                                       --*/
    /*-- ************************************************************************************** --*/
    /**
     * Parse JSONArray to List<User>
     * */
    public static List<User> parseJsonUserList(JSONObject jsonObject){

        List<User> usersList = new ArrayList<>();
        JSONArray jsonArray;

        try {
            jsonArray = jsonObject.getJSONArray("users");
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject object = jsonArray.getJSONObject(i);
                usersList.add(parseJsonUser(object));
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return usersList;
    }

    /**
     * Parse JSONObject to User
     */
    public static User parseJsonUser(JSONObject object){
        User user = null;
        try {
            // Parse JSONArray to List<image> And List<GenderPreference>
            List<Image> images = parseJsonImage( object.getJSONArray("images") );
            List<GenderPreference> genderPreferences = parseJsonGenderPreferences( object.getJSONArray("genderPreferences") );

            user = new User(
                    object.optInt("id"),
                    object.getString("telephoneNumber"),
                    object.getString("nick"),
                    object.getString("birthdate"),
                    object.optInt("gender"),
                    object.getString("email"),
                    object.optDouble("lat"),
                    object.optDouble("lng"),
                    object.getString("city"),
                    object.getString("description"),
                    object.getString("job"),
                    object.getString("company"),
                    object.getString("school"),
                    object.optInt("maxDistancePreference"),
                    object.optInt("minAgePreference"),
                    object.optInt("maxAgePreference"),
                    (object.optInt("active") == 1),
                    object.getString("disabled"),
                    images,
                    genderPreferences
            );
        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("Error de parsing: "+ e.getMessage());
        }
        return user;
    }

    /**
     * Parse Image Array Of User
     */
    public static List<Image> parseJsonImage(JSONArray jsonArray){
        List<Image> images = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++){
                JSONObject imageObject = jsonArray.getJSONObject(i);
                Image image = new Image();
                image.setId(imageObject.optInt("id"));
                image.setUrl(imageObject.getString("url"));
                image.setUser(imageObject.optInt("user"));
                image.setProfile((imageObject.optInt("isProfile") == 1));
                image.setEliminationDate(imageObject.getString("eliminationDate"));
                images.add(image);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return images;
    }

    /*-- ************************************************************************************** --*/
    /*--                                      GenderService                                     --*/
    /*-- ************************************************************************************** --*/
    /**
     * Parse Image Array Of User
     */
    public static List<GenderPreference> parseJsonGenderPreferences(JSONArray jsonArray){
        List<GenderPreference> genderPreferences = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++){
                JSONObject genderPreferencesObject = jsonArray.getJSONObject(i);
                GenderPreference genderPreference = new GenderPreference();
                genderPreference.setUser(genderPreferencesObject.optInt("user"));
                genderPreference.setGender(genderPreferencesObject.optInt("gender"));
                genderPreferences.add(genderPreference);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return genderPreferences;
    }

    /*-- ************************************************************************************** --*/
    /*--                                      ChatService                                       --*/
    /*-- ************************************************************************************** --*/
    /**
     * Parse JSONArray to List<User>
     * */
    public static List<ChatResponse> parseJsonChatResponseList(JSONObject jsonObject){
        List<ChatResponse> chatResponseList = new ArrayList<>();
        JSONArray jsonArray;

        try {
            // Recogemos el array de la propiedad 'chats' del objeto, y recorremos cada elemento uno parseándolo
            jsonArray = jsonObject.getJSONArray("chats");
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject object = jsonArray.getJSONObject(i);
                chatResponseList.add( parseJsonChatResponse(object) );
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return chatResponseList;
    }

    /**
     * Parse JSONObject to ChatResponse
     * */
    public static ChatResponse parseJsonChatResponse(JSONObject object){
        ChatResponse chatResponse = null;
        try {
            // Parse Each Parameter Of Response Body And Create Object ChatResponse
            Chat chat = parseJsonChat(object.getJSONObject("chat"));
            Message message = null;
            if(!object.isNull("message")) message = parseJsonMessage(object.getJSONObject("message"));
            User user = parseJsonUser(object.getJSONObject("user"));

            chatResponse = new ChatResponse(
                    chat,
                    message,
                    user
            );
        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("Error de parsing: "+ e.getMessage());
        }
        return chatResponse;
    }

    /**
     * Parse JSONObject to Chat
     */
    public static Chat parseJsonChat(JSONObject object){
        Chat chat = null;
        try {
            chat = new Chat(
                    object.getInt("id"),
                    object.getInt("matchUser1"),
                    object.getInt("matchUser2")
            );
        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("Error de parsing: "+ e.getMessage());
        }
        return chat;
    }

    /**
     * Parse JSONObject to Message
     */
    public static Message parseJsonMessage(JSONObject object){
        Message message = new Message();
        try {
            message = new Message(
                    object.getInt("id"),
                    object.getInt("chat"),
                    object.getInt("userSender"),
                    object.getInt("userReceiver"),
                    object.getString("message"),
                    object.getString("date")
            );
        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("Error de parsing: "+ e.getMessage());
        }
        return message;
    }

    /*-- *************************************************************************************************************************** --*/
    /*--                                      This Is Not Part Of Parsing Responses From DDBB                                        --*/
    /*-- Get Profile Image From a List<Image> or If It Doesn't Have, The First in Array. If There Are no Images, Put a Default Image --*/
    /*-- *************************************************************************************************************************** --*/
    public static String getProfileImage(List<Image> imagesArray){
        String profileImage = "";

        // If Doesn't Exists This Propertie - TODO: Esto no debería de poder suceder, ya que aunque no hayan imágenes obtendremos un array vacío.
        if(imagesArray == null) {
            profileImage = "https://source.unsplash.com/Xq1ntWruZQI/600x800";
            return profileImage;
        }

        for (int i = 0; i < imagesArray.size(); i++){
            if(imagesArray.get(i).isProfile()) profileImage = imagesArray.get(i).getUrl();
        }
        if(imagesArray.size() > 0) profileImage = (profileImage.length() > 0) ? SERVER_URL+IMAGES_URL + profileImage : SERVER_URL+IMAGES_URL + imagesArray.get(0).getUrl();
        else profileImage = "https://source.unsplash.com/Xq1ntWruZQI/600x800";
        return profileImage;
    }

}
