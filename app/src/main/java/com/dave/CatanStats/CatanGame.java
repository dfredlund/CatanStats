package com.dave.CatanStats;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 Created: DFredlund 02/13/2018
 */

/*
CatanGame will encapsulate the entirety of a game of Catan.
 GetTurnAdapter() returns the ArrayAdapter showing the list of turns
 Todo: Game Statistics
 Todo: Edit Players/Colors
 Todo: Edit Turns
 */
public class CatanGame {

    //region internals
    private ArrayList<CatanTurn> turnList = new ArrayList<>();
    private int currentTurn = 1;
    private int gameID = 0;
    private List<Player> playerOrder = new ArrayList<>();
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
    //endregion

    //region private methods

    //endregion

    //region internal classes/enums

    /*
    CatanTurn Encapsulates information about a single turn in each CatanGame.
    There may be optimization necessary for the "Amount Rolled So Far" logic
    */
    public class CatanTurn
    {
        private Player player;
        private int turnNumber;
        private int rollValue;
        private int amountRolledSoFar;
        private HashMap<Integer,Float> expectedValues;
        private ForegroundColorSpan red = new ForegroundColorSpan(Color.rgb(255,0,0));
        private ForegroundColorSpan green = new ForegroundColorSpan(Color.rgb(0,255,0));
        public CatanTurn(Player player, final int turnNumber, int rollValue, ArrayList<CatanTurn> turnList)
        {
            this.player = player;
            this.turnNumber=turnNumber;
            this.rollValue=rollValue;
            expectedValues = new ExpectedValues().getExpectedValueMap();
            UpdateAmountRolledSoFar(turnList.parallelStream().
                    filter(c -> c.getTurnNumber() < turnNumber).
                    collect(Collectors.toCollection(ArrayList::new)));
        }

        public int getTurnNumber(){
            return turnNumber;
        }
        public int getRollValue(){
            return rollValue;
        }
        public String GetPlayerNameFormatted() {
            return "Player: "+ player.name + (player.name.isEmpty() ? "" : " ,Color: ") + player.color;
        }
        public String GetTurnNumberFormatted()
        {
            return "Turn Number: " + Integer.toString(turnNumber);
        }
        public String GetRollNumberFormatted()
        {
            return "Roll Value: " + Integer.toString(rollValue);
        }

        /*
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
            Long tempCount = turnList.
                    parallelStream().
                    filter(c -> c.getRollValue() == this.getRollValue()).
                    count();
            amountRolledSoFar = tempCount.intValue() + 1;
        }
    }

    private class Player
    {
        PlayerColor color;
        String name;
        public Player(PlayerColor c, String n)
        {
            color = c;
            name = (n != null) ?  n : "";
        }
    }

    private enum PlayerColor
    {
        RED,BLUE,WHITE,GREEN
    }

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
    //endregion
}
