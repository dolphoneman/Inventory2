package com.example.smason.inventory2;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.smason.inventory2.data.InventoryContract;
import com.example.smason.inventory2.data.InventoryContract.ProductEntry;
import com.example.smason.inventory2.data.InventoryDBHelper;

public class InventoryActivity extends AppCompatActivity {

    //DB helper to use the database
    //private InventoryDBHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_activity);

        // Setup FAB to open EditorActivity
        Button button = findViewById(R.id.edit_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InventoryActivity.this, EditInventoryActivity.class);
                startActivity(intent);
            }
        });

        //mDbHelper = new InventoryDBHelper (this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
        //inserts duplicate hardcoded data only after second start
        //insertProduct();
    }

    private void displayDatabaseInfo() {

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_PRODUCT_SUPP_NAME,
                ProductEntry.COLUMN_PRODUCT_SUPP_PHONE };

        // Perform a query on the whole product table
        Cursor cursor = getContentResolver().query(
                ProductEntry.CONTENT_URI,   // The table to query
                projection,            // The columns to return
                null,         // The columns for the WHERE clause
                null,      // The values for the WHERE clause
                null);       // The sort order

        ListView productListView = findViewById(R.id.list);

        // Setup an Adapter to create a list item for each row of pet data in the Cursor.
        ProductCursorAdapter adapter = new ProductCursorAdapter(this, cursor);

        // Attach the adapter to the ListView.
        productListView.setAdapter(adapter);
    }

    /**
     * Helper method to insert hardcoded data into the database. For debugging purposes only and
     * does run on application startup adding duplicate information each time. The only difference
     * in data is the _ID number.
     */
    private void insertProduct() {
        // Gets the database in write mode
        //SQLiteDatabase inventorydb = mDbHelper.getWritableDatabase();

        // Create a ContentValues object where column names are the keys,
        // and the products attributes are the values.
        ContentValues values = new ContentValues();
        values.put(InventoryContract.ProductEntry.COLUMN_PRODUCT_NAME, "Ethernet Cable");
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, 5);
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, 10);
        values.put(ProductEntry.COLUMN_PRODUCT_SUPP_NAME, "MediaBridge");
        values.put(ProductEntry.COLUMN_PRODUCT_SUPP_PHONE, "555-555-1234");

        // Insert a new row in the database, returning the ID of that new row.
        // The first argument for inventorydb.insert() is the products table name.
        // The second argument provides the name of a column in which the framework
        // can insert NULL in the event that the ContentValues is empty (if
        // this is set to "null", then the framework will not insert a row when
        // there are no values).
        // The third argument is the ContentValues object containing the info for the product.
        Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_inventory, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertProduct();
                displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
