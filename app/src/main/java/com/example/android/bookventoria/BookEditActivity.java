/*
 * Created by Sonia M on 9/27/18 1:35 PM for educational purposes.
 *  Tips, Guidance, and in some cases code snippets are obtained from Udacity Lessons
 *  relevant to this project (Pets app). Overriden methods use javadoc from Super Class.
 *  Any additional guidance for specific methods is outlined in
 *  the javadocs for those specific methods.
 */

package com.example.android.bookventoria;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import java.text.NumberFormat;

import com.example.android.bookventoria.data.BookContract.BookEntry;

/**
 * This class allows user to add a new book to the inventory or edit or delete an existing one.
 * Also implements LoaderManager/LoaderCallBacks<>
 */
public class BookEditActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /* Log Tag used for debugging purposes */
    private static final String LOG_TAG = BookEditActivity.class.getSimpleName();

    /* View Tags for identifying views in onClickListener methods */
    private static final String PRICE_TEXT_VIEW_TAG = "price_text_view_tag";
    private static final String SUPPLIER_PHONE_TEXT_VIEW_TAG = "supplier_phone_text_view_tag";
    private static final String BOOK_NAME_TEXT_VIEW_TAG = "book_name_text_view_tag";

    /* Identifier for the book inventory data loader  */
    private static final int EXISTING_BOOK_LOADER = 0;

    // TextInputLayout for Book Name
    private TextInputLayout bookNameTextInput;
    // Save Now button, which can be used in addition to the Done checkbox on the toolbar
    private Button saveButtonTextView;
    /* EditText field to enter the book's name   */
    private EditText bookNameEditText;
    /* EditText field to enter the book's author  */
    private EditText authorEditText;
    /* EditText field to enter the book's price. This field has an input limit of 8 characters */
    private EditText priceEditText;
    /* EditText field to enter the book's quantity */
    private EditText quantityEditText;
    /* EditText field to enter the book's sales */
    private EditText salesEditText;
    /* EditText field to enter the book's supplier name  */
    private EditText supplierNameEditText;
    /* EditText field to enter the book's supplier phone number. This field has an input limit of
     14 characters */
    private EditText supplierPhoneEditText;
    /* EditText field to enter the book's genre */
    private Spinner mGenreSpinner;

    /**
     * Genre / category of the book.
     * 0 for unknown genre
     */
    private int mGenre = BookEntry.GENRE_UNKNOWN;
    /* Uri data passed in with intent for viewing/ editing books */
    private Uri mCurrentBookUri;
    /* Boolean flag that keeps track of whether the book has been edited (true) or not (false) */
    private boolean mBookHasChanged = false;
    /* Keep track of retrieved values for Book Name, Author, Supplier Name, Supplier Phone
    Number, Genre, Price, Quantity, and Sales */
    private String retrievedBookName = "";
    private String retrievedAuthor = "";
    private String retrievedSupplierName = "";
    private String retrievedSupplierPhone = "";
    private int retrievedGenre = BookEntry.GENRE_UNKNOWN;
    private int retrievedPrice = 0;
    private int retrievedQuantity = 0;
    private int retrievedSales = 0;

    /* Boolean that keeps track of whether the FAB is visible */
    private boolean isFabVisible = true;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mBookHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Set up toolbar (action bar)
        Toolbar editToolbar = (Toolbar) findViewById(R.id.edit_toolbar);
        setSupportActionBar(editToolbar);

        // Configure the Up button on toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Find Save Now button, which can be used in addition to the Done checkbox on the toolbar
        saveButtonTextView = findViewById(R.id.save_button_edit);

        // Find TextInputLayout field for Book Name
        bookNameTextInput = findViewById(R.id.text_input_book_name);

        // Retrieve the intent that was used to launch this activity. It contains the uri
        // which indicates if we're adding a new book to the database or editing an existing
        // one.
        mCurrentBookUri = getIntent().getData();

        // Use conditional statement to determine the next steps
        if (mCurrentBookUri == null) {
            // Uri is null, set title to display "Add a Book"
            setTitle(R.string.editor_activity_title_new_book);

            // Set Bottom button to say "Save"
            saveButtonTextView.setText(getString(R.string.save_button_text));

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a book that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            // Uri is not null, set title to display "Edit Book"
            setTitle(R.string.editor_activity_title_edit_book);

            // Set Bottom button to say "Update"
            saveButtonTextView.setText(getString(R.string.editor_section_update_button_text));

            // Show hint on Book Name text view. This step is included to address a styling issue
            // regarding the look of the TextInput field when it's automatically selected
            // (because it's the first field)
            bookNameTextInput.setHintEnabled(true);

            // Initialize the loader to retrieve book data
            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        bookNameEditText = (EditText) findViewById(R.id.edit_book_name);
        bookNameEditText.setTag(BOOK_NAME_TEXT_VIEW_TAG);
        authorEditText = (EditText) findViewById(R.id.edit_author);
        salesEditText = (EditText) findViewById(R.id.edit_sales);
        mGenreSpinner = (Spinner) findViewById(R.id.spinner_genre);
        priceEditText = (EditText) findViewById(R.id.edit_price);
        quantityEditText = (EditText) findViewById(R.id.edit_quantity);
        supplierNameEditText = (EditText) findViewById(R.id.edit_supplier_name);
        supplierPhoneEditText = (EditText) findViewById(R.id.edit_supplier_phone);

        // Set additional tags on EditText fields that will use a mask
        priceEditText.setTag(PRICE_TEXT_VIEW_TAG);
        supplierPhoneEditText.setTag(SUPPLIER_PHONE_TEXT_VIEW_TAG);

        // Setup FAB to open EditorActivity
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.editor_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //hideKeyboardThenSave();
                saveBook();
            }
        });

        // Find reference to NestedScrollView
        NestedScrollView nestedScrollView = findViewById(R.id.editor_nested_scroll_view);

        // add Scroll change listener so that menu options
        // can be toggled based on fab visibility. Tips and guidance for how to use this listener
        // found on StackOverflow.com:
        // https://stackoverflow.com/questions/34560770/hide-fab-in-nestedscrollview-when-scrolling
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX,
                                       int oldScrollY) {
                // Hide fab when user scrolls down
                if (scrollY > oldScrollY) {
                    fab.hide();

                    // Update boolean value
                    isFabVisible = false;
                    // Call method to Invalidate Menu options so that onPrepareOptionsMenu can be called
                    // and the "save book" menu option will be shown on the toolbar
                    invalidateOptionsMenu();

                } else {
                    // Show fab when user scrolls all the way back up
                    fab.show();
                    // Update boolean value
                    isFabVisible = true;
                    // Call method to Invalidate Menu options so that onPrepareOptionsMenu can
                    // be called and the "save book" menu option will be hidden from the toolbar
                    invalidateOptionsMenu();
                }
            }
        });

        // Call method to set up the spinner which contains the Genre selections
        setupSpinner();

        // Call method to set up all the listeners needed
        setupListeners();
    }

    /**
     * Setup the listeners for various EditText fields
     */
    private void setupListeners() {
        // Add on touch listeners to the fields and spinner
        bookNameEditText.setOnTouchListener(mTouchListener);
        authorEditText.setOnTouchListener(mTouchListener);
        salesEditText.setOnTouchListener(mTouchListener);
        priceEditText.setOnTouchListener(mTouchListener);
        quantityEditText.setOnTouchListener(mTouchListener);
        supplierNameEditText.setOnTouchListener(mTouchListener);
        supplierPhoneEditText.setOnTouchListener(mTouchListener);
        mGenreSpinner.setOnTouchListener(mTouchListener);

        // Create a new PhoneNumberFormattingTextWatcher object that will format the Supplier Phone
        // Number EditText field. Tips and Guidance found on Android Developer Site
        supplierPhoneEditText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        // Add Text Changed listener to Price EditText field, parameter is a new
        // TextWatcher object that will format the Price EditText into a monetary
        // format. Tips and Guidance received from: "Convert input value to Currency in TextWatcher"
        // at http://www.java2s.com/Code/Android/UI/ConvertinputvaluetoCurrencyinTextWatcher.htm
        priceEditText.addTextChangedListener(new TextWatcher() {

            // Create boolean to keep track of whether the price edit text field is being edited
            boolean isPriceBeingEdited = false;

            /**
             * From SuperClass: This method is called to notify you that, within <code>s</code>,
             * the <code>count</code> characters beginning at <code>start</code>
             * are about to be replaced by new text with length <code>after</code>.
             */
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No actions needed for this method
            }

            /**
             * From SuperClass: This method is called to notify you that, within <code>s</code>,
             * the <code>count</code> characters beginning at <code>start</code>
             * have just replaced old text that had length <code>before</code>.
             */
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Book has changed
                mBookHasChanged = true;
            }

            /**
             * From super method: This method is called to notify you that, somewhere within
             * <code>s</code>, the text has been changed.
             * @param s The editable that has been changed
             */
            @Override
            public void afterTextChanged(Editable s) {
                mBookHasChanged = true;
                if (!isPriceBeingEdited) {
                    // Set boolean to true
                    isPriceBeingEdited = true;

                    // Convert the editable object (s) that was passed in to string, and call the
                    // replaceAll() method on the string, then store result in stringDigits
                    String stringDigits = s.toString().replaceAll("\\D", "");

                    // Create NumberFormat object which will format the price field based on the
                    // currency of the default locale
                    NumberFormat numberFormat = NumberFormat.getCurrencyInstance();

                    // Use try / catch to format the number as a double (dividing input by
                    // 100 to convert to dollars
                    try {
                        String formattedPrice = numberFormat.format(Double.parseDouble(stringDigits) / 100);
                        s.replace(0, s.length(), formattedPrice);
                    } catch (NumberFormatException nfe) {
                        s.clear();
                    }

                    // Set boolean back to false
                    isPriceBeingEdited = false;
                }
            }
        });

        // Add Text Changed Listener to BookName field. This will be in effect when users are
        // adding new books. The purpose of the listener was to address style-related issues
        // that came about due to this field being the first field on the form.
        // The javadocs for the TextWatcher methods are omitted this time since
        // they are exactly the same as the ones used above when setting listener on the price
        // EditText field
        bookNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No actions required in this method
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // If adding a new book, show the hint if the user clicks on the Book Name
                if (mCurrentBookUri == null) {
                    bookNameTextInput.setHintEnabled(true);
                    mBookHasChanged = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Book has changed
                mBookHasChanged = true;
            }
        });

        // Add listener to Save/Update button. When the button is clicked, the
        // saveBook method is called.
        saveButtonTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBook();
            }
        });
    }

    /**
     * Setup the dropdown spinner that allows the user to select the genre of the book. This
     * method is based on the same named method in the Udacity Pets app lesson.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genreSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_genre_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genreSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mGenreSpinner.setAdapter(genreSpinnerAdapter);

        // Set Listener that will immediately update the int variable mGenre to the correct
        // corresponding GENRE constant value, as outlined in the BookContract class
        mGenreSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.genre_nonfiction))) {
                        mGenre = BookEntry.GENRE_NONFICTION; // Nonfiction
                    } else if (selection.equals(getString(R.string.genre_fiction))) {
                        mGenre = BookEntry.GENRE_FICTION; // Fiction
                    } else if (selection.equals(getString(R.string.genre_mystery_suspense))) {
                        mGenre = BookEntry.GENRE_MYSTERY_SUSPENSE; // Mystery, Suspense
                    } else if (selection.equals(getString(R.string.genre_childrens))) {
                        mGenre = BookEntry.GENRE_CHILDRENS; // Children's
                    } else if (selection.equals(getString(R.string.genre_history))) {
                        mGenre = BookEntry.GENRE_HISTORY; // History
                    } else if (selection.equals(getString(R.string.genre_finance_business))) {
                        mGenre = BookEntry.GENRE_FINANCE_BUSINESS; // Finance
                    } else if (selection.equals(getString(R.string.genre_science_fiction_fantasy))) {
                        mGenre = BookEntry.GENRE_SCIENCE_FICTION_FANTASY; // Sci-Fi, Fantasy
                    } else if (selection.equals(getString(R.string.genre_romance))) {
                        mGenre = BookEntry.GENRE_ROMANCE; // Romance
                    } else if (selection.equals(getString(R.string.genre_home_food))) {
                        mGenre = BookEntry.GENRE_HOME_FOOD; // Home, Food
                    } else if (selection.equals(getString(R.string.genre_teens_young_adult))) {
                        mGenre = BookEntry.GENRE_TEENS_YOUNG_ADULT; // Teens, Young Adult
                    } else if (selection.equals(getString(R.string.genre_other))) {
                        mGenre = BookEntry.GENRE_OTHER; // Other, Miscellaneous
                    } else {
                        mGenre = BookEntry.GENRE_UNKNOWN; // Unknown
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            // The default value will be "Unknown"
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGenre = 0; // Unknown
            }
        });
    }

    /**
     * Takes user input to create ContentValues object
     *
     * @param name          Book Name string
     * @param author        Author string
     * @param genre         Book Genre (integer)
     * @param price         Book price in cents (integer)
     * @param quantity      Book quantity (integer)
     * @param sales         Book sales (integer)
     * @param supplierName  Book's supplier name (string)
     * @param supplierPhone Book's supplier phone number (string)
     * @return ContentValues object containing the user input
     */
    private ContentValues setValues(String name, String author, int genre, int price,
                                    int quantity, int sales, String supplierName,
                                    String supplierPhone) {

        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_NAME, name);
        values.put(BookEntry.COLUMN_BOOK_AUTHOR, author);
        values.put(BookEntry.COLUMN_BOOK_GENRE, genre);
        values.put(BookEntry.COLUMN_BOOK_PRICE, price);
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, quantity);
        values.put(BookEntry.COLUMN_BOOK_SALES, sales);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_NAME, supplierName);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE, supplierPhone);
        return values;
    }

    /**
     * Initialize the contents of the Activity's standard options menu.
     *
     * @param menu The options menu.
     * @return Boolean value must return true for the menu to be displayed;
     * if you return false it will not be shown.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * Actions to take when user selects the Home/Back/or Close button from menu
     */
    public void userPressedBackOrClose() {
        // Respond to a click on the Up/Back/or Close button on the app bar

        // If all the fields are null, return to last activity / main screen
        if (allFieldsNull()) {
            // Nothing was added, return to main screen
            Log.v(LOG_TAG, "Nothing was added, returning to main screen!");

            // If uri is null, return to MainActivity (if book has not changed)
            if (mCurrentBookUri == null) {
                NavUtils.navigateUpFromSameTask(BookEditActivity.this);

                Log.v(LOG_TAG, "book has changed value is " + mBookHasChanged +
                        "Current uri is: " + mCurrentBookUri);

            } else {
                // If the uri is not null (it's in book update mode),
                // navigate back to DetailActivity
                finish();
            }

        } else {
            // There are potentially unsaved changes. Use a dialog to warn
            // the user to notify the user they have unsaved changes
            // Create a click listener to handle the user confirming that
            // changes should be discarded.
            DialogInterface.OnClickListener discardButtonClickListener =
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // User clicked Cancel/close button while editing a new book, navigate
                            // back to parent activity.
                            if (mCurrentBookUri == null) {
                                // Return to Main Activity
                                NavUtils.navigateUpFromSameTask
                                        (BookEditActivity.this);
                            } else {
                                // User canceled an edit of an existing book. Return to Detail Activity
                                finish();
                            }
                        }
                    };
            // Pass the created listener in to the showUnsavedChangesDialog method
            showUnsavedChangesDialog(discardButtonClickListener);
        }
    }

    /**
     * From superclass doc: This hook is called whenever an item in your options menu is selected.
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to
     * proceed, true to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Use switch statement to handle the option selected
        switch (item.getItemId()) {
            case R.id.action_save:
                // Respond to a click on the "Save" menu option
                // Call method to validate data and then save or update book
                saveBook();
                return true;

            case R.id.action_delete:
                // Respond to a click on the "Delete" menu option
                // Show confirmation dialogue
                showDeleteConfirmationDialog();
                return true;

            case R.id.action_close:
                // Handle a click on the close / cancel button
                // Respond the same way as if the Up arrow was pressed.
                // Actions handled by helper method:
                userPressedBackOrClose();
                return true;

            case android.R.id.home:
                // Respond to a click on the "Up" arrow button in the app bar
                userPressedBackOrClose();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
        // If this is a new book, hide the "Delete" menu item and close book menu item.
        if (mCurrentBookUri == null) {
            // Find reference to delete menu item
            MenuItem deleteMenuItem = menu.findItem(R.id.action_delete);
            deleteMenuItem.setVisible(false);

            // Find reference to close menu item
            MenuItem closeBookMenuItem = menu.findItem(R.id.action_close);
            closeBookMenuItem.setVisible(false);
        }

        // Find reference to save menu item
        MenuItem saveMenuItem = menu.findItem(R.id.action_save);

        // If Fab is visible, hide save menu item
        if (isFabVisible) {
            saveMenuItem.setVisible(false);
        } else {
            // Fab is not visible, show save menu item
            saveMenuItem.setVisible(true);
        }
        return true;
    }

    /**
     * Confirm Deletion with Confirmation Dialog. Code snippets obtained from Pets App lesson from
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
                // deleteBook()
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
     * This method is called when the back button is pressed. Code snippets obtained
     * from Pets App lesson from Udacity Android Basics course
     */
    @Override
    public void onBackPressed() {
        // If the book hasn't changed, continue with handling back button press
        if (!mBookHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes by calling method and passing in the
        // listener created above as a parameter
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor. Code snippets obtained from Pets App lesson from
     * Udacity Android Basics course
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
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
     * Perform the deletion of the book in the database.
     */
    private void deleteBook() {
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
            Toast.makeText(getApplicationContext(), getString(R.string.editor_delete_book_successful),
                    Toast.LENGTH_SHORT).show();

        } else {
            // the deletion was unsuccessful and we can display a toast.
            Toast.makeText(getApplicationContext(), getString(R.string.editor_delete_book_failed),
                    Toast.LENGTH_SHORT).show();
        }

        // Return to main activity
        NavUtils.navigateUpFromSameTask(BookEditActivity.this);
    }

    /**
     * Checks if all fields are null. If so, returns true
     *
     * @return boolean value that will be true if all fields are null
     */
    private boolean allFieldsNull() {
        if (TextUtils.isEmpty(bookNameEditText.getText().toString().trim()) &&
                TextUtils.isEmpty(authorEditText.getText().toString().trim()) &&
                TextUtils.isEmpty(priceEditText.getText().toString().trim()) &&
                TextUtils.isEmpty(quantityEditText.getText().toString().trim()) &&
                TextUtils.isEmpty(salesEditText.getText().toString().trim()) &&
                TextUtils.isEmpty(supplierNameEditText.getText().toString().trim()) &&
                TextUtils.isEmpty(supplierPhoneEditText.getText().toString().trim()) &&
                mGenre == BookEntry.GENRE_UNKNOWN) {
            // If all fields are null, return true
            return true;
        } else {
            // At least one field has been changed. Return false
            return false;
        }
    }

    /**
     * Calls method that will either add a new book or update an existing book in the books database
     */
    private void saveBook() {
        // Find reference to error text views
        TextView errorTextViewTop = findViewById(R.id.error_textview_1);
        TextView errorTextViewBottom = findViewById(R.id.error_textview_2);

        // Validate input (check for null fields) before adding to the database
        if (!validateInput()) {
            // If the input is incomplete or invalid, show toast message asking for user to
            Toast.makeText(getApplicationContext(), R.string.editor_null_fields_alert,
                    Toast.LENGTH_SHORT).show();

            // Show Error TextViews
            errorTextViewTop.setVisibility(View.VISIBLE);
            errorTextViewBottom.setVisibility(View.VISIBLE);
        } else {
            // Hide Error TextViews if all the required fields are properly filled
            errorTextViewTop.setVisibility(View.GONE);
            errorTextViewBottom.setVisibility(View.GONE);

            // If no values are null, then either update or save based on URI
            // Check the Uri, if it's null, a new book is being inserted. Call insertBook method
            if (mCurrentBookUri == null) {
                insertBook();
            } else {
                // If the Uri is not null, a book is being updated. Call the updateBook method
                updateBook();
            }
        }
    }

    /**
     * Insert a new book into the database
     */
    private void insertBook() {

        // Reaching this step means all the required fields were filled
        // Extract strings from the EditText views of required fields and assign to local variables
        String nameString = bookNameEditText.getText().toString().trim();
        int quantityInt = Integer.parseInt(quantityEditText.getText().toString().trim());
        String supplierNameString = supplierNameEditText.getText().toString().trim();
        String supplierPhoneString = supplierPhoneEditText.getText().toString().trim();

        // Strip the price field of special characters like the $, periods, or commas
        // Create local String to store the EditText String
        String stripPriceText = priceEditText.getText().toString().trim();

        // Use replace() method to remove the special characters
        stripPriceText = stripPriceText.replace("$", "");
        stripPriceText = stripPriceText.replace(",", "");
        stripPriceText = stripPriceText.replace(".", "");

        Log.v(LOG_TAG, "Price field is now: " + stripPriceText);

        // Now parse the final result so it can be stored in int (priceInt)
        int priceInt = Integer.parseInt(stripPriceText);

        Log.v(LOG_TAG, "Parsed Price field is now: " + priceInt);

        // If any of the OPTIONAL fields were left blank, replace String/integer with default value
        // The optional fields are Author, Genre, and Sales
        // Author is set to "Unknown" unless the user entered a value
        String authorString = BookEntry.AUTHOR_DEFAULT_VALUE;

        // Book sales are set to 0 unless the user entered a value
        int bookSalesInt = BookEntry.SALES_DEFAULT_VALUE;

        if (!TextUtils.isEmpty(authorEditText.getText().toString().trim())) {
            authorString = authorEditText.getText().toString().trim();
        }

        if (!TextUtils.isEmpty(salesEditText.getText().toString().trim())) {
            bookSalesInt = Integer.parseInt(salesEditText.getText().toString().trim());
        }

        // Place user entered data into ContentValues
        // Create a ContentValues object where column names are the keys,
        // and book attributes entered by user are the values.
        ContentValues values = setValues(nameString, authorString, mGenre, priceInt, quantityInt,
                bookSalesInt, supplierNameString, supplierPhoneString);

        // Define a new Uri object that receives the result of the insertion
        Uri newUri = getContentResolver().insert(
                BookEntry.CONTENT_URI,           // the books content URI
                values);

        // Show a toast message depending on whether or not the insertion was successful
        if (newUri == null) {
            // If the new content URI is null, then there was an error with insertion.
            Toast.makeText(getApplicationContext(), getString(R.string.editor_save_error),
                    Toast.LENGTH_SHORT).show();

        } else {
            // Otherwise, the insertion was successful and we can display a toast.
            Toast.makeText(getApplicationContext(), getString(R.string.editor_book_saved),
                    Toast.LENGTH_SHORT).show();
        }

        // Close editor activity, if appropriate
        NavUtils.navigateUpFromSameTask(BookEditActivity.this);
    }

    /**
     * Update an existing book in the database
     */
    private void updateBook() {

        // Extract strings from the EditText views of required fields and assign to local variables
        String nameString = bookNameEditText.getText().toString().trim();
        int quantityInt = Integer.parseInt(quantityEditText.getText().toString().trim());
        String supplierNameString = supplierNameEditText.getText().toString().trim();
        String supplierPhoneString = supplierPhoneEditText.getText().toString().trim();

        // Strip the price field of special characters like the $, periods, or commas
        String stripPriceText = priceEditText.getText().toString().trim();

        stripPriceText = stripPriceText.replace("$", "");
        stripPriceText = stripPriceText.replace(",", "");
        stripPriceText = stripPriceText.replace(".", "");

        // Now parse the final result so it can be stored in int (priceInt)
        int priceInt = Integer.parseInt(stripPriceText);

        // If any of the OPTIONAL fields were left blank, replace String/integer with default value
        // The optional fields are Author, Genre, and Sales
        // Author is set to "Unknown" unless the user entered a value
        String authorString = BookEntry.AUTHOR_DEFAULT_VALUE;

        // Book sales are set to 0 unless the user entered a value
        int bookSalesInt = BookEntry.SALES_DEFAULT_VALUE;

        if (!TextUtils.isEmpty(authorEditText.getText().toString().trim())) {
            authorString = authorEditText.getText().toString().trim();
        }

        if (!TextUtils.isEmpty(salesEditText.getText().toString().trim())) {
            bookSalesInt = Integer.parseInt(salesEditText.getText().toString().trim());
        }

        // Now check to see which, if any, fields have values that have changed
        if (nameString.equals(retrievedBookName) && authorString.equals(retrievedAuthor)
                && mGenre == retrievedGenre && priceInt == retrievedPrice
                && quantityInt == retrievedQuantity && bookSalesInt == retrievedSales
                && supplierNameString.equals(retrievedSupplierName)
                && supplierPhoneString.equals(retrievedSupplierPhone)) {
            // Nothing has changed, show toast message and
            // Return to main activity
            Toast.makeText(getApplicationContext(), getString(R.string.update_nothing_changed),
                    Toast.LENGTH_SHORT).show();

        } else {
            // Create a ContentValues object where column names are the keys,
            // and book attributes entered by user are the values.
            ContentValues values = new ContentValues();

            // Check if Book Name was changed
            if (!nameString.equals(retrievedBookName)) {
                values.put(BookEntry.COLUMN_BOOK_NAME, nameString);
                Log.v(LOG_TAG, getString(R.string.updated_book_name));
            }

            // Check if author was changed
            if (!authorString.equals(retrievedAuthor)) {
                values.put(BookEntry.COLUMN_BOOK_AUTHOR, authorString);
                Log.v(LOG_TAG, getString(R.string.updated_author));
            }

            // Check if genre was changed
            if (mGenre != retrievedGenre) {
                values.put(BookEntry.COLUMN_BOOK_GENRE, mGenre);
                Log.v(LOG_TAG, getString(R.string.updated_genre));
            }

            // Check if price was changed
            if (priceInt != retrievedPrice) {
                values.put(BookEntry.COLUMN_BOOK_PRICE, priceInt);
                Log.v(LOG_TAG, getString(R.string.updated_price));
            }

            // Check if quantity was changed
            if (quantityInt != retrievedQuantity) {
                values.put(BookEntry.COLUMN_BOOK_QUANTITY, quantityInt);
                Log.v(LOG_TAG, getString(R.string.updated_quantity));
            }

            // Check if sales was changed
            if (bookSalesInt != retrievedSales) {
                values.put(BookEntry.COLUMN_BOOK_SALES, bookSalesInt);
                Log.v(LOG_TAG, getString(R.string.updated_sales));
            }
            // Check if supplier name was changed
            if (!supplierNameString.equals(retrievedSupplierName)) {
                values.put(BookEntry.COLUMN_BOOK_SUPPLIER_NAME, supplierNameString);
                Log.v(LOG_TAG, getString(R.string.updated_supplier_name));
            }

            // Check if supplier phone was changed
            if (!supplierPhoneString.equals(retrievedSupplierPhone)) {
                values.put(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE, supplierPhoneString);
                Log.v(LOG_TAG, getString(R.string.updated_supplier_Phone));
            }

            // Defines a new int variable that receives the number of rows updated
            int rowsUpdated = getContentResolver().update(
                    mCurrentBookUri,         // Uri of book
                    values,                 // ContentValues object
                    null,         // Selection criteria, null because it's defined in BookProvider
                    null);      // Selection args, null because it's defined in BookProvider

            // If 1 or more rows were updated, then display a toast message indicating that the
            // update was successful
            if (rowsUpdated != 0) {
                // the update was successful and we can display a toast.
                Toast.makeText(getApplicationContext(), getString(R.string.update_book_successful),
                        Toast.LENGTH_SHORT).show();

            } else {
                // Unable to update book
                Toast.makeText(getApplicationContext(), getString(R.string.update_book_error),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close editor activity
        NavUtils.navigateUpFromSameTask(BookEditActivity.this);
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
     * Update inputs with book data from the Cursor      *
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
            // These values will be needed again in another method when checking whether fields
            // were updated
            retrievedBookName = cursor.getString(nameColumnIndex);
            retrievedAuthor = cursor.getString(authorColumnIndex);
            retrievedGenre = cursor.getInt(genreColumnIndex);
            retrievedPrice = cursor.getInt(priceColumnIndex);
            retrievedQuantity = cursor.getInt(quantityColumnIndex);
            retrievedSales = cursor.getInt(salesColumnIndex);
            retrievedSupplierName = cursor.getString(supplierNameColumnIndex);
            retrievedSupplierPhone = cursor.getString(supplierPhoneColumnIndex);

            // Set edit text fields to show the data from the cursor
            bookNameEditText.setText(retrievedBookName);
            authorEditText.setText(retrievedAuthor);
            priceEditText.setText(Integer.toString(retrievedPrice));
            quantityEditText.setText(Integer.toString(retrievedQuantity));
            salesEditText.setText(Integer.toString(retrievedSales));
            supplierNameEditText.setText(retrievedSupplierName);
            supplierPhoneEditText.setText(retrievedSupplierPhone);

            // Set up spinner to show the correct genre
            switch (retrievedGenre) {
                case BookEntry.GENRE_NONFICTION:
                    mGenreSpinner.setSelection(BookEntry.GENRE_NONFICTION);
                    break;

                case BookEntry.GENRE_FICTION:
                    mGenreSpinner.setSelection(BookEntry.GENRE_FICTION);
                    break;

                case BookEntry.GENRE_MYSTERY_SUSPENSE:
                    mGenreSpinner.setSelection(BookEntry.GENRE_MYSTERY_SUSPENSE);
                    break;

                case BookEntry.GENRE_CHILDRENS:
                    mGenreSpinner.setSelection(BookEntry.GENRE_CHILDRENS);
                    break;

                case BookEntry.GENRE_HISTORY:
                    mGenreSpinner.setSelection(BookEntry.GENRE_HISTORY);
                    break;

                case BookEntry.GENRE_FINANCE_BUSINESS:
                    mGenreSpinner.setSelection(BookEntry.GENRE_FINANCE_BUSINESS);
                    break;

                case BookEntry.GENRE_SCIENCE_FICTION_FANTASY:
                    mGenreSpinner.setSelection(BookEntry.GENRE_SCIENCE_FICTION_FANTASY);
                    break;

                case BookEntry.GENRE_ROMANCE:
                    mGenreSpinner.setSelection(BookEntry.GENRE_ROMANCE);
                    break;

                case BookEntry.GENRE_HOME_FOOD:
                    mGenreSpinner.setSelection(BookEntry.GENRE_HOME_FOOD);
                    break;

                case BookEntry.GENRE_TEENS_YOUNG_ADULT:
                    mGenreSpinner.setSelection(BookEntry.GENRE_TEENS_YOUNG_ADULT);
                    break;

                case BookEntry.GENRE_OTHER:
                    mGenreSpinner.setSelection(BookEntry.GENRE_OTHER);
                    break;

                default:
                    mGenreSpinner.setSelection(BookEntry.GENRE_UNKNOWN);
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

        // Clear EditText views
        // Set edit text fields to show the data from the cursor
        bookNameEditText.getText().clear();
        authorEditText.getText().clear();
        priceEditText.getText().clear();
        quantityEditText.getText().clear();
        salesEditText.getText().clear();
        supplierNameEditText.getText().clear();
        supplierPhoneEditText.getText().clear();
        mGenreSpinner.setSelection(BookEntry.GENRE_UNKNOWN);

        // Reset Member variables
        retrievedBookName = "";
        retrievedAuthor = "";
        retrievedGenre = BookEntry.GENRE_UNKNOWN;
        retrievedPrice = BookEntry.PRICE_DEFAULT_VALUE;
        retrievedQuantity = BookEntry.QUANTITY_DEFAULT_VALUE;
        retrievedSales = BookEntry.SALES_DEFAULT_VALUE;
        retrievedSupplierName = "";
        retrievedSupplierPhone = "";
    }

    /**
     * Validates user input from EditText fields before saving data. The form will not save or
     * update unless the following fields are filled: Name, Price, Quantity, Supplier Name,
     * Supplier Phone. The other fields are optional and null cases save with default values
     *
     * @return boolean true if all the data is valid
     */
    private boolean validateInput() {
        // Each validation method has to run so that ALL of the null fields will be highlighted.
        // Otherwise, only the first null field will be highlighted

        boolean allFieldsFilled = validateBookName();
        allFieldsFilled = validateBookPrice();
        allFieldsFilled = validateBookQuantity();
        allFieldsFilled = validateSupplierName();
        allFieldsFilled = validateSupplierPhone();
        return validateBookName() && validateBookPrice() && validateBookQuantity()
                && validateSupplierName() && validateSupplierPhone();
    }

    /**
     * Validates data entered into the Book Name field
     *
     * @return boolean true if the field is not null and contains valid text
     */
    private boolean validateBookName() {
        if (TextUtils.isEmpty(bookNameEditText.getText().toString().trim())) {
            // The field has a null value. Change field color to red
            bookNameEditText.setBackgroundColor(getResources().getColor(R.color.nullFieldColor));
            // Return false
            return false;
        } else {
            // The field is not null change background color of field to transparent
            bookNameEditText.setBackgroundColor(Color.TRANSPARENT);
            return true;
        }
    }

    /**
     * Validates data entered into the Book Price field
     *
     * @return boolean true if the field is not null and contains valid text
     */
    private boolean validateBookPrice() {
        if (TextUtils.isEmpty(priceEditText.getText().toString().trim())) {
            // The field has a null value. Change field color to red
            priceEditText.setBackgroundColor(getResources().getColor(R.color.nullFieldColor));
            // Return false
            return false;
        } else {
            // The field is not null change background color of field to transparent
            priceEditText.setBackgroundColor(Color.TRANSPARENT);
            return true;
        }
    }

    /**
     * Validates data entered into the Book quantity field
     *
     * @return boolean true if the field is not null and contains valid text
     */
    private boolean validateBookQuantity() {
        if (TextUtils.isEmpty(quantityEditText.getText().toString().trim())) {
            // The field has a null value. Change field color to red
            quantityEditText.setBackgroundColor(getResources().getColor(R.color.nullFieldColor));
            // Return false
            return false;
        } else {
            // The field is not null change background color of field to transparent
            quantityEditText.setBackgroundColor(Color.TRANSPARENT);
            return true;
        }
    }

    /**
     * Validates data entered into the Book supplier name field
     *
     * @return boolean true if the field is not null and contains valid text
     */
    private boolean validateSupplierName() {
        if (TextUtils.isEmpty(supplierNameEditText.getText().toString().trim())) {
            // The field has a null value. Change field color to red
            supplierNameEditText.setBackgroundColor(getResources().getColor(R.color.nullFieldColor));
            // Return false
            return false;
        } else {
            // The field is not null change background color of field to transparent
            supplierNameEditText.setBackgroundColor(Color.TRANSPARENT);
            return true;
        }
    }

    /**
     * Validates data entered into the Book supplier phone field
     *
     * @return boolean true if the field is not null and contains valid text
     */
    private boolean validateSupplierPhone() {
        if (TextUtils.isEmpty(supplierPhoneEditText.getText().toString().trim())) {
            // The field has a null value. Change field color to red
            supplierPhoneEditText.setBackgroundColor(getResources().getColor(R.color.nullFieldColor));
            // Return false
            return false;
        } else {
            // The field is not null change background color of field to transparent
            supplierPhoneEditText.setBackgroundColor(Color.TRANSPARENT);
            return true;
        }
    }
}
