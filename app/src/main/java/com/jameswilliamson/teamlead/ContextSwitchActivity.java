/**
 * ContextSwitchActivity.java
 *
 * Android activity for the main ContextSwitch screen, where the user will switch between tasks they are actively
 * performing with the push of a button.
 *
 * @author James Williamson
 * @version 0.2.0
 *
 * @formatter:off
 */

package com.jameswilliamson.teamlead;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import java.util.Timer;
import java.util.TimerTask;

public class ContextSwitchActivity extends AppCompatActivity
{
    // Private member fields
    private Workday m_UserWorkday;             // The user workday model to depict on screen
    private GridView m_TaskGrid;               // The grid of buttons (tasks) displayed to the user
    private Timer m_ScreenUpdateTimer;         // A timer used to repaint the screen
    private TaskButtonAdapter m_GridAdapter;   // The adapter used with the GridView

    /**
     * Called when the activity is created - initialization for the activity is performed here.
     *
     * @param savedInstanceState The saved instance state
     */
    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        // Call superclass implementation first
        super.onCreate( savedInstanceState );

        // Inflate layout
        setContentView( R.layout.activity_context_switch );

        // Initialization of members
        m_UserWorkday = ( (TeamLeadApplication)getApplication() ).getWorkdayModel();
        m_TaskGrid = (GridView)findViewById( R.id.context_switch_grid );
        m_ScreenUpdateTimer = new Timer();
        m_GridAdapter = new TaskButtonAdapter( this, m_UserWorkday );

        // Set up adapter and listener, then begin timer that will repaint the view
        m_TaskGrid.setAdapter( m_GridAdapter );
        m_TaskGrid.setOnItemClickListener( new gridClickHandler() );
        m_ScreenUpdateTimer.scheduleAtFixedRate( new ActivityTimerTask( this ), 0, 1000 );
    }

    /**
     * Private class for handling the user's button presses on the grid.
     */
    private class gridClickHandler implements AdapterView.OnItemClickListener
    {
        @Override
        public void onItemClick( AdapterView<?> parent, View view, int position, long id )
        {
            m_UserWorkday.contextSwitch( position );
        }
    }

    /**
     * Private class for executing a task associated with the screen update timer.
     */
    private class ActivityTimerTask extends TimerTask
    {
        // Private member fields
        private Activity m_Activity;

        /**
         * Constructs the ActivityTimerTask.
         *
         * @param activity A reference to the associated activity
         */
        public ActivityTimerTask( Activity activity )
        {
            m_Activity = activity;
        }

        @Override
        public void run()
        {
            m_Activity.runOnUiThread( new GridRefresher() );
        }
    }

    /**
     * Private class for updating the grid. This should only be run on the UI thread.
     */
    private class GridRefresher implements Runnable
    {
        @Override
        public void run()
        {
            m_TaskGrid.invalidateViews();
        }
    }
}
