/*
 * Created by Sonia M on 9/27/18 3:57 PM.
 *  Tips, Guidance, and in some cases code snippets are obtained from Udacity Lessons
 *  relevant to this project. Overriden methods use javadoc from Super Class. Any additional
 *  guidance for specific methods is outlined in the javadocs for those specific methods.
 */

package com.example.android.bookventoria;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookventoria.data.BookContract;
import com.example.android.bookventoria.data.BookContract.BookEntry;

import java.text.NumberFormat;

/**
 * Allows user to view details about a specific book title. The user can also adjust the book's
 * quantity, delete, or edit the book directly from the user interface. Also implements
 * LoaderManager.LoaderCallBacks<Cursor> interface
 */
public class BookDetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Log tag for debugging
     */
    private static final String LOG_TAG = BookDetailActivity.class.getSimpleName();

    /**
     * Identifier for the book inventory data loader
     */
    private static final int EXISTING_BOOK_LOADER = 0;
    // App Support Toolbar
    Toolbar detailToolbar;
    /**
     * Uri data passed in with intent for viewing/ editing books
     */
    private Uri mCurrentBookUri;
    /**
     * int variable keeps track of quantity and allows for updating that value
     */
    private int mCurrentQuantity;
    /**
     * String variable keeps track of supplier phone number
     */
    private String mSupplierPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        // Examine the intent that was used to launch this activity to retrieve the book's id/uri
        mCurrentBookUri = getIntent().getData();

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_detail);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BookDetailActivity.this, BookEditActivity.class);
                intent.setData(mCurrentBookUri);
                startActivity(intent);
            }
        });

        // Add listeners to increment/ decrement quantity buttons
        // First find the textviews
        TextView incrementTextView = (TextView) findViewById(R.id.increment);
        TextView decrementTextView = (TextView) findViewById(R.id.decrement);

        // Attach listeners to the increment/ decrement buttons
        incrementTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incrementQuantity();
            }
        });

        decrementTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decrementQuantity();
            }
        });

        // Set up toolbar (action bar)
        detailToolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(detailToolbar);

        // Configure the Up button on toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Hide title on Toolbar
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Initialize the loader to retrieve book data
        getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
    }

    /**
     * Initialize the contents of the Activity's standard options menu.
     * @param menu The options menu.
     * @return Boolean value must return true for the menu to be displayed;
     *         if you return false it will not be shown.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_detail.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    /**
     * This hook is called whenever an item in the options menu is selected.
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to
     *         proceed, true to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {

            // Respond to a click on the "Add New Book" menu option
            case R.id.action_add:
                // Send intent to open Book Edit Activity, passing null for uri
                Intent intentAdd = new Intent(BookDetailActivity.this,
                        BookEditActivity.class);
                intentAdd.setData(null);
                startActivity(intentAdd);
                return true;

            // Respond to a click on the "Edit" menu option
            case R.id.action_edit:
                // Send intent to open Book Edit Activity, passing in uri
                Intent intent = new Intent(BookDetailActivity.this,
                        BookEditActivity.class);
                intent.setData(mCurrentBookUri);
                startActivity(intent);
                return true;

            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Show confirmation dialogue
                showDeleteConfirmationDialog();
                return true;

            // Respond to a click on the "Close" menu option
            case R.id.action_close:
                // Return to main activity
                finish();
                return true;

            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(BookDetailActivity.this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Confirm Deletion with Confirmation Dialog.  Code snippets obtained from Pets App lesson from
     * Udacity Android Basics course
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Set message (question) that will be shown on the warning
        builder.setMessage(R.string.delete_dialog_msg);
        // Set text for Button that will confirm the user wants to proceed with the delete action
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the book by calling method
                deleteBook();
            }
        });
        // Set text for the button the user can click to cancel the operation
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the book.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Finally, create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Increase the Quantity by 1. This method is called when the user presses the "+" button to
     * increase quantity
     */
    private void incrementQuantity() {
        // Increase member variable for quantity by 1
        mCurrentQuantity++;

        // Create content values object, then call Content Resolver to update the values
        ContentValues values = new ContentValues();
        values.put(BookContract.BookEntry.COLUMN_BOOK_QUANTITY, mCurrentQuantity);
        getContentResolver().update(mCurrentBookUri, values, null, null);
    }

    /**
     * Decrease the Quantity by 1, unless the quantity count is at 0. This method is called when
     * the user presses the "-" button to decrease quantity
     */
    private void decrementQuantity() {
        if (mCurrentQuantity > 0) {

            // If current quantity is greater than 0, decrease by 1
            mCurrentQuantity--;

            // Create content values object, then call Content Resolver to update the values
            ContentValues values = new ContentValues();
            values.put(BookContract.BookEntry.COLUMN_BOOK_QUANTITY, mCurrentQuantity);
            getContentResolver().update(mCurrentBookUri, values, null, null);
        }
    }

    /**
     * Perform the deletion of the book in the database.
     */
    private void deleteBook() {
        // If the Uri is equal to null, return. The uri must include the id of a specific book
        if (mCurrentBookUri == null) {
            return;
        }
        // Defines a new int variable that receives the number of rows deleted
        int rowsDeleted = getContentResolver().delete(
                mCurrentBookUri,         // Uri of book
                null,         // Selection criteria, null because it's defined in BookProvider
                null);      // Selection args, null because it's defined in BookProvider

        // If the book was deleted, then display a toast message indicating that the
        // delete operation was successful
        if (rowsDeleted != 0) {
            // the deletion was successful and we can display a toast.
            // To get the toast message to display without crashing the app on certain phones, had
            // to use "getApplicationContext()". This was discovered through trial and error.
            Toast.makeText(getApplicationContext(), getString(R.string.editor_delete_book_successful),
                    Toast.LENGTH_SHORT).show();

            // Close editor activity
            finish();
        } else {
            // the deletion was unsuccessful and we can display a toast.
            // To get the toast message to display without crashing the app on certain phones, had
            // to use "getApplicationContext()"
            Toast.makeText(getApplicationContext(), getString(R.string.editor_delete_book_failed),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Instantiate and return a new Loader for the given ID. URI for one book
     *
     * @param i      The ID whose loader is to be created.
     * @param bundle Any arguments supplied by the caller.
     * @return Return a new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_NAME,
                BookEntry.COLUMN_BOOK_AUTHOR,
                BookEntry.COLUMN_BOOK_GENRE,
                BookEntry.COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_BOOK_QUANTITY,
                BookEntry.COLUMN_BOOK_SALES,
                BookEntry.COLUMN_BOOK_SUPPLIER_NAME,
                BookEntry.COLUMN_BOOK_SUPPLIER_PHONE};

        // This loader will execute the ContentProvider's query method on a background thread
        // It's only called when the Uri passed from MainActivity is NOT null
        return new CursorLoader(this,   // Parent activity context
                mCurrentBookUri,                // Provider content URI to query
                projection,                     // Columns to include in the resulting Cursor
                null,                  // No selection clause
                null,               // No selection arguments
                null);                 // Default sort order
    }

    /**
     * Update TextView fields with book data from the Cursor
     *
     * @param loader The Loader that has finished.
     * @param cursor The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        // Immediately return if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Move the cursor to the 0th position before extracting out column values
        // moveToFirst returns a boolean of true or false
        if (cursor.moveToFirst()) {

            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(BookEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME);
            int authorColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_AUTHOR);
            int genreColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_GENRE);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);
            int salesColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SALES);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE);

            // Use that index to extract the String or Int value of the word
            // at the current row the cursor is on.
            String retrievedBookName = cursor.getString(nameColumnIndex);
            String retrievedAuthor = cursor.getString(authorColumnIndex);
            int retrievedGenre = cursor.getInt(genreColumnIndex);
            int retrievedPrice = cursor.getInt(priceColumnIndex);
            mCurrentQuantity = cursor.getInt(quantityColumnIndex);
            int retrievedSales = cursor.getInt(salesColumnIndex);
            String retrievedSupplierName = cursor.getString(supplierNameColumnIndex);
            String retrievedSupplierPhone = cursor.getString(supplierPhoneColumnIndex);

            // Find TextView fields from xml layout
            TextView bookNameTextView = (TextView) findViewById(R.id.book_name);
            TextView authorTextView = (TextView) findViewById(R.id.author);
            TextView salesTextView = (TextView) findViewById(R.id.sales);
            TextView genreTextView = (TextView) findViewById(R.id.genre);
            TextView priceTextView = (TextView) findViewById(R.id.price);
            TextView quantityTextView = (TextView) findViewById(R.id.quantity);
            TextView supplierNameTextView = (TextView) findViewById(R.id.supplier_name);
            TextView supplierPhoneTextView = (TextView) findViewById(R.id.supplier_phone);

            // Set TextView fields to show the data from the cursor
            bookNameTextView.setText(retrievedBookName);
            quantityTextView.setText(Integer.toString(mCurrentQuantity));
            salesTextView.setText(Integer.toString(retrievedSales));
            supplierNameTextView.setText(retrievedSupplierName);
            supplierPhoneTextView.setText(retrievedSupplierPhone);

            // Author will be passed into string resource so that the word 'by' precedes it
            authorTextView.setText(getString(R.string.detail_by_author, retrievedAuthor));

            // Format Price to show currency
            // First divide by 100 to get in dollars
            float priceDollars = (float) retrievedPrice / 100;

            // Now set price textview, formatted as currency
            priceTextView.setText(NumberFormat.getCurrencyInstance().format(priceDollars));

            // Set up Call Now button
            // Find Button then add click listener to it
            Button callNowTextView = findViewById(R.id.call_now_button);

            // Get the tel: prefix from resources, then append the phone number
            mSupplierPhoneNumber = getString(R.string.detail_phone_number_prefix);
            mSupplierPhoneNumber = mSupplierPhoneNumber + retrievedSupplierPhone;

            // Add On click listener to Call Now button
            callNowTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Send Intent to call number
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse(mSupplierPhoneNumber));

                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }
            });

            // Set up the genre textview to show the correct genre
            switch (retrievedGenre) {
                case BookEntry.GENRE_NONFICTION:
                    genreTextView.setText(BookEntry.FORMATTED_GENRE_NONFICTION);
                    break;

                case BookEntry.GENRE_FICTION:
                    genreTextView.setText(BookEntry.FORMATTED_GENRE_FICTION);
                    break;

                case BookEntry.GENRE_MYSTERY_SUSPENSE:
                    genreTextView.setText(BookEntry.FORMATTED_GENRE_MYSTERY_SUSPENSE);
                    break;

                case BookEntry.GENRE_CHILDRENS:
                    genreTextView.setText(BookEntry.FORMATTED_GENRE_CHILDRENS);
                    break;

                case BookEntry.GENRE_HISTORY:
                    genreTextView.setText(BookEntry.FORMATTED_GENRE_HISTORY);
                    break;

                case BookEntry.GENRE_FINANCE_BUSINESS:
                    genreTextView.setText(BookEntry.FORMATTED_GENRE_FINANCE_BUSINESS);
                    break;

                case BookEntry.GENRE_SCIENCE_FICTION_FANTASY:
                    genreTextView.setText(BookEntry.FORMATTED_GENRE_SCIENCE_FICTION_FANTASY);
                    break;

                case BookEntry.GENRE_ROMANCE:
                    genreTextView.setText(BookEntry.FORMATTED_GENRE_ROMANCE);
                    break;

                case BookEntry.GENRE_HOME_FOOD:
                    genreTextView.setText(BookEntry.FORMATTED_GENRE_HOME_FOOD);
                    break;

                case BookEntry.GENRE_TEENS_YOUNG_ADULT:
                    genreTextView.setText(BookEntry.FORMATTED_GENRE_TEENS_YOUNG_ADULT);
                    break;

                case BookEntry.GENRE_OTHER:
                    genreTextView.setText(BookEntry.FORMATTED_GENRE_OTHER);
                    break;

                default:
                    genreTextView.setText(BookEntry.FORMATTED_GENRE_UNKNOWN);
                    break;
            }
        }
    }

    /**
     * Clear the input fields. Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Set uri back to null
        mCurrentBookUri = null;
    }
}
