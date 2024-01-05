package com.blood.attendanceapp;

import static com.blood.attendanceapp.Request.emp_id;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class Activity_Settings extends AppCompatActivity {

    private ImageView _back_button;
    private TextView _device_id_txt, _emp_id_txt, _network_txt, _ip_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initView();
    }

    @Override
    protected void onStart() {
//        _device_id_txt.setText(device_id);
        _emp_id_txt.setText(emp_id);

        if(com.blood.attendanceapp.Request.use_wan_text.equals("false"))
            _network_txt.setText("LAN");
        else
            _network_txt.setText("WAN");

        super.onStart();
    }

    protected void initView() {
        _back_button = findViewById(R.id.back_button);
        _emp_id_txt = findViewById(R.id.emp_id_txt);
        _device_id_txt = findViewById(R.id.device_id_txt);
        _network_txt = findViewById(R.id.network_txt);
        _ip_txt = findViewById(R.id.ip_txt);

        _back_button.setOnClickListener(v -> {
            finish();
//            Intent i = new Intent(this, MainActivity.class);
//            startActivity(i);
        });
    }
}