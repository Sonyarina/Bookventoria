package com.example.android.bookventoria;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

/**
 * Class for Settings menu which will allow user to Sort books by ID, Name, Author, Sales,
 * Quantity, or Supplier Name in either ascending or descending order. Tips and guidance received
 * from Udacity's Earthquake app lesson
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

    /**
     * PreferenceFragment inside SettingsActivity which is used to present widgets for editing user
     * preferences.
     */
    public static class BooksPreferenceFragment extends PreferenceFragment implements Preference
            .OnPreferenceChangeListener {

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Find reference to settings resource file
            addPreferencesFromResource(R.xml.settings_main);

            // Create and find reference to the orderBy preference
            Preference orderBy = findPreference(getString(R.string.settings_order_by_key));
            bindPreferenceSummaryToValue(orderBy);

            // Create and find reference to the sortDirection preference
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
            // Create and find reference to default SharedPreferences
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference
                    .getContext());
            // Add change listener
            preference.setOnPreferenceChangeListener(this);

            // Create String that will store the current preference value
            String preferenceString = preferences.getString(preference.getKey(), "");

            // Pass the preference and preferenceString values to the OnPreferenceChange method
            onPreferenceChange(preference, preferenceString);

            // Get current time and date, then call method to update the logs, passing along the time
            // and event description
            String dateTime = QueryUtils.dateTimeString(getActivity());

            // Form a description of what was changed
            String preferenceLog = "Preference: " + preference.getKey() +
                    " was changed to " + preferenceString;

            // Update User Activity Logs using the date/time and preferenceLog
            QueryUtils.updateLogs(getActivity(),dateTime,preferenceLog);
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
            // Set the passed in value to a String
            String stringValue = newValue.toString();

            // If the current preference is a ListPreference, iterate through the arrays
            // containing the labels/values. Tips/code from Udacity earthquake app lesson.
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                }
            } else {
                // If not, just set the summary to show the new value
                preference.setSummary(stringValue);
            }
            // Return true
            return true;
        }
    }
}
