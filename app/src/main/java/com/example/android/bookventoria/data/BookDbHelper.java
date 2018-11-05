/*
 * Created by Sonia M on 9/26/18 8:41 PM for educational purposes. The images and/or icons that
 *  were not created by me were obtained with permission from Freepik.com and/or
 *  flaticon.com.
 *  Tips, Guidance, and in some cases code snippets are obtained from Udacity Lessons
 *  relevant to this project. Any additional guidance for specific methods is outlined in
 *  the javadocs for those specific methods.
 */

package com.example.android.bookventoria.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.bookventoria.data.BookContract.BookEntry;

public class BookDbHelper extends SQLiteOpenHelper {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = BookDbHelper.class.getSimpleName();

    /**
     * Name of the database file
     */
    private static final String DATABASE_NAME = "inventory.db";

    /**
     * Database version. If database schema changes, increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link BookDbHelper}.
     *
     * @param context of the app
     */
    public BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        /* Create a String that contains the SQL statement to create the books inventory table

        CREATE TABLE books (
        _id INTEGER PRIMARY KEY AUTOINCREMENT,
        book_name TEXT NOT NULL,
        author TEXT NOT NULL DEFAULT 'UNKNOWN',
               price INTEGER,
        quantity INTEGER NOT NULL DEFAULT 1,
        sales INTEGER NOT NULL DEFAULT 0,
        genre INTEGER NOT NULL DEFAULT 0,
         supplier_name TEXT NOT NULL DEFAULT 'UNKNOWN',
        supplier_phone TEXT NOT NULL DEFAULT 'UNKNOWN');

         */

        String SQL_CREATE_BOOKS_TABLE = "CREATE TABLE " + BookEntry.TABLE_NAME + " ("
                + BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BookEntry.COLUMN_BOOK_NAME + " TEXT NOT NULL, "
                + BookEntry.COLUMN_BOOK_AUTHOR + " TEXT NOT NULL, "
                + BookEntry.COLUMN_BOOK_GENRE + " INTEGER NOT NULL DEFAULT 0, "
                + BookEntry.COLUMN_BOOK_PRICE + " INTEGER NOT NULL DEFAULT 0, "
                + BookEntry.COLUMN_BOOK_QUANTITY + " INTEGER NOT NULL DEFAULT 1, "
                + BookEntry.COLUMN_BOOK_SALES + " INTEGER NOT NULL DEFAULT 0, "
                + BookEntry.COLUMN_BOOK_SUPPLIER_NAME + " TEXT NOT NULL, "
                + BookEntry.COLUMN_BOOK_SUPPLIER_PHONE + " TEXT NOT NULL);";

        // execSQL takes in a statement (string) and Executes the SQL statement
        db.execSQL(SQL_CREATE_BOOKS_TABLE);
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
