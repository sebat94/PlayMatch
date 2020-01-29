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
import com.dam.daniel.playmatch.models.User;
import com.dam.daniel.playmatch.utils.Constants;
import com.dam.daniel.playmatch.utils.HttpStatusCode;
import com.dam.daniel.playmatch.utils.LocalSorage;
import com.dam.daniel.playmatch.utils.VolleySupport;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class AuthService {

    private Context context;
    private int statusCode;
    private String errorMessage;

    /**
     * CONSTRUCTOR
     * @param context
     */
    public AuthService(Context context) {
        this.context = context;
    }

    /**
     * ASK FOR SMS WITH OneTimeCode
     * @param serverCallbackEmpty
     * @param serverCalbackError
     * @param jsonObject
     */
    public void smsAskOneTimeCode(final VolleySupport.ServerCallbackEmpty serverCallbackEmpty, final VolleySupport.ServerCalbackError serverCalbackError, JSONObject jsonObject){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.POST,
                Constants.SERVER_URL + "/auth/sms",
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(HttpStatusCode.isStatusOk(statusCode)){
                            serverCallbackEmpty.onSuccess();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        grabStatusAndErrorMessage(volleyError);
                        serverCalbackError.onError("Server error, try again in a few minutes...");
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

                return params;
            }
            /**
             * Get HTTP status code for SUCCESSFULL requests with Volley
             * */
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                statusCode = response.statusCode;
                // Do the trick for empty responses from Server. Volley need to receive an Object in the response of the server.
                // Then, when we send an empty response from server, we intercept here the empty response and add an empty object although we are going to ignore it.
                Map<String, String> headers = new HashMap<String, String>() { { put("Content-Type", "application/json"); } };
                try {
                    if (response.data.length == 0) {
                        byte[] responseData = "{}".getBytes("UTF8");
                        // @deprecated
                        // This constructor cannot handle server responses containing multiple headers with the same name.
                        // This constructor may be removed in a future release of Volley.
                        response = new NetworkResponse(statusCode, responseData, headers, response.notModified);
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return super.parseNetworkResponse(response);
            }
        };
        requestQueue.add(objectRequest);
    }

    /**
     * SEND A VERIFY OneTimeCode
     * @param serverCallbackEmpty
     * @param serverCalbackError
     * @param jsonObject
     */
    public void smsVerifyOneTimeCode(final VolleySupport.ServerCallbackEmpty serverCallbackEmpty, final VolleySupport.ServerCalbackError serverCalbackError, JSONObject jsonObject){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.POST,
                Constants.SERVER_URL + "/auth/sms/verify",
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(HttpStatusCode.isStatusOk(statusCode)){
                            serverCallbackEmpty.onSuccess();
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

                return params;
            }
            /**
             * Get HTTP status code for SUCCESSFULL requests with Volley
             * */
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                statusCode = response.statusCode;
                // Do the trick for empty responses from Server. Volley need to receive an Object in the response of the server.
                // Then, when we send an empty response from server, we intercept here the empty response and add an empty object although we are going to ignore it.
                Map<String, String> headers = new HashMap<String, String>() { { put("Content-Type", "application/json"); } };
                try {
                    if (response.data.length == 0) {
                        byte[] responseData = "{}".getBytes("UTF8");
                        // @deprecated
                        // This constructor cannot handle server responses containing multiple headers with the same name.
                        // This constructor may be removed in a future release of Volley.
                        response = new NetworkResponse(statusCode, responseData, headers, response.notModified);
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return super.parseNetworkResponse(response);
            }
        };
        requestQueue.add(objectRequest);
    }

    /**
     * CHECK IF USER WAS LOGGED WHEN ENTER TO APP
     * @param serverCallbackEmpty
     * @param serverCalbackError
     */
    public void isUserLogged(final VolleySupport.ServerCallbackEmpty serverCallbackEmpty, final VolleySupport.ServerCalbackError serverCalbackError, User user){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest objectRequest = new JsonObjectRequest(
                Request.Method.GET,
                Constants.SERVER_URL + "/auth/token/" + user.getId() + "/" + user.getTelephoneNumber(),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(HttpStatusCode.isStatusOk(statusCode)){
                            serverCallbackEmpty.onSuccess();
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
             * Get HTTP status code for SUCCESSFULL requests with Volley
             * */
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                statusCode = response.statusCode;
                // Do the trick for empty responses from Server. Volley need to receive an Object in the response of the server.
                // Then, when we send an empty response from server, we intercept here the empty response and add an empty object although we are going to ignore it.
                Map<String, String> headers = new HashMap<String, String>() { { put("Content-Type", "application/json"); } };
                try {
                    if (response.data.length == 0) {
                        byte[] responseData = "{}".getBytes("UTF8");
                        // @deprecated
                        // This constructor cannot handle server responses containing multiple headers with the same name.
                        // This constructor may be removed in a future release of Volley.
                        response = new NetworkResponse(statusCode, responseData, headers, response.notModified);
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
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
