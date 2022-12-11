package com.example.kalahapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); //hides action bar
        setContentView(R.layout.activity_main);
    }

    public void startGameButton(View v)
    {
        Intent i = new Intent(this,KalahActivity.class); //create intent that launches KalahActivity
        startActivity(i); //starts new activity
    }
}
