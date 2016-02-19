package io.centeno.weatherfinder;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by patrickcenteno on 2/14/16.
 */
public class LocationsDBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Locations.db";

    public LocationsDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("LocationsDBHelper", "onCreate() called " + LocationsDB.CREATE_TABLE);
        db.execSQL(LocationsDB.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("LocationsDBHelper", "TableUpgrading");
        db.execSQL(LocationsDB.RESET_TABLE);
        onCreate(db);
    }
}
