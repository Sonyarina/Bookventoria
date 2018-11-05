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
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.internal.view.SupportMenu;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bookventoria.data.BookContract.BookEntry;

import java.text.NumberFormat;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnSupplierDetailFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SupplierDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SupplierDetailFragment extends Fragment implements LoaderCallbacks<Cursor> {

    /* Tag for the log messages */
    public static final String LOG_TAG = SupplierDetailFragment.class.getSimpleName();

    /* Identifier for the suppliers list data loader */
    private static final int SUPPLIER_DETAIL_LOADER = 0;

    // the fragment initialization parameters
    private static final String CURRENT_URI_KEY = "current_uri_key";

    // Context
    Context context;

    /**
     * Uri data passed in with fragment initialization
     */
    private Uri mCurrentBookUri;

    /**
     * String variable keeps track of supplier phone number
     */
    private String mSupplierPhoneNumber;

    /**
     * Reference TextViews from the layout file
     */
    private TextView supplierNameTextView, supplierPhoneTextView, callSupplierTextViewButton,
            viewAllBooksTextViewButton, supplierDetailSummaryTextView;

    private OnSupplierDetailFragmentInteractionListener supplierDetailListener;

    public SupplierDetailFragment() {
        // Required empty public constructor
        this.context = getActivity();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param uriId Uri of current book/supplier
     * @return A new instance of fragment SupplierDetailFragment.
     */
    public static SupplierDetailFragment newInstance(long uriId) {
        SupplierDetailFragment fragment = new SupplierDetailFragment();
        Bundle args = new Bundle();
        args.putLong(CURRENT_URI_KEY, uriId);
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
            long uriId = getArguments().getLong(CURRENT_URI_KEY);

            // Form the content URI for the specific book that was clicked on
            mCurrentBookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, uriId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.supplier_detail, container, false);

        // Report that this fragment would like to participate in populating
        // the options menu by receiving a call to {@link #onCreateOptionsMenu}
        // and related methods.
        setHasOptionsMenu(true);

        // Find TextView fields from xml layout
        supplierDetailSummaryTextView = (TextView) rootView.findViewById(R.id.supplier_summary_text_view);
        viewAllBooksTextViewButton = (TextView) rootView.findViewById(R.id.view_all_books_from_supplier);
        callSupplierTextViewButton = (TextView) rootView.findViewById(R.id.call_now_button);
        supplierNameTextView = (TextView) rootView.findViewById(R.id.supplier_name);
        supplierPhoneTextView = (TextView) rootView.findViewById(R.id.supplier_phone);

        // Find view and Set text for helpful tip summary area
        TextView helpfulSummaryTextView = (TextView) rootView.findViewById(R.id.helpful_tip_summary_text);
        helpfulSummaryTextView.setText(R.string.supplier_helpful_tips_text);

        // Start loader Manager
        getLoaderManager().initLoader(SUPPLIER_DETAIL_LOADER, null, this);

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

        // Hide delete all books menu item
        MenuItem deleteAllBooks = menu.findItem(R.id.action_delete_all_books);
        deleteAllBooks.setVisible(false);

        // Hide sort books menu item
        MenuItem sortBooks = menu.findItem(R.id.action_sort);
        sortBooks.setVisible(false);

        // Hide Insert Demo Data menu item
        MenuItem addDemoData = menu.findItem(R.id.action_insert_demo_data);
        addDemoData.setVisible(false);

        // Hide Add New Book button
        MenuItem addNewBook = menu.findItem(R.id.action_add);
        addDemoData.setVisible(false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;

        if (context instanceof OnSupplierDetailFragmentInteractionListener) {
            supplierDetailListener = (OnSupplierDetailFragmentInteractionListener) context;
            // Call method in MainActivity that will change the toolbar to allow sorting of
            // Suppliers
        } else {
            /*throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");*/
        }
    }

    /**
     * Called when the fragment is visible to the user and actively running.
     */
    @Override
    public void onResume() {

        // Call loader
        getLoaderManager().restartLoader(SUPPLIER_DETAIL_LOADER, null, this);
        super.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // Call method in MainActivity that will change the layout views when this fragment is
        // detached
        supplierDetailListener.detachSupplierDetailFragment();
        supplierDetailListener = null;
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
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_NAME,
                BookEntry.COLUMN_BOOK_QUANTITY,
                BookEntry.COLUMN_BOOK_SALES,
                BookEntry.COLUMN_BOOK_SUPPLIER_NAME,
                BookEntry.COLUMN_BOOK_SUPPLIER_PHONE};

        // Return new CursorLoader where selection/selectionArgs are null
        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this.context,   // Parent activity context
                mCurrentBookUri,  // Provider content URI to query
                projection,              // Columns to include in the resulting Cursor
                null,           // Selection clause
                null,       // Selection arguments
                null);         // Default sort order

    }

    /**
     * Called when a previously created loader has finished its load.
     *
     * @param loader The Loader that has finished.
     * @param cursor The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor
            cursor) {

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
            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);
            int salesColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SALES);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE);

            // Use that index to extract the String or Int value of the word
            // at the current row the cursor is on.
            String retrievedBookName = cursor.getString(nameColumnIndex);
            int retrievedQuantity = cursor.getInt(quantityColumnIndex);
            int retrievedSales = cursor.getInt(salesColumnIndex);
            final String retrievedSupplierName = cursor.getString(supplierNameColumnIndex);
            String retrievedSupplierPhone = cursor.getString(supplierPhoneColumnIndex);

            // Set TextView fields to show the data from the cursor
            supplierNameTextView.setText(retrievedSupplierName);
            supplierPhoneTextView.setText(retrievedSupplierPhone);

            // Get the tel: prefix from resources, then append the phone number
            mSupplierPhoneNumber = getString(R.string.detail_phone_number_prefix);
            mSupplierPhoneNumber = mSupplierPhoneNumber + retrievedSupplierPhone;

            // Create listener for how to respond to a user attempt to call supplier
            View.OnClickListener callNumber = new View.OnClickListener() {

                /**
                 * Called when a Supplier Phone number related has been clicked.
                 * @param v The view that was clicked.
                 */
                @Override
                public void onClick(View v) {
                    // Call method callSupplier, which is handled by the MainActivity
                    // Pass the phone number as String parameter
                    supplierDetailListener.callSupplier(mSupplierPhoneNumber);
                }
            };

            // Add On click listener to Call Now buttons
            supplierPhoneTextView.setOnClickListener(callNumber);
            callSupplierTextViewButton.setOnClickListener(callNumber);

            // Set the textview containing the supplier's latest activity. The string that will
            // be used is based on the sales of the book.
            // If sales are 10 or more, TextView will be set with a neutral/good description; If
            // sales are 9 or less, the description won't be as positive
            // For each case, Pass in as a parameter the book name, sales, quantity.
            // The string resource contains html tags which must also be parsed

            if (retrievedSales >= 10) {
                // Check quantity before selecting good string (based on multiple, zero, or 1 quantity left
                switch (retrievedQuantity) {
                    case 0:
                        supplierDetailSummaryTextView.setText
                                (Html.fromHtml(getString(R.string.supplier_latest_activity_summary_good_0,
                                        retrievedBookName, retrievedSupplierName, retrievedSales,
                                        retrievedBookName)));
                        break;
                    case 1:
                        supplierDetailSummaryTextView.setText
                                (Html.fromHtml(getString(R.string.supplier_latest_activity_summary_good_1,
                                        retrievedBookName, retrievedSupplierName, retrievedSales,
                                        retrievedBookName)));
                        break;
                    default:
                        supplierDetailSummaryTextView.setText
                                (Html.fromHtml(getString(R.string.supplier_latest_activity_summary_good,
                                        retrievedBookName, retrievedSupplierName, retrievedSales,
                                        retrievedBookName, retrievedQuantity)));
                        break;
                }
            } else {
                // Sales are low (9 or less). Check specific amount of sales to know which string to
                // use
                switch (retrievedSales) {
                    case 0:
                        supplierDetailSummaryTextView.setText
                                (Html.fromHtml(getString(R.string.supplier_latest_activity_summary_bad_0,
                                        retrievedBookName, retrievedSupplierName)));
                        break;
                    case 1:
                        supplierDetailSummaryTextView.setText
                                (Html.fromHtml(getString(R.string.supplier_latest_activity_summary_bad_1,
                                        retrievedBookName, retrievedSupplierName)));
                        break;
                    default:
                        supplierDetailSummaryTextView.setText
                                (Html.fromHtml(getString(R.string.supplier_latest_activity_summary_bad_multiple,
                                        retrievedBookName, retrievedSupplierName, retrievedSales)));
                        break;
                }
            }

            // Add listener to the "View All Books From This Supplier" TextView/Button
            viewAllBooksTextViewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Call helper method implemented in the MainActivity which will query the
                    // database for all books from the supplier
                    supplierDetailListener.viewAllBooksFromSupplier(retrievedSupplierName);
                }
            });
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
        mCurrentBookUri = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity. Tips and guidance obtained from Android Training lesson
     * http://developer.android.com/training/basics/fragments/communicating.html
     * "Communicating with Other Fragments"
     */
    public interface OnSupplierDetailFragmentInteractionListener {

        /**
         * Method that handles user attempt to call supplier
         */
        void callSupplier(String phoneNumberLink);

        /**
         * Method that handles click on "View All Books From This Supplier" TextView/button
         */
        void viewAllBooksFromSupplier(String supplierName);

        /**
         * Method that handles when the SupplierDetailFragment is detached
         */
        void detachSupplierDetailFragment();

    }
}
