package com.dave.CatanStats;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.SystemClock;
import android.provider.BaseColumns;
import android.text.format.DateUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 Created: DFredlund 02/13/2018
 */

public class CatanStatsDatabase extends SQLiteOpenHelper {

	//region static finals
	public static final String DATABASE_NAME = "CatanStats";
	public static final int DATABASE_VERSION = 1;
	private static CatanStatsDatabase sInstance;
	private static DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM);
	private static DateFormat dateTimeFormat = SimpleDateFormat.getDateTimeInstance();
	//endregion

	public static synchronized CatanStatsDatabase getInstance(Context context)
	{
		if(sInstance == null)
		{
			sInstance = new CatanStatsDatabase(context.getApplicationContext());
		}
		return sInstance;
	}

	//region constructor
	private CatanStatsDatabase(Context context){
		super(context,DATABASE_NAME, null,DATABASE_VERSION);
	}
	//endregion

	//region DBCreate/Upgrade
	@Override
	public void onCreate (SQLiteDatabase db){
		Log.d("DEBUG", SQL_CREATE_HISTORICALGAMES);
		db.execSQL(SQL_CREATE_HISTORICALGAMES);
		Log.d("DEBUG", SQL_CREATE_HISTORICALGAMETURNS);
		db.execSQL(SQL_CREATE_HISTORICALGAMETURNS);

	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		Log.w(CatanStatsDatabase.class.getName(),
				"Upgrading database from version "+ oldVersion + " to "
						+newVersion);
	}
	//endregion

	//region public methods
	public Cursor GetTurnListByGameID(int gameID)
	{
		SQLiteDatabase db = getReadableDatabase();
		Cursor gameCursor = db.query(HistoricalGameTurns.TABLE_NAME,new String[]{HistoricalGameTurns.COLUMN_TurnJSON},
				"HistoricalGames_GameNumber_FK= ?",new String[]{String.valueOf(gameID)},null, null, HistoricalGameTurns.COLUMN_HistoricalGames_TurnNumber +" ASC");

		return gameCursor;
	}

	public void UpsertGame(int gameID)
	{
		SQLiteDatabase readableDatabase = getReadableDatabase();
		Cursor cursor = readableDatabase.query(HistoricalGames.TABLE_NAME, new String[]{HistoricalGames._ID},
				String.format("%s= ?", HistoricalGames.COLUMN_GameNumber), new String[]{String.valueOf(gameID)},
				null,null,null);
		int idOfExisting= -1;
		if(cursor != null && cursor.getCount() >0 && cursor.moveToNext())
			idOfExisting = cursor.getInt(0);

		SQLiteDatabase writableDatabase = getWritableDatabase();
		ContentValues contentValues = new ContentValues();
		contentValues.put(HistoricalGames.COLUMN_ModifiedTimestamp, dateTimeFormat.format(Calendar.getInstance().getTime()).toString());

		if(idOfExisting == -1)
		{
			contentValues.put(HistoricalGames.COLUMN_GameNumber, gameID);
			contentValues.put(HistoricalGames.COLUMN_EntryDate, dateTimeFormat.format(Calendar.getInstance().getTime()).toString());
			contentValues.put(HistoricalGames.COLUMN_CreatedTimestamp, dateTimeFormat.format(Calendar.getInstance().getTime()).toString());
			writableDatabase.insertWithOnConflict(HistoricalGames.TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
		}
		else
			writableDatabase.updateWithOnConflict(HistoricalGames.TABLE_NAME, contentValues,"_id = ?", new String[]{String.valueOf(idOfExisting)}, SQLiteDatabase.CONFLICT_IGNORE);

		if(cursor != null)
			cursor.close();
	}

	public void UpsertTurn(JSONObject jsonObject, int gameID)
	{
		try
		{
			SQLiteDatabase readableDatabase = getReadableDatabase();
			int turnNumber = (int) jsonObject.get(CatanTurn.TURN_NUMBER);
			Cursor cursor = readableDatabase.query(HistoricalGameTurns.TABLE_NAME, new String[]{HistoricalGameTurns._ID},
					String.format("%s= ? and %s= ?", HistoricalGameTurns.COLUMN_HistoricalGames_GameNumber_FK,
							HistoricalGameTurns.COLUMN_HistoricalGames_TurnNumber), new String[]{String.valueOf(gameID),String.valueOf(turnNumber)},
					null,null,null);
			int idOfExisting= -1;
			if(cursor != null && cursor.getCount() >0 && cursor.moveToNext())
				idOfExisting = cursor.getInt(0);

			if(cursor != null)
				cursor.close();

			SQLiteDatabase writableDatabase = getWritableDatabase();
			ContentValues contentValues = new ContentValues();
			contentValues.put(HistoricalGameTurns.COLUMN_ModifiedTimestamp, dateTimeFormat.format(Calendar.getInstance().getTime()).toString());
			contentValues.put(HistoricalGameTurns.COLUMN_TurnJSON, jsonObject.toString());

			if(idOfExisting == -1)
			{
				contentValues.put(HistoricalGameTurns.COLUMN_HistoricalGames_GameNumber_FK, gameID);
				contentValues.put(HistoricalGameTurns.COLUMN_HistoricalGames_TurnNumber, turnNumber);
				contentValues.put(HistoricalGameTurns.COLUMN_CreatedTimestamp, dateTimeFormat.format(Calendar.getInstance().getTime()).toString());
				writableDatabase.insertWithOnConflict(HistoricalGameTurns.TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
			}
			else
				writableDatabase.updateWithOnConflict(HistoricalGameTurns.TABLE_NAME, contentValues, "_id = ?", new String[]{String.valueOf(idOfExisting)}, SQLiteDatabase.CONFLICT_IGNORE);
		}
		catch(JSONException e)
		{
			e.printStackTrace();
		}
	}

	public int GetNextGameNumber()
	{
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(HistoricalGames.TABLE_NAME, new String[] {HistoricalGames.COLUMN_GameNumber},null,null,null,null,HistoricalGames.COLUMN_GameNumber + " DESC");
		int gameNumber = 1;
		if(cursor != null && cursor.getCount()>0 && cursor.moveToNext())
			gameNumber = cursor.getInt(0) + 1;
		if(cursor != null)
			cursor.close();
		return gameNumber;
	}

	public ArrayList<JSONObject> GetHistoricalGames()
	{
		ArrayList<JSONObject> historicalGames = new ArrayList<JSONObject>();
		SQLiteDatabase readableDatabase = getReadableDatabase();
		String query = "Select "+HistoricalGames.COLUMN_GameNumber+" , "+HistoricalGames.COLUMN_EntryDate
				               + " , max(ifnull("+HistoricalGameTurns.COLUMN_HistoricalGames_TurnNumber+",0)) as NumTurns from "+HistoricalGames.TABLE_NAME +" hg Left Join "+HistoricalGameTurns.TABLE_NAME +
							   " hgt On hg."+HistoricalGames.COLUMN_GameNumber+"=hgt."+HistoricalGameTurns.COLUMN_HistoricalGames_GameNumber_FK
				+" Group By "+HistoricalGames.COLUMN_GameNumber+ " Order By "+ HistoricalGames.COLUMN_GameNumber + ";";
		Cursor cursor = readableDatabase.rawQuery(query, null);
		try
		{
			while (cursor.moveToNext())
			{
				JSONObject jsonObject = new JSONObject();
				jsonObject.put(CatanHistoricalGamesActivity.GAME_NUMBER, cursor.getInt(0));
				jsonObject.put(CatanHistoricalGamesActivity.GAME_PLAYED_ON, dateFormat.format(dateTimeFormat.parse(cursor.getString(1))));
				jsonObject.put(CatanHistoricalGamesActivity.NUMBER_TURNS, cursor.getInt(2));
				jsonObject.put(CatanHistoricalGamesActivity.PLAYER_NAMES, "Dave, Me, Friend");
				historicalGames.add(jsonObject);
			}
		}catch (JSONException | ParseException e)
		{
			e.printStackTrace();
			cursor.close();
			return historicalGames;
		}
		finally
		{
			cursor.close();
		}
		return historicalGames;
	}
	//endregion

	//region DB Creation
	public static final String SQL_CREATE_HISTORICALGAMES = "CREATE TABLE "+HistoricalGames.TABLE_NAME
			                                                        +" (" +
			                                                        HistoricalGames._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
			                                                        HistoricalGames.COLUMN_EntryDate + " TEXT NOT NULL, " +
			                                                        HistoricalGames.COLUMN_GameNumber + " INTEGER NOT NULL, " +
			                                                        HistoricalGames.COLUMN_CreatedTimestamp + " TEXT NOT NULL, " +
			                                                        HistoricalGames.COLUMN_ModifiedTimestamp + " TEXT NOT NULL" +
			                                                        " )";

	public static class HistoricalGames implements BaseColumns {
		public static final String TABLE_NAME = "HistoricalGames";
		public static final String COLUMN_EntryDate = "EntryDate";
		public static final String COLUMN_GameNumber = "GameNumber";
		public static final String COLUMN_CreatedTimestamp = "CreatedOn";
		public static final String COLUMN_ModifiedTimestamp = "ModifiedOn";
	}

	public static final String SQL_CREATE_HISTORICALGAMETURNS =
			"CREATE TABLE "+HistoricalGameTurns.TABLE_NAME
					+" (" +
					HistoricalGameTurns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
					HistoricalGameTurns.COLUMN_HistoricalGames_GameNumber_FK + " INTEGER NOT NULL, " +
					HistoricalGameTurns.COLUMN_HistoricalGames_TurnNumber + " INTEGER NOT NULL, " +
					HistoricalGameTurns.COLUMN_CreatedTimestamp + " TEXT NOT NULL, " +
					HistoricalGameTurns.COLUMN_ModifiedTimestamp + " TEXT NOT NULL, " +
					HistoricalGameTurns.COLUMN_TurnJSON + " TEXT " +
					" )";

	public static class HistoricalGameTurns implements BaseColumns {
		public static final String TABLE_NAME = "HistoricalGameTurns";
		public static final String COLUMN_HistoricalGames_GameNumber_FK = "HistoricalGames_GameNumber_FK";
		public static final String COLUMN_HistoricalGames_TurnNumber = "HistoricalGames_TurnNumber";
		public static final String COLUMN_CreatedTimestamp = "CreatedOn";
		public static final String COLUMN_ModifiedTimestamp = "ModifiedOn";
		public static final String COLUMN_TurnJSON = "TurnJSON";
	}
	//endregion
}
