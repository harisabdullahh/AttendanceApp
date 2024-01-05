package com.blood.attendanceapp;

import static com.blood.attendanceapp.Request.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {

    private String SHARED_PREFS = "shared_prefs";
    String convertedImage;
    private static final int pic_id = 123;
    private boolean debug = false;
    private boolean post_success = false;
    private boolean get_success = false;
    public boolean use_wan = false;
    private boolean post_pressed = false;
    private boolean get_pressed = false;
    private boolean hold_button = false;
    private boolean start_func = false;
    MaterialButton get_button, post_button;
    LinearLayout LinearMain;
    private MaterialButton _lan_button, _wan_button;
    private TextView _date_text, _time_text, _post_text, _network_text, _time_in_text;
    private CardView _card_prompt;
    private ImageView _settings_button;
    private ProgressBar _progressBar1;
    private ProgressBar _progressBar2;
    private Handler handler;
    private Runnable _updateRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start_func = true;
        initView();
//        ping();
    }

    @Override
    protected void onStart() {

//        hold_button = true;
//        get_button.setBackgroundColor(ContextCompat.getColor(this, androidx.cardview.R.color.cardview_dark_background));
//        post_button.setBackgroundColor(ContextCompat.getColor(this, androidx.cardview.R.color.cardview_dark_background));
//        makeGetRequest(get_status_wan);
        if(get_success){
            String formattedDate = convertDateFormat(status_date, outputDateFormat);
            String formattedTime = convertTimeFormat(status_time, outputTimeFormat);
            _time_text.setText(formattedTime);
            _date_text.setText(formattedDate);
        }
        super.onStart();
    }

    private void ping() {
        new PingTask().execute("172.16.1.237");
    }

    protected void initView() {

        //Initializtion
        LinearMain = findViewById(R.id.LinearMain);
        post_button = findViewById(R.id.post_button);
        _date_text = findViewById(R.id.date_text);
        _time_text = findViewById(R.id.time_text);
        _post_text = findViewById(R.id.post_text);
        _settings_button = findViewById(R.id.settings_button);
        _progressBar1 = findViewById(R.id.progressBar1);
        _card_prompt = findViewById(R.id.card_prompt);
        _time_in_text = findViewById(R.id.time_in_text);



        //Action
        _progressBar1.setVisibility(View.GONE);
//        _progressBar2.setVisibility(View.GONE);
        _card_prompt.setVisibility(View.GONE);


        //Listeners
//        get_button.setOnClickListener(v -> {
//            if(!hold_button){
//                get_button.setVisibility(View.GONE);
//                _progressBar2.setVisibility(View.VISIBLE);
//                get_pressed = true;
////                ping();
////                if(use_wan){
////                    makeGetRequest(get_status_wan);
////                } else {
////                    makeGetRequest(get_status_lan);
////                }
//            }
//        });

        post_button.setOnClickListener(v -> {

//            post_button.setVisibility(View.GONE);
//            _progressBar1.setVisibility(View.VISIBLE);
//            sendPostRequest();

            Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Start the activity with camera_intent, and request pic id
            startActivityForResult(camera_intent, pic_id);



//            ping();

//            if(!hold_button){
//                post_button.setVisibility(View.GONE);
//                _progressBar1.setVisibility(View.VISIBLE);
//                post_pressed = true;
////                ping();
//            }

        });


        _settings_button.setOnClickListener(v -> {
            Intent i = new Intent(this, Activity_Settings.class);
            startActivity(i);
        });


    }

    private OkHttpClient client = new OkHttpClient();

    // Method to make the GET request
    public void makeGetRequest(String url) {
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    try {
                        get_success = true;
                        ResponseBody responseBody = response.body();
                        if (responseBody != null) {
                            String json = responseBody.string();
                            // Handle the JSON response
                            handleJSONResponse(json);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                // Handle the failure
            }
        });
    }

    // Method to handle the JSON response
    private void handleJSONResponse(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray resultArray = jsonObject.getJSONArray("result");

            if (resultArray.length() > 0) {
                JSONObject firstResult = resultArray.getJSONObject(0);

                String lastAttendance = firstResult.getString("lastAttendance");
                if(debug)
                    Log.d("Tracking: ", lastAttendance);
                status_result = lastAttendance;
                int indexOfT = lastAttendance.indexOf('T');
                int lengthOfT = lastAttendance.length();
                status_date = lastAttendance.substring(0, indexOfT);
                status_time = lastAttendance.substring(indexOfT+1, indexOfT+6);

                // Update UI or perform actions with the lastAttendance data
                runOnUiThread(() -> {
                    _progressBar2.setVisibility(View.GONE);
                    get_button.setVisibility(View.VISIBLE);
                    String formattedDate = convertDateFormat(status_date, outputDateFormat);
                    String formattedTime = convertTimeFormat(status_time, outputTimeFormat);
                    _time_text.setText(formattedTime);
                    _date_text.setText(formattedDate);
                    updateEarlyDate(formattedDate, formattedTime);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//        private void sendPostRequest() {
//            Log.d("Tracking: ", "Length: " + part1.length() + "\n Part 1: " +part1);
//            Log.d("Tracking: ", "Length: " + part6.length() + "\n Part 6: " +part6);
////            Log.d("Tracking: ", "Length: " + part2.length() + "\n Part 2: " +part2);
////            Log.d("Tracking: ", "Length: " + part3.length() + "\n Part 3: " +part3);
////            Log.d("Tracking: ", "Length: " + part4.length() + "\n Part 4: " +part4);
////            Log.d("Tracking: ", "Length: " + part5.length() + "\n Part 5: " +part5);
//        }



    private void sendPostRequest() {
//        String convertedImage = (part1 + part2 + part3 + part4 + part5 + part6 + part7 + part8 + part9 + part10 + part11 + part12 + part13 + part14 + part15 + part16 + part17 + part18 + part19 + part20 + part21 + part22 + part23 + part24);
        PostRequestTask.PostRequestCallback callback = new PostRequestTask.PostRequestCallback() {
            @Override
            public void onSuccess(String result) {
                post_button.setVisibility(View.VISIBLE);
                _progressBar1.setVisibility(View.GONE);
                get_pressed = true;



                try {
                    JSONObject jsonResponse = new JSONObject(result);

                    if(jsonResponse.has("verification_result" )) {
                        JSONObject a = jsonResponse.getJSONObject("verification_result");
                        String verified = a.getString("verified");
                        Log.i("Tracking", "Verification Status: " + verified);

                        if (verified.equals("True")){
                            setPrompt("Attendance Marked Successfuly");
                        } else if (verified.equals("False")) {
                            setPrompt("Face Not Matched");
                        }
//                            if (jsonResponse.has("result") && jsonResponse.getJSONArray("result").length() > 0) {
//                                // Get the first item in the "result" array
//                                JSONObject resultObject = jsonResponse.getJSONArray("result").getJSONObject(0);
//                                JSONArray messagesArray = jsonResponse.getJSONArray("messages");
//                                firstMessage = messagesArray.getString(0);
//                                setPrompt(firstMessage);
//
//                                // Extract the name from the resultObject
//                                firstName = resultObject.optString("firstName", "");
//
//                                // Now you have the name (firstName)
////                                Log.d("Tracking: Name", "Name: " + firstName);
////                                Log.d("Tracking: Messages", "Messages: " + firstMessage);
//                            }
//                        } else if(status_code == 400){
//                            // Get the first item in the "result" array
//                            JSONArray messagesArray = jsonResponse.getJSONArray("messages");
//                            firstMessage = messagesArray.getString(0);
//                            setPrompt(firstMessage);
//                        }
                    } else if (jsonResponse.has("error")){
                        String error = jsonResponse.getString("error");
                        setPrompt(error);
                        Log.e("Tracking", "Error: " + error);
                    }
                } catch (JSONException e) {
                    Log.e("Tracking", "Error parsing JSON: " + e.getMessage());
                }




            }

            @Override
            public void onError() {
                post_button.setVisibility(View.VISIBLE);
                _progressBar1.setVisibility(View.GONE);
            }
        };

        // Assuming you have the base64ImageData, pass it to the constructor
        PostRequestTask postRequestTask = new PostRequestTask(convertedImage, callback);
        postRequestTask.execute();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Match the request 'pic id with requestCode
        if (requestCode == pic_id) {
            // BitMap is data structure of image file which store the image in memory
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 60, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            convertedImage = new String(Base64.encodeToString(byteArray, Base64.DEFAULT));
//            Log.d("Tracking: ", String.valueOf(convertedImage));
            Log.d("Tracking", "Image captured and saved.");
            post_button.setVisibility(View.GONE);
            _progressBar1.setVisibility(View.VISIBLE);
            sendPostRequest();
            // Set the image in imageview for display
//            click_image_id.setImageBitmap(photo);
        }
    }

    private void updateEarlyDate(String varDate, String varTime) {
        if(debug)
            Log.d("1: ", "1");

        SharedPreferences sh = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        String timetemp = sh.getString("time", "");
        String datetemp = sh.getString("date", "");

        if(debug)
            Log.d("1: ", "Date: " + datetemp + " | Time: " + timetemp);


        if(!datetemp.equals("") && !timetemp.equals("")){
            if(debug)
                Log.d("1: ", "2");

            DateTimeFormatter dateFormatter = null;
            LocalDate date = null;
            LocalDate currentDate = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy");
                date = LocalDate.parse(varDate, dateFormatter);
                currentDate = LocalDate.now();
            }

            if (currentDate.equals(date)) {

                if(debug)
                    Log.d("1: ", "3");

                DateTimeFormatter timeFormatter = null;
                LocalTime time1 = null, time2 = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    timeFormatter = DateTimeFormatter.ofPattern("h:mm a");
                    time1 = LocalTime.parse(varTime, timeFormatter);
                    time2 = LocalTime.parse(timetemp, timeFormatter);

                    if (time2.isAfter(time1)) {

                        if(debug)
                            Log.d("1: ", "4");

                        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("time", varTime);
                        editor.putString("date", varDate);
                        editor.apply();
                        _time_in_text.setText("Time in:\n" + varTime);

                    } else if (time2.isBefore(time1)) {

                        if(debug)
                            Log.d("1: ", "5");

                        _time_in_text.setText("Time in:\n" + timetemp);
                    }
                }

            } else {

                if(debug)
                    Log.d("1: ", "6");

                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("time", varTime);
                editor.putString("date", varDate);
                editor.apply();
                Log.d("1: ", varDate + ", " + varTime);
                _time_in_text.setText("Time in:\n" + varTime);
            }

        } else {
            if(debug)
                Log.d("Tracking: ", "7");

            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("time", varTime);
            editor.putString("date", varDate);
            if(debug)
                Log.d("1: ", varDate + ", " + varTime);
            editor.apply();
            _time_in_text.setText("Time in:\n" + varTime);
        }


    }

    private void setPrompt(String prompt) {
        Snackbar snackbar
                = Snackbar
                .make(
                        LinearMain,
                        prompt,
                        Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private static String convertDateFormat(String inputDate, String outputFormat) {
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat outputDateFormat = new SimpleDateFormat(outputFormat, Locale.getDefault());

        try {
            Date date = inputDateFormat.parse(inputDate);
            return outputDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String convertTimeFormat(String inputTime, String outputFormat) {
        SimpleDateFormat sdf24 = new SimpleDateFormat("HH:mm", Locale.getDefault());
        SimpleDateFormat sdf12 = new SimpleDateFormat(outputFormat, Locale.getDefault());

        try {
            Date date = sdf24.parse(inputTime);
            return sdf12.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private class PingTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            String serverAddress = params[0];
            int timeout = 2000; // Timeout in milliseconds

            return PingUtils.isServerReachable(serverAddress, timeout);
        }

        @Override
        protected void onPostExecute(Boolean isReachable) {
            if (isReachable) {
                Toast.makeText(MainActivity.this, "Server is reachable", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
