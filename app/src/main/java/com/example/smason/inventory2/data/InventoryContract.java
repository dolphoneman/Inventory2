package com.example.smason.inventory2.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class InventoryContract {

    private InventoryContract() {}

    //Name the content authority of the Uri
    public static final String CONTENT_AUTHORITY = "com.example.smason.inventory2";

    //Name the base of the Uri w/content authority
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //Possible paths for data locations after the content authority
    public static final String PATH_PRODUCT = "product";

    /**
     * Inner class that defines constant values for the product database table.
     * Each entry in the table represents a single product.
     */
    public static final class ProductEntry implements BaseColumns {

        //full Uri to access data
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_PRODUCT);

         //for a list of products.
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCT;

        //for a single product.
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCT;

        //Name of database table for inventory
        public final static String TABLE_NAME = "product";

        //column names
        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_PRODUCT_NAME ="name";

        public final static String COLUMN_PRODUCT_PRICE = "price";

        public final static String COLUMN_PRODUCT_QUANTITY = "quantity";

        public final static String COLUMN_PRODUCT_SUPP_NAME = "supplier_name";

        public final static String COLUMN_PRODUCT_SUPP_PHONE = "phone_number";


    }

}
