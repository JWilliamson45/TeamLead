/**
 * SettingsActivity.java
 *
 * Android activity for the settings screen, which displays configurable user preferences for the application.
 *
 * @author James Williamson
 *
 * @formatter:off
 */

package com.jameswilliamson.teamlead;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

public class SettingsActivity extends AppCompatActivity
{
    /**
     * Called when the activity is created - initialization for the activity is performed here.
     *
     * @param savedInstanceState The saved instance state.
     */
    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        /* Call superclass implementation first */
        super.onCreate( savedInstanceState );

        /* Inflate layout */
        setContentView( R.layout.activity_settings );

        /* Set up toolbar */
        Toolbar m_Toolbar = (Toolbar)findViewById( R.id.settings_toolbar );
        m_Toolbar.setTitle( R.string.settings_activity_title );
        setSupportActionBar( m_Toolbar );

        /* Display the settings fragment as the main content */
        getFragmentManager().beginTransaction().replace( R.id.settings_content, new SettingsFragment() ).commit();
    }

    /**
     * Fragment that is used to display the actual settings. This is embedded on the parent activity.
     */
    public static class SettingsFragment extends PreferenceFragment
            implements SharedPreferences.OnSharedPreferenceChangeListener
    {
        /* Private member fields */
        private SharedPreferences m_SharedPreferences;

        /**
         * Called to perform initial creation of the fragment.
         *
         * @param savedInstanceState If the fragment is being recreated from a previous saved state, this is the state.
         */
        @Override
        public void onCreate( Bundle savedInstanceState )
        {
            super.onCreate( savedInstanceState );

            /* Load the user preferences */
            addPreferencesFromResource( R.xml.preferences );

            /* Initialize fields */
            m_SharedPreferences = getPreferenceManager().getSharedPreferences();
        }

        /**
         * Called when the fragment is visible to the user and actively running.
         */
        @Override
        public void onResume()
        {
            super.onResume();
            m_SharedPreferences.registerOnSharedPreferenceChangeListener( this );
        }

        /**
         * Called when the Fragment is no longer resumed.
         */
        @Override
        public void onPause()
        {
            super.onPause();
            m_SharedPreferences.unregisterOnSharedPreferenceChangeListener( this );
        }

        /**
         * Called when a shared preference is changed, added, or removed. This may be called even if a preference
         * is set to its existing value.
         *
         * @param sharedPreferences The SharedPreferences that received the change.
         * @param key The key of the preference that was changed, added, or removed.
         */
        @Override
        public void onSharedPreferenceChanged( SharedPreferences sharedPreferences, String key )
        {
            TeamLeadApplication app = (TeamLeadApplication)getActivity().getApplication();
            ListPreference preference = (ListPreference)findPreference( key );

            if( key.equals( getResources().getString( R.string.pref_tile_color_key ) ) )
            {
                /* Task tile color preference has been selected */
                // TODO: 4/4/2017 This has been deprecated in favor of the individual task coloring feature - remove
                UnsupportedFunctionDialog errDialog = new UnsupportedFunctionDialog();
                errDialog.show( getActivity().getFragmentManager(), UnsupportedFunctionDialog.TAG );
            }
            else if( key.equals( getResources().getString( R.string.pref_tile_refresh_key ) ) )
            {
                /* Task tile refresh preference has been selected */
                if( preference.getValue().equals( "refresh-100ms" ) )
                {
                    app.setTaskTileRefreshRate( TeamLeadApplication.REFRESH_RATE_100_MS );
                }
                else if( preference.getValue().equals( "refresh-500ms" ) )
                {
                    app.setTaskTileRefreshRate( TeamLeadApplication.REFRESH_RATE_500_MS );
                }
                else if( ( preference.getValue().equals( "refresh-1s" ) ) )
                {
                    app.setTaskTileRefreshRate( TeamLeadApplication.REFRESH_RATE_1000_MS );
                }
                else if( ( preference.getValue().equals( "refresh-5s" ) ) )
                {
                    app.setTaskTileRefreshRate( TeamLeadApplication.REFRESH_RATE_5000_MS );
                }
            }
        }
    }
}
