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
/**
 Created: DFredlund 02/16/2018
 TODO: Add/Update player list/Names
 */

public class StartScreenActivity extends AppCompatActivity
{
	public final int CATAN_NEW_GAME_REQUEST_CODE = 1;
	public final int CATAN_LOAD_GAME_REQUEST_CODE = 2;
	public final int CATAN_GET_HISTORICAL_GAMES_REQUEST_CODE = 3;
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
				Intent intent = new Intent(StartScreenActivity.this, CatanGameActivity.class);
				intent.putExtra(GAME_NUMBER, catanStatsDatabase.GetNextGameNumber());
				startActivityForResult(intent,CATAN_NEW_GAME_REQUEST_CODE);
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

		if (requestCode == CATAN_NEW_GAME_REQUEST_CODE)
		{
			if (resultCode == RESULT_OK)
			{
				int gameNumber = (int) intent.getSerializableExtra(GAME_NUMBER);
				catanStatsDatabase.UpsertGame(gameNumber);
			}
		}

		if (requestCode == CATAN_GET_HISTORICAL_GAMES_REQUEST_CODE)
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
