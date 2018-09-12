package com.example.smason.inventory2.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.smason.inventory2.data.InventoryContract.ProductEntry;

/**
 * {@link ContentProvider} for Inventory2 app.
 */
public class InventoryProvider extends ContentProvider {

    //Uri matcher codes
    private static final int PRODUCTS = 1000;
    private static final int PRODUCT_ID = 1001;

    //Uri matcher object
    private static final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        //content URIs
        mUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_PRODUCT, PRODUCTS);
        mUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_PRODUCT + "/#", PRODUCT_ID);
    }

    /**
     * Database helper that will provide us access to the database
     */
    private InventoryDBHelper mDbHelper;

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = InventoryProvider.class.getSimpleName();

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        //Create and initialize a InventoryDbHelper object to gain access to the database.
        mDbHelper = new InventoryDBHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Create and/or open a database to read from it
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = mUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                // For the PRODUCTS code, query the table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the table.

                cursor = database.query(
                        InventoryContract.ProductEntry.TABLE_NAME,   // The table to query
                        projection,            // The columns to return
                        selection,                  // The columns for the WHERE clause
                        selectionArgs,                  // The values for the WHERE clause
                        null,                  // Don't group the rows
                        null,                  // Don't filter by row groups
                        sortOrder);                   // The sort order
                break;
            case PRODUCT_ID:
                // For the PRODUCT_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.smason.inventory2/product/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = InventoryContract.ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the products table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(InventoryContract.ProductEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        //Set notification URI so we know what content URI the cursor was created for
        //if the data at this URI changes, then we know we need to update the cursor
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = mUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a product into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertProduct(Uri uri, ContentValues values) {

        // Check that the name is not null
        String name = values.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Product requires a valid name");
        }

        //Check that the price is not null or < 0
        Integer price = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_PRICE);
        if (price == null || price < 0) {
            throw new IllegalArgumentException("Product requires a valid price");
        }

        //Check that the price is not null or < 0
        Integer quantity = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        if (quantity == null || price < 0) {
            throw new IllegalArgumentException("Product requires a valid quantity");
        }

        // Gets the database in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Insert a new row for pet in the database, returning the ID of that new row.
        long newRowId = db.insert(InventoryContract.ProductEntry.TABLE_NAME, null, values);

        if (newRowId == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        //Notify all listeners that the data has changed for the product content URI
        //URI = content://com.example.smason/inventory2/products
        getContext().getContentResolver().notifyChange(uri, null);

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, newRowId);
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        final int match = mUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // Check that the name is not null if needed
        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Product requires a valid name");
            }
        }

        //Check that the price is not null or < 0 if needed
        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_PRICE)) {
            Integer price = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_PRICE);
            if (price == null || price < 0) {
                throw new IllegalArgumentException("Product requires a valid price");
            }
        }

        //Check that the quantity is not null or < 0 if needed
        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_QUANTITY)) {
            Integer quantity = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            if (quantity == null || quantity < 0) {
                throw new IllegalArgumentException("Product requires a valid quantity");
            }
        }

        //if there are no values that need updated, do nothing
        if (values.size() == 0) {
            return 0;
        }

        // Gets the database in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int updatedRows = db.update(ProductEntry.TABLE_NAME, values, selection,selectionArgs);

        //If 1 or more rows were updated, then notify all listeners that the data at the given URI has changed
        if (updatedRows !=0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return updatedRows;

    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        // Get writeable database
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = mUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = db.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                // Delete a single row given by the ID in the URI
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = db.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        //If 1 or more rows were deleted, then notify all listeners that the data at the given URI has changed
        if (rowsDeleted !=0 ) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {

        final int match = mUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
