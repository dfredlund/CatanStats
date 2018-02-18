package com.dave.CatanStats;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dave.CatanStats.CatanTurn;

import java.util.ArrayList;

/**
Created: DFredlund 02/13/2018
 */

public class CatanTurnAdapter extends ArrayAdapter<CatanTurn> {

    public CatanTurnAdapter(Context context, ArrayList<CatanTurn> catanTurns)
    {
        super(context, 0, catanTurns);
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        CatanTurn turn = getItem(position);
        if(convertView == null)
        {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.adapter_catan_turn,null);
        }
        TextView playerName = (TextView) convertView.findViewById(R.id.playerNameID);
        TextView turnNumber = (TextView) convertView.findViewById(R.id.turnNumberID);
        TextView rollNumber = (TextView) convertView.findViewById(R.id.rollNumberID);
        TextView totalRolledAmount = (TextView) convertView.findViewById(R.id.totalRolledAmountID);

        playerName.setText(turn.GetPlayerNameFormatted());
        turnNumber.setText(turn.GetTurnNumberFormatted());
        rollNumber.setText(turn.GetRollNumberFormatted());
        totalRolledAmount.setText(turn.GetAsOfRolledAmountFormatted());
        return convertView;
    }

}
