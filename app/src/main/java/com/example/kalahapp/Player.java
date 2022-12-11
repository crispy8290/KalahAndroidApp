package com.example.kalahapp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Player
{
    private String playerName;
    private Integer playerWinCounter = 0;

    public Player(String playerName, int playerNum)
    {
        Pattern pattern = Pattern.compile("^\\s*$"); //regex that identifies empty strings or strings that only contains whitespaces
        Matcher matcher = pattern.matcher(playerName);
        boolean matchFound = matcher.find();

        if(playerNum == 1 && (playerName.equals("Player 1 Name") || matchFound)) //if player 1 and regex match found
        {
            playerName = "Player 1"; //set default player 1 name
        }
        else if(playerNum == 2 && (playerName.equals("Player 2 Name") || matchFound)) //if player 2 and regex match found
        {
            playerName = "Player 2"; //set default player 2 name
        }
        this.playerName = playerName; //initialize playerName
    }

    public String getPlayerName()
    {
        return playerName;
    }

    public Integer getPlayerWinCounter()
    {
        return playerWinCounter;
    }

    public void incPlayerWinCounter()
    {
        playerWinCounter++;
    }
}
