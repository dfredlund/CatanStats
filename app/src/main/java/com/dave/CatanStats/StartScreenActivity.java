package com.dave.CatanStats;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 Created: DFredlund 02/16/2018
 */

public class StartScreenActivity extends AppCompatActivity
{
	public final int CATAN_NEW_GAME_REQUEST_CODE = 1;
	public final int CATAN_LOAD_GAME_REQUEST_CODE = 2;
	public final int CATAN_GET_HISTORICAL_GAMES_REQUEST_CODE = 3;
	public final int CATAN_PREGAME_OPTIONS_REQUEST_CODE = 4;
	public static final String GAME_NUMBER = "Game_Number";
	private CatanStatsDatabase catanStatsDatabase;
	@Override
	protected void onCreate (Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start_screen);
		catanStatsDatabase = CatanStatsDatabase.getInstance(getApplicationContext());

		final Button newGame = findViewById(R.id.buttonNewGame);
		final Button loadGame = findViewById(R.id.buttonLoadGame);

		newGame.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				//Intent intent = new Intent(StartScreenActivity.this, CatanGameActivity.class);
				Intent intent = new Intent(StartScreenActivity.this, CatanPreGameOptionsActivity.class);
				//intent.putExtra(GAME_NUMBER, catanStatsDatabase.GetNextGameNumber());
				//startActivityForResult(intent,CATAN_NEW_GAME_REQUEST_CODE);
				startActivityForResult(intent,CATAN_PREGAME_OPTIONS_REQUEST_CODE);
			}
		});
		loadGame.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(StartScreenActivity.this, CatanHistoricalGamesActivity.class);
				startActivityForResult(intent,CATAN_GET_HISTORICAL_GAMES_REQUEST_CODE);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent)
	{
		super.onActivityResult(requestCode, resultCode, intent);

		if(requestCode == CATAN_PREGAME_OPTIONS_REQUEST_CODE && resultCode == RESULT_OK)
		{
			ArrayList<CatanGame.Player> playerArrayList = (ArrayList<CatanGame.Player>) intent.getSerializableExtra(CatanGameActivity.PLAYER_LIST);
			Intent newIntent = new Intent(StartScreenActivity.this, CatanGameActivity.class);
			int nextGameNumber = catanStatsDatabase.GetNextGameNumber();
			newIntent.putExtra(GAME_NUMBER, nextGameNumber);
			newIntent.putExtra(CatanGameActivity.PLAYER_LIST, playerArrayList);
			catanStatsDatabase.UpsertGame(nextGameNumber);
			catanStatsDatabase.BatchUpdateGamePlayer(playerArrayList,nextGameNumber);
			startActivityForResult(newIntent,CATAN_NEW_GAME_REQUEST_CODE);
		}
		else if (requestCode == CATAN_NEW_GAME_REQUEST_CODE || requestCode == CATAN_LOAD_GAME_REQUEST_CODE)
		{
			if (resultCode == RESULT_OK) //TODO: Not currently possible (only out is back button)
			{
				int gameNumber = (int) intent.getSerializableExtra(GAME_NUMBER);
				catanStatsDatabase.UpsertGame(gameNumber);
			}
		}
		else if (requestCode == CATAN_GET_HISTORICAL_GAMES_REQUEST_CODE)
		{
			if (resultCode == RESULT_OK)
			{
				int gameNumber = (int) intent.getSerializableExtra(GAME_NUMBER);
				Intent newIntent = new Intent(StartScreenActivity.this, CatanGameActivity.class);
				newIntent.putExtra(GAME_NUMBER, gameNumber);
				startActivityForResult(newIntent,CATAN_LOAD_GAME_REQUEST_CODE);
			}
		}
	}
}
