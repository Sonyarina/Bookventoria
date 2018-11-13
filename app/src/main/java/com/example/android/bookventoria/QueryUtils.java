package com.example.android.bookventoria;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;

import com.example.android.bookventoria.data.BookContract.BookEntry;

/**
 * Helper methods and constants related to storing or retrieving data in the app. Created using
 * knowledge obtained in Udacity's Android Basics course.
 */
public final class QueryUtils {

    // Declare key name for event history log
    public static final String LOG_EVENT_HISTORY_KEY = "event_history";

    /* Query Code Key */
    public static final String QUERY_CODE_KEY = "query_code_key";

    /* Genre Code Key */
    public static final String GENRE_CODE_KEY = "genre_code_key";

    /* Supplier Code Key */
    public static final String SUPPLIER_CODE_KEY = "supplier_code_key";

    /* SharedPreference key for boolean: Any Books In Stock? */
    public static final String ANY_BOOKS_BOOLEAN_KEY = "any_books_boolean_key";

    /* SharedPreference key for total number of books in stock */
    public static final String BOOK_COUNT_INT_KEY = "book_count_int_key";

    /* Query Code Values used to determine which query to run */
    public static final int QUERY_ALL_BOOKS = 0;
    public static final int QUERY_LOW_INVENTORY = 1;
    public static final int QUERY_GENRE = 2;
    public static final int QUERY_SUPPLIER_BOOKS = 3;
    public static final int QUERY_ALL_SALES = 5;

    /* Identifier for the selection key used in Bundle passed in to loader  */
    public static final String PROJECTION_KEY = "projection_key";

    /* Identifier for the selection key used in Bundle passed in to loader  */
    public static final String SELECTION_KEY = "selection_key";

    /* Identifier for the selectionArgs key used in Bundle passed in to loader  */
    public static final String SELECTION_ARGS_KEY = "selection_args_key";

    /* Identifier for the column count of gridview in GenresFragment */
    public static final String ARG_COLUMN_COUNT = "column-count";

    // Final GenresList items
    public static final GenresList unknownGenre = new GenresList(BookEntry.FORMATTED_GENRE_UNKNOWN,
            R.drawable.unknown, BookEntry.GENRE_UNKNOWN);

    public static final GenresList nonfictionGenre = new GenresList(
            BookEntry.FORMATTED_GENRE_NONFICTION, R.drawable.nonfiction, BookEntry.GENRE_NONFICTION);

    public static final GenresList fictionGenre = new GenresList(
            BookEntry.FORMATTED_GENRE_FICTION, R.drawable.fiction, BookEntry.GENRE_FICTION);

    public static final GenresList mysteryGenre = new GenresList(
            BookEntry.FORMATTED_GENRE_MYSTERY_SUSPENSE, R.drawable.mystery,
            BookEntry.GENRE_MYSTERY_SUSPENSE);

    public static final GenresList childrensGenre = new GenresList(
            BookEntry.FORMATTED_GENRE_CHILDRENS, R.drawable.childrens,
            BookEntry.GENRE_CHILDRENS);

    public static final GenresList historyGenre = new GenresList(
            BookEntry.FORMATTED_GENRE_HISTORY, R.drawable.history,
            BookEntry.GENRE_HISTORY);

    public static final GenresList financeGenre = new GenresList(
            BookEntry.FORMATTED_GENRE_FINANCE_BUSINESS, R.drawable.financial,
            BookEntry.GENRE_FINANCE_BUSINESS);

    public static final GenresList scifiGenre = new GenresList(
            BookEntry.FORMATTED_GENRE_SCIENCE_FICTION_FANTASY, R.drawable.scifi,
            BookEntry.GENRE_SCIENCE_FICTION_FANTASY);

    public static final GenresList romanceGenre = new GenresList(
            BookEntry.FORMATTED_GENRE_ROMANCE, R.drawable.romance,
            BookEntry.GENRE_ROMANCE);

    public static final GenresList homeFoodGenre = new GenresList(
            BookEntry.FORMATTED_GENRE_HOME_FOOD, R.drawable.home,
            BookEntry.GENRE_HOME_FOOD);

    public static final GenresList teenGenre = new GenresList(
            BookEntry.FORMATTED_GENRE_TEENS_YOUNG_ADULT, R.drawable.teens,
            BookEntry.GENRE_TEENS_YOUNG_ADULT);

    public static final GenresList otherGenre = new GenresList(
            BookEntry.FORMATTED_GENRE_OTHER, R.drawable.misc,
            BookEntry.GENRE_OTHER);

    /**
     * Adds an event to the logs
     *
     * @param context Context needed to use SharedPreferences
     * @param time    Time of the event, passed in as a string
     * @param event   Description of the event, passed in as a string
     */
    public static void updateLogs(Context context, String time, String event) {

        // Initialize SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                (context);

        // Retrieve current log from SharedPreferences
        String updatedLog = sharedPreferences.getString(LOG_EVENT_HISTORY_KEY, "");

        // Add updated information that was passed in to method
        updatedLog = updatedLog + "\n" + time + " - " + event;

        // Overwrite current log in SharedPreferences with new value
        sharedPreferences.edit().putString(LOG_EVENT_HISTORY_KEY, updatedLog).apply();
    }

