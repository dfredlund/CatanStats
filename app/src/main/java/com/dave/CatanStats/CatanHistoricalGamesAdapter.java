package com.dave.CatanStats;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dave.CatanStats.CatanGame.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
Created: DFredlund 02/18/2018
 */

public class CatanHistoricalGamesAdapter extends ArrayAdapter<JSONObject> {

	public CatanHistoricalGamesAdapter(Context context, ArrayList<JSONObject> players)
	{
		super(context, 0, players);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		JSONObject game = getItem(position);
		try
		{
			if (convertView == null)
			{
				LayoutInflater inflater = LayoutInflater.from(getContext());
				convertView = inflater.inflate(R.layout.adapter_catan_game, null);
			}
			TextView gameNumber = (TextView) convertView.findViewById(R.id.gameNumber);
			TextView gamePlayedOnDate = (TextView) convertView.findViewById(R.id.gamePlayedOnDate);
			TextView numberTurns = (TextView) convertView.findViewById(R.id.numberTurns);
			TextView playerNames = (TextView) convertView.findViewById(R.id.playerNames);

			gameNumber.setText("Game Number" + String.valueOf(game.getInt(CatanHistoricalGamesActivity.GAME_NUMBER)));
			gamePlayedOnDate.setText("Game Played On: "+ game.getString(CatanHistoricalGamesActivity.GAME_PLAYED_ON));
			numberTurns.setText("No. Turns: " + String.valueOf(game.getInt(CatanHistoricalGamesActivity.NUMBER_TURNS)));
			playerNames.setText("Players: " + game.getString(CatanHistoricalGamesActivity.PLAYER_NAMES));
			return convertView;
		}
		catch(JSONException e)
		{
			e.printStackTrace();
			LayoutInflater inflater = LayoutInflater.from(getContext());
			convertView = inflater.inflate(R.layout.adapter_catan_game, null);
			return convertView;
		}
	}


}
