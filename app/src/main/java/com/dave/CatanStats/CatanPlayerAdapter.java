package com.dave.CatanStats;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dave.CatanStats.CatanGame.*;

import java.util.ArrayList;

/**
Created: DFredlund 2/11/2018.
 */

public class CatanPlayerAdapter extends ArrayAdapter<Player> {

	public CatanPlayerAdapter(Context context, ArrayList<Player> players)
	{
		super(context, 0, players);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		if(convertView == null)
		{
			LayoutInflater inflater = LayoutInflater.from(getContext());
			convertView = inflater.inflate(android.R.layout.simple_spinner_item,null);
		}
		Player player = getItem(position);
		if(player != null)
		{
			String playerNameFormatted = player.GetPlayerNameFormatted();
			TextView spinnerText = convertView.findViewById(android.R.layout.simple_spinner_item);
		}
		return convertView;
	}


}
