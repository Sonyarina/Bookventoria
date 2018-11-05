package com.example.android.bookventoria;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;

import com.example.android.bookventoria.data.BookContract.BookEntry;


/**
 * A fragment representing a list of GenresList items
 * Activities containing this fragment MUST implement the {@link OnGenreFragmentInteractionListener}
 * interface.
 */
public class GenresListFragment extends Fragment {

    /* Tag for the log messages */
    public static final String LOG_TAG = GenresListFragment.class.getSimpleName();

    // Selection value needed for queries: Selection specifies the rows that will be shown in the
    // ListView. In this case, the only rows that will be needed are the ones of the same genre
    // that was clicked on
    private static final String SELECTION = BookEntry.COLUMN_BOOK_GENRE + "=?";

    // Context
    Context context;

    // References column count for gridview
    private int mColumnCount = 2;

    // Fragment Interface listener
    private OnGenreFragmentInteractionListener genreListener;
    //Declare ArrayList containing GenresList objects
    private ArrayList<GenresList> genresListArrayList = new ArrayList<>();
    ;
    // The number representing the genre that was clicked on
    private int selectedGenreCode;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public GenresListFragment() {
    }

    @SuppressWarnings("unused")
    public static GenresListFragment newInstance(int columnCount) {
        GenresListFragment fragment = new GenresListFragment();
        Bundle args = new Bundle();
        args.putInt(QueryUtils.ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(QueryUtils.ARG_COLUMN_COUNT);
        }

        // Populate array with GenresList items
        genresListArrayList.add(QueryUtils.unknownGenre);
        genresListArrayList.add(QueryUtils.nonfictionGenre);
        genresListArrayList.add(QueryUtils.fictionGenre);
        genresListArrayList.add(QueryUtils.mysteryGenre);
        genresListArrayList.add(QueryUtils.childrensGenre);
        genresListArrayList.add(QueryUtils.historyGenre);
        genresListArrayList.add(QueryUtils.financeGenre);
        genresListArrayList.add(QueryUtils.scifiGenre);
        genresListArrayList.add(QueryUtils.romanceGenre);
        genresListArrayList.add(QueryUtils.homeFoodGenre);
        genresListArrayList.add(QueryUtils.teenGenre);
        genresListArrayList.add(QueryUtils.otherGenre);
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here
     * @return Return the View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_genreslist, container, false);

        // Report that this fragment would like to participate in populating
        // the options menu by receiving a call to {@link #onCreateOptionsMenu}
        // and related methods.
        setHasOptionsMenu(true);

        // Find the GridView from the layout file
        GridView genreGridView = view.findViewById(R.id.grid_list_view);

        // Set number of columns in gridview, either using default number of 2, or whatever was
        // passed in the Bundle during fragment initialization
        genreGridView.setNumColumns(mColumnCount);

        // Create the adapter
        GenresListAdapter genresListAdapter = new GenresListAdapter(view.getContext(), R.layout
                .gridview_list_item, genresListArrayList);

        // Make the GridView use the GenresListAdapter by calling the setAdapter method on the
        // {@link GridView} object
        genreGridView.setAdapter(genresListAdapter);

        // Set a click listener on each GenreList item to show the books from the genre when clicked
        genreGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Retrieve information on the Genre that was clicked
                GenresList genresList = genresListArrayList.get(position);

                // Get corresponding database code for the category, which will be used to create
                // the SELECTION/SELECTIONARGS for the query
                selectedGenreCode = genresList.getGenreCode();

                // Call show matching books method, which will be implemented by MainActivity
                genreListener.showBooksFromGenre(selectedGenreCode);
            }
        });
        return view;
    }

    /**
     * Called when the fragment is attached.
     *
     * @param context The context of the activity
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Set the member context variable equal to the passed in context
        this.context = context;

        // Check whether the activity implemented the interface methods
        if (context instanceof OnGenreFragmentInteractionListener) {
            genreListener = (OnGenreFragmentInteractionListener) context;
        }
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
    }

    /**
     * Called when the fragment is no longer attached to its activity.  This
     * is called after {@link #onDestroy()}.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        genreListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnGenreFragmentInteractionListener {

        /**
         * Method that shows a list of books matching the user specified category
         *
         * @param genreCode the genre that was clicked by the user
         */
        void showBooksFromGenre(int genreCode);
    }
}
