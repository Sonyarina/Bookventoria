/*
 * Created by Sonia M on 9/30/18 6:05 PM for educational purposes.
 *  Tips, Guidance, and in some cases code snippets are obtained from Udacity Lessons
 *  relevant to this project. Any additional guidance for specific methods is outlined in
 *  the javadocs for those specific methods.
 */

package com.example.android.bookventoria;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.bookventoria.data.BookContract.BookEntry;

/**
 * {@link SupplierCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of book data as its data source.
 */
public class SupplierCursorAdapter extends CursorAdapter {

    /* Tag for the log messages */
    public static final String LOG_TAG = SupplierCursorAdapter.class.getSimpleName();

    /**
     * Constructs a new {@link SupplierCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public SupplierCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new view to hold the data pointed to by cursor.
     *
     * @param context Interface to application's global information
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.supplier_list_item, parent, false);
    }

    /**
     * Bind an existing view to the data pointed to by cursor. This method binds the book data (in
     * the current row pointed to by cursor) to the given list item layout.
     *
     * @param view    Existing view, returned earlier by newView
     * @param context Interface to application's global information
     * @param cursor  The cursor from which to get the data.
     */
    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        // Find fields to populate in inflated template
        TextView supplierNameTextView = (TextView) view.findViewById(R.id.supplier_name);
        TextView supplierPhoneTextView = (TextView) view.findViewById(R.id.supplier_phone);

        // Extract column indices for requested columns
        int idColumnIndex = cursor.getColumnIndex(BookEntry._ID);
        int supplierNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_NAME);
        int supplierPhoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE);

        // Use that index to extract the String or Int value of the word
        // at the current row the cursor is on.
        // Extract properties from cursor if the column index is not equal to -1
        // Use that index to extract the String or Int value of the word
        // at the current row the cursor is on.

        String retrievedSupplierName = cursor.getString(supplierNameColumnIndex);
        String retrievedSupplierPhone = cursor.getString(supplierPhoneColumnIndex);

        // Set up the textviews with the retrieved text
        supplierNameTextView.setText(retrievedSupplierName);
        supplierPhoneTextView.setText(retrievedSupplierPhone);
    }
}
