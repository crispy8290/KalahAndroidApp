package com.example.kalahapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

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
        YoYo.with(Techniques.Pulse).duration(300).playOn(v); //button will pulse for 300ms
        Intent kalahActivity = new Intent(this,KalahActivity.class); //create intent that launches KalahActivity

        //pause for 500ms before going to next activity
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            startActivity(kalahActivity); //starts new activity
        }, 300);
    }
}
