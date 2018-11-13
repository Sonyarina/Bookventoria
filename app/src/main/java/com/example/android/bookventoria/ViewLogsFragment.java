package com.example.android.bookventoria;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass that will display user activity logs to the user.
 */
public class ViewLogsFragment extends Fragment {

    /* Tag for the log messages */
    public static final String LOG_TAG = ViewLogsFragment.class.getSimpleName();

    // Context, which is passed in from MainActivity
    Context context;

    // TextView that will hold the User Log text
    TextView userLogsTextView;

    // Create reference to SharedPreferences
    private SharedPreferences sharedPreferences;

    // Listener for interface that allows this fragment to communicate with the MainActivity
    private OnViewLogsFragmentInteractionListener onViewLogsFragmentInteractionListener;

    /**
     * Constructor for ViewLogsFragment
     */
    public ViewLogsFragment() {
        // Required empty public constructor
        this.context = getActivity();
    }

    /**
     * Factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ViewLogsFragment.
     */
    public static ViewLogsFragment newInstance() {
        return new ViewLogsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Report that this fragment would like to participate in populating
        // the options menu by receiving a call to {@link #onCreateOptionsMenu}
        // and related methods.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_view_logs, container, false);

        // Create reference to SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // Find reference to the TextView which will display logs text
        userLogsTextView = rootView.findViewById(R.id.logs_heading);

        // Call helper method to populate the TextView
        populateTextView();

        // Return view
        return rootView;
    }

    /**
     * Called when the fragment is visible to the user and actively running.
     */
    @Override
    public void onResume() {

        // Update TextView with latest information
        populateTextView();
        super.onResume();
    }

    /**
     * Method will populate the 1 textview from the layout with the user log history
     */
    private void populateTextView() {
        // Create String to hold log text.
        String logString = getString(R.string.logs_no_data);

        // Retrieve log, if present, from SharedPreferences
        // Remove key that contains history, if there is one
        if (sharedPreferences.contains(QueryUtils.LOG_EVENT_HISTORY_KEY)) {
            logString = sharedPreferences.getString(QueryUtils.LOG_EVENT_HISTORY_KEY,
                    getString(R.string.logs_no_data));
        }

        // Set the text of the TextView that will display the logString
        userLogsTextView.setText(logString);
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
        addNewBook.setVisible(false);

        // Hide View Logs button
        MenuItem viewLogs = menu.findItem(R.id.action_view_logs);
        viewLogs.setVisible(false);
    }

    /**
     * Called when a fragment is first attached to its context.
     * {@link #onCreate(Bundle)} will be called after this.
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Verify that activity has implemented the interface and set the listener using the
        // context that was passed in
        if (context instanceof OnViewLogsFragmentInteractionListener) {
            onViewLogsFragmentInteractionListener = (OnViewLogsFragmentInteractionListener) context;
        }
    }

    /**
     * Called when the fragment is no longer attached to its activity.  This
     * is called after {@link #onDestroy()}.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        // Call method in MainActivity that will change the layout views when this fragment is
        // detached
        onViewLogsFragmentInteractionListener.detachViewLogsFragment();
        onViewLogsFragmentInteractionListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity. Tips and guidance obtained from Android Training lesson
     * http://developer.android.com/training/basics/fragments/communicating.html
     * "Communicating with Other Fragments"
     */
    public interface OnViewLogsFragmentInteractionListener {

        /**
         * Method that handles when the ViewLogsFragment is detached
         */
        void detachViewLogsFragment();
    }

}
