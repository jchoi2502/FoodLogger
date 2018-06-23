package com.example.jchoi.foodlogger;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();


    }

    public void init() {
        findViewById(R.id.btn_google).setOnClickListener(this);
        findViewById(R.id.btn_facebook).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_google:
                goToGoogle();
                break;
            case R.id.btn_facebook:
                goToFacebook();
                break;
        }

    }

    public void goToGoogle() {
        Intent myIntent = new Intent(MainActivity.this, SurveyActivity.class);
        MainActivity.this.startActivity(myIntent);
    }

    public void goToFacebook() {
        Intent myIntent = new Intent(MainActivity.this, SurveyActivity.class);
        MainActivity.this.startActivity(myIntent);

    }
}
