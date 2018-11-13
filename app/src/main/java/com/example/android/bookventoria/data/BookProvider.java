/*
 * Created by Sonia M on 9/26/18 8:37 PM for educational purposes.
 *  Tips, Guidance, and in some cases code snippets are obtained from Udacity Lessons
 *  relevant to this project. Any additional guidance for specific methods is outlined in
 *  the javadocs for those specific methods.
 */

package com.example.android.bookventoria.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.util.Log;

import com.example.android.bookventoria.QueryUtils;
import com.example.android.bookventoria.R;
import com.example.android.bookventoria.data.BookContract.BookEntry;

/**
 * {@link ContentProvider} for Bookventoria app.
 */
public class BookProvider extends ContentProvider {

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = BookProvider.class.getSimpleName();
    /**
     * URI matcher code for the content URI for the books table
     */
    private static final int BOOKS = 100;
    /**
     * URI matcher code for the content URI for a book in the books inventory table
     */
    private static final int BOOK_ID = 101;
    /**
     * URI matcher code for the content URI for a book using the DISTINCT key word
     */
    private static final int SUPPLIERS = 102;
    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        /* Adds content URI to URI matcher for entire Books table. Sets code to 100  */
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS, BOOKS);

        /* Adds content URI to URI matcher for a single row of the Books table. Sets code to 101  */
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS
                + "/#", BOOK_ID);

        /* Adds content URI to URI matcher for entire Books table, but using the DISTINCT query
        option.*/
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_SUPPLIERS, SUPPLIERS);
    }

    /**
     * Database helper that will provide us access to the database
     */
    private BookDbHelper mDbHelper;

    /**
     * Implemented to initialize the content provider on startup.   *
     *
     * @return true if the provider was successfully loaded, false otherwise
     */
    @Override
    public boolean onCreate() {
        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        mDbHelper = new BookDbHelper(getContext());
        return true;
    }

    /**
     * Implemented to handle query requests.
     *
     * @param uri           The URI to query.
     * @param projection    The list of columns to put into the cursor. If
     *                      {@code null} all columns are included.
     * @param selection     A selection criteria to apply when filtering rows.
     *                      If {@code null} then all rows are included.
     * @param selectionArgs You may include ?s in selection, which will be replaced by
     *                      the values from selectionArgs, in order that they appear in the selection.
     * @param sortOrder     How the rows in the cursor should be sorted.
     *                      If {@code null} then the provider is free to define the sort order.
     * @return a Cursor or {@code null}.
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor = null;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                // For the BOOKS code, query the books table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the books table.
                cursor = database.query(BookEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case BOOK_ID:
                // For the BOOK_ID code, extract out the ID from the URI.
                // 1 question mark in the selection means there will be 1 String in the selection
                // arguments' String array.
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the books table where the id corresponds to the row
                // of the table that will be returned by the cursor.
                cursor = database.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            case SUPPLIERS:
                // For SUPPLIERS code, query the books table directly with the projection which
                // is narrowed down to Supplier Names and Phone Numbers. The cursor will contain
                // multiple rows, but no duplicates. Guidance for using this query constructor
                // found at: https://www.tutorialspoint.com/sqlite/sqlite_distinct_keyword.htm
                // and additional guidance found in Android Developer documentation
                // https://developer.android.com/reference/android/database/sqlite/SQLiteDatabase

                cursor = database.query(true, BookEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        BookEntry.COLUMN_BOOK_SUPPLIER_NAME, // Group by Supplier Name Column
                        null,
                        sortOrder,
                        null);
                break;
            default:
                // Log unknown URI
                Log.e(LOG_TAG, "Cannot query unknown URI " + uri);
                break;
        }

        // Set notification URI on the Cursor to keep track of what content URI the Cursor was
        // created for. If the data at this URI changes, that means the cursor needs to be updated
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Update user logs. Get Date and time, then pass to helper method
        String dateAndTime = DateUtils.formatDateTime(getContext(), System.currentTimeMillis(),
                DateUtils
                        .FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_TIME);

        // Update logs, passing date, time, message into helper method
        QueryUtils.updateLogs(getContext(), dateAndTime,
                getContext().getString(R.string.database_queried));

        // Return the cursor
        return cursor;
    }

    /**
     * Implement this to handle requests to insert a new row. This method will only process a
     * URI ending with the BOOKS uri matcher code
     *
     * @param uri    The content:// URI of the insertion request. This must not be {@code null}.
     * @param values A set of column_name/value pairs to add to the database.
     *               This must not be {@code null}.
     * @return The URI for the newly inserted item.
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertBook(uri, values);
            default:
                // Log unknown URI error
                Log.e(LOG_TAG, "IllegalArgumentException: Insertion not supported for URI " + uri);
                return null;
        }
    }

    /**
     * Insert a book into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     *
     * @param uri    The content:// URI of the insertion request. This must not be {@code null}.
     * @param values A set of column_name/value pairs to add to the database.
     *               This must not be {@code null}.
     * @return The URI for the newly inserted item.
     */
    private Uri insertBook(Uri uri, ContentValues values) {

        // Retrieve ContentValues data
        String name = values.getAsString(BookEntry.COLUMN_BOOK_NAME);
        String author = values.getAsString(BookEntry.COLUMN_BOOK_AUTHOR);
        String supplierName = values.getAsString(BookEntry.COLUMN_BOOK_SUPPLIER_NAME);
        String supplierPhone = values.getAsString(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE);
        Integer genre = values.getAsInteger(BookEntry.COLUMN_BOOK_GENRE);
        Integer price = values.getAsInteger(BookEntry.COLUMN_BOOK_PRICE);
        Integer quantity = values.getAsInteger(BookEntry.COLUMN_BOOK_QUANTITY);
        Integer sales = values.getAsInteger(BookEntry.COLUMN_BOOK_SALES);

        // Gets the database in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Insert a new book into the inventory database, returning the ID of that new row.
        long id = db.insert(BookEntry.TABLE_NAME, null, values);

        Log.v(LOG_TAG, "New book ID " + id);

        // Get current time and date, which will be used when updating logs
        String dateAndTime = DateUtils.formatDateTime(getContext(), System.currentTimeMillis(), DateUtils
                .FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_TIME);

        // If the id returned is -1, an error occured
        if (id == -1) {
            // Log error
            Log.e(LOG_TAG, "Failed to insert row for " + uri);

            // Update user logs with the error
            QueryUtils.updateLogs(getContext(), dateAndTime,
                    getContext().getString(R.string.add_book_error));
            return null;
        }

        // Notify all listeners that the data has changed for the book content URI
        // uri: content://com.example.android.bookventoria/books
        getContext().getContentResolver().notifyChange(uri, null);

        // Call helper method to update logs
        QueryUtils.updateLogs(getContext(), dateAndTime,
                getContext().getString(R.string.editor_book_saved));

        // Call helper method to update shared preferences boolean value
        QueryUtils.updateAnyBooksBoolean(getContext(), true);

        // Return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * This helps to insert multiple rows of demo data/ dummy data into the database.
     * From Super Class: Override bulkInsert to handle requests to insert a
     * set of new rows, or the default implementation will iterate over the values and call
     * {@link #insert} on each of them.
     * As a courtesy, call ContentResolver #notifyChange(android.net.Uri ,
     * android.database.ContentObserver) notifyChange()}
     * after inserting.
     *
     * Tips and guidance for using this method found while browsing the super class
     * (ContentProvider) on Android Studio.
     *
     * @param uri    The content:// URI of the insertion request.
     * @param values An array of sets of column_name/value pairs to add to the database.
     *               This must not be {@code null}.
     * @return The number of values that were inserted.
     */
    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        // The number of objects (books) in the ContentValues array
        int numValues = values.length;
        // Gets the database in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Long variable stores id of new row
        long id;

        /*
        call beginTransaction which will begin the transaction in EXCLUSIVE mode.
        Tips and guidance for this technique found on stackoverflow.com.
        Enclosing operations in "beginTransaction() and endTransaction() significantly reduces the
        amount of resources/ memory used
        https://stackoverflow.com/questions/16175563/inserting-multiple-entries-into-sqlite-database-android
         */
        db.beginTransaction();

        /* Wrap actions to insert demo data in a try/ finally clause
        According to documentation: When the outer transaction is ended all of the work done in
        that transaction and all of the nested transactions will be committed or rolled back.
        The changes will be rolled back if any transaction is ended without being marked as clean (
        by calling setTransactionSuccessful). Otherwise they will be committed.
         */
        try {
            // For loop iterates through the ContentValues array, adding a new row for each
            // ContentValue object in the array
            for (int i = 0; i < numValues; i++) {
                id = db.insert(BookEntry.TABLE_NAME, null, values[i]);

                // If the id = -1, there was an error
                if (id != -1) {
                    getContext().getContentResolver().notifyChange(uri, null);
                } else {
                    // Log error
                    Log.e(LOG_TAG, "Failed to insert row for " + uri);
                }
            }

            // Marks the current transaction as successful.
            db.setTransactionSuccessful();
        }finally {
            // End transaction
            db.endTransaction();
        }

        // Get current time and date, which will be used when updating logs
        String dateAndTime = DateUtils.formatDateTime(getContext(), System.currentTimeMillis(), DateUtils
                .FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_TIME);

        // Call helper method to update logs, passing in the number of rows added
        QueryUtils.updateLogs(getContext(), dateAndTime,
                getContext().getString(R.string.database_demo_data_rows, numValues));

        // Update the shared preferences boolean which references whether any books are in the
        // database using helper method
        QueryUtils.updateAnyBooksBoolean(getContext(), true);

        // return the number of ContentValues objects (books) that were added
        return numValues;
    }

    /**
     * Implemented to handle requests to delete one or more rows (books).
     * The implementation should apply the selection clause when performing
     * deletion, allowing the operation to affect multiple rows in a directory.     *
     *
     * @param uri           The full URI to query, including a row ID (if a specific record is requested).
     * @param selection     An optional restriction to apply to rows when deleting.
     * @param selectionArgs The string array containing selection values
     * @return The number of rows affected.
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Get Uri matcher code
        final int match = sUriMatcher.match(uri);

        // Track the number of rows that were deleted
        int rowsDeleted = 0;

        // Use switch statement to determine next steps based on uri match code
        switch (match) {
            case BOOKS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);

                // Update shared preferences using helper method
                QueryUtils.updateAnyBooksBoolean(getContext(), false);
                break;

            case BOOK_ID:
                // Delete a single row given by the ID in the URI
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                // Log error
                Log.e(LOG_TAG, "IllegalArgumentException: Deletion is not supported for " + uri);
        }

        // Get current time and date, then call method to update the logs, passing along the time
        // and event description
        String dateAndTime = DateUtils.formatDateTime(getContext(), System.currentTimeMillis(),
                DateUtils
                        .FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_TIME);

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);

            if (rowsDeleted > 1) {
                // Update logs to show that the delete was successful
                QueryUtils.updateLogs(getContext(), dateAndTime,
                        getContext().getString(R.string.main_delete_books_successful, rowsDeleted));
            } else {
                QueryUtils.updateLogs(getContext(), dateAndTime,
                        getContext().getString(R.string.main_delete_one_book_successful));
            }
        } else {
            // No rows were deleted. Update logs to show that the deletion failed
            QueryUtils.updateLogs(getContext(), dateAndTime,
                    getContext().getString(R.string.query_error_delete_not_supported, uri));
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }

    /**
     * Implemented to handle requests to update one or more rows.
     * The implementation should update all rows matching the selection
     * to set the columns according to the provided values map.
     *
     * @param uri           The URI to query. This can potentially have a record ID if this
     *                      is an update request for a specific record.
     * @param values        A set of column_name/value pairs to update in the database.
     *                      This must not be {@code null}.
     * @param selection     An optional filter to match rows to update.
     * @param selectionArgs String array containing selection arguments
     * @return the number of rows affected.
     */
    @Override
    public int update(@NonNull Uri uri, ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {

        // Get uri match code
        final int match = sUriMatcher.match(uri);

        // Use switch statement to determine next steps based on uri match code
        switch (match) {
            case BOOKS:
                // Update the entire books table. This option is ready for use, but has not been
                // added to the user interface yet
                return updateBook(uri, values, selection, selectionArgs);

            case BOOK_ID:
                // For the BOOK_ID code, extract out the ID from the URI
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateBook(uri, values, selection, selectionArgs);

                default:
                // Log error
                Log.e(LOG_TAG, "IllegalArgumentException: Update is not supported for " + uri);
                return 0;
        }
    }

    /**
     * Update books in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more books).
     * Return the number of rows that were successfully updated.
     *
     * @param uri           The URI to query. This can potentially have a record ID if this
     *                      is an update request for a specific record.
     * @param values        A set of column_name/value pairs to update in the database.
     *                      This must not be {@code null}.
     * @param selection     An optional filter to match rows to update.
     * @param selectionArgs String array containing selection arguments
     * @return the number of rows affected.
     **/
    private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Validate ContentValues, starting with name. If the field cannot be validated, return 0.
        if (values.containsKey(BookEntry.COLUMN_BOOK_NAME)) {
            // Check that the name is not null
            String name = values.getAsString(BookEntry.COLUMN_BOOK_NAME);

            if (name == null) {
                // Log error
                Log.e(LOG_TAG, getContext().getString(R.string.error_name_null));
                return 0;
            }
        }

        // Validate author
        if (values.containsKey(BookEntry.COLUMN_BOOK_AUTHOR)) {
            // Check that the name is not null
            String author = values.getAsString(BookEntry.COLUMN_BOOK_AUTHOR);
            if (author == null) {
                // Log error
                Log.e(LOG_TAG, getContext().getString(R.string.error_author_null));
                return 0;
            }
        }

        // Validate supplier name
        if (values.containsKey(BookEntry.COLUMN_BOOK_SUPPLIER_NAME)) {
            // Check that the supplier name is not null
            String supplierName = values.getAsString(BookEntry.COLUMN_BOOK_SUPPLIER_NAME);
            if (supplierName == null) {
                // Log error
                Log.e(LOG_TAG, getContext().getString(R.string.error_supplier_name_null));
                return 0;
            }
        }

        // Validate supplier phone
        if (values.containsKey(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE)) {
            // Check that the supplier phone is not null
            String supplierPhone = values.getAsString(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE);
            if (supplierPhone == null) {
                // Log error
                Log.e(LOG_TAG, getContext().getString(R.string.error_supplier_phone_null));
                return 0;
            }
        }

        // Validate Genre
        if (values.containsKey(BookEntry.COLUMN_BOOK_GENRE)) {
            // Check that genre is valid
            Integer genre = values.getAsInteger(BookEntry.COLUMN_BOOK_GENRE);
            if (genre == null || !BookEntry.isValidGenre(genre)) {
                // Log error
                Log.e(LOG_TAG, getContext().getString(R.string.error_invalid_genre));
                return 0;
            }
        }

        // Validate price
        if (values.containsKey(BookEntry.COLUMN_BOOK_PRICE)) {
            // Check that price is not negative
            Integer price = values.getAsInteger(BookEntry.COLUMN_BOOK_PRICE);
            if (price < 0) {
                // Log error
                Log.e(LOG_TAG, getContext().getString(R.string.error_price_invalid));
                return 0;
            }
        }

        // Validate quantity
        if (values.containsKey(BookEntry.COLUMN_BOOK_QUANTITY)) {
            // Check that quantity is not negative
            Integer quantity = values.getAsInteger(BookEntry.COLUMN_BOOK_QUANTITY);
            if (quantity < 0) {
                // Log error
                Log.e(LOG_TAG, getContext().getString(R.string.error_quantity_invalid));
                return 0;
            }
        }

        // Validate sales
        if (values.containsKey(BookEntry.COLUMN_BOOK_SALES)) {
            // Check that sales is not negative
            Integer sales = values.getAsInteger(BookEntry.COLUMN_BOOK_SALES);
            if (sales < 0) {
                // Log error
                Log.e(LOG_TAG, getContext().getString(R.string.error_sales_invalid));
                return 0;
            }
        }

        // Get the database in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Declare int which will store the number of rows updated
        int rowsUpdated = db.update(BookEntry.TABLE_NAME, values, selection, selectionArgs);

        // Get current time and date, then call method to update the logs, passing along the time
        // and event description
        String dateAndTime = DateUtils.formatDateTime(getContext(), System.currentTimeMillis(),
                DateUtils
                        .FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_TIME);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);

            // Update logs, passing date, time, message into helper method
            QueryUtils.updateLogs(getContext(), dateAndTime,
                    getContext().getString(R.string.update_book_successful));
        } else {
            // Update logs, passing date, time, message into helper method
            QueryUtils.updateLogs(getContext(), dateAndTime,
                    getContext().getString(R.string.update_book_error));
        }
        // Return number of rows updated
        return rowsUpdated;
    }

    /**
     * Returns the MIME type of data for the content URI.
     * UriMatcher BOOKS case → Return MIME type BookEntry.CONTENT_LIST_TYPE
     * UriMatcher BOOK_ID case → Return MIME type BookEntry.CONTENT_ITEM_TYPE
     *
     * @param uri the URI to query.
     * @return a MIME type string, or {@code null} if there is no type.
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        // Get Uri match code
        final int match = sUriMatcher.match(uri);
        // Use switch statement to determine which mime type to return
        switch (match) {
            case BOOKS:
                return BookEntry.CONTENT_LIST_TYPE;
            case BOOK_ID:
                return BookEntry.CONTENT_ITEM_TYPE;
            default:
                // Log error:
                Log.e(LOG_TAG, "IllegalStateException: Unknown URI " + uri + " with match " +
                        match);

                return null;
        }
    }
}
