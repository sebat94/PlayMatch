package com.dam.daniel.playmatch.services;

import android.content.Context;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dam.daniel.playmatch.models.User;
import com.dam.daniel.playmatch.utils.Constants;
import com.dam.daniel.playmatch.utils.HttpStatusCode;
import com.dam.daniel.playmatch.utils.LocalSorage;
import com.dam.daniel.playmatch.utils.ServiceParserUtils;
import com.dam.daniel.playmatch.utils.VolleySupport;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ImageService {

    private Context context;
    private int statusCode;
    private String errorMessage;
    private String token;

    public ImageService(Context context) { this.context = context; }

    /**
     * POST IMAGE/S
     * */
    public void postImages(final VolleySupport.ServerCallbackUser serverCallbackUser, final VolleySupport.ServerCalbackError serverCalbackError, JSONObject jsonObject){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.POST,
                Constants.SERVER_URL + "/user/me/add/images",
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // response --> {"images":[...]}
                        JSONObject userResponse;
                        if(HttpStatusCode.isStatusOk(statusCode)){
                            try{
                                userResponse = response.getJSONObject("user");
                                User user = ServiceParserUtils.parseJsonUser(userResponse);

                                LocalSorage.saveDataInSharedPreferences(context, token, user);
                                serverCallbackUser.onSuccess(user);
                            }catch (JSONException e) { e.printStackTrace(); }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        grabStatusAndErrorMessage(volleyError);
                        serverCalbackError.onError(errorMessage);
                    }
                }
        ){
            /**
             * Set HTTP Headers in the Request
             * @return
             */
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String>  params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("Authorization", LocalSorage.loadDataFromSharedPreferences(context).getToken());

                return params;
            }

            /**
             * Get HTTP status code for SUCCESSFULL requests with Volley
             * */
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                statusCode = response.statusCode;
                token = response.headers.get("Authorization");
                return super.parseNetworkResponse(response);
            }
        };
        requestQueue.add(objectRequest);
    }

    /**
     * SET AS PROFILE IMAGE
     * */
    public void updateProfileImage(final VolleySupport.ServerCallbackUser serverCallbackUser, final VolleySupport.ServerCalbackError serverCalbackError, JSONObject jsonObject){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.PATCH,
                Constants.SERVER_URL + "/user/me/images/setProfileImage",
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // response --> {"images":[...]}
                        JSONObject userResponse;
                        if(HttpStatusCode.isStatusOk(statusCode)){
                            try{
                                userResponse = response.getJSONObject("user");
                                User user = ServiceParserUtils.parseJsonUser(userResponse);

                                LocalSorage.saveDataInSharedPreferences(context, token, user);
                                serverCallbackUser.onSuccess(user);
                            }catch (JSONException e) { e.printStackTrace(); }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        grabStatusAndErrorMessage(volleyError);
                        serverCalbackError.onError(errorMessage);
                    }
                }
        ){
            /**
             * Set HTTP Headers in the Request
             * @return
             */
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String>  params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("Authorization", LocalSorage.loadDataFromSharedPreferences(context).getToken());

                return params;
            }

            /**
             * Get HTTP status code for SUCCESSFULL requests with Volley
             * */
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                statusCode = response.statusCode;
                token = response.headers.get("Authorization");
                return super.parseNetworkResponse(response);
            }
        };
        requestQueue.add(objectRequest);
    }

    /**
     * DELETE IMAGE
     * */
    public void deleteImage(final VolleySupport.ServerCallbackUser serverCallbackUser, final VolleySupport.ServerCalbackError serverCalbackError, int imageId){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.DELETE,
                Constants.SERVER_URL + "/user/me/images/delete/" + imageId,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // response --> {"images":[...]}
                        JSONObject userResponse;
                        if(HttpStatusCode.isStatusOk(statusCode)){
                            try{
                                userResponse = response.getJSONObject("user");
                                User user = ServiceParserUtils.parseJsonUser(userResponse);

                                LocalSorage.saveDataInSharedPreferences(context, token, user);
                                serverCallbackUser.onSuccess(user);
                            }catch (JSONException e) { e.printStackTrace(); }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        grabStatusAndErrorMessage(volleyError);
                        serverCalbackError.onError(errorMessage);
                    }
                }
        ){
            /**
             * Set HTTP Headers in the Request
             * @return
             */
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String>  params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("Authorization", LocalSorage.loadDataFromSharedPreferences(context).getToken());

                return params;
            }

            /**
             * Get HTTP status code for SUCCESSFULL requests with Volley
             * */
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                statusCode = response.statusCode;
                token = response.headers.get("Authorization");
                return super.parseNetworkResponse(response);
            }
        };
        requestQueue.add(objectRequest);
    }

    /**
     * Get HTTP status code for UN-SUCCESSFULL requests with Volley
     * Get errorMessage from body of request returned in a json
     *
     * It will be used to display an error message in the activity.
     * */
    private void grabStatusAndErrorMessage(VolleyError volleyError){
        statusCode = volleyError.networkResponse.statusCode;
        if (volleyError.networkResponse.data != null){
            VolleyError volleyErrorData = new VolleyError(new String(volleyError.networkResponse.data));
            try {
                JSONObject obj = new JSONObject(volleyErrorData.getMessage());
                if((obj.getString("errorMessage")) != null) errorMessage = obj.getString("errorMessage");
            } catch (JSONException e) {
                e.printStackTrace();
                errorMessage = e.getMessage();
            }
        }
    }

}
