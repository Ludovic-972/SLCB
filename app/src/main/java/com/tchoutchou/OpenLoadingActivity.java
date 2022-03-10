package com.tchoutchou;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.tchoutchou.model.Trip;
import com.tchoutchou.util.JDBCUtils;
import com.tchoutchou.util.NoConnectionException;

public class OpenLoadingActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_screen);

        getSupportActionBar().hide();

        Handler handler = new Handler();

        Runnable change = () -> {
            Intent intent = new Intent(OpenLoadingActivity.this, MainActivity.class);
            startActivity(intent);
            finish();

        };

        Runnable loading = () -> {
            try {
                if (JDBCUtils.hasConnection(this)) {
                    Thread.sleep(2000);
                    Trip.clean();
                    handler.post(change);
                } else {
                    Thread.sleep(2000);
                    Intent intent = new Intent(OpenLoadingActivity.this, NoConnectionActivity.class);
                    startActivity(intent);
                    finish();
                }
            } catch (InterruptedException | NoConnectionException e) {
                Intent intent = new Intent(OpenLoadingActivity.this, NoConnectionActivity.class);
                startActivity(intent);
                finish();
            }

        };

        new Thread(loading).start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}