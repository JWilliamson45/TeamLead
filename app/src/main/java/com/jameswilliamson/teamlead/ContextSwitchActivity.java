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
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import java.util.Timer;
import java.util.TimerTask;

public class ContextSwitchActivity extends AppCompatActivity
{
    /* Private member fields */
    private Workday m_UserWorkday;             /* The user workday model to depict on screen */
    private GridView m_TaskGrid;               /* The grid of buttons (tasks) displayed to the user */
    private Activity m_ThisActivity;           /* A reference to this activity */

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
        setContentView( R.layout.activity_context_switch );

        /* Set up toolbar */
        Toolbar m_Toolbar = (Toolbar)findViewById( R.id.context_switch_toolbar );
        m_Toolbar.setTitle( R.string.context_switch_activity_title );
        setSupportActionBar( m_Toolbar );

        /* Initialization of members */
        m_UserWorkday = ( ( TeamLeadApplication)getApplication() ).getWorkdayModel();
        m_TaskGrid = (GridView)findViewById( R.id.context_switch_grid );
        m_ThisActivity = this;

        /* Set up the grid adapter */
        TaskButtonAdapter m_GridAdapter = new TaskButtonAdapter( this, m_UserWorkday );
        m_TaskGrid.setAdapter( m_GridAdapter );

        /* Assign listeners */
        m_TaskGrid.setOnItemClickListener( new gridClickHandler() );

        /* Set up the timer that will repaint the view */
        Timer m_ScreenUpdateTimer = new Timer();
        m_ScreenUpdateTimer.scheduleAtFixedRate( new ActivityTimerTask( this ), 0, 1000 );
    }

    /**
     * Initializes the content of the ContextSwitchActivity's standard options menu.
     *
     * @param menu The menu in which the items are shown.
     * @return True if the menu is to be displayed, false otherwise.
     */
    @Override
    public boolean onCreateOptionsMenu( Menu menu )
    {
        /* Inflate the menu from the resources file that includes the menu entries */
        MenuInflater inflater = getMenuInflater();
        inflater.inflate( R.menu.context_switch_menu, menu );

        return( true );
    }

    /**
     * Called whenever the ContextSwitchActivity's options menu is clicked by the user.
     *
     * @param item The selected menu item.
     * @return False to allow normal menu processing to proceed, true to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected( MenuItem item )
    {
        boolean itemHandled = true;

        // Handle item selection
        switch( item.getItemId() )
        {
            case R.id.settings:
                startActivity( new Intent( m_ThisActivity, SettingsActivity.class ) );
                break;

            case R.id.about:
                break;

            default:
                itemHandled = super.onOptionsItemSelected( item );
                break;
        }

        return( itemHandled );
    }

    /**
     * Private class for handling the user's button presses on the grid.
     */
    private class gridClickHandler implements AdapterView.OnItemClickListener
    {
        @Override
        public void onItemClick( AdapterView<?> parent, View view, int position, long id )
        {
            if( m_UserWorkday.getTaskName( position ).equals( getString( R.string.add_task_label ) ) )
            {
                /* Launch new activity to gather user input for the new task */
                startActivity( new Intent( m_ThisActivity, AddNewTaskActivity.class ) );
            }
            else if( m_UserWorkday.getTaskName( position ).equals( getString( R.string.end_workday_label ) ) )
            {
                m_UserWorkday.endWorkday();

                /* Launch activity to summarize the workday for the user */
                startActivity( new Intent( m_ThisActivity, WorkdaySummaryActivity.class ) );
            }
            else
            {
                /* Normal user task; context switch to whatever task has been selected */
                m_UserWorkday.contextSwitch( position );

                /* Force a repaint */
                m_TaskGrid.invalidateViews();
            }
        }
    }

    /**
     * Private class for executing a task associated with the screen update timer.
     */
    private class ActivityTimerTask extends TimerTask
    {
        /* Private member fields */
        private Activity m_Activity;

        /**
         * Constructs the ActivityTimerTask.
         *
         * @param activity A reference to the associated activity.
         */
        private ActivityTimerTask( Activity activity )
        {
            m_Activity = activity;
        }

        /**
         * Executes the grid refresh task on the UI thread.
         */
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
        /**
         * Runs the task that forces a repaint on the UI grid.
         */
        @Override
        public void run()
        {
            m_TaskGrid.invalidateViews();
        }
    }
}
