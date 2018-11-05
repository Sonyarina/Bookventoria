/*
 * Created by Sonia M on 9/26/18 6:12 PM for educational purposes. The images and/or icons that
 *  were not created by me were obtained with permission from Freepik.com and/or
 *  flaticon.com.
 *  Tips, Guidance, and in some cases code snippets are obtained from Udacity Lessons
 *  relevant to this project. Any additional guidance for specific methods is outlined in
 *  the javadocs for those specific methods.
 */

package com.example.android.bookventoria.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * BookContract defines a database which keeps track of a bookstore's book inventory. A row in
 * the books inventory table has 9 columns which store a book's ID, name,
 * author, genre, supplier name, supplier phone number, quantity, price, and number of sales.
 */
public final class BookContract {

    /**
     * The name for the entire content provider.
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.bookventoria";
    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.android.bookventoria/books/ is a valid path for
     * looking at book data.
     */
    public static final String PATH_BOOKS = "books";
    /**
     * Path for suppliers
     */
    public static final String PATH_SUPPLIERS = "suppliers";

    /**
     * Private Constructor for BookContract() class
     */
    private BookContract() {
    }

    /**
     * Inner class that defines constant values for the books inventory database table.
     * Each entry in the table represents a specific book and contains information about
     * how many of each book is in stock.
     */
    public static final class BookEntry implements BaseColumns {

        /**
         * The content URI to access the book data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);


        /**
         * The content URI to access the supplier data in the provider
         */
        public static final Uri CONTENT_URI_DISTINCT = Uri.withAppendedPath(BASE_CONTENT_URI,
                PATH_SUPPLIERS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of books.
         * Makes use of the constants defined in the ContentResolver class: CURSOR_DIR_BASE_TYPE
         * (which maps to the constant "vnd.android.cursor.dir")
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a viewing a single book title.
         * Makes use of the constants defined in the ContentResolver class: CURSOR_ITEM_BASE_TYPE
         * which maps to the constant “vnd.android.cursor.item”)
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        // Database Table name: books
        public static final String TABLE_NAME = "books";

        /*
        The table columns are:
         */

        // This column contains the Book's Unique ID, Data Type: INTEGER
        public static final String _ID = BaseColumns._ID;

        // This column contains the Book's Name/Title, Data Type: TEXT
        public static final String COLUMN_BOOK_NAME = "book_name";

        // This column contains the Book's Author, Data Type: TEXT
        public static final String COLUMN_BOOK_AUTHOR = "author";

        // This column contains the Supplier's Name, Data Type: TEXT
        public static final String COLUMN_BOOK_SUPPLIER_NAME = "supplier_name";

        // This column contains the Supplier's Phone Number, Data Type: TEXT
        public static final String COLUMN_BOOK_SUPPLIER_PHONE = "supplier_phone";

        // This column contains the Book's Price, Data Type: INTEGER
        public static final String COLUMN_BOOK_PRICE = "price";

        // This column contains the Book's Quantity, Data Type: INTEGER
        public static final String COLUMN_BOOK_QUANTITY = "quantity";

        // This column contains the Book's Sales, Data Type: INTEGER
        public static final String COLUMN_BOOK_SALES = "sales";

        // This column contains the Book's Genre, Data Type: INTEGER.
        // There are 12 possible values, each of which corresponds to a number
        // ranging from 0 through 11 (see below)
        public static final String COLUMN_BOOK_GENRE = "genre";

        /* The 12 possible values for book genre / category are as follows: */
        // Genre: Unknown (default value)
        public static final int GENRE_UNKNOWN = 0;

        // Genre: Nonfiction
        public static final int GENRE_NONFICTION = 1;

        // Genre: Fiction
        public static final int GENRE_FICTION = 2;

        // Genre: Mystery, Suspense
        public static final int GENRE_MYSTERY_SUSPENSE = 3;

        // Genre: Children's
        public static final int GENRE_CHILDRENS = 4;

        // Genre: History
        public static final int GENRE_HISTORY = 5;

        // Genre: Finance, Business
        public static final int GENRE_FINANCE_BUSINESS = 6;

        // Genre: Science Fiction, Fantasy
        public static final int GENRE_SCIENCE_FICTION_FANTASY = 7;

