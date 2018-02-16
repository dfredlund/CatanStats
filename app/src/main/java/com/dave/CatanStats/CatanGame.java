package com.dave.CatanStats;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONException;
import org.json.JSONObject;

/**
 Created: DFredlund 02/13/2018
 */

/*
CatanGame will encapsulate the entirety of a game of Catan.
 GetTurnAdapter() returns the ArrayAdapter showing the list of turns
 Todo: Game Statistics
 Todo: Edit Players/Colors
 Todo: Edit Turns
 Todo: Add missing turn
 */
public class CatanGame {

    //region internals
    private ArrayList<CatanTurn> turnList = new ArrayList<>();
    private int currentTurn = 1;
    private int gameID = 0;
    private ArrayList<Player> playerOrder = new ArrayList<>();
    private CatanTurnAdapter turnAdapter = null;
    private Context context;
    //endregion

    //region Constructors
    public CatanGame(Context context)
    {
        playerOrder.addAll(Arrays.asList(new Player(PlayerColor.RED, null), new Player(PlayerColor.BLUE, null), new Player(PlayerColor.WHITE, null)));
        this.context = context;
    }
    public CatanGame(Context context, Cursor c)
    {
        this(context);
    }
    //endregion

    //region public methods
    public void RollDice(View v, int buttonNumber)
    {
        turnList.add(new CatanTurn(playerOrder.get(currentTurn % playerOrder.size()), currentTurn++, buttonNumber, this.turnList));
        if(turnAdapter != null)
            turnAdapter.notifyDataSetChanged();
    }

    public CatanTurnAdapter GetTurnAdapter()
    {
        if(turnAdapter == null)
            turnAdapter = new CatanTurnAdapter(context, turnList);
        return turnAdapter;
    }

    public void UpdateTurn(CatanTurn updatedTurn)
    {
		if(updatedTurn != null)
		{
			CatanTurn matchingTurn = turnList.parallelStream().filter(c -> c.getTurnNumber() == updatedTurn.getTurnNumber()).findFirst().orElse(null);
			if(matchingTurn != null)
			{
				matchingTurn.setPlayer(updatedTurn.getPlayer());
				matchingTurn.setRollValue(updatedTurn.getRollValue());
			}
			if(turnAdapter != null) turnAdapter.notifyDataSetChanged();
		}
    }

    public ArrayList<Player> GetPlayerList()
    {
        return playerOrder;
    }

    //endregion

    //region private methods

    //endregion

    //region internal classes/enums




    public static class Player implements Serializable
    {
        PlayerColor color;
        String name;
        public Player(PlayerColor c, String n)
        {
            color = c;
            name = (n != null) ?  n : "";
        }
	    public String GetPlayerNameFormatted() {
		    return "Player: "+ this.name + (this.name.isEmpty() ? "" : " ,Color: ") + this.color;
	    }
	    @Override
	    public String toString()
	    {
	    	return GetPlayerNameFormatted();
	    }

	    @Override
	    public boolean equals(Object obj)
	    {
	    	if(obj == null)
	    		return false;

	    	if(!Player.class.isAssignableFrom(obj.getClass()))
	    	    return false;

		    final Player other = (Player) obj;

		    return other.color == this.color && this.name.equalsIgnoreCase(other.name);

	    }

    }

    public enum PlayerColor
    {
        RED,
        BLUE,
        WHITE,
        GREEN
    }


    //endregion
}
