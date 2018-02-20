package com.dave.CatanStats;

import android.content.Context;
import android.database.Cursor;
import android.view.View;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 Created: DFredlund 02/13/2018
 */

/**
 CatanGame will encapsulate the entirety of a game of Catan.
 GetTurnAdapter() returns the ArrayAdapter showing the list of turns
 Todo: Game Statistics
 Todo: Add missing turn
 Todo: Delete turn
 Todo: Roll for me
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
	private CatanGame(Context context, int gameID)
	{
		//playerOrder.addAll(Arrays.asList(new Player(PlayerColor.RED, null), new Player(PlayerColor.BLUE, null), new Player(PlayerColor.WHITE, null)));
		this.gameID = gameID;
		this.context = context;
	}
	//endregion

	//region public methods
	public static CatanGame GetExistingOrCreateCatanGame(Context context, int gameID, ArrayList<Player> playerArrayList)
	{
		CatanGame catanGame = GetExistingOrCreateCatanGame(context, gameID);
		catanGame.playerOrder = playerArrayList;
		return catanGame;
	}
	public static CatanGame GetExistingOrCreateCatanGame(Context context, int gameID)
	{
		CatanGame catanGame = new CatanGame(context, gameID);
		CatanStatsDatabase catanStatsDatabase = CatanStatsDatabase.getInstance(context);
		Cursor turnCursor = catanStatsDatabase.GetTurnListByGameID(gameID);
		while(turnCursor!= null && turnCursor.moveToNext())
		{
			catanGame.turnList.add(new CatanTurn(turnCursor.getString(0)));
		}
		Cursor playerCursor = catanStatsDatabase.GetOrderedPlayerCursorByGameID(gameID);
		while(playerCursor!= null && playerCursor.moveToNext())
		{
			catanGame.playerOrder.add(new Player(PlayerColor.valueOf(playerCursor.getString(0)),playerCursor.getString(1)));
		}
		catanGame.currentTurn = catanGame.turnList.size() + 1;
		return catanGame;
	}

	public CatanTurn RollDice(View v, int buttonNumber)
	{
		CatanTurn newTurn = new CatanTurn(playerOrder.get((currentTurn - 1) % playerOrder.size()), currentTurn++, buttonNumber, this.turnList);
		turnList.add(newTurn);
		if(turnAdapter != null)
			turnAdapter.notifyDataSetChanged();
		return newTurn;
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
				//Todo: Update Future turns stats
				matchingTurn.UpdateAmountRolledSoFar(this.turnList.parallelStream().filter(c -> c.getTurnNumber() < matchingTurn.getTurnNumber()).collect(Collectors.toCollection(ArrayList::new)));
				if(turnAdapter != null) turnAdapter.notifyDataSetChanged();
			}

		}
	}

	public ArrayList<Player> GetPlayerList()
	{
		return playerOrder;
	}

	public int GetGameID()
	{
		return gameID;
	}

	public static ArrayList<PlayerColor> GetPlayerColors()
	{
		return new ArrayList<PlayerColor>()
		{
			{
				add(PlayerColor.RED);
				add(PlayerColor.BLUE);
				add(PlayerColor.WHITE);
				add(PlayerColor.GREEN);
				add(PlayerColor.ORANGE);
				add(PlayerColor.BROWN);
			}
		};
	}
	//endregion

	//region private methods

	//endregion

	//region internal classes/enums
	public static class Player implements Serializable
	{
		private PlayerColor color;
		private String name;
		public Player(PlayerColor c, String n)
		{
			color = c;
			name = (n != null) ?  n : "";
		}
		public String GetPlayerNameFormatted() {
			return "Player: "+ this.name + (this.name.isEmpty() ? "" : ", Color: ") + this.color;
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
		public void setColor(PlayerColor color)
		{
			this.color = color;
		}
		public void setName(String name)
		{
			this.name = name;
		}
		public PlayerColor getColor()
		{
			return color;
		}
		public String getName()
		{
			return name;
		}

	}

	public enum PlayerColor
	{
		RED,
		BLUE,
		WHITE,
		GREEN,
		ORANGE,
		BROWN
	}




	//endregion
}
