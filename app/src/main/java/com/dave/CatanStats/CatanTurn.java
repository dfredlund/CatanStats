package com.dave.CatanStats;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

import com.dave.CatanStats.CatanGame.*;
/**
Created: DFredlund 02/14/2018
 */
    /*
    CatanTurn Encapsulates information about a single turn in each CatanGame.
    There may be optimization necessary for the "Amount Rolled So Far" logic
    */
public class CatanTurn implements Serializable
{
	//region private variables
	private CatanGame.Player player;
	private int turnNumber;
	private int rollValue;
	private int amountRolledSoFar;
	private HashMap<Integer,Float> expectedValues;
	private transient final ForegroundColorSpan red = new ForegroundColorSpan(Color.rgb(255,0,0));
	private transient final ForegroundColorSpan green = new ForegroundColorSpan(Color.rgb(0,255,0));
	private final String COLOR = "color";
	private final String PLAYER_NAME= "playerName";
	private final String TURN_NUMBER = "turnNumber";
	private final String ROLL_VALUE = "rollValue";
	private final String AMOUNT_ROLLED_SO_FAR = "amountRolledSoFar";
	private static final long serialVersionUID = -8959832007991513854L;

	//endregion

	//region constructor
	public CatanTurn(Player player, final int turnNumber, int rollValue, ArrayList<CatanTurn> turnList)
	{
		this.player = player;
		this.turnNumber=turnNumber;
		this.rollValue=rollValue;
		expectedValues = new ExpectedValues().getExpectedValueMap();
		UpdateAmountRolledSoFar(turnList.parallelStream().filter(c -> c.getTurnNumber() < turnNumber).collect(Collectors.toCollection(ArrayList::new)));
	}
	public CatanTurn(){};

	public CatanTurn (JSONObject jsonObject)
	{
		try
		{
			String color = jsonObject.getString(COLOR);
			String playerName = jsonObject.getString(PLAYER_NAME);
			this.player = new Player(PlayerColor.valueOf(color), playerName);
			this.turnNumber = jsonObject.getInt(TURN_NUMBER);
			this.rollValue = jsonObject.getInt(ROLL_VALUE);
			this.amountRolledSoFar = jsonObject.getInt(AMOUNT_ROLLED_SO_FAR);
			this.expectedValues = new CatanTurn.ExpectedValues().getExpectedValueMap();
		}
		catch(JSONException e)
		{
			e.printStackTrace();
		}
	}

	//endregion

	//region public methods
	public int getTurnNumber(){
		return turnNumber;
	}
	public int getRollValue(){
		return rollValue;
	}
	public Player getPlayer(){return player;}
	public void setRollValue(int roll){rollValue = roll;}
	public void setPlayer(Player p) { player = p;}
	public String GetPlayerNameFormatted() {
		return player.GetPlayerNameFormatted();
	}
	public String GetTurnNumberFormatted()
	{
		return "Turn Number: " + Integer.toString(turnNumber);
	}
	public String GetRollNumberFormatted()
	{
		return "Roll Value: " + Integer.toString(rollValue);
	}

	/**
	 Format is Rolled: [# Rolled so far in game] [Percent of rolls equal to this value]% [Difference from expected value]%
	 Difference from Expected value is formatted to be Red if below average, green if above average
	 */
	public Spannable GetAsOfRolledAmountFormatted()
	{
		float differenceFromExpected = (float)amountRolledSoFar / turnNumber - expectedValues.get(this.rollValue);
		SpannableStringBuilder formattedSpan = new SpannableStringBuilder("Rolled: "+ Integer.toString(amountRolledSoFar) +" ("
				                                                                  + String.format("%.2f",100 * (float)amountRolledSoFar / turnNumber)+
				                                                                  "%) ");
		int initialSpannableLength = formattedSpan.length();
		formattedSpan.append(String.format("%.2f",differenceFromExpected * 100));
		ForegroundColorSpan differenceColor = differenceFromExpected >= 0 ? green: red;
		formattedSpan.setSpan(differenceColor,initialSpannableLength, formattedSpan.length(),Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		return formattedSpan;
	}
	public void UpdateAmountRolledSoFar(ArrayList<CatanTurn> turnList)
	{
		//TurnList should be filtered down to turns that have happened so far (Before this Turn #)
		Long tempCount = turnList.parallelStream().filter(c -> c.getRollValue() == this.getRollValue()).count();
		amountRolledSoFar = tempCount.intValue() + 1;
	}

	public JSONObject toJSONObect()
	{
		JSONObject catanTurn = new JSONObject();
		try
		{
			catanTurn.put(COLOR, this.player.color);
			catanTurn.put(PLAYER_NAME, this.player.name);
			catanTurn.put(TURN_NUMBER, this.turnNumber);
			catanTurn.put(ROLL_VALUE, this.rollValue);
			catanTurn.put(AMOUNT_ROLLED_SO_FAR, this.amountRolledSoFar);

		}
		catch (JSONException e)
		{
			e.printStackTrace();
			return new JSONObject();
		}
		return catanTurn;
	}
	//endregion

	private static class ExpectedValues
	{
		private HashMap<Integer, Float> expectedValueMap = new HashMap<Integer,Float>();
		public HashMap<Integer, Float> getExpectedValueMap()
		{
			return expectedValueMap;
		}
		public void setExpectedValueMapValue(Integer rollNumber, Float expectedValue)
		{
			expectedValueMap.put(rollNumber, expectedValue);
		}

		public ExpectedValues()
		{
			this.setExpectedValueMapValue(2,1/36f);
			this.setExpectedValueMapValue(3,2/36f);
			this.setExpectedValueMapValue(4,3/36f);
			this.setExpectedValueMapValue(5,4/36f);
			this.setExpectedValueMapValue(6,5/36f);
			this.setExpectedValueMapValue(7,6/36f);
			this.setExpectedValueMapValue(8,5/36f);
			this.setExpectedValueMapValue(9,4/36f);
			this.setExpectedValueMapValue(10,3/36f);
			this.setExpectedValueMapValue(11,2/36f);
			this.setExpectedValueMapValue(12,1/36f);

		}
	}
}
