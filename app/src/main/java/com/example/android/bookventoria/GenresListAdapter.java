package com.example.android.bookventoria;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.bookventoria.data.BookContract;
import com.example.android.bookventoria.data.BookContract.BookEntry;

import java.util.ArrayList;

/**
 * {@link GenresListAdapter} is an {@link ArrayAdapter} that can provide the layout for each list
 * based on a data source, which is a list of {@link GenresList} objects
 */
public class GenresListAdapter extends ArrayAdapter<GenresList> {

    /**
     * Custom Constructor
     *
     * @param context     The current context.
     * @param resource    The resource ID for a layout file containing a TextView to use when
     *                    instantiating views.
     * @param genresLists The GenresList objects to represent in the ListView.
     */
    public GenresListAdapter(@NonNull Context context, int resource, @NonNull
            ArrayList<GenresList> genresLists) {

        // Initialize the ArrayAdapter's internal storage for the context and the list.
        super(context, resource, genresLists);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View gridItemView = convertView;

        // Check if view is null, if so, inflate view
        if (gridItemView == null) {
            gridItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.gridview_list_item, parent, false);
        }

        // Get the GenresList object located at this position in the list
        GenresList currentGenre = getItem(position);

        // Find the textview in the item layout with the genre name
        TextView genreNameTextView = gridItemView.findViewById(R.id.gridview_genre_text);

        // Find the ImageView in the item layout that will hold genre icon image
        ImageView genreIconImageView = gridItemView.findViewById(R.id.gridview_genre_image);

        // Check whether currentGenre object created above is null before preceding with the
        // following methods
        if (currentGenre != null) {
            // Get the genre name from the current GenresList object and
            // set this text on the TextView
            genreNameTextView.setText(currentGenre.getGenreName());

            // Get the image resource ID from the current GenresList object and
            // set the image to the ImageView
            genreIconImageView.setImageResource(currentGenre.getImageId());

            // Put the genre colors in the background of the TextView containing the genre name
            switch(currentGenre.getGenreCode()){
                case BookEntry.GENRE_NONFICTION:
                    genreNameTextView.setBackgroundColor(getContext().getResources().getColor(R
                            .color
                            .genre_nonfiction));
                    break;

                case BookEntry.GENRE_FICTION:
                    genreNameTextView.setBackgroundColor(getContext().getResources().getColor(R.color
                            .genre_fiction));
                    break;

                case BookEntry.GENRE_MYSTERY_SUSPENSE:
                    genreNameTextView.setBackgroundColor(getContext().getResources().getColor(R.color
                            .genre_mystery));
                    break;

                case BookEntry.GENRE_CHILDRENS:
                    genreNameTextView.setBackgroundColor(getContext().getResources().getColor(R.color
                            .genre_children));
                    break;

                case BookEntry.GENRE_HISTORY:
                    genreNameTextView.setBackgroundColor(getContext().getResources().getColor(R.color
                            .genre_history));
                    break;

                case BookEntry.GENRE_FINANCE_BUSINESS:
                    genreNameTextView.setBackgroundColor(getContext().getResources().getColor(R.color
                            .genre_finance));
                    break;

                case BookEntry.GENRE_SCIENCE_FICTION_FANTASY:
                    genreNameTextView.setBackgroundColor(getContext().getResources().getColor(R.color
                            .genre_scifi));
                    break;

                case BookEntry.GENRE_ROMANCE:
                    genreNameTextView.setBackgroundColor(getContext().getResources().getColor(R.color
                            .genre_romance));
                    break;

                case BookEntry.GENRE_HOME_FOOD:
                    genreNameTextView.setBackgroundColor(getContext().getResources().getColor(R.color
                            .genre_home_food));
                    break;

                case BookEntry.GENRE_TEENS_YOUNG_ADULT:
                    genreNameTextView.setBackgroundColor(getContext().getResources().getColor(R.color
                            .genre_teens));
                    break;

                case BookContract.BookEntry.GENRE_OTHER:
                    genreNameTextView.setBackgroundColor(getContext().getResources().getColor(R.color
                            .genre_other));
                    break;

                default:
                    genreNameTextView.setBackgroundColor(getContext().getResources().getColor(R.color
                            .genre_unknown));
                    break;
            }
        }

        // Return the item layout view containing 1 TextView and 1 ImageView so that it can be
        // shown in the ListView
        return gridItemView;
    }
}
