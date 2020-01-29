package com.dam.daniel.playmatch.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.dam.daniel.playmatch.MatchActivity;
import com.dam.daniel.playmatch.models.Image;
import com.dam.daniel.playmatch.models.User;
import com.dam.daniel.playmatch.utils.Constants;
import com.dam.daniel.playmatch.utils.HttpStatusCode;
import com.dam.daniel.playmatch.utils.LocalSorage;
import com.dam.daniel.playmatch.utils.ServiceParserUtils;
import com.dam.daniel.playmatch.utils.VolleySupport;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.util.Objects.isNull;

public class UserService {

    private Context context;
    private List<User> users = new ArrayList<>();
    private int statusCode;
    private String errorMessage;
    private String token;

    /**
     * CONSTRUCTOR
     * */
     public UserService(Context context){
         this.context = context;
     }

    /**
     * GET ALL USERS
     * */
    public void getAllUsers(final VolleySupport.ServerCallbackUserList serverCallbackUserList, final VolleySupport.ServerCalbackError serverCalbackError){
        // Crear nueva cola de peticiones
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        // Nueva petición JSONObject
        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.GET,
                Constants.SERVER_URL + "/user/all",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // response --> {"users":[{...}, {...}]}
                        if(HttpStatusCode.isStatusOk(statusCode)){
                            users = ServiceParserUtils.parseJsonUserList(response);

                            LocalSorage.saveDataInSharedPreferences(context, token, null);     // LocalStorage
                            serverCallbackUserList.onSuccess(users);                              // Call Callback function here
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
     * GET USER BY ID - Synchronous Call (Example Synchronous Call)
     * */
    public void getUserByIdSync(final VolleySupport.ServerCallbackUser serverCallbackUser, final VolleySupport.ServerCalbackError serverCallbackError, final int userId){
        final RequestQueue requestQueue = Volley.newRequestQueue(context);
        Thread thread = new Thread() {
            @Override
            public void run() {
                RequestFuture<JSONObject> future = RequestFuture.newFuture();
                // Since we pass the RequestFuture to the JsonObjectRequest constructor in place of both the success and error listener,
                // that's mean when we future.get() we manually have to differentiate between success and error
                JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, Constants.SERVER_URL + "/user/" + userId, null, future, future){
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
                requestQueue.add(objectRequest);

                try {
                    JSONObject response = null;
                    while (response == null) {
                        try {
                            response = future.get(3, TimeUnit.SECONDS); // Block thread, waiting for response, timeout after 3 seconds
                            JSONObject userResponse;
                            if(HttpStatusCode.isStatusOk(statusCode)){
                                try{
                                    userResponse = response.getJSONObject("user");
                                    User user = ServiceParserUtils.parseJsonUser(userResponse);

                                    LocalSorage.saveDataInSharedPreferences(context, token, user);
                                    serverCallbackUser.onSuccess(user);
                                }catch (JSONException e) { e.printStackTrace(); }
                            }
                        } catch (InterruptedException e) {
                            // Received interrupt signal, but still don't have response
                            // Restore thread's interrupted status to use higher up on the call stack
                            Thread.currentThread().interrupt();
                            // Continue waiting for response (unless you specifically intend to use the interrupt to cancel your request)
                        }
                    }
                } catch (ExecutionException | TimeoutException e) {
                    grabStatusAndErrorMessage(new VolleyError(e));
                    serverCallbackError.onError(errorMessage);
                } finally {
                    Thread.currentThread().interrupt();
                }
            }
        };

        thread.start();
    }

    /**
     * GET USER BY ID
     * */
    public void getUserById(final VolleySupport.ServerCallbackUser serverCallbackUser, final VolleySupport.ServerCalbackError serverCalbackError, int userId){
        // Crear nueva cola de peticiones
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        // Nueva petición JSONObject
        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.GET,
                Constants.SERVER_URL + "/user/" + userId,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // response --> {"user":{...}}
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
     * UPDATE USER
     */
    public void updateUser(final VolleySupport.ServerCallbackUser serverCallbackUser, final VolleySupport.ServerCalbackError serverCalbackError, JSONObject jsonObject){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.PATCH,
                Constants.SERVER_URL + "/user/me/update",
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // response --> {"user":{...}}
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
     * REGISTER USER
     * */
    public void postRegisterUser(final VolleySupport.ServerCallbackUser serverCallbackUser, final VolleySupport.ServerCalbackError serverCalbackError, JSONObject jsonObject){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.POST,
                Constants.SERVER_URL + "/auth/register",
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // response --> {"user":{...}}
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