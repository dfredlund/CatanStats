package com.dave.CatanStats;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;


/**
 Created: DFredlund 02/13/2018
 */

/*
 This Activity will appear once a user has created a game, showing a list of Turns and the ability to
 enter new rolls
    TODO Update CatanTurn clickable to update turn values + update statistics
    TODO add a Button to update Players
    TODO add a way to retrieve recent games (Likely in Different Activity)
    TODO add a way to create a new game (Likely in different Activity)
    TODO save game to database
 */
public class RollActivity extends AppCompatActivity {


    private ListView catanGameTurnList;
    CatanStatsDatabase catanStatsDatabase;
    CatanGame catanGame;
    public static final String TURN = "Turn";
    public static final String PLAYER_LIST = "Player_List";
    private static final int CATAN_TURN_MODIFIED_REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roll);
        catanStatsDatabase = new CatanStatsDatabase(this);
        catanGameTurnList = findViewById(R.id.turnList);
        catanGame = new CatanGame(this);
        //region declare Buttons
        final Button button2 = findViewById(R.id.button2);
        final Button button3 = findViewById(R.id.button3);
        final Button button4 = findViewById(R.id.button4);
        final Button button5 = findViewById(R.id.button5);
        final Button button6 = findViewById(R.id.button6);
        final Button button7 = findViewById(R.id.button7);
        final Button button8 = findViewById(R.id.button8);
        final Button button9 = findViewById(R.id.button9);
        final Button button10 = findViewById(R.id.button10);
        final Button button11 = findViewById(R.id.button11);
        final Button button12 = findViewById(R.id.button12);
        //endregion

        //region set OnClickListeners
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NumberButtonClicked(v,2);
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NumberButtonClicked(v,3);
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NumberButtonClicked(v,4);
            }
        });

        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NumberButtonClicked(v,5);
            }
        });

        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NumberButtonClicked(v,6);
            }
        });

        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NumberButtonClicked(v,7);
            }
        });

        button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NumberButtonClicked(v,8);
            }
        });

        button9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NumberButtonClicked(v,9);
            }
        });

        button10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NumberButtonClicked(v,10);
            }
        });

        button11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NumberButtonClicked(v,11);
            }
        });

        button12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NumberButtonClicked(v,12);
            }
        });


        //endregion

        CatanTurnAdapter catanTurnAdapter = catanGame.GetTurnAdapter();

        catanGameTurnList.setAdapter(catanTurnAdapter);
        catanGameTurnList.setOnItemClickListener(
                new OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View v, int position, long rowId){
                        Intent intent = new Intent(RollActivity.this, CatanTurnEditorActivity.class);
                        intent.putExtra(TURN, catanTurnAdapter.getItem(position));
                        intent.putExtra(PLAYER_LIST, catanGame.GetPlayerList());
                        startActivityForResult(intent,CATAN_TURN_MODIFIED_REQUEST_CODE);
                    }
                }
        );
    }


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent)
	{
		super.onActivityResult(requestCode, resultCode, intent);

		if (requestCode == CATAN_TURN_MODIFIED_REQUEST_CODE)
		{
			if (resultCode == RESULT_OK)
			{
				CatanTurn updatedTurn = (CatanTurn) intent.getSerializableExtra(TURN);
				catanGame.UpdateTurn(updatedTurn);
			}
		}
	}
    public void NumberButtonClicked(View vw, int buttonNumber)
    {
        catanGame.RollDice(vw, buttonNumber);
    }

}



