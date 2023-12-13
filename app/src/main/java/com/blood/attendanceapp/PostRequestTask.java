package com.blood.attendanceapp;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PostRequestTask extends AsyncTask<Void, Void, String> {

    private String deviceId;
    private String base64ImageData; // Base64-encoded image data
    private PostRequestCallback callback;

    public PostRequestTask(String deviceId, String base64ImageData, PostRequestCallback callback) {
        this.deviceId = deviceId;
        this.base64ImageData = base64ImageData;
        this.callback = callback;
    }

    @Override
    protected String doInBackground(Void... voids) {
        String apiUrl = "";
        if(Request.use_wan_text.equals("false"))
            apiUrl = Request.post_mark_attendance_lan;
        else
            apiUrl = Request.post_mark_attendance_lan;

        String boundary = "*****";
        String lineEnd = "\r\n";

        try {
            // Create URL object
            URL url = new URL(apiUrl);

            // Open connection
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            urlConnection.setDoOutput(true);
            Log.d("Tracking: ", "before try");
            try (DataOutputStream dos = new DataOutputStream(urlConnection.getOutputStream())) {
                Log.d("Tracking: ", "inside try");
                // Construct the JSON payload
                String jsonPayload = "{\n" +
                        "  \"image\": \"" + base64ImageData + "\"\n" +
                        "}";

                // Write the JSON data to the output stream
                dos.writeBytes(jsonPayload);
                dos.flush();
                Log.d("Tracking: ", "dov.flush");
            }

            // Get the response from the server
            Log.d("Tracking: ", "before try 2");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                Log.d("Tracking: ", "inside try 2");
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                Log.d("Tracking: ", "Response: " + response);
                return response.toString();
            } finally {
                // Disconnect the connection
                urlConnection.disconnect();
                Log.d("Tracking: ", "end");
            }
        } catch (IOException e) {
            Log.e("PostRequestTask", "Error: " + e.getMessage());
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        // Handle the result, for example, update UI or perform other tasks
        Log.d("PostRequestTask", "Response: " + result);
        if (result != null) {
            Log.d("PostRequestTask", "Response: " + result);
            if (callback != null) {
                callback.onSuccess(result);
            }
        } else {
            Log.e("PostRequestTask", "Error in POST request");
            if (callback != null) {
                callback.onError();
            }
        }
    }

    public interface PostRequestCallback {
        void onSuccess(String result);
        void onError();
    }
}