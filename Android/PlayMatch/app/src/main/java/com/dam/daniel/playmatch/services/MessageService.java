package com.dam.daniel.playmatch.services;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dam.daniel.playmatch.models.Message;
import com.dam.daniel.playmatch.utils.Constants;
import com.dam.daniel.playmatch.utils.HttpStatusCode;
import com.dam.daniel.playmatch.utils.LocalSorage;
import com.dam.daniel.playmatch.utils.ServiceParserUtils;
import com.dam.daniel.playmatch.utils.VolleySupport;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageService {

    private Context context;
    private List<Message> messages = new ArrayList<>();
    private int statusCode;
    private String errorMessage;
    private String token;

    /**
     * CONSTRUCTOR
     * */
    public MessageService(Context context){
        this.context = context;
    }

    /**
     * GET ALL MESSAGES FROM A CHAT
     * */
    public void getPreviousMessages(final VolleySupport.ServerCallbackMessageList serverCallbackMessageList, final VolleySupport.ServerCalbackError serverCalbackError, int chatId){
        // Crear nueva cola de peticiones
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        // Nueva petición JSONObject
        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.GET,
                Constants.SERVER_URL + "/message/chat/" + chatId,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // response --> {"users":[{...}, {...}]}
                        if(HttpStatusCode.isStatusOk(statusCode)){

                            // Parse Messages
                            JSONArray jsonArray;
                            try {
                                jsonArray = response.getJSONArray("messages");
                                for(int i = 0; i < jsonArray.length(); i++){
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    messages.add(ServiceParserUtils.parseJsonMessage(object));
                                }
                            }catch (JSONException e) {
                                e.printStackTrace();
                            }

                            LocalSorage.saveDataInSharedPreferences(context, token, null);     // LocalStorage
                            serverCallbackMessageList.onSuccess(messages);                           // Call Callback function here
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
             * @throws AuthFailureError
             */
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String>  params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("Authorization", LocalSorage.loadDataFromSharedPreferences(context).getToken());

                return params;
            }

            /**
             * Get HTTP status code for SUCCESSFULL requests and TOKEN with Volley
             * */
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                statusCode = response.statusCode;
                token = response.headers.get("Authorization");
                return super.parseNetworkResponse(response);
            }
        };
        // Añadir petición a la cola
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
