package com.example.kalahapp;

import static java.lang.System.exit;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

public class gameActivity extends AppCompatActivity
{
    private Integer[] board;
    private final int pits = 14;
    private final int seeds = 6;
    private int player1StoreIndex;
    private int player2StoreIndex;
    private int roundNumber = 0;
    private int playerTurn = 1;
    private int curr;
    private int startPoint;
    private Player player1;
    private Player player2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); //hides action bar
        setContentView(R.layout.activity_game);

        //obtains player names and sets them on screen
        Intent kalahActivity = getIntent();
        player1 = new Player(kalahActivity.getStringExtra("p1name"), 1); //create player1 object
        ((TextView) findViewById(R.id.player1)).setText(player1.getPlayerName()); //set player 1's name in activity
        player2 = new Player(kalahActivity.getStringExtra("p2name"), 2); //create player2 object
        ((TextView) findViewById(R.id.player2)).setText(player2.getPlayerName()); //set player 2's name in activity

        startGame();
    }

    public void startGame() //method that holds the game "steps" (unnecessary method?)
    {
        roundNumber++;
        makeBoard(); //initializes board array
        showBoard(); //updates every pit in activity to show the number of seeds (stored in board array)

        if(roundNumber % 2 == 1) //we are on odd-number rounds
        {
            playerTurn = 1; //player 1 starts
        }
        else //roundNumber % 2 == 0, we are on even-number rounds
        {
            playerTurn = 2; //player 2 starts
        }

        showPlayerTurn(); //shows who's turn it is
        disableEnablePits(); //disables and enables pit buttons based on playerTurn value
    }

    public void makeBoard() //initializes board array
    {
        board = new Integer [pits]; //define array size
        for(int i=0;i<pits;i++) //initialize array by inserting number of seeds in each pit
        {
            if(i == 0) //player 2's store
            {
                board[i] = 0;
                player2StoreIndex = 0;
            }
            else if(i == pits/2) //player 1's store
            {
                board[i] = 0;
                player1StoreIndex = pits/2;
            }
            else
            {
                board[i] = seeds;
            }
        }
    }

    public void showBoard() //updates every pit in activity to show the number of seeds (stored in board array)
    {
        for(int index=0;index<board.length;index++)
        {
            ((Button) findViewById(getPitID(index))).setText(board[index].toString());
        }
    }

    public void showPlayerTurn() //updates the player turn's name
    {
        if(playerTurn == 1)
        {
            View v = findViewById(R.id.player1);
            TextView text = (TextView) v;
            String name = text.getText().toString();
            ((TextView) findViewById(R.id.playerTurnName)).setText(name); //sets player turn to show player 1's name
        }
        else //playerTurn == 2
        {
            View v = findViewById(R.id.player2);
            TextView text = (TextView) v;
            String name = text.getText().toString();
            ((TextView) findViewById(R.id.playerTurnName)).setText(name); //sets player turn to show player 2's name
        }
    }

    public void firstTurn(View v) //first move of each game
    {
        pulse(v, 300); //pit pulses for 300ms

        Button button = (Button)v;
        startPoint = getPitIndex(button.getId());
        curr = startPoint;
        int seedDrop = board[startPoint]; //stores number of seeds to be moved
        board[startPoint] = 0; //sets starting point to 0

        for(int i=seedDrop;i>0;i--) //moves until there are no more seeds left to be dropped
        {
            int next = curr+1; //keeps track of next pit index

            if(curr % (pits-1) == 0 && curr != 0) //reached end of array and makes sure curr != 0 (curr is only 0 when pointer resets to beginning of array)
            {
                curr = -1;
                next = 0;
            }

            if(playerTurn == 1 && next == player2StoreIndex || playerTurn == 2 && next == player1StoreIndex) //if opponent's store, skip seed drop
            {
                i++; //no seed is dropped, counter restores by one
            }
            else //drop seed
            {
                board[next] = board[next]+1; //drops a seed
            }
            curr++; //moves to next index
        }

        showBoard(); //shows the move but don't change player turn
        playerTurnSwitch(); //switch player turns
        disablePitButtons(); //disable all pit buttons
        promptSteal();//enable and make visible eventPrompt Buttons and steal TextViews
    }

    public void disablePitButtons()
    {
        for(int index=0;index<board.length;index++) //iterate through all pits
        {
            View v = findViewById(getPitID(index)); //get View object
            v.setEnabled(false); //makes button disabled
        }
    }

    public void promptSteal()
    {
        showMessage(true, 1); //displays prompt for first turn steal
    }

    public void yesSteal(View v) //yes to steal is pressed
    {
        pulse(v, 300); //button pulses for 300ms

        //runs the stealing algorithm after 300ms delay
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            makeBoard(); //resets the board

            //perform identical move for stealing player
            int newStartPoint;
            if(roundNumber % 2 == 1) //if player 1 started
            {
                newStartPoint = (pits/2) + startPoint; //initialize new start point for player 2
            }
            else //player 2 started
            {
                newStartPoint = startPoint - (pits/2); //initialize new start point for player 1
            }
            curr = newStartPoint; //keep track of new starting point index
            int seedDrop = board[newStartPoint]; //obtain number of seeds to be dropped
            board[newStartPoint] = 0; //set starting point to 0 seeds

            for(int i=seedDrop;i>0;i--) //moves until there are no more seeds left to be dropped
            {
                int next = curr+1; //keeps track of next pit index

                if(curr % (pits-1) == 0 && curr != 0) //reached end of array and makes sure curr != 0 (curr is only 0 when pointer resets to beginning of array)
                {
                    curr = -1;
                    next = 0;
                }

                if(playerTurn == 1 && next == player2StoreIndex || playerTurn == 2 && next == player1StoreIndex) //if opponent's store, skip seed drop
                {
                    i++; //no seed is dropped, counter restores by one
                }
                else //drop seed
                {
                    board[next] = board[next]+1; //drops a seed
                }
                curr++; //moves to next index
            }
            hideYesNoButtons(); //hides and disables yes/no buttons
            hideMessage(); //hides eventPrompt and playerName messages
            checkRepeatTurn(); //check to see if copied move allows for a repeat turn
            playerTurnSwitch(); //change player turn
            showPlayerTurn(); //update player turn
            disableEnablePits(); //restore pit buttons' functionality
            changeRegularPitButtons(); //changes all on-click to playerMove
            showBoard(); //show board
        }, 300);
    }

    public void noSteal(View v) //no to steal is pressed
    {
        pulse(v, 300); //button pulses for 300ms

        //change message and buttons after 300ms
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            playerTurnSwitch(); //switch player turns
            hideYesNoButtons(); //hides and disables yes/no buttons
            hideMessage(); //hides eventPrompt and playerName messages
            checkRepeatTurn(); //check to see if copied move allows for a repeat turn
            playerTurnSwitch(); //change player turn
            showPlayerTurn(); //update player turn
            disableEnablePits(); //restore pit buttons' functionality
            changeRegularPitButtons(); //changes all on-click to playerMove
        }, 300);
    }

    public void changeRegularPitButtons() //change all on-click to playerMove
    {
        for(int index=1;index<board.length;index++)
        {
            if(index == 7) //player 1's pit
            {
                continue; //skip iteration
            }
            Button button = (Button) findViewById(getPitID(index));
            button.setOnClickListener(this::playerMove);
        }
    }

    public void playerMove(View v) //regular player move
    {
        pulse(v, 300); //pit pulses for 300ms
        Button button = (Button)v;
        int startPoint = getPitIndex(button.getId());
        curr = startPoint;
        int seedDrop = board[startPoint]; //stores number of seeds to be moved
        board[startPoint] = 0; //sets starting point to 0

        for(int i=seedDrop;i>0;i--) //moves until there are no more seeds left to be dropped
        {
            int next = curr+1; //keeps track of next pit index

            if(curr % (pits-1) == 0 && curr != 0) //reached end of array and makes sure curr != 0 (curr is only 0 when pointer resets to beginning of array)
            {
                curr = -1;
                next = 0;
            }

            if(playerTurn == 1 && next == player2StoreIndex || playerTurn == 2 && next == player1StoreIndex) //if opponent's store, skip seed drop
            {
                i++; //no seed is dropped, counter restores by one
            }
            else //drop seed
            {
                board[next] = board[next]+1; //drops a seed
            }
            curr++; //moves to next index
        }

        int check1 = checkStealSeeds(); //returns 1 if steal occurred
        boolean gameEnd = gameEndCheck(); //calls showBoard() if game end condition is met

        if(!gameEnd) //if game end conditions are not yet met
        {
            int check2 = checkRepeatTurn(); //returns 1 if repeat turn occurred

            if(check1 == 0 && check2 == 0) //if neither a steal or repeat occurred
            {
                hideMessage(); //ensure that both promptName and eventPrompt are hidden
            }

            playerTurnSwitch();
            showPlayerTurn();
            disableEnablePits(); //disables and enables pit buttons based on playerTurn value
            showBoard();
        }
        else //gameEnd == true
        {
            disablePitButtons(); //disable pit buttons since game is over
            showContinueButton(); //display continue button, which displays play again message after being clicked
        }
    }

    public void playerTurnSwitch() //switches playerTurn value
    {
        if(playerTurn == 1)
        {
            playerTurn = 2;
        }
        else //playerTurn == 2
        {
            playerTurn = 1;
        }
    }

    public void disableEnablePits() //method to determine which pits are enabled based on playerTurn
    {
        if(playerTurn == 1)
        {
            for(int index=8;index<14;index++) //iterate through player 2's pits (index 8-13 inclusive)
            {
                View v = findViewById(getPitID(index)); //get View object
                v.setEnabled(false); //makes button disabled
            }
            for(int index=1;index<7;index++) //iterate through player 1's pits (index 1-7 inclusive)
            {
                View v = findViewById(getPitID(index)); //get View object
                v.setEnabled(board[index] != 0); //makes button enabled if not empty and disabled if empty
            }
        }
        else //playerTurn == 2
        {
            for(int index=8;index<14;index++) //iterate through player 2's pits (index 8-13 inclusive)
            {
                View v = findViewById(getPitID(index)); //get View object
                v.setEnabled(board[index] != 0); //makes button enabled if not empty and disabled if empty
            }
            for(int index=1;index<7;index++) //iterate through player 1's pits (index 1-7 inclusive)
            {
                View v = findViewById(getPitID(index)); //get View object
                v.setEnabled(false); //makes button enabled
            }
        }
    }

    public int checkRepeatTurn() //checks to see if sowing ends in own store
    {
        if(playerTurn == 1 && curr == player1StoreIndex || playerTurn == 2 && curr == player2StoreIndex) //player ended turn by dropping seed in own store
        {
            showMessage(true, 2); //repeatTurn message
            playerTurnSwitch(); //change player turn so playerTurnSwitch() from playerMove will change it back
            return 1;
        }
        else
        {
            return 0;
        }
    }

    public int checkStealSeeds() //checks to see if sowing ends on own empty pit, if yes last seed + opposing pit seeds is all deposited in store
    {
        int temp, steal;

        if(playerTurn == 1 && curr > 0 && curr < pits/2 && board[curr] == 1) //player 1's last seed drops on own empty pit
        {
            board[curr] = 0; //last pit set to 0 (that last seed gets added to player 1's store)
            temp = (pits/2)-curr; //distance from curr to pits/2
            steal = board[(pits/2)+temp]; //steal distance from pits/2 in P2's side
            board[(pits/2)+temp] = 0; //set p2 pit where seeds were stolen to 0 seeds
            board[player1StoreIndex] += steal+1; //add all seeds to P2's store

            showMessage(true, 3); //displays steal seeds message
            return 1;
        }
        else if(playerTurn == 2 && curr < pits && curr > pits/2 && board[curr] == 1) //player 2's last seed drops on own empty pit
        {
            board[curr] = 0; //last pit set to 0 (that last seed gets added to player 2's store)
            temp = curr-(pits/2); //distance from curr to pits/2
            steal = board[(pits/2)-temp]; //steal distance from pits/2 in P1's side
            board[(pits/2)-temp] = 0; //set p1 pit where seeds were stolen to 0 seeds
            board[player2StoreIndex] += steal+1; //add all seeds to P2's store

            showMessage(true, 3); //displays steal seeds message
            return 1;
        }
        else
        {
            return 0;
        }
    }

    public boolean gameEndCheck() //if win condition is met, initialize/replace winner value
    {
        //condition 1 for winning the game: either player holds the majority of seeds in their store
        if(board[player1StoreIndex] > (((pits-2) * seeds) / 2)) //if player 1 has majority of seeds, player 1 wins
        {
            showBoard();
            player1.incPlayerWinCounter();
            displayWinner(1, 1);
            showPlayerWinCounter();
            return true;
        }
        else if(board[player2StoreIndex] > (((pits-2) * seeds) / 2)) //if player 2 has majority of seeds, player 2 wins
        {
            showBoard();
            player2.incPlayerWinCounter();
            displayWinner(2, 1);
            showPlayerWinCounter();
            return true;
        }

        //condition 2 for winning the game: either player's pits are all empty
        int pitsPerPlayer = (pits-2) / 2; //number of pits per player
        int p1EmptyPitCounter = 0; //number of player 1 pits that are empty
        int p2EmptyPitCounter = 0; //number of player 2 pits that are empty

        if(playerTurn == 1)
        {
            for(int i=0;i<pitsPerPlayer;i++)
            {
                if(board[i+1] != 0) //if any of player 1's pits are not empty, exit loop
                {
                    break;
                }
                p1EmptyPitCounter++; //increment number of empty pits counted
            }
        }
        else //playerTurn == 2
        {
            for(int i=0;i<pitsPerPlayer;i++)
            {
                if(board[pits-1-i] != 0) //if any of player 1's pits are not empty, exit loop
                {
                    break;
                }
                p2EmptyPitCounter++; //increment number of empty pits counted
            }
        }

        int temp;

        if(p1EmptyPitCounter == pitsPerPlayer) //all of player 1's pits are empty
        {
            for(int i=0;i<pitsPerPlayer;i++) //player 2 takes all of their own seeds into their own store
            {
                temp = board[pits-1-i];
                board[pits-1-i] = 0;
                board[player2StoreIndex] += temp;
            }

            showBoard(); //display new board

            //determine winner
            if(board[player1StoreIndex] > board[player2StoreIndex])
            {
                player1.incPlayerWinCounter();
                displayWinner(1, 2);
                showPlayerWinCounter();
                return true;
            }
            else if(board[player2StoreIndex] > board[player1StoreIndex])
            {
                player2.incPlayerWinCounter();
                displayWinner(2, 2);
                showPlayerWinCounter();
                return true;
            }
            else //tie
            {
                displayWinner(0, 2);
                showPlayerWinCounter();
                return true;
            }
        }
        else if(p2EmptyPitCounter == pitsPerPlayer) //all of player 2's pits are empty
        {
            for(int i=0;i<pitsPerPlayer;i++) //player 1 takes all of their own seeds into their own store
            {
                temp = board[i+1];
                board[i+1] = 0;
                board[player1StoreIndex] += temp;
            }

            showBoard(); //display new board

            //determine winner
            if(board[player1StoreIndex] > board[player2StoreIndex])
            {
                player1.incPlayerWinCounter();
                displayWinner(1, 2);
                showPlayerWinCounter();
                return true;
            }
            else if(board[player2StoreIndex] > board[player1StoreIndex])
            {
                player2.incPlayerWinCounter();
                displayWinner(2, 2);
                showPlayerWinCounter();
                return true;
            }
            else //tie
            {
                displayWinner(0, 2);
                showPlayerWinCounter();
                return true;
            }
        }
        return false;
    }

    public void displayWinner(int player, int messageType)
    {
        /*
        messageType
        1 = Win by majority seeds
        2 = Win by empty pits
        player
        1 = Player 1 wins
        2 = Player 2 wins
        0 = Tie
         */

        View v1, v2;
        String player1Name;
        String player2Name;

        v1 = findViewById(R.id.player1); //get player1 name
        TextView text1 = (TextView) v1;
        player1Name = text1.getText().toString(); //initialize playerName

        v2 = findViewById(R.id.player2); //get player2 name
        TextView text2 = (TextView) v2;
        player2Name = text2.getText().toString(); //initialize playerName

        View promptName = findViewById(R.id.promptName); //get namePrompt view object object
        View eventPrompt = findViewById(R.id.eventPrompt); //get eventPrompt view object

        if(messageType == 1) //(win by majority seeds)
        {
            if(player == 1)
            {
                ((TextView) promptName).setText(player1Name);
                promptName.setVisibility(View.VISIBLE);
            }
            else if(player == 2)
            {
                ((TextView) promptName).setText(player2Name);
                promptName.setVisibility(View.VISIBLE);
            }
            ((TextView) eventPrompt).setText("wins by having the majority of seeds!");
        }
        else //messageType == 2 (win by empty pits)
        {
            //display who captures the remaining seeds
            if(playerTurn == 1)
            {
                ((TextView) promptName).setText(player2Name+" captures the remaining seeds.");
                promptName.setVisibility(View.VISIBLE);
            }
            else //playerTurn == 2
            {
                ((TextView) promptName).setText(player1Name+" captures the remaining seeds.");
                promptName.setVisibility(View.VISIBLE);
            }

            //display winner
            if(player == 1)
            {
                ((TextView) eventPrompt).setText(player1Name+" wins by having the majority of seeds!");
            }
            else if(player == 2)
            {
                ((TextView) eventPrompt).setText(player2Name+" wins by having the majority of seeds!");
            }
            else //player == 0
            {
                ((TextView) eventPrompt).setText("game is tied!");
            }
        }
        eventPrompt.setVisibility(View.VISIBLE); //makes eventPrompt visible
    }

    public void showPlayerWinCounter()
    {
        TextView p1WinCounter = (TextView)findViewById(R.id.p1WinCounter);
        TextView p2WinCounter = (TextView)findViewById(R.id.p2WinCounter);
        p1WinCounter.setText(player1.getPlayerWinCounter().toString());
        p2WinCounter.setText(player2.getPlayerWinCounter().toString());
    }

    public void showContinueButton() //shows continue button
    {
        View v = findViewById(R.id.continueButton); //get View object
        Button continueButton = (Button)v;
        continueButton.setEnabled(true); //makes button enabled
        continueButton.setVisibility(View.VISIBLE); //make button visible
    }

    public void continueButtonPressed(View v) //shows playAgain message and buttons, hides and disables continueButton
    {
        pulse(v, 300); //button pulses for 300ms

        //change message and buttons after 300ms
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            hideMessage(); //hides winner message
            showMessage(false,4); //shows playAgain message and buttons
            Button continueButton = (Button)v;
            continueButton.setEnabled(false); //makes button enabled
            continueButton.setVisibility(View.INVISIBLE); //make button visible
        }, 300);
    }

    public void yesPlayAgain(View v) //yes to playAgain is pressed
    {
        pulse(v, 300); //button pulses for 300ms

        //change message, buttons, and start the game after 300ms
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            hideYesNoButtons(); //hide both yes and no buttons
            hideMessage();
            changeFirstTurnPitButtons();
            startGame();
        }, 300);
    }

    public void noPlayAgain(View v) //no to playAgain is pressed, app exits after 5 seconds
    {
        pulse(v, 300); //button pulses for 300ms

        //change message and buttons after 300ms
        Handler handler1 = new Handler();
        handler1.postDelayed(() -> {
            hideYesNoButtons(); //hide both yes and no buttons
            showMessage(false, 5); //thank you for playing message displayed
        }, 300);

        //go to main menu after 5 secs
        Handler handler2 = new Handler(); //create Handler object
        handler2.postDelayed(() -> {
            Intent i = new Intent(v.getContext(), MainActivity.class); //create intent to main page
            startActivity(i); //go to main page
            finish(); //finish current activity
        }, 5000); //5 second delay
    }

    public void showMessage(boolean showPlayerName, int messageType)
    {
        /*
        messageType
        1 = Prompt for first-turn steal
        2 = Repeat turn
        3 = Steal seeds by landing on empty pit
        4 = Prompt to play again
        5 = Thank you for playing
         */

        if(showPlayerName) //if showPlayerName == true
        {
            String playerName;
            View v;
            if(playerTurn == 1)
            {
                v = findViewById(R.id.player1); //get player1 name
            }
            else //playerTurn == 2
            {
                v = findViewById(R.id.player2); //get player2 name
            }

            //set namePrompt as playerName and make visible
            TextView text = (TextView) v;
            playerName = text.getText().toString(); //initialize player2Name
            View promptName = findViewById(R.id.promptName); //get namePrompt view object object
            ((TextView) promptName).setText(playerName);
            promptName.setVisibility(View.VISIBLE);
        }

        //set eventPrompt message and make visible
        View eventPrompt = findViewById(R.id.eventPrompt); //get eventPrompt view object
        switch(messageType) //determine message
        {
            case 1:
                ((TextView) eventPrompt).setText("steal first move?");
                showYesNoButtons(1);
                break;
            case 2:
                ((TextView) eventPrompt).setText("gets another turn!");
                break;
            case 3:
                ((TextView) eventPrompt).setText("made a steal!");
                break;
            case 4:
                ((TextView) eventPrompt).setText("Play again?");
                showYesNoButtons(2);
                break;
            case 5:
                ((TextView) eventPrompt).setText("Thank you for playing!");
                break;
            default:
                exit(0);
        }
        eventPrompt.setVisibility(View.VISIBLE); //makes eventPrompt visible
    }

    public void hideMessage()
    {
        View promptName = findViewById(R.id.promptName); //get View object
        promptName.setVisibility(View.INVISIBLE); //makes eventPrompt visible

        View eventPrompt = findViewById(R.id.eventPrompt); //get View object
        eventPrompt.setVisibility(View.INVISIBLE); //makes eventPrompt visible
    }

    public void showYesNoButtons(int type) //shows the yes no buttons
    {
        View v1 = findViewById(R.id.yes); //get View object
        Button yesButton = (Button)v1;
        View v2 = findViewById(R.id.no); //get View object
        Button noButton = (Button)v2;

        if(type == 1) //yes no button for first-turn steal
        {
            yesButton.setOnClickListener(this::yesSteal); //set yes button to yesSteal
            noButton.setOnClickListener(this::noSteal); //set no button to noSteal
        }
        else //type == 2, yes no button for play again
        {
            yesButton.setOnClickListener(this::yesPlayAgain); //set yes button to yesPlayAgain
            noButton.setOnClickListener(this::noPlayAgain); //set no button to noPlayAgain
        }

        yesButton.setEnabled(true); //makes button enabled
        yesButton.setVisibility(View.VISIBLE); //make button visible
        noButton.setEnabled(true); //makes button enabled
        noButton.setVisibility(View.VISIBLE); //make button visible
    }

    public void hideYesNoButtons() //hides the yes no buttons
    {
        View v1 = findViewById(R.id.yes); //get View object
        v1.setEnabled(false); //makes button enabled
        v1.setVisibility(View.INVISIBLE); //make button visible

        View v2 = findViewById(R.id.no); //get View object
        v2.setEnabled(false); //makes button enabled
        v2.setVisibility(View.INVISIBLE); //make button visible
    }

    public void changeFirstTurnPitButtons() //change all on-click to firstTurn (occurs at the end of every game)
    {
        for(int index=1;index<board.length;index++)
        {
            if(index == 7) //player 1's pit
            {
                continue; //skip iteration
            }
            Button button = (Button) findViewById(getPitID(index));
            button.setOnClickListener(this::firstTurn);
        }
    }

    public int getPitID(int index) //returns the ID number of the corresponding pit in activity
    {
        switch(index)
        {
            case 0: return R.id.index0;
            case 1: return R.id.index1;
            case 2: return R.id.index2;
            case 3: return R.id.index3;
            case 4: return R.id.index4;
            case 5: return R.id.index5;
            case 6: return R.id.index6;
            case 7: return R.id.index7;
            case 8: return R.id.index8;
            case 9: return R.id.index9;
            case 10: return R.id.index10;
            case 11: return R.id.index11;
            case 12: return R.id.index12;
            case 13: return R.id.index13;
            default: exit(0); //should never execute
                return 0; //should never execute
        }
    }

    public int getPitIndex(int viewID) //returns the corresponding index of the pit (button) pressed
    {
        switch(viewID)
        {
            case R.id.index1: return 1;
            case R.id.index2: return 2;
            case R.id.index3: return 3;
            case R.id.index4: return 4;
            case R.id.index5: return 5;
            case R.id.index6: return 6;
            case R.id.index8: return 8;
            case R.id.index9: return 9;
            case R.id.index10: return 10;
            case R.id.index11: return 11;
            case R.id.index12: return 12;
            case R.id.index13: return 13;
            default: exit(0); //should never execute
                return 0; //should never execute
        }
    }

    public void pulse(View v, int duration)
    {
        YoYo.with(Techniques.Pulse).duration(duration).playOn(v); //view will pulse for duration ms
    }
}