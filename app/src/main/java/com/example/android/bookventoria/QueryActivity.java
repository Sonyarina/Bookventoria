package com.example.android.bookventoria;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;

import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookventoria.data.BookContract.BookEntry;

/**
 * Displays queries made on the Books table. Tips, Guidance, and in some
 * cases code snippets are obtained from Udacity Lessons relevant to this project.
 * Any additional guidance for specific methods is outlined in the javadocs for those specific methods.
 */
public class QueryActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /* Tag for the log messages */
    private static final String LOG_TAG = QueryActivity.class.getSimpleName();

    /* Identifier for the book inventory data loader */
    private static final int QUERY_BOOK_LOADER = 0;

    /* Current Query Code (Default is 0) */
    int currentQueryCode = 0;

    /* Current Genre Code */
    int currentGenreCode = 0;

    /* Current Supplier Name */
    String currentSupplierName = "";

    /* SharedPreferences to retrieve and store user preferences */
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedPrefEditor;

    /* App Support Toolbar */
    Toolbar queryToolbar;

    /* Adapter for the ListView */
    BookCursorAdapter mCursorAdapter;

    // TextView inside the headerView which will be changed based on which query is
    // being run
    TextView headerText;

    // TextView that shows report description
    private TextView queryDescriptionTextView, queryEmptyViewTextView;

    // Selection value, which is updated through data sent from intents
    private String mSelection;

    // SelectionArgs, which is updated through data sent from intents
    private String mSelectionArgs[];

    // The ListView which will be populated with the book data
    private ListView queryBookListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);

        // Set up toolbar (action bar)
        queryToolbar = (Toolbar) findViewById(R.id.query_toolbar);
        setSupportActionBar(queryToolbar);

        // Configure the Up button on toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Retrieve intent data, which includes query code
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentQueryCode = extras.getInt(QueryUtils.QUERY_CODE_KEY);
            Log.v(LOG_TAG, "Current query code is " + currentQueryCode);

            // Retrieve Genre code if the query code is for genre
            if (currentQueryCode == QueryUtils.QUERY_GENRE) {
                currentGenreCode = extras.getInt(QueryUtils.GENRE_CODE_KEY);
                Log.v(LOG_TAG, "Current genre code is " + currentGenreCode);
            }

            // Retrieve Supplier Name if the query code is for Supplier's Books
            if (currentQueryCode == QueryUtils.QUERY_SUPPLIER_BOOKS) {
                currentSupplierName = extras.getString(QueryUtils.SUPPLIER_CODE_KEY);
                Log.v(LOG_TAG, "Current supplier name is " + currentSupplierName);
            }
        }

        // Find query description textview header
        queryDescriptionTextView = findViewById(R.id.query_description_text_view);
        //queryDescriptionTextView.setVisibility(View.GONE);

        // Find empty view textview
        queryEmptyViewTextView = findViewById(R.id.empty_subtitle_text);

        // Find the ListView which will be populated with the book data
        queryBookListView = (ListView) findViewById(R.id.list);

        // Find and inflate view that will be used to add head view to ListView
        View headerView = getLayoutInflater().inflate(R.layout.query_listview_header, null);

        // Find the TextView inside the headerView which will be changed based on which query is
        // being run
        headerText = headerView.findViewById(R.id.listview_header_text);

        // Add Headerview to list view
        queryBookListView.addHeaderView(headerView);

        // Setup an Adapter to create a list item for each row of book data in the Cursor.
        // There is no book data yet (until the loader finishes) so pass in null for the Cursor.
        mCursorAdapter = new BookCursorAdapter(this, null);
        queryBookListView.setAdapter(mCursorAdapter);

        // Setup item click listener
        queryBookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Make sure the user clicked on a book and not the ListView header
                if (id == -1) {
                    // The header has an id of -1. If it was clicked, do nothing
                    Log.v(LOG_TAG,"User clicked header view");

                } else {
                    // Create new intent to go to {@link BookDetailActivity}
                    Intent intent = new Intent(QueryActivity.this, BookDetailActivity.class);

                    // Form the content URI for the specific book that was clicked on
                    Uri currentBookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);

                    // Set the URI on the data field of the intent
                    intent.setData(currentBookUri);

                    // Launch the {@link BookDetailActivity} to display the data for the current book.
                    startActivity(intent);
                }
            }
        });

        // Figure out which query to run, using the intent data that was passed in
        switch (currentQueryCode) {
            case QueryUtils.QUERY_ALL_BOOKS:
                // Call QueryAllBooks method
                queryAllBooks();
                break;

            case QueryUtils.QUERY_LOW_INVENTORY:
                // Call Low Inventory query method
                queryLowStockBooks();
                break;

            case QueryUtils.QUERY_GENRE:
                // Call Query by Genre method
                queryByGenre();
                break;

            case QueryUtils.QUERY_SUPPLIER_BOOKS:
                // Call Query books from supplier method
                queryBooksBySupplier();
                break;

            case QueryUtils.QUERY_ALL_SALES:
                // Call Query All Sales
                queryAllSales();
                break;
        }
    }

    /**
     * According to super class, this method will Initialize the contents of the Activity's
     * standard options menu.
     *
     * @param menu The options menu in which the items are placed
     * @return Must return true for the menu to be displayed;
     * if you return false it will not be shown.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_main.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_query, menu);
        return true;
    }

    /**
     * Determines which actions will take place when the user interacts with items on the toolbar
     *
     * @param item menu item clicked
     * @return Return false to allow normal menu processing to proceed, true to
     * consume it here (from super class doc)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {

            // Respond to a click on the "Sort Order Settings" menu option
            case R.id.action_sort:
                // Call settings fragment
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;

            // Respond to a click on the "Add New Book" menu option
            case R.id.action_add:
                // Send intent to open Book Edit Activity, passing null for uri
                Intent intentAdd = new Intent(QueryActivity.this,
                        BookEditActivity.class);
                intentAdd.setData(null);
                startActivity(intentAdd);
                return true;

            // Because the QueryActivity is called from several different fragments and
            // activities, the home button will act as the back button. Tips and guidance
            // found on StackOverflow.com
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method will query the Books table for books with low inventory numbers. Low stock is
     * defined as less than 10 books left in stock. It will pass the selection and selectionArgs
     * to the loader
     */
    private void queryLowStockBooks() {

        // Set title
        queryToolbar.setTitle(R.string.query_low_inventory_header);

        // Hide header text view, since title is all that's needed for this query
        headerText.setVisibility(View.GONE);

        // The selection and selectionArgs is WHERE quantity is less than 10
        mSelection = BookEntry.COLUMN_BOOK_QUANTITY + "<?";
        mSelectionArgs = new String[]{String.valueOf(10)};

        // Set empty view text
        queryEmptyViewTextView.setText(R.string.empty_view_low_inventory);

        // Kick off the loader
        getLoaderManager().initLoader(QUERY_BOOK_LOADER, null, this);
    }

    /**
     * This method will query all books with at least 1 sale. It will pass the selection and
     * selectionArgs to the loader
     */
    private void queryAllSales() {

        // Set title
        queryToolbar.setTitle(R.string.query_books_with_sales_header);

        // Hide header text view, since title is all that's needed for this query
        headerText.setVisibility(View.GONE);

        // The selection and selectionArgs is WHERE sales amount is at least 1
        mSelection = BookEntry.COLUMN_BOOK_SALES + ">=?";
        mSelectionArgs = new String[]{String.valueOf(1)};

        // Set empty view text
        queryEmptyViewTextView.setText(R.string.empty_view_low_inventory);

        // Kick off the loader
        getLoaderManager().initLoader(QUERY_BOOK_LOADER, null, this);
    }

    /**
     * This method will query the Books table for books of a specified genre. In future
     * adaptations, the layout might also include fab button where the user can add a new book with
     * the genre preselected
     */
    private void queryByGenre() {
        // Get the genre's name in String format
        String genreName = QueryUtils.getGenreStringFormat(currentGenreCode);

        // Set title
        queryToolbar.setTitle(R.string.query_by_genre_header);

        // Set ListView HeaderView text
        headerText.setText(genreName);

        // The selection and selectionArgs is WHERE genre is equal to the code that was passed in
        mSelection = BookEntry.COLUMN_BOOK_GENRE + "=?";
        mSelectionArgs = new String[]{String.valueOf(currentGenreCode)};

        // Set empty view text
        queryEmptyViewTextView.setText(getString(R.string.empty_view_genre, genreName));

        // Kick off the loader
        getLoaderManager().initLoader(QUERY_BOOK_LOADER, null, this);
    }

    /**
     * This method will query the Books table for books of a specified genre
     */
    private void queryBooksBySupplier() {

        // Set title
        queryToolbar.setTitle(R.string.query_supplier_books_header);

        // Set ListView HeaderView text
        headerText.setText(currentSupplierName);

        // The selection and selectionArgs is WHERE supplier name is equal to the supplier name
        // that was passed in from the MainActivity
        mSelection = BookEntry.COLUMN_BOOK_SUPPLIER_NAME + "=?";
        mSelectionArgs = new String[]{currentSupplierName};

        // Set empty view text
        queryEmptyViewTextView.setText(R.string.empty_view_supplier_books);

        // Kick off the loader
        getLoaderManager().initLoader(QUERY_BOOK_LOADER, null, this);
    }


    /**
     * This method will query the Books table for all books with no filters
     */
    private void queryAllBooks() {

        // Set title
        queryToolbar.setTitle(R.string.query_all_books_header);

        // Hide header text view, since title is all that's needed for this query
        headerText.setVisibility(View.GONE);

        // The selection and selectionArgs are null since all rows will be returned
        mSelection = null;
        mSelectionArgs = null;

        // Set empty view text
        queryEmptyViewTextView.setText(R.string.empty_view_title_text);

        // Kick off the loader
        getLoaderManager().initLoader(QUERY_BOOK_LOADER, null, this);
    }

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id   The ID whose loader is to be created.
     * @param args Any arguments supplied by the caller.
     * @return Return a new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Define a projection that specifies the columns that will be shown in the ListView
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

        // Retrieve user sort preferences from SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Retrieve OrderBy preference
        String orderBy = sharedPreferences.getString(
                getString(R.string.settings_order_by_key), getString(R.string.settings_order_by_default));

        // Retrieve sort direction preference
        String sortDirection = sharedPreferences.getString(
                getString(R.string.settings_sort_direction_key), getString(R.string.settings_sort_direction_default));

        String sortOrder = orderBy + " " + sortDirection;

        // Check whether the selection is null
        if (TextUtils.isEmpty(mSelection) || (mSelection.equals(""))) {
            // Return new CursorLoader where selection/selectionArgs are null
            // This loader will execute the ContentProvider's query method on a background thread
            return new CursorLoader(this,   // Parent activity context
                    BookEntry.CONTENT_URI,   // Provider content URI to query
                    projection,              // Columns to include in the resulting Cursor
                    null,           // Selection clause
                    null,       // Selection arguments
                    sortOrder);         // Default sort order
        }
        // If selection is not null, return the loader with the selection and selectionArgs
        // passed in as parameters
        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                BookEntry.CONTENT_URI,   // Provider content URI to query
                projection,              // Columns to include in the resulting Cursor
                mSelection,           // Selection clause
                mSelectionArgs,       // Selection arguments
                sortOrder);         // Default sort order
    }

    /**
     * Called when a previously created loader has finished its load.
     *
     * @param loader The Loader that has finished.
     * @param data   The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update {@link BookCursorAdapter} with this new cursor containing updated book data
        mCursorAdapter.swapCursor(data);

        // Find empty view for ListView
        View emptyView = findViewById(R.id.empty_view);

        // Reference to ProgressBar View in activity layout
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar_view);

        if (data != null && data.getCount() > 0) {
            // Change the visibility of the emptyView to Gone
            emptyView.setVisibility(View.GONE);

            // Hide progress bar
            progressBar.setVisibility(View.GONE);

        } else {

            // Make description text disappear
            queryDescriptionTextView.setVisibility(View.GONE);
            headerText.setVisibility(View.GONE);

            // Hide progress bar
            progressBar.setVisibility(View.GONE);

            // Set empty view on the ListView, so that it only shows when the list has 0 items.
            queryBookListView.setEmptyView(emptyView);
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.v(LOG_TAG, "onResume called");

        // Restart Loader
        getLoaderManager().restartLoader(QUERY_BOOK_LOADER, null, this);
    }
}
