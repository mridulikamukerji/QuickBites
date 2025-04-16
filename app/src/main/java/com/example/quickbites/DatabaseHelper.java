package com.example.quickbites;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "FoodApp.db";
    private static final int DATABASE_VERSION = 1;

    // Customer Table
    public static final String TABLE_CUSTOMERS = "Customers";
    public static final String COLUMN_CUST_ID = "id";
    public static final String COLUMN_CUST_NAME = "name";
    public static final String COLUMN_CUST_EMAIL = "email";
    public static final String COLUMN_CUST_PHONE = "phone";
    public static final String COLUMN_CUST_PASSWORD = "password";
    public static final String COLUMN_CUST_IMAGE = "profile_picture";

    // Caterer Table
    public static final String TABLE_CATERERS = "Caterers";
    public static final String COLUMN_CAT_ID = "id";
    public static final String COLUMN_CAT_NAME = "name";
    public static final String COLUMN_CAT_EMAIL = "email";
    public static final String COLUMN_CAT_PHONE = "phone";
    public static final String COLUMN_CAT_PASSWORD = "password";
    public static final String COLUMN_CAT_BUSINESS_NAME = "business_name";
    public static final String COLUMN_CAT_BUSINESS_ADDRESS = "business_address";
    public static final String COLUMN_CAT_IMAGE = "profile_picture";

    // Bestsellers Table
    private static final String TABLE_BESTSELLERS = "Bestsellers";
    private static final String COLUMN_BESTSELLER_ID = "id";
    private static final String COLUMN_BESTSELLER_IMAGE = "image";
    private static final String COLUMN_BESTSELLER_NAME = "name";

    // Customer Specials Table
    private static final String TABLE_CUSTOMER_SPECIALS = "CustomerSpecials";
    private static final String COLUMN_SPECIAL_ID = "id";
    private static final String COLUMN_SPECIAL_IMAGE = "image";
    private static final String COLUMN_SPECIAL_NAME = "name";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CUSTOMERS_TABLE = "CREATE TABLE " + TABLE_CUSTOMERS + " ("
                + COLUMN_CUST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_CUST_NAME + " TEXT, "
                + COLUMN_CUST_EMAIL + " TEXT UNIQUE, "
                + COLUMN_CUST_PHONE + " TEXT, "
                + COLUMN_CUST_PASSWORD + " TEXT, "
                + COLUMN_CUST_IMAGE + " TEXT)";

        String CREATE_CATERERS_TABLE = "CREATE TABLE " + TABLE_CATERERS + " ("
                + COLUMN_CAT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_CAT_NAME + " TEXT, "
                + COLUMN_CAT_EMAIL + " TEXT UNIQUE, "
                + COLUMN_CAT_PHONE + " TEXT, "
                + COLUMN_CAT_PASSWORD + " TEXT, "
                + COLUMN_CAT_BUSINESS_NAME + " TEXT, "
                + COLUMN_CAT_BUSINESS_ADDRESS + " TEXT, "
                + COLUMN_CAT_IMAGE + " TEXT)";

        String CREATE_BESTSELLERS_TABLE = "CREATE TABLE " + TABLE_BESTSELLERS + " ("
                + COLUMN_BESTSELLER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_BESTSELLER_IMAGE + " BLOB, "
                + COLUMN_BESTSELLER_NAME + " TEXT)";

        String CREATE_CUSTOMER_SPECIALS_TABLE = "CREATE TABLE " + TABLE_CUSTOMER_SPECIALS + " ("
                + COLUMN_SPECIAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_SPECIAL_IMAGE + " BLOB, "
                + COLUMN_SPECIAL_NAME + " TEXT)";

        db.execSQL(CREATE_CUSTOMERS_TABLE);
        db.execSQL(CREATE_CATERERS_TABLE);
        db.execSQL(CREATE_BESTSELLERS_TABLE);
        db.execSQL(CREATE_CUSTOMER_SPECIALS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CUSTOMERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATERERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BESTSELLERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CUSTOMER_SPECIALS);
        onCreate(db);
    }

    // Insert Customer
    public long insertCustomer(String name, String email, String phone, String password, String imageUri) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CUST_NAME, name);
        values.put(COLUMN_CUST_EMAIL, email);
        values.put(COLUMN_CUST_PHONE, phone);
        values.put(COLUMN_CUST_PASSWORD, password);
        values.put(COLUMN_CUST_IMAGE, imageUri);
        long id = -1;
        try {
            id = db.insertOrThrow(TABLE_CUSTOMERS, null, values);
        } catch (SQLiteConstraintException e) {
            e.printStackTrace();
        }
        return id;
    }

    // Insert Caterer
    public long insertCaterer(String name, String email, String phone, String password,
                              String businessName, String businessAddress, String imageUri) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CAT_NAME, name);
        values.put(COLUMN_CAT_EMAIL, email);
        values.put(COLUMN_CAT_PHONE, phone);
        values.put(COLUMN_CAT_PASSWORD, password);
        values.put(COLUMN_CAT_BUSINESS_NAME, businessName);
        values.put(COLUMN_CAT_BUSINESS_ADDRESS, businessAddress);
        values.put(COLUMN_CAT_IMAGE, imageUri);
        long id = -1;
        try {
            id = db.insertOrThrow(TABLE_CATERERS, null, values);
        } catch (SQLiteConstraintException e) {
            e.printStackTrace();
        }
        return id;
    }

    // Fetch All Bestsellers (Ensure the images are decoded as Bitmap)
    public List<BestsellerItem> getAllBestsellers() {
        List<BestsellerItem> itemList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_BESTSELLERS, null);

        if (cursor.moveToFirst()) {
            do {
                byte[] image = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_BESTSELLER_IMAGE));
                Bitmap bitmapImage = BitmapFactory.decodeByteArray(image, 0, image.length);
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BESTSELLER_NAME));

                // Add item to list
                itemList.add(new BestsellerItem(name, bitmapImage));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return itemList;
    }

    // Fetch All Customer Specials (with Bitmap conversion)
    public List<SpecialItem> getAllCustomerSpecials() {
        List<SpecialItem> itemList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CUSTOMER_SPECIALS, null);

        if (cursor.moveToFirst()) {
            do {
                byte[] image = cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_SPECIAL_IMAGE));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SPECIAL_NAME));

                // Convert byte[] to Bitmap
                Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);

                // Add item to list
                itemList.add(new SpecialItem(bitmap, name, 0));  // Add count as needed
            } while (cursor.moveToNext());
        }
        cursor.close();
        return itemList;
    }

    // Insert Customer Special
    public boolean insertCustomerSpecial(byte[] image, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SPECIAL_IMAGE, image);
        values.put(COLUMN_SPECIAL_NAME, name);
        long result = db.insert(TABLE_CUSTOMER_SPECIALS, null, values);
        return result != -1;
    }

    // Insert Bestseller Item
    public boolean insertBestsellerItem(byte[] image, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_BESTSELLER_IMAGE, image);
        values.put(COLUMN_BESTSELLER_NAME, name);
        long result = db.insert(TABLE_BESTSELLERS, null, values);
        return result != -1;
    }

    // Check if customer exists with given email and password
    public boolean checkCustomerCredentials(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_CUSTOMERS +
                " WHERE " + COLUMN_CUST_EMAIL + " = ? AND " + COLUMN_CUST_PASSWORD + " = ?";
        String[] selectionArgs = {email, password};
        return db.rawQuery(query, selectionArgs).getCount() > 0;
    }

    // Check if caterer exists with given email and password
    public boolean checkCatererCredentials(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_CATERERS +
                " WHERE " + COLUMN_CAT_EMAIL + " = ? AND " + COLUMN_CAT_PASSWORD + " = ?";
        String[] selectionArgs = {email, password};
        return db.rawQuery(query, selectionArgs).getCount() > 0;
    }
}
