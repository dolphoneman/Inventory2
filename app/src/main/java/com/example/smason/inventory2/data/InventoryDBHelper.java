package com.example.smason.inventory2.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.smason.inventory2.data.InventoryContract.ProductEntry;

public class InventoryDBHelper extends SQLiteOpenHelper {

    //database file name
    private static final String DATABASE_NAME = "inventory.db";


    //Database version. If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    // Constructs a new instance of InventoryDBHelper.
    public InventoryDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //This is called when the database is created for the first time.
    @Override
    public void onCreate(SQLiteDatabase inventorydb) {
        // Create a String that contains the SQL statement to create the products table
        String SQL_CREATE_PRODUCT_TABLE =  "CREATE TABLE " + InventoryContract.ProductEntry.TABLE_NAME + " ("
                + ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ProductEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + ProductEntry.COLUMN_PRODUCT_PRICE + " INTEGER NOT NULL DEFAULT 0, "
                + ProductEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + ProductEntry.COLUMN_PRODUCT_SUPP_NAME + " TEXT, "
                + ProductEntry.COLUMN_PRODUCT_SUPP_PHONE + " TEXT);";

        // Execute the SQL statement
        inventorydb.execSQL(SQL_CREATE_PRODUCT_TABLE);

    }

    //This is called when the database needs to be upgraded.
    @Override
    public void onUpgrade(SQLiteDatabase inventorydb, int oldVersion, int newVersion) {

    }
}
