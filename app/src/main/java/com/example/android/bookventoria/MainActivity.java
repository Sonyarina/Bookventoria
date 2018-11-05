package com.example.android.bookventoria;

import android.app.AlertDialog;


import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.android.bookventoria.data.BookContract.BookEntry;

/**
 * Displays list of books that were entered and stored into the app. Tips, Guidance, and in some
 * cases code snippets are obtained from Udacity Lessons relevant to this project.
 * Any additional guidance for specific methods is outlined in the javadocs for those specific methods.
 */
public class MainActivity extends AppCompatActivity implements
        BookListFragment.OnFragmentInteractionListener, GenresListFragment
        .OnGenreFragmentInteractionListener, SuppliersFragment
        .OnSupplierFragmentInteractionListener, SupplierDetailFragment.OnSupplierDetailFragmentInteractionListener {

    /* Tag for the log messages */
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    // There are 4 pages accessible through bottom navigation layout
    // BooksList, Genres, Reports, Logs
    private static final int BOOKS_LIST = 0;
    private static final int GENRES = 1;
    private static final int SUPPLIERS = 2;
    private static final int REPORTS = 3;

    // Layout manager that allows the user to flip left and right through pages of data
    ViewPager viewPager;

    // Create new BooksPagerAdapter object to manage which fragment should be shown on each page
    BooksPagerAdapter adapter;

    // FrameLayout view which holds the layout used for Attraction Detail Screen
    FrameLayout supplierDetailsFrameLayoutView;

    // LinearLayout view which holds the layout used for tab fragments
    LinearLayout tabsLinearLayoutView;

    // Boolean to keep track of whether the Suppliers Fragment is running. False by default
    boolean isSuppliersFragmentRunning = false;

    /* App Support Toolbar */
    private Toolbar mainToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up toolbar (action bar)
        mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);

        // Find views
        tabsLinearLayoutView = findViewById(R.id.main_tabs_layout);
        supplierDetailsFrameLayoutView = findViewById(R.id.suppliers_detail_view_container);

        // Find the ViewPager which allows user to swipe between fragments
        // Then assign it to new ViewPager object
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        // Create new BooksPagerAdapter object to manage which fragment should be shown on each page
        adapter = new BooksPagerAdapter(this, getSupportFragmentManager());

        // Set the above created adapter onto the viewPager
        viewPager.setAdapter(adapter);

        // Set OnPageChangeListener to the viewpager. This requires the implementation of three
        // abstract methods from the superclass
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            /**
             * This method will be invoked when the current page is scrolled, either as part
             * of a programmatically initiated smooth scroll or a user initiated touch scroll.
             *
             * @param position Position index of the first page currently being displayed.
             *                 Page position+1 will be visible if positionOffset is nonzero.
             * @param positionOffset Value from [0, 1) indicating the offset from the page at position.
             * @param positionOffsetPixels Value in pixels indicating the offset from position.
             * (Javadoc copied from superclass.)
             */
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.v(LOG_TAG, "Page " + position + "was scrolled");
            }

            /**
             * This method will be invoked when a new page becomes selected. This method is required to
             * implement the ViewPager.OnPageSelected interface from
             * the BookList, Genres, Reports, and Logs fragment classes. It provides instructions
             * for how to change GUI when a new category is selected and visible
             * @param position the current page number/position
             */
            @Override
            public void onPageSelected(int position) {
                Log.v(LOG_TAG, "Page is" + position);
                switch (position) {
                    case BOOKS_LIST:
                        break;
                    case GENRES:
                        break;
                    case REPORTS:
                        break;
                    case SUPPLIERS:
                        break;
                }
            }

            /**
             * Called when the scroll state changes. Useful for
             * discovering when the user begins dragging, when the pager is automatically
             * settling to the current page, or when it is fully stopped/idle.
             * (Javadoc copied from superclass.)
             *
             * @param state The new scroll state.
             * @see ViewPager#SCROLL_STATE_IDLE
             * @see ViewPager#SCROLL_STATE_DRAGGING
             * @see ViewPager#SCROLL_STATE_SETTLING
             */
            @Override
            public void onPageScrollStateChanged(int state) {
                Log.v(LOG_TAG, "Page state is " + state);
            }
        });

        // Give the TabLayout the ViewPager
        // Find view containing TabLayout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);

        // Connect the tab layout with the view pager.
        tabLayout.setupWithViewPager(viewPager);

        // Add tab icons
        tabLayout.getTabAt(BOOKS_LIST).setIcon(R.drawable.books1);
        tabLayout.getTabAt(GENRES).setIcon(R.drawable.category);
        tabLayout.getTabAt(REPORTS).setIcon(R.drawable.report);
        tabLayout.getTabAt(SUPPLIERS).setIcon(R.drawable.network);

        // Add Color States to Tabs using a ColorStateList
        // Tips and guidance for this process was obtained at StackOverFlow:
        // https://stackoverflow.com/questions/34562117/how-do-i-change-the-color-of-icon-of
        // -the-selected-tab-of-tablayout
        ColorStateList tabColors;

        // Test necessary to make sure the code works properly on older android devices
        if (Build.VERSION.SDK_INT >= 23) {
            tabColors = getResources().getColorStateList(R.color.tab_color_states2, getTheme());
        } else {
            tabColors = getResources().getColorStateList(R.color.tab_color_states2);
        }
        // Loop through the tabLayout and set the Tint List on each icon
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            Drawable icon = tab.getIcon();

            if (icon != null) {
                icon = DrawableCompat.wrap(icon);
                DrawableCompat.setTintList(icon, tabColors);
            }
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     * Code snippets obtained from Pets App lesson from
     * Udacity Android Basics course
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

      /*
              switch(viewPager.getCurrentItem()){

        }

      // If this is a new book, hide the "Delete" menu item.
        if (mCurrentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }*/
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
            // Respond to a click on the "Insert demo data" menu option
            case R.id.action_insert_demo_data:
                // Insert sample data to demonstrate how the app works
                insertBookDemoData();
                return true;

            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_books:
                // Call method to delete all books
                showDeleteConfirmationDialog();
                return true;

            // Respond to a click on the "Sort Order Settings" menu option
            case R.id.action_sort:
                // Call settings fragment
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;

            // Respond to a click on the "Add New Book" menu option
            case R.id.action_add:
                // Send intent to open Book Edit Activity, passing null for uri
                Intent intentAdd = new Intent(MainActivity.this,
                        BookEditActivity.class);
                intentAdd.setData(null);
                startActivity(intentAdd);
                return true;

            // Respond to click on the back button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Helper method to insert hardcoded book data into the database. For demonstration / debugging purposes only.
     */
    public void insertBookDemoData() {
        // Create a ContentValues array object by calling helper method, which will retrieve the
        // data from a set of arrays
        ContentValues values[] = QueryUtils.createDemoValues(this);

        // Insert multiple new rows into the provider using the ContentResolver.
        // Use the {@link BookEntry#CONTENT_URI} to indicate that we want to insert
        // into the books database table.
        int rowsAdded = getContentResolver().bulkInsert(BookEntry.CONTENT_URI, values);

        // Show a toast message depending on whether or not the insertion was successful
        if (rowsAdded == 0) {
            // If the number of rows added is 0, then there was an error with insertion.
            Toast.makeText(this, getString(R.string.database_demo_data_failure),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.database_demo_data_rows, rowsAdded),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Calls Helper Method to Insert Demo Book Data
     */
    @Override
    public void addDemoData() {
        insertBookDemoData();
    }

    /**
     * Handles fab press to insert a new book (in BookListFragment)
     */
    @Override
    public void addBookFabPress() {
        Intent intent = new Intent(MainActivity.this, BookEditActivity.class);
        intent.setData(null);
        startActivity(intent);
    }

    /**
     * Queries the Books inventory database for books with low quantity amount. Less than 10
     * books remaining is considered to be Low Quantity
     */
    @Override
    public void lowInventoryQuery() {
        // Create new intent to go to {@link QueryActivity}
        Intent intent = new Intent(this, QueryActivity.class);

        // Put extra information which includes the query code to use
        intent.putExtra(QueryUtils.QUERY_CODE_KEY, QueryUtils.QUERY_LOW_INVENTORY);

        // Launch the {@link QueryActivity} to display the books
        startActivity(intent);
    }

    /**
     * Queries the Books inventory database for books with at least 1 sale
     */
    @Override
    public void viewAllBooksWithSalesQuery() {
        // Create new intent to go to {@link QueryActivity}
        Intent intent = new Intent(this, QueryActivity.class);

        // Put extra information which includes the query code to use
        intent.putExtra(QueryUtils.QUERY_CODE_KEY, QueryUtils.QUERY_ALL_SALES);

        // Launch the {@link QueryActivity} to display the books
        startActivity(intent);
    }

    /**
     * Queries all the books in database
     */
    @Override
    public void allBooksQuery() {
        // Create new intent to go to {@link QueryActivity}
        Intent intent = new Intent(this, QueryActivity.class);

        // Put extra information which includes the query code to use
        intent.putExtra(QueryUtils.QUERY_CODE_KEY, QueryUtils.QUERY_ALL_BOOKS);

        // Launch the {@link QueryActivity} to display the books
        startActivity(intent);
    }

    /**
     * Confirm Deletion with Confirmation Dialog.  Code snippets obtained from Pets App lesson from
     * Udacity Android Basics course
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_all_books_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the book.
                deleteAllBooks();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the book.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of all books in the database.
     */
    private void deleteAllBooks() {

        // Defines a new int variable that receives the number of rows deleted
        int rowsDeleted = getContentResolver().delete(
                BookEntry.CONTENT_URI,         // Uri of book
                null,         // Selection criteria, null because it's defined in BookProvider
                null);      // Selection args, null because it's defined in BookProvider

        // If the deletion worked, then display a toast message indicating that the
        // delete operation was successful
        if (rowsDeleted >= 2) {
            // the deletion was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.main_delete_books_successful, rowsDeleted),
                    Toast.LENGTH_SHORT).show();

        } else if (rowsDeleted == 1) {
            // the database's 1 and only book was deleted successfully, display a toast
            Toast.makeText(this, getString(R.string.main_delete_one_book_successful),
                    Toast.LENGTH_SHORT).show();
        } else {
            // the deletion was unsuccessful and we can display error a toast.
            Toast.makeText(this, getString(R.string.main_delete_books_failed),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Method that shows a list of books matching the user specified category
     *
     * @param genreCode the genre that was clicked by the user
     */
    @Override
    public void showBooksFromGenre(int genreCode) {

        // Create intent and pass the information on to the Query Activity
        Intent intent = new Intent(this, QueryActivity.class);

        // Put extra information which includes the query code to use
        intent.putExtra(QueryUtils.QUERY_CODE_KEY, QueryUtils.QUERY_GENRE);
        // Put extra information regarding the genre that was selected by the user
        intent.putExtra(QueryUtils.GENRE_CODE_KEY, genreCode);

        // Launch the {@link QueryActivity} to display the books
        startActivity(intent);
    }

    /**
     * Method that handles a click on the Suppliers ListView. Will show all books with that
     * suppplier
     */
    @Override
    public void showSupplierDetails(long uriId) {
        // Show list of books with that supplier information
        //Instantiate the DetailsFragment
        SupplierDetailFragment supplierDetailFragment = SupplierDetailFragment.newInstance(uriId);

        // Get the FragmentManager and start a transaction.
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        //Add the DetailsFragment
        fragmentTransaction.add(R.id.suppliers_detail_view_container,
                supplierDetailFragment, "SupplierDetails").addToBackStack(null).commit();

        //Hide the tabs layout and reveal the supplier details layout
        tabsLinearLayoutView.setVisibility(View.GONE);
        supplierDetailsFrameLayoutView.setVisibility(View.VISIBLE);

        // Toolbar: Show the Up button, Change title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.supplier_detail_page_title);
    }

    /**
     * Method that handles when the SupplierDetailFragment is detached
     */
    @Override
    public void detachSupplierDetailFragment() {
        // Hide the FrameLayout that contained the Supplier Detail Fragment, and show the tab
        // layout again
        supplierDetailsFrameLayoutView.setVisibility(View.GONE);
        tabsLinearLayoutView.setVisibility(View.VISIBLE);

        // Toolbar: Remove the Up button, change the title back to the app name
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle(R.string.app_name);
    }

    /**
     * Method that handles this fragment being attached. The sort option will change to
     * Supplier Sort
     */
    @Override
    public void onSuppliersAttached() {
        isSuppliersFragmentRunning = true;
    }

    /**
     * Method that handles this fragment no longer being attached. The sort option will
     * change back
     */
    @Override
    public void onSuppliersDetached() {
        isSuppliersFragmentRunning = false;
    }

    /**
     * Method that handles user attempt to call supplier
     *
     * @param phoneNumberLink
     */
    @Override
    public void callSupplier(String phoneNumberLink) {
        //Send Intent to call number
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse(phoneNumberLink));

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    /**
     * Method that handles click on "View All Books From This Supplier" TextView/button
     *
     * @param supplierName
     */
    @Override
    public void viewAllBooksFromSupplier(String supplierName) {

        // Create intent and pass the information on to the Query Activity
        Intent intent = new Intent(this, QueryActivity.class);

        // Put extra information which includes the query code to use
        intent.putExtra(QueryUtils.QUERY_CODE_KEY, QueryUtils.QUERY_SUPPLIER_BOOKS);
        // Put extra information regarding the Supplier's Name
        intent.putExtra(QueryUtils.SUPPLIER_CODE_KEY, supplierName);

        // Launch the {@link QueryActivity} to display the books
        startActivity(intent);

    }
}
