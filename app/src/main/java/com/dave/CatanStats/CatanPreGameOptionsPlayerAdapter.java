package com.dave.CatanStats;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import com.dave.CatanStats.CatanGame.*;

/**
 Created: DFredlund 02/19/2018
 */

public class CatanPreGameOptionsPlayerAdapter extends ArrayAdapter<Player> {

	public CatanPreGameOptionsPlayerAdapter(Context context, ArrayList<Player> players)
	{
		super(context, 0, players);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		Player player = getItem(position);
		if (convertView == null)
		{
			LayoutInflater inflater = LayoutInflater.from(getContext());
			convertView = inflater.inflate(R.layout.adapter_player, null);
		}

		TextView adapterPlayerTurnOrder = (TextView) convertView.findViewById(R.id.adapterPlayerTurnOrder);
		TextView adapterPlayerColor = (TextView) convertView.findViewById(R.id.adapterPlayerColor);
		TextView adapterPlayerName = (TextView) convertView.findViewById(R.id.adapterPlayerName);

		adapterPlayerTurnOrder.setText("Turn Order: " + String.valueOf(position + 1));
		adapterPlayerColor.setText("Color: "+ player.getColor());
		adapterPlayerName.setText("Name: " + player.getName());
		return convertView;
	}


}
