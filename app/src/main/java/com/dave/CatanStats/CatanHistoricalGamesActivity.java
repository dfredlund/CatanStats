package com.dave.CatanStats;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 Created: DFredlund 02/18/2017
 */

public class CatanHistoricalGamesActivity extends Activity {

	public static final String GAME_NUMBER = "GameNumber";
	public static final String GAME_PLAYED_ON = "GamePlayedOn";
	public static final String NUMBER_TURNS = "NumberTurns";
	public static final String PLAYER_NAMES = "PlayerNames";
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_historical_games);

		ListView catanGames = findViewById(R.id.historicalGameList);
		ArrayList<JSONObject> gamesFromDB = CatanStatsDatabase.getInstance(this).GetHistoricalGames();

		ArrayAdapter<JSONObject> arrayAdapter = new CatanHistoricalGamesAdapter(this, gamesFromDB);
		catanGames.setAdapter(arrayAdapter);

		catanGames.setOnItemClickListener(new AdapterView.OnItemClickListener()
		                                  {
			                                  @Override
			                                  public void onItemClick(AdapterView<?> parent, View view, int position, long id)

			                                  {
			                                  	try
			                                    {
				                                    Intent intent = new Intent();
				                                    intent.putExtra(StartScreenActivity.GAME_NUMBER, arrayAdapter.getItem(position).getInt(GAME_NUMBER));
				                                    setResult(RESULT_OK, intent);
				                                    finish();
			                                    }
			                                    catch(JSONException e)
			                                    {
			                                    	e.printStackTrace();
			                                    	setResult(RESULT_CANCELED, new Intent());
			                                    	finish();
			                                    }
			                                  }
		                                  }
		);

	}
}
