package io.centeno.weatherfinder;

/**
 * Created by patrickcenteno on 2/14/16.
 * Do not instatiate this class. Must be kept
 * for static calls.
 */
public class LocationsDB {
    public static final String TABLE_NAME = "locations";
    public static final String COLUMN_ENTRY_ID = "ID";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_LAT = "latitude";
    public static final String COLUMN_LON = "longitude";
    public static final String TEXT_TYPE = " TEXT";
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ENTRY_ID + " INTEGER PRIMARY KEY, " +
                    COLUMN_ADDRESS + TEXT_TYPE + ", " +
                    COLUMN_LAT + TEXT_TYPE + ", " +
                    COLUMN_LON + TEXT_TYPE + " )";
    public static final String RESET_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

}