    /**
     * Deletes the logs/ history of actions
     *
     * @param context Context needed to use SharedPreferences
     */
    public static void deleteLogs(Context context) {

        // Initialize SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                (context);

        // Remove key that contains history, if there is one
        if (sharedPreferences.contains(LOG_EVENT_HISTORY_KEY)) {
            sharedPreferences.edit().remove(LOG_EVENT_HISTORY_KEY).commit();
        }
    }

    /**
     * Returns String containing date and time
     * @param context The context needed
     * @return string showing current date and time
     */
    public static String dateTimeString(Context context) {

        return DateUtils.formatDateTime(context, System.currentTimeMillis(),
                DateUtils
                        .FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_TIME);
    }

    /**
     * Updates boolean that keeps track of whether there are any books in the database
     *
     * @param context  Context needed to use SharedPreferences
     * @param anyBooks the new boolean value
     */
    public static void updateAnyBooksBoolean(Context context, boolean anyBooks) {

        // Initialize SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                (context);

        // Update sharedpreferences based on boolean value
        if (anyBooks) {
            sharedPreferences.edit().putBoolean(ANY_BOOKS_BOOLEAN_KEY, true).commit();
        } else {
            sharedPreferences.edit().putBoolean(ANY_BOOKS_BOOLEAN_KEY, false).commit();
        }
    }

    /**
     * Takes in the integer code for the genre and returns the name in String format
     *
     * @param genreCode the numerical code that corresponds with the genre
     */
    public static String getGenreStringFormat(int genreCode) {
        // Create local String that will be returned
        switch (genreCode) {
            case BookEntry.GENRE_NONFICTION:
                return BookEntry.FORMATTED_GENRE_NONFICTION;

            case BookEntry.GENRE_FICTION:
                return BookEntry.FORMATTED_GENRE_FICTION;

            case BookEntry.GENRE_MYSTERY_SUSPENSE:
                return BookEntry.FORMATTED_GENRE_MYSTERY_SUSPENSE;

            case BookEntry.GENRE_CHILDRENS:
                return BookEntry.FORMATTED_GENRE_CHILDRENS;

            case BookEntry.GENRE_HISTORY:
                return BookEntry.FORMATTED_GENRE_HISTORY;

            case BookEntry.GENRE_FINANCE_BUSINESS:
                return BookEntry.FORMATTED_GENRE_FINANCE_BUSINESS;

            case BookEntry.GENRE_SCIENCE_FICTION_FANTASY:
                return BookEntry.FORMATTED_GENRE_SCIENCE_FICTION_FANTASY;

            case BookEntry.GENRE_ROMANCE:
                return BookEntry.FORMATTED_GENRE_ROMANCE;

            case BookEntry.GENRE_HOME_FOOD:
                return BookEntry.FORMATTED_GENRE_HOME_FOOD;

            case BookEntry.GENRE_TEENS_YOUNG_ADULT:
                return BookEntry.FORMATTED_GENRE_TEENS_YOUNG_ADULT;

            case BookEntry.GENRE_OTHER:
                return BookEntry.FORMATTED_GENRE_OTHER;

            default:
                return BookEntry.FORMATTED_GENRE_UNKNOWN;
        }
    }

    /**
     * Creates ContentValues array with demo/dummy data
     *
     * @param context Context needed to use retrieve resources
     * @return values ContentValues array with demo data
     */
    public static ContentValues[] createDemoValues(Context context) {
        // Assign dummy data arrays to local String arrays
        String bookNames[] = context.getResources().getStringArray(R.array.book_name);
        String authors[] = context.getResources().getStringArray(R.array.author_names);
        String supplierNames[] = context.getResources().getStringArray(R.array.supplier_name);
        String supplierPhones[] = context.getResources().getStringArray(R.array.supplier_phone);
        int[] genres = context.getResources().getIntArray(R.array.book_genre_integers);
        int[] price = context.getResources().getIntArray(R.array.book_price);
        int[] quantity = context.getResources().getIntArray(R.array.book_quantity);
        int[] sales = context.getResources().getIntArray(R.array.book_sales);

        // Declare new ContentValues object to store attributes for each book that will be added
        ContentValues values[] = new ContentValues[bookNames.length];

        // use For loop to add data from arrays into the content values object
        for (int i = 0; i < bookNames.length; i++) {
            ContentValues currentBookValues = new ContentValues();
            currentBookValues.put(BookEntry.COLUMN_BOOK_NAME, bookNames[i]);
            currentBookValues.put(BookEntry.COLUMN_BOOK_AUTHOR, authors[i]);
            currentBookValues.put(BookEntry.COLUMN_BOOK_GENRE, genres[i]);
            currentBookValues.put(BookEntry.COLUMN_BOOK_PRICE, price[i]);
            currentBookValues.put(BookEntry.COLUMN_BOOK_QUANTITY, quantity[i]);
            currentBookValues.put(BookEntry.COLUMN_BOOK_SALES, sales[i]);
            currentBookValues.put(BookEntry.COLUMN_BOOK_SUPPLIER_NAME, supplierNames[i]);
            currentBookValues.put(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE, supplierPhones[i]);
            values[i] = currentBookValues;
        }
        // Return ContentValues array
        return values;
    }
}
