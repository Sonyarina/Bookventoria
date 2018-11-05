package com.example.android.bookventoria;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.support.v4.app.LoaderManager.LoaderCallbacks;

import com.example.android.bookventoria.data.BookContract.BookEntry;

import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BookListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BookListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookListFragment extends Fragment implements LoaderCallbacks<Cursor> {

    /* Tag for the log messages */
    public static final String LOG_TAG = BookListFragment.class.getSimpleName();

    /* Identifier for the book inventory data loader */
    private static final int BOOK_LOADER = 0;

    // Context
    Context context;

    /* Adapter for the ListView */
    BookCursorAdapter mCursorAdapter;

    // The ListView which will be populated with the book data
    ListView bookListView;
    // Reference to ProgressBar View in activity layout
    ProgressBar progressBar;
    // Empty view for ListView
    View emptyView;
    // Setup FAB to open EditorActivity
    FloatingActionButton fab;
    // Parameters that will be needed for queries: Selection, SelectionArgs, and Projection
    // Selection specifies the rows that will be shown in the ListView
    private String mSelection;
    // Selection args are the criteria that the rows must match to be displayed
    private String[] mSelectionArgs;
    // Projection that specifies the columns that will be shown in the ListView
    private String[] mProjection;
    // TextView which displays the totalInventoryBooks value
    private TextView totalBooksTextView;
    // TextView which displays the totalInventorySales value
    private TextView totalSalesTextView;
    // TextView which displays the lowInventory value
    private TextView lowInventoryTextView;
    // Inventory Summary Linear Layout view
    private LinearLayout inventorySummaryLayout;
    private OnFragmentInteractionListener mListener;
    // Reference to Add New Book Menu Item
    MenuItem addNewBookMenuItem;

    public BookListFragment() {
        // Required empty public constructor
        this.context = getActivity();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param projection    Query Projection
     * @param selection     Query Selection
     * @param selectionArgs Query Selection Arguments
     * @return A new instance of fragment BookListFragment.
     */
    public static BookListFragment newInstance(String[] projection, String selection, String[]
            selectionArgs) {
        BookListFragment fragment = new BookListFragment();
        Bundle args = new Bundle();
        args.putStringArray(QueryUtils.PROJECTION_KEY, projection);
        args.putString(QueryUtils.SELECTION_KEY, selection);
        args.putStringArray(QueryUtils.SELECTION_ARGS_KEY, selectionArgs);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mProjection = getArguments().getStringArray(QueryUtils.PROJECTION_KEY);
            mSelection = getArguments().getString(QueryUtils.SELECTION_KEY);
            mSelectionArgs = getArguments().getStringArray(QueryUtils.SELECTION_ARGS_KEY);
        }
        // Check for a null projection
        if (mProjection == null || mProjection.length == 0) {
            // If the projection is null, provide default values
            mProjection = new String[]{
                    BookEntry._ID,
                    BookEntry.COLUMN_BOOK_NAME,
                    BookEntry.COLUMN_BOOK_AUTHOR,
                    BookEntry.COLUMN_BOOK_GENRE,
                    BookEntry.COLUMN_BOOK_PRICE,
                    BookEntry.COLUMN_BOOK_QUANTITY,
                    BookEntry.COLUMN_BOOK_SALES,
                    BookEntry.COLUMN_BOOK_SUPPLIER_NAME,
                    BookEntry.COLUMN_BOOK_SUPPLIER_PHONE};
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_book_list, container, false);

        // Setup FAB to open EditorActivity
        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.addBookFabPress();
            }
        });

        // Report that this fragment would like to participate in populating
        // the options menu by receiving a call to {@link #onCreateOptionsMenu}
        // and related methods.
        setHasOptionsMenu(true);

        // Find Add New Book menu item
        //addNewBookMenuItem = rootView.findViewById(R.id.action_add);

        // Find Inventory Summary TextViews
        totalBooksTextView = rootView.findViewById(R.id.inventory_summary_text);
        totalSalesTextView = rootView.findViewById(R.id.sales_summary_text);
        lowInventoryTextView = rootView.findViewById(R.id.low_stock_summary_text);

        // Find Inventory Summary view
        inventorySummaryLayout = rootView.findViewById(R.id.inventory_summary_layout_view1);

        // Find the ListView which will be populated with the book data
        bookListView = (ListView) rootView.findViewById(R.id.list);

        // Find and inflate view that will be used to add head view to ListView
        View headerView = inflater.inflate(R.layout.header_view, null);
        bookListView.addHeaderView(headerView);
        /* Find Text button for adding demo data & set onClick Listener */
        TextView addDemoDataText = rootView.findViewById(R.id.add_dummy_data_text);
        addDemoDataText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.addDemoData();
            }
        });

        // Setup an Adapter to create a list item for each row of book data in the Cursor.
        // There is no book data yet (until the loader finishes) so pass in null for the Cursor.
        mCursorAdapter = new BookCursorAdapter(getActivity(), null);
        bookListView.setAdapter(mCursorAdapter);

        // Setup item click listener
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Make sure the user clicked on a book and not the ListView header
                if(id == -1){
                    // The header has an id of -1. If it was clicked, call View All Books query
                    mListener.allBooksQuery();

                } else {
                    // Create new intent to go to {@link BookDetailActivity}
                    Intent intent = new Intent(getActivity(), BookDetailActivity.class);

                    // Form the content URI for the specific book that was clicked on
                    Uri currentBookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);

                    // Set the URI on the data field of the intent
                    intent.setData(currentBookUri);

                    // Launch the {@link BookDetailActivity} to display the data for the current book.
                    startActivity(intent);
                }
            }
        });

        // Add on scroll listener to ListView in order to hide fab and inventory summary
        bookListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // Hide inventory summary, unless first visible item is the first row
                // (the first row is the 0 index)
                if (firstVisibleItem == 0) {
                    // Show the fab and the Linear Layout Table containing the Inventory Summary
                    // Data
                    inventorySummaryLayout.setVisibility(View.VISIBLE);
                    fab.setVisibility(View.VISIBLE);

                    // Hide Menu Item when fab is visible
                    //addNewBookMenuItem.setVisible(false);
                } else {
                    // Hide the fab and the Linear Layout Table containing the Inventory Summary
                    // Data
                    inventorySummaryLayout.setVisibility(View.GONE);
                    fab.setVisibility(View.GONE);

                    // Show Menu Item when fab is gone
                    //addNewBookMenuItem.setVisible(true);
                }
            }
        });

        // Find empty view for ListView
        emptyView = rootView.findViewById(R.id.empty_view);

        // Reference to ProgressBar View in activity layout
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar_view);

        getLoaderManager().initLoader(BOOK_LOADER, null, this);

        // Return rootView
        return rootView;
    }

    /**
     * This method is called so that the
     * menu can be updated (some menu items can be hidden or made visible).
     * Code snippets obtained from Pets App lesson from
     * Udacity Android Basics course
     */
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;

        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    /**
     * Called when the fragment is visible to the user and actively running.
     */
    @Override
    public void onResume() {

        // Call loader
        getLoaderManager().restartLoader(BOOK_LOADER, null, this);
        super.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
        // Retrieve user sort preferences from SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this
                .context);

        // Retrieve OrderBy preference
        String orderBy = sharedPreferences.getString(
                getString(R.string.settings_order_by_key), getString(R.string.settings_order_by_default));

        // Retrieve sort direction preference
        String sortDirection = sharedPreferences.getString(
                getString(R.string.settings_sort_direction_key), getString(R.string.settings_sort_direction_default));

        String sortOrder = orderBy + " " + sortDirection;

        // Check whether the selection is null
        if (TextUtils.isEmpty(mSelection) || (mSelection.equals("")) || mSelection == null) {
            // Return new CursorLoader where selection/selectionArgs are null
            // This loader will execute the ContentProvider's query method on a background thread
            return new CursorLoader(this.context,   // Parent activity context
                    BookEntry.CONTENT_URI,   // Provider content URI to query
                    mProjection,              // Columns to include in the resulting Cursor
                    null,           // Selection clause
                    null,       // Selection arguments
                    sortOrder);         // Default sort order
        }

        // If selection is not null, return the loader with the selection and selectionArgs
        // passed in as parameters
        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this.context,   // Parent activity context
                BookEntry.CONTENT_URI,   // Provider content URI to query
                mProjection,              // Columns to include in the resulting Cursor
                mSelection,           // Selection clause
                mSelectionArgs,       // Selection arguments
                sortOrder);         // Default sort order
    }

    /**
     * Called when a previously created loader has finished its load.
     *
     * @param loader     The Loader that has finished.
     * @param cursorData The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor
            cursorData) {

        // Cast the data object to cursor
        Cursor data = (Cursor) cursorData;

        // Update {@link BookCursorAdapter} with this new cursor containing updated book data
        mCursorAdapter.swapCursor(data);

        // Used in inventory summary area. Variable type int will store total sales
        int totalInventorySales = 0;

        // Used in inventory summary area. Variable type int will store books that are either close to
        // or completely out of stock
        int lowInventory = 0;

        // Check for null data, and show empty view if it's null
        if (data != null && data.getCount() > 0) {
            // Change the visibility of the emptyView to Gone
            emptyView.setVisibility(View.GONE);

            // Hide progress bar
            progressBar.setVisibility(View.GONE);

            // Retrieve Inventory Summary Data from the cursor
            // Get the total number of books in the cursor and set equal to total books
            int totalInventoryBooks = data.getCount();

            // Set the textview to show the total books. Pass in as a parameter the total
            // books inventory amount. The string resource contains html
            // tags which must also be parsed
            totalBooksTextView.setText(Html.fromHtml(getString(R.string.inventory_total_books,
                    totalInventoryBooks)));

            // Get the index of the relevant columns
            int quantityColumnIndex = data.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);
            int salesColumnIndex = data.getColumnIndex(BookEntry.COLUMN_BOOK_SALES);

            int retrievedQuantity = 0;
            int retrievedSales = 0;

            // Have the cursor move to first position
            data.moveToPosition(-1);

            // Iterate through the cursor to obtain the sales and quantity totals
            while (data.moveToNext()) {
                Log.v(LOG_TAG, "Iterating through cursor");
                // Retrieve quantity
                retrievedQuantity = data.getInt(quantityColumnIndex);

                Log.v(LOG_TAG, "Retrieved Quantity is " + retrievedQuantity);

                // If quantity is less than 10, add to low inventory count
                if (retrievedQuantity < 10) {
                    lowInventory++;
                    Log.v(LOG_TAG, "Low Inventory Count is now " + lowInventory);
                }

                // Retrieve sales total
                retrievedSales = data.getInt(salesColumnIndex);
                Log.v(LOG_TAG, "retrieved sales for this book is: " + retrievedSales);

                // Add the sales number to the total sales count
                totalInventorySales = totalInventorySales + retrievedSales;
                Log.v(LOG_TAG, "totalInventory sales is: " + totalInventorySales);
            }

            // Update the inventory summary total sales textview with the retrieved information
            // Pass in as a parameter the total sales number. The string resource
            // contains html tags which must also be parsed
            totalSalesTextView.setText(Html.fromHtml(getString(R.string
                    .inventory_total_sales, totalInventorySales)));

            // Update the inventory summary low inventory textview with the retrieved
            // information. Pass in as a parameter the low inventory amount. The string
            // resource contains html tags which must also be parsed
            lowInventoryTextView.setText(Html.fromHtml(getString(R.string
                    .inventory_low_stock, lowInventory)));

            // Now add click listeners to the Inventory Snapshot TextViews.
            // Add listener to totalBooksTextView that will lead to all
            // the books in a listview
            totalBooksTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Call allBooksQuery method, which will be handled by MainActivity
                    mListener.allBooksQuery();
                }
            });

            // Set new click listener on the low inventory textview
            // When user clicks it, it will pull up a list of all books with low inventory
            lowInventoryTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Call low InventoryQuery method, which will be handled by MainActivity
                    mListener.lowInventoryQuery();
                }
            });

            // Set new click listener on the Sales summary in the Inventory Snapshot area
            // When user clicks it, it will pull up a list of all books with low inventory
            totalSalesTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Call low InventoryQuery method, which will be handled by MainActivity
                    mListener.viewAllBooksWithSalesQuery();
                }
            });

            // Show the Linear Layout Table containing the Inventory Summary Data
            inventorySummaryLayout.setVisibility(View.VISIBLE);

        } else {
            // Hide the Linear Layout Table containing the Inventory Summary Data
            inventorySummaryLayout.setVisibility(View.GONE);

            // Hide progress bar
            progressBar.setVisibility(View.GONE);

            // Set empty view on the ListView, so that it only shows when the list has 0 items.
            bookListView.setEmptyView(emptyView);
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
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity. Tip and guidance obtained from Android Training lesson
     * http://developer.android.com/training/basics/fragments/communicating.html
     * "Communicating with Other Fragments"
     */
    public interface OnFragmentInteractionListener {

        /**
         * Method that adds demo data to the books inventory
         */
        void addDemoData();

        /**
         * Method that responds to the Add Book fab button press
         */
        void addBookFabPress();

        /**
         * Queries the Books inventory database for books with low quantity amount. Less than 10
         * books remaining is considered to be Low Quantity
         */
        void lowInventoryQuery();

        /**
         * Queries the Books inventory database for books with at least 1 sale
         */
        void viewAllBooksWithSalesQuery();

        /**
         * Queries all the books in database
         */
        void allBooksQuery();

    }
}
