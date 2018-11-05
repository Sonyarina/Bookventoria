/*
 * Created by Sonia M on 9/30/18 6:05 PM for educational purposes.
 *  Tips, Guidance, and in some cases code snippets are obtained from Udacity Lessons
 *  relevant to this project. Any additional guidance for specific methods is outlined in
 *  the javadocs for those specific methods.
 */

package com.example.android.bookventoria;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookventoria.data.BookContract.BookEntry;

import java.text.NumberFormat;

/**
 * {@link BookCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of book data as its data source.
 */
public class BookCursorAdapter extends CursorAdapter {

    /* Tag for the log messages */
    public static final String LOG_TAG = BookCursorAdapter.class.getSimpleName();

    /**
     * Constructs a new {@link BookCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public BookCursorAdapter(Context context, Cursor c) {
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
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
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
        TextView bookNameTextView = (TextView) view.findViewById(R.id.name);
        TextView authorTextView = (TextView) view.findViewById(R.id.author);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);
        TextView genreTextView = (TextView) view.findViewById(R.id.genre);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        TextView salesTextView = (TextView) view.findViewById(R.id.sales_amt);
        TextView bookIconTextView = (TextView) view.findViewById(R.id.book_letter);

        // Extract column indices for requested columns
        int idColumnIndex = cursor.getColumnIndex(BookEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME);
        int authorColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_AUTHOR);
        int genreColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_GENRE);
        int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);
        int salesColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SALES);
        int supplierNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_NAME);
        int supplierPhoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER_PHONE);

        Log.v(LOG_TAG, "NAME INDEX: " + nameColumnIndex
                + "\nAuthor Index: " + authorColumnIndex
                + "\nGenre Index: " + genreColumnIndex
                + "\nPrice Index: " + priceColumnIndex
                + "\nQuantity Index: " + quantityColumnIndex
                + "\nSales Index: " + salesColumnIndex
                + "\nSupplier Name Index: " + supplierNameColumnIndex
                + "\nSupplier Phone Index: " + supplierPhoneColumnIndex);
        // Use that index to extract the String or Int value of the word
        // at the current row the cursor is on.
        // Extract properties from cursor if the column index is not equal to -1
        // Use that index to extract the String or Int value of the word
        // at the current row the cursor is on.

        final long retrievedBookID = cursor.getLong(idColumnIndex);
        String retrievedBookName = cursor.getString(nameColumnIndex);
        String retrievedAuthor = cursor.getString(authorColumnIndex);
        int retrievedGenre = cursor.getInt(genreColumnIndex);
        int retrievedPrice = cursor.getInt(priceColumnIndex);
        final int retrievedQuantity = cursor.getInt(quantityColumnIndex);
        final int retrievedSales = cursor.getInt(salesColumnIndex);
        String retrievedSupplierName = cursor.getString(supplierNameColumnIndex);
        String retrievedSupplierPhone = cursor.getString(supplierPhoneColumnIndex);

        // Set up the textviews with the retrieved text
        bookNameTextView.setText(retrievedBookName);
        authorTextView.setText(retrievedAuthor);
        salesTextView.setText(Integer.toString(retrievedSales));

        // Set up book icon with first letter of book name
        Character bookIconLetter = retrievedBookName.charAt(0);
        bookIconTextView.setText(bookIconLetter.toString());

        // Convert price to number format
        // First divide by 100 to get in dollars
        retrievedPrice = retrievedPrice / 100;

        // Now set price textview, formatted as currency
        priceTextView.setText(NumberFormat.getCurrencyInstance().format(retrievedPrice));

        // The quantity must be converted to string before setting TextView
        quantityTextView.setText(Integer.toString(retrievedQuantity));

        // Find add sale button
        ImageView addSaleButton = (ImageView) view.findViewById(R.id.add_sale_button);

        // Add click listener to the sale button. When the user clicks this button, if the
        // quantity is greater than 0, then decrease quantity by 1, and increase sales by 1
        addSaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(LOG_TAG, "SaleButton was pressed!");

                // Make sure the quantity is greater than 0
                if (retrievedQuantity > 0) {
                    // Create Uri to update the book's values for quantity and sales
                    Uri bookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, retrievedBookID);
                    ContentValues values = new ContentValues();
                    values.put(BookEntry.COLUMN_BOOK_QUANTITY, retrievedQuantity - 1);
                    values.put(BookEntry.COLUMN_BOOK_SALES, retrievedSales + 1);
                    context.getContentResolver().update(bookUri, values, null, null);

                    // Call swapCursor
                    swapCursor(cursor);

                    // display toast
                    Toast.makeText(context, R.string.list_item_sale_success, Toast.LENGTH_SHORT).show();
                } else {
                    // If retrieved quantity was 0, show toast message explaining why the sale
                    // cannot be added
                    Toast.makeText(context, R.string.list_item_quantity_zero, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set up the genre textview to show the correct genre
        //  genre icon, and corresponding color
        switch (retrievedGenre) {
            case BookEntry.GENRE_NONFICTION:
                genreTextView.setText(BookEntry.FORMATTED_GENRE_NONFICTION);
                bookIconTextView.setBackgroundColor(context.getResources().getColor(R.color
                        .genre_nonfiction));
                break;

            case BookEntry.GENRE_FICTION:
                genreTextView.setText(BookEntry.FORMATTED_GENRE_FICTION);
                bookIconTextView.setBackgroundColor(context.getResources().getColor(R.color
                        .genre_fiction));
                break;

            case BookEntry.GENRE_MYSTERY_SUSPENSE:
                genreTextView.setText(BookEntry.FORMATTED_GENRE_MYSTERY_SUSPENSE);
                bookIconTextView.setBackgroundColor(context.getResources().getColor(R.color
                        .genre_mystery));
                break;

            case BookEntry.GENRE_CHILDRENS:
                genreTextView.setText(BookEntry.FORMATTED_GENRE_CHILDRENS);
                bookIconTextView.setBackgroundColor(context.getResources().getColor(R.color
                        .genre_children));
                break;

            case BookEntry.GENRE_HISTORY:
                genreTextView.setText(BookEntry.FORMATTED_GENRE_HISTORY);
                bookIconTextView.setBackgroundColor(context.getResources().getColor(R.color
                        .genre_history));
                break;

            case BookEntry.GENRE_FINANCE_BUSINESS:
                genreTextView.setText(BookEntry.FORMATTED_GENRE_FINANCE_BUSINESS);
                bookIconTextView.setBackgroundColor(context.getResources().getColor(R.color
                        .genre_finance));
                break;

            case BookEntry.GENRE_SCIENCE_FICTION_FANTASY:
                genreTextView.setText(BookEntry.FORMATTED_GENRE_SCIENCE_FICTION_FANTASY);
                bookIconTextView.setBackgroundColor(context.getResources().getColor(R.color
                        .genre_scifi));
                break;

            case BookEntry.GENRE_ROMANCE:
                genreTextView.setText(BookEntry.FORMATTED_GENRE_ROMANCE);
                bookIconTextView.setBackgroundColor(context.getResources().getColor(R.color
                        .genre_romance));
                break;

            case BookEntry.GENRE_HOME_FOOD:
                genreTextView.setText(BookEntry.FORMATTED_GENRE_HOME_FOOD);
                bookIconTextView.setBackgroundColor(context.getResources().getColor(R.color
                        .genre_home_food));
                break;

            case BookEntry.GENRE_TEENS_YOUNG_ADULT:
                genreTextView.setText(BookEntry.FORMATTED_GENRE_TEENS_YOUNG_ADULT);
                bookIconTextView.setBackgroundColor(context.getResources().getColor(R.color
                        .genre_teens));
                break;

            case BookEntry.GENRE_OTHER:
                genreTextView.setText(BookEntry.FORMATTED_GENRE_OTHER);
                bookIconTextView.setBackgroundColor(context.getResources().getColor(R.color
                        .genre_other));
                break;

            default:
                genreTextView.setText(BookEntry.FORMATTED_GENRE_UNKNOWN);
                bookIconTextView.setBackgroundColor(context.getResources().getColor(R.color
                        .genre_unknown));
                break;
        }
    }
}
