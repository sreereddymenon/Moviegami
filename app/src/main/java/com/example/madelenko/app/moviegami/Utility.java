package com.example.madelenko.app.moviegami;

import android.support.annotation.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

/**
 * Class containing utility methods and parameters.
 */
public final class Utility {

    public static final String API_KEY = "api_key";


    /*
     * Method that downloads the results of an Http connection and saves them as a String.
     * @Param: HttpUrlConnection from which we will download our resources.
     * @Return: A string with the contents of the connection
     */
    @NonNull
    public static String downloadResources(@NonNull HttpURLConnection resourceConnection) {
        BufferedReader reader = null;
        InputStreamReader streamReader = null;
        StringBuilder builder = new StringBuilder();
        String line = null;
        try {
            streamReader = new InputStreamReader(resourceConnection.getInputStream());
            reader = new BufferedReader(streamReader);
            while ((line = reader.readLine()) != null) {
                builder.append(line + "%n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
                streamReader.close();
                resourceConnection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return builder.toString();
    }
}
