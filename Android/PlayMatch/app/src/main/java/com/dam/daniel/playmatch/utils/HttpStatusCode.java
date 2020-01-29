package com.dam.daniel.playmatch.utils;

import java.net.HttpURLConnection;

public class HttpStatusCode {

    /**
     * Returns Boolean according to the Status returned of the server
     * */
    public static Boolean isStatusOk (int statusCode){
        Boolean isOk;

        switch (statusCode){
            case HttpURLConnection.HTTP_OK:             // 200  (OK)
            case HttpURLConnection.HTTP_CREATED:        // 201  (Created)
            case HttpURLConnection.HTTP_NOT_MODIFIED:   // 304  (Not Mofified)
                isOk = true;
                break;
            case HttpURLConnection.HTTP_CONFLICT:   // 409
            default:
                isOk = false;
            break;
        }

        return isOk;
    }

}
