package com.example.kalahapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

public class KalahActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); //hides action bar
        setContentView(R.layout.activity_kalah);
    }

    public void continueButton(View v)
    {
        Intent gameActivity = new Intent(this,gameActivity.class); //create intent that will launch gameActivity
        String player1Name = ((EditText)findViewById(R.id.player1Name)).getText().toString(); //get player 1's name
        String player2Name = ((EditText)findViewById(R.id.player2Name)).getText().toString(); //get player 2's name
        gameActivity.putExtra("p1name", player1Name); //pass in player1Name
        gameActivity.putExtra("p2name", player2Name); //pass in player2Name

        YoYo.with(Techniques.Pulse).duration(300).playOn(v); //button will pulse for 300ms

        //pause for 500ms before going to next activity
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            startActivity(gameActivity); //starts new activity
        }, 300);
    }
}

