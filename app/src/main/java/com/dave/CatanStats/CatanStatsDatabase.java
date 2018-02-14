package com.dave.CatanStats;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by Dave on 1/21/2018.
 */

public class CatanStatsDatabase extends SQLiteOpenHelper {

        //region static finals
        public static final String DATABASE_NAME = "CatanStats";
        public static final int DATABASE_VERSION = 1;
        //endregion

        //region constructor
        CatanStatsDatabase(Context context){
            super(context,DATABASE_NAME, null,DATABASE_VERSION);
        }
        //endregion

        //region DBCreate/Upgrade
        @Override
        public void onCreate (SQLiteDatabase db){
            Log.d("DEBUG", SQL_CREATE_HISTORICALGAMES);
            db.execSQL(SQL_CREATE_HISTORICALGAMES);

        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
            Log.w(CatanStatsDatabase.class.getName(),
                    "Upgrading database from version "+ oldVersion + " to "
                            +newVersion);
        }
        //endregion

        //region public methods
        public void GetCatanGameByID(int gameID)
        {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor gameCursor = db.query("HistoricalGames",new String[]{"ID","EntryDate","CreatedOn","TurnNumber","RollValue"},
                    "ID = ?",new String[]{"ID"},null, null, "RollValue Desc");

            //if(gameCursor != null)
            //return new CatanGame(gameCursor);
            return;
        }
        //endregion

        //region DB Creation
        public static final String SQL_CREATE_HISTORICALGAMES = "CREATE TABLE "+HistoricalGames.TABLE_NAME
                +" (" +
                HistoricalGames._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                HistoricalGames.COLUMN_EntryDate + " TEXT NOT NULL, " +
                HistoricalGames.COLUMN_CreatedTimestamp + " TEXT NOT NULL, " +
                HistoricalGames.COLUMN_ModifiedTimestamp + " TEXT NOT NULL, " +
                HistoricalGames.COLUMN_TurnNumber + " INTEGER NOT NULL, " +
                HistoricalGames.COLUMN_RollValue + " INTEGER" +
                " )";

        public static class HistoricalGames implements BaseColumns {
            public static final String TABLE_NAME = "HistoricalGames";
            public static final String COLUMN_EntryDate = "EntryDate";
            public static final String COLUMN_CreatedTimestamp = "CreatedOn";
            public static final String COLUMN_ModifiedTimestamp = "ModifiedOn";
            public static final String COLUMN_TurnNumber = "TurnNumber";
            public static final String COLUMN_RollValue = "RollValue";
        }
        //endregion
}
