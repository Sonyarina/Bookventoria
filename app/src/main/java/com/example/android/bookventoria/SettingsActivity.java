package com.example.android.bookventoria;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Class for Settings menu which will allow user to Sort books by Name or Author, in either
 * ascending or descending order. Tips and guidance received from Udacity's Earthquake app lesson
 */
public class SettingsActivity extends AppCompatActivity {

    /* Tag for the log messages */
    public static final String LOG_TAG = SettingsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        // Find Toolbar
        Toolbar settingsToolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(settingsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    /**
     * This hook is called whenever an item in the options menu is selected.
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to
     * proceed, true to consume it here.
     * @see #onCreateOptionsMenu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Use switch statement to handle the option selected
        switch (item.getItemId()) {

            case android.R.id.home:
                // Respond to a click on the "Up" arrow button in the app bar
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class BooksPreferenceFragment extends PreferenceFragment implements Preference
            .OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.settings_main);

            Preference orderBy = findPreference(getString(R.string.settings_order_by_key));
            bindPreferenceSummaryToValue(orderBy);

            Preference sortDirection = findPreference(getString(R.string.settings_sort_direction_key));
            bindPreferenceSummaryToValue(sortDirection);
        }

        /**
         * This method sets this fragment as the OnPreferenceChangeListener and updates the summary
         * so that it displays the current value stored in SharedPreferences
         *
         * @param preference reference to the preference
         */
        private void bindPreferenceSummaryToValue(Preference preference) {

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference
                    .getContext());
            preference.setOnPreferenceChangeListener(this);

            String preferenceString;

            Log.v(LOG_TAG, "current preference is " + preference.getKey() +
                    " and current value is " + preferences.getString(preference.getKey(), ""));

            preferenceString = preferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, preferenceString);
        }

        /**
         * Called when a Preference has been changed by the user. This is
         * called before the state of the Preference is about to be updated and
         * before the state is persisted.
         *
         * @param preference The changed Preference.
         * @param newValue   The new value of the Preference.
         * @return True to update the state of the Preference with the new value.
         */
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            // The code in this method takes care of updating the displayed
            // preference summary after it has been changed

            String stringValue = newValue.toString();

            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                }
            } else {
                preference.setSummary(stringValue);

            }
            return true;
        }

        /**
         * Called when a Preference has been clicked.
         *
         * @param preference The Preference that was clicked.
         * @return True if the click was handled.
         */
        @Override
        public boolean onPreferenceClick(Preference preference) {
            return false;
        }
    }
}
