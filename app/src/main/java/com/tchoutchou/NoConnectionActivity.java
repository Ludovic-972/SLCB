package com.tchoutchou;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import java.util.Objects;

public class NoConnectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.no_connection_layout);

        Objects.requireNonNull(getSupportActionBar()).show();
        getSupportActionBar().setTitle(R.string.no_connection);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(NoConnectionActivity.this,OpenLoadingActivity.class);
        startActivity(intent);
        finish();
    }
}