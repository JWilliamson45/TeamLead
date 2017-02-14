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
    private Workday m_Workday;                 // Models a user's workday
    private GridView m_Grid;                   // The grid of buttons displayed to the user
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
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_context_switch );

        // Initialization of members
        // TODO: 2/13/2017 The workday is erased when screen orientation is flipped; this cannot happen in final product
        m_Workday = new Workday();
        m_Grid = (GridView)findViewById( R.id.context_switch_grid );
        m_ScreenUpdateTimer = new Timer();
        m_GridAdapter = new TaskButtonAdapter( this, m_Workday );

        // Create sample tasks
        m_Workday.addTask( new Task( "Code" ) );
        m_Workday.addTask( new Task( "Design" ) );
        m_Workday.addTask( new Task( "Test" ) );
        m_Workday.addTask( new Task( "Plan" ) );
        m_Workday.addTask( new Task( "Debug" ) );

        // Set up adapter and listener, then begin timer that will repaint the view
        m_Grid.setAdapter( m_GridAdapter );
        m_Grid.setOnItemClickListener( new gridClickHandler() );
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
            m_Workday.contextSwitch( position );
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
            m_Grid.invalidateViews();
        }
    }
}
