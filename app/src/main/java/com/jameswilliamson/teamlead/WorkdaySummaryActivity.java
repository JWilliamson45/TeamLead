/**
 * WorkdaySummaryActivity.java
 *
 * Presents a summary of the user's workday, typically in the form of a graph. What is actually displayed by this
 * activity may depend on user preferences.
 *
 * @author James Williamson
 *
 * @formatter:off
 */

package com.jameswilliamson.teamlead;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class WorkdaySummaryActivity extends AppCompatActivity
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
        setContentView( R.layout.activity_workday_summary );

        /* Set up toolbar */
        Toolbar m_Toolbar = (Toolbar)findViewById( R.id.workday_summary_toolbar );
        m_Toolbar.setTitle( R.string.workday_summary_activity_title );
        setSupportActionBar( m_Toolbar );

        /* Display the fragment as the main content */
        getFragmentManager().beginTransaction().replace( R.id.workday_summary_content,
                new WorkdaySummaryFragment() ).commit();
    }

    /**
     * Fragment that is used to display the summary of the workday to the user.
     */
    public static class WorkdaySummaryFragment extends Fragment
    {
        /**
         * Called to do initial creation of the fragment.
         *
         * @param savedInstanceState If the fragment is being recreated from a previous saved state, this is the state.
         */
        @Override
        public void onCreate( Bundle savedInstanceState )
        {
            super.onCreate( savedInstanceState );
        }

        /**
         * Called to have the fragment instantiate its user interface view.
         *
         * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
         * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
         *                           The fragment should not add the view itself, but this can be used to generate the
         *                           LayoutParams of the view.
         * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state
         *                           as given here.
         * @return Return the View for the fragment's UI, or null.
         */
        @Override
        public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
        {
            /* Create a new PieChart to be shown to the user */
            PieChart chart = new PieChart( getActivity() );
            Workday userWorkday = ( (TeamLeadApplication)getActivity().getApplication() ).getWorkdayModel();

            /* For each entry, paint a segment that is sized using the task runtime */
            for( int index = 0; index < userWorkday.getNumberOfUserTasks(); index++ )
            {
                String taskName = userWorkday.getTaskName( index );
                double percentage = userWorkday.getTaskPercentage( index );

                chart.addEntry( new PieChartEntry( taskName, (float)percentage, index ) );
            }

            return( chart );
        }
    }
}

