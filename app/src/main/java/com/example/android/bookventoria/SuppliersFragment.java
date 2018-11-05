package com.example.android.bookventoria;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.bookventoria.data.BookContract.BookEntry;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnSupplierFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SuppliersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SuppliersFragment extends Fragment implements LoaderCallbacks<Cursor> {

    /* Tag for the log messages */
    public static final String LOG_TAG = SuppliersFragment.class.getSimpleName();

    /* Identifier for the suppliers list data loader */
    private static final int SUPPLIER_LOADER = 0;

    // Context
    Context context;
    // The ListView which will be populated with the suppliers data
    ListView supplierListView;
    // Reference to ProgressBar View in activity layout
    ProgressBar progressBar;
    // Empty view for ListView
    View emptyView;
    /* Adapter for the ListView */
    private SupplierCursorAdapter supplierCursorAdapter;
    // Parameters that will be needed for queries: Selection, SelectionArgs, and Projection
    // Selection specifies the rows that will be shown in the ListView
    private String mSelection;
    // Selection args are the criteria that the rows must match to be displayed
    private String[] mSelectionArgs;
    // Projection that specifies the columns that will be shown in the ListView
    private String[] mProjection;
    // TextView that references header text on supplier list fragment page
    TextView supplierHeaderTextView;
    // Listener for interface that allows this fragment to communicate with the MainActivity
    private OnSupplierFragmentInteractionListener supplierListener;

    public SuppliersFragment() {
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
    public static SuppliersFragment newInstance(String[] projection, String selection, String[]
            selectionArgs) {
        SuppliersFragment fragment = new SuppliersFragment();
        Bundle args = new Bundle();
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

        mProjection = new String[]{
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_SUPPLIER_NAME,
                BookEntry.COLUMN_BOOK_SUPPLIER_PHONE};
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_suppliers, container, false);

        // Report that this fragment would like to participate in populating
        // the options menu by receiving a call to {@link #onCreateOptionsMenu}
        // and related methods.
        setHasOptionsMenu(true);

        // Find TextView that shows header for this fragment's layout
        supplierHeaderTextView = rootView.findViewById(R.id.suppliers_header_text);

        // Find the ListView which will be populated with the book data
        supplierListView = (ListView) rootView.findViewById(R.id.suppliers_list);

        // Setup an Adapter to create a list item for each row of book data in the Cursor.
        // There is no book data yet (until the loader finishes) so pass in null for the Cursor.
        supplierCursorAdapter = new SupplierCursorAdapter(getActivity(), null);
        supplierListView.setAdapter(supplierCursorAdapter);

        // Setup item click listener. When user clicks on a supplier, the supplier's detail page
        // will display
        supplierListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Call method showSupplierDetails, which will be handled by the mainactivity
                // Pass in the id of the clicked supplier/book as parameter
                supplierListener.showSupplierDetails(id);

            }
        });

        // Find empty view for ListView
        emptyView = rootView.findViewById(R.id.empty_view);

        // Reference to ProgressBar View in activity layout
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar_view);

        getLoaderManager().initLoader(SUPPLIER_LOADER, null, this);

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

        // Hide Insert Demo Data menu item
        MenuItem addDemoData = menu.findItem(R.id.action_insert_demo_data);
        addDemoData.setVisible(false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;

        if (context instanceof OnSupplierFragmentInteractionListener) {
            supplierListener = (OnSupplierFragmentInteractionListener) context;
            // Call method in MainActivity that will change the toolbar to allow sorting of
            // Suppliers
            supplierListener.onSuppliersAttached();
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
        getLoaderManager().restartLoader(SUPPLIER_LOADER, null, this);
        super.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // Call method in MainActivity that will change the toolbar back to it's normal look
        supplierListener.onSuppliersDetached();
        supplierListener = null;
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
                    BookEntry.CONTENT_URI_DISTINCT,   // Provider content URI to query
                    mProjection,              // Columns to include in the resulting Cursor
                    null,           // Selection clause
                    null,       // Selection arguments
                    sortOrder);         // Default sort order
        }

        // If selection is not null, return the loader with the selection and selectionArgs
        // passed in as parameters
        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this.context,   // Parent activity context
                BookEntry.CONTENT_URI_DISTINCT,   // Provider content URI to query
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
        supplierCursorAdapter.swapCursor(data);

        // Check for null data, and show empty view if it's null
        if (data != null && data.getCount() > 0) {
            // Change the visibility of the emptyView to Gone
            emptyView.setVisibility(View.GONE);

            // Hide progress bar
            progressBar.setVisibility(View.GONE);

            // Show header view for this fragment
            supplierHeaderTextView.setVisibility(View.VISIBLE);

        } else {
            // Hide progress bar
            progressBar.setVisibility(View.GONE);

            // Hide header view for this fragment
            supplierHeaderTextView.setVisibility(View.GONE);

            // Set empty view on the ListView, so that it only shows when the list has 0 items.
            supplierListView.setEmptyView(emptyView);
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
        supplierCursorAdapter.swapCursor(null);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * Tips and guidance obtained from Android Training lesson
     * http://developer.android.com/training/basics/fragments/communicating.html
     * "Communicating with Other Fragments"
     */
    public interface OnSupplierFragmentInteractionListener {

        /**
         * Method that handles a click on the Suppliers ListView. Will show supplier detail page
         *
         * @param uriId the ID of the book/supplier that was clicked
         */
        void showSupplierDetails(long uriId);

        /**
         * Method that handles this fragment being attached. The sort option will change to
         * Supplier Sort
         */
        void onSuppliersAttached();

        /**
         * Method that handles this fragment no longer being attached. The sort option will
         * change back
         */
        void onSuppliersDetached();

    }
}