        // Genre: Romance
        public static final int GENRE_ROMANCE = 8;

        // Genre: Home, Food, Cooking
        public static final int GENRE_HOME_FOOD = 9;

        // Genre: Teens, Young Adults
        public static final int GENRE_TEENS_YOUNG_ADULT = 10;

        // Genre: Other, Miscellaneous
        public static final int GENRE_OTHER = 12;

        // Default Quantity
        public static final int QUANTITY_DEFAULT_VALUE = 0;

        // Default SALES
        public static final int SALES_DEFAULT_VALUE = 0;

        // Default PRICE
        public static final int PRICE_DEFAULT_VALUE = 0;

        // Default AUTHOR
        public static final String AUTHOR_DEFAULT_VALUE = "Unknown";

        // Projection String Arrays
        public static final String[] DEFAULT_PROJECTION = {
                _ID,
                COLUMN_BOOK_NAME,
                COLUMN_BOOK_AUTHOR,
                COLUMN_BOOK_GENRE,
                COLUMN_BOOK_PRICE,
                COLUMN_BOOK_QUANTITY,
                COLUMN_BOOK_SALES,
                COLUMN_BOOK_SUPPLIER_NAME,
                COLUMN_BOOK_SUPPLIER_PHONE};

        // Formatted Column Names (for use in textviews)
        public static final String FORMATTED_COLUMN_ID = "ID";
        public static final String FORMATTED_COLUMN_BOOK_NAME = "Name";
        public static final String FORMATTED_COLUMN_BOOK_PRICE = "Price";
        public static final String FORMATTED_COLUMN_BOOK_QUANTITY = "Quantity";
        public static final String FORMATTED_COLUMN_BOOK_SUPPLIER_NAME = "Supplier";
        public static final String FORMATTED_COLUMN_BOOK_SUPPLIER_PHONE = "Supplier Phone";
        public static final String FORMATTED_COLUMN_BOOK_AUTHOR = "Author";
        public static final String FORMATTED_COLUMN_BOOK_GENRE = "Genre";
        public static final String FORMATTED_COLUMN_BOOK_SALES = "Sales";

        // Genres formatted for Text views
        public static final String FORMATTED_GENRE_UNKNOWN = "Unknown";
        public static final String FORMATTED_GENRE_NONFICTION = "Nonfiction";
        public static final String FORMATTED_GENRE_FICTION = "Fiction";
        public static final String FORMATTED_GENRE_MYSTERY_SUSPENSE = "Mystery and Suspense";
        public static final String FORMATTED_GENRE_CHILDRENS = "Children\'s";
        public static final String FORMATTED_GENRE_HISTORY = "History";
        public static final String FORMATTED_GENRE_FINANCE_BUSINESS = "Finance, Business";
        public static final String FORMATTED_GENRE_SCIENCE_FICTION_FANTASY = "Science Fiction, Fantasy";
        public static final String FORMATTED_GENRE_ROMANCE = "Romance";
        public static final String FORMATTED_GENRE_HOME_FOOD = "Home, Food, Cooking";
        public static final String FORMATTED_GENRE_TEENS_YOUNG_ADULT = "Teens, Young Adults";
        public static final String FORMATTED_GENRE_OTHER = "Other, Miscellaneous";

        // US country code (for phone numbers)
        public static final String US_COUNTRY_CODE = "+1";

        /**
         * Returns whether or not the given genre is valid
         */
        public static boolean isValidGenre(int genre) {
            if (genre == GENRE_UNKNOWN ||
                    genre == GENRE_NONFICTION ||
                    genre == GENRE_FICTION ||
                    genre == GENRE_MYSTERY_SUSPENSE ||
                    genre == GENRE_CHILDRENS ||
                    genre == GENRE_HISTORY ||
                    genre == GENRE_FINANCE_BUSINESS ||
                    genre == GENRE_SCIENCE_FICTION_FANTASY ||
                    genre == GENRE_ROMANCE ||
                    genre == GENRE_HOME_FOOD ||
                    genre == GENRE_TEENS_YOUNG_ADULT ||
                    genre == GENRE_OTHER) {

                return true;
            }
            return false;
        }
    }
}
