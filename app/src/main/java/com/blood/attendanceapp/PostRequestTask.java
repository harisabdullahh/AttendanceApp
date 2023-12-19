package com.blood.attendanceapp;

import static com.blood.attendanceapp.Request.status_result;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

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

    public PostRequestTask(String base64ImageData, PostRequestCallback callback) {
        this.base64ImageData = base64ImageData;
        this.callback = callback;
    }

    @Override
    protected String doInBackground(Void... voids) {
        String apiUrl = Request.post_mark_attendance_lan;

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
//                Log.d("Tracking: ", base64ImageData);
                String base64ImageDataWithoutLineBreaks = base64ImageData.replaceAll("\\s", "");

                String jsonPayload = "{\n" +
                        "  \"image\": \"" + base64ImageDataWithoutLineBreaks + "\"\n" +
                        "}";
                Log.d("Tracking: ", jsonPayload);
                // Write the JSON data to the output stream
                dos.writeBytes(jsonPayload);
                dos.flush();
                Log.d("Tracking: ", "dov.flush");
            }

            // Get the response from the server
            Log.d("Tracking: ", "before try 2");
            try {
                Log.d("Tracking: ", "inside try 2");
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                Log.d("Tracking: ", "reader: " + reader);
                StringBuilder response = new StringBuilder();
                Log.d("Tracking: ", "response try 2: " + response);
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                Log.d("Tracking: ", "Response: " + response);
//                try {
//                    status_result = String.valueOf(responseStatus(String.valueOf(response)));
//                    Log.d("Tracking: Response Status", status_result);
//
//                } catch (JSONException e) {
//                    throw new RuntimeException(e);
//                }
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

    protected Boolean responseStatus(String jsonResponse) throws JSONException {
//        String jsonResponse = "{ \"verification_result\": { \"distance\": \"0.16498515530859992\", \"verified\": \"True\" }}";

        // Parse the JSON response
        JSONObject jsonObject = new JSONObject(jsonResponse);

        // Get the "verification_result" object
        JSONObject verificationResult = jsonObject.getJSONObject("verification_result");

        // Get the "verified" value
        String verifiedValue = verificationResult.getString("verified");

        // Convert the string value to boolean
        boolean isVerified = Boolean.parseBoolean(verifiedValue);

        return isVerified;
    }

    @Override
    protected void onPostExecute(String result) {
        // Handle the result, for example, update UI or perform other tasks

//        if(status_result.equals("true")){
//            callback.onSuccess(result);
//        } else {
//            callback.onError();
//        }

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