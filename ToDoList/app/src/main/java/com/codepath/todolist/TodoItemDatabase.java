package com.codepath.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by acampos on 8/2/15.
 */
public class TodoItemDatabase extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 5;
    // Database Name
    private static final String DATABASE_NAME = "ItemsDB";

    public TodoItemDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create table
        String CREATE_ITEMS_TABLE = "CREATE TABLE items ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, "+
                "status INTEGER, "+
                "due INTEGER, "+
                "category INTEGER )";

        // create table
        db.execSQL(CREATE_ITEMS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if exists
        db.execSQL("DROP TABLE IF EXISTS items");

        // create table
        this.onCreate(db);
    }

    /*
    DB Operations
     */

// Items table name
    private static final String TABLE_ITEMS = "items";

    // Items Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_STATUS = "status";
    private static final String KEY_DUE = "due";
    private static final String KEY_CAT = "category";

    private static final String[] COLUMNS = {KEY_ID,KEY_NAME,KEY_STATUS,KEY_DUE,KEY_CAT};

    public void addItem(Item item){
        Log.d("addItem", item.toString());

        SQLiteDatabase db = this.getWritableDatabase();


        ContentValues values = new ContentValues();
        values.put(KEY_NAME, item.getName());
        values.put(KEY_STATUS, item.getStatus());
        values.put(KEY_DUE, item.getDue());
        values.put(KEY_CAT, item.getCategory());

        db.insert(TABLE_ITEMS,
                null,
                values);

        db.close();
    }

    public int getItemId(String name) {
        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(TABLE_ITEMS, // a. table
                        COLUMNS, // b. column names
                        " name = ?", // c. selections
                        new String[] { String.valueOf(name) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        if (cursor != null)
            cursor.moveToFirst();

        return (Integer.parseInt(cursor.getString(0)));

    }
    public Item getItem(int id){

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(TABLE_ITEMS, // a. table
                        COLUMNS, // b. column names
                        " id = ?", // c. selections
                        new String[] { String.valueOf(id) }, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();

        // 4. build item object
        Item item = new Item();
        item.setId(Integer.parseInt(cursor.getString(0)));
        item.setName(cursor.getString(1));
        item.setStatus(cursor.getInt(2));
        item.setDue(cursor.getInt(3));
        item.setCategory(cursor.getInt(4));

        Log.d("getItem(" + id + ")", item.toString());

        // 5. return item
        return item;
    }


    public List<Item> getItemsList() {

        List<Item> todoItems = new ArrayList<Item>();

        int i = getMaxItems();  // Is this needed when DB is empty?
        if (i > 0 ) {
            String query = "SELECT name, status, due FROM " + TABLE_ITEMS;

            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    Item myItem = new Item();
                    myItem.setName(cursor.getString(0));
                    myItem.setDue(cursor.getInt(1));
                    myItem.setStatus(cursor.getInt(2));
                    todoItems.add(myItem);
                } while (cursor.moveToNext());
            }

            db.close();
        }
        return todoItems;
    }


    // Get All Items
    public List<Item> getAllItems() {
        List<Item> items = new LinkedList<Item>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_ITEMS;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build item and add it to list
        Item item = null;
        if (cursor.moveToFirst()) {
            do {
                item = new Item();
                item.setId(Integer.parseInt(cursor.getString(0)));
                item.setName(cursor.getString(1));
                item.setStatus(cursor.getInt(2));
                item.setDue(cursor.getInt(3));
                item.setCategory(cursor.getInt(4));

                // Add item to items
                items.add(item);
            } while (cursor.moveToNext());
        }

        Log.d("getAllItems()", items.toString());
        db.close();

        return items;
    }

    // Updating single item
    public int updateItem(Item item) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put("status", item.getStatus()); // reset status to ACTIVE
        values.put("name", item.getName()); // update item name
        int i;
        // 3.1 update by ID
       // if (item.getId() > 0) {
            i = db.update(TABLE_ITEMS, //table
                    values, // column/value
                    KEY_ID + " = ?", // selections
                    new String[]{String.valueOf(item.getId())}); //selection args
//        }
//        else // 3.2 Update by name
//        {
//            i = db.update(TABLE_ITEMS,
//                    values,
//                    KEY_NAME + " = ?",
//                    new String[]{String.valueOf(item.getName())});
//        }
        db.close();
        return i;
    }

    // Deleting single item
    public void deleteItem(Item item) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2.1 Delete by ID
//        if (item.getId() > 0) {
//            db.delete(TABLE_ITEMS,
//                    KEY_ID + " = ?",
//                    new String[]{String.valueOf(item.getId())});
//        }
//        else {
            // 2.2 delete by Name
            db.delete(TABLE_ITEMS,
                    KEY_NAME + " = ?",
                    new String[]{String.valueOf(item.getName())});
 //       }

        db.close();
    }

    public int getMaxItems () {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor mCount= db.rawQuery("select count(*) from " + TABLE_ITEMS + " where " + KEY_STATUS + " > 0"
         +" ", null);
        mCount.moveToFirst();
        int count= mCount.getInt(0);
        mCount.close();

        return count;
    }


}
