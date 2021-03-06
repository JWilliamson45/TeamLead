/**
 * ContextSwitchActivity.java
 *
 * Android activity for the main ContextSwitch screen, where the user will switch between tasks they are actively
 * performing with the push of a button.
 *
 * @author James Williamson
 *
 * @formatter:off
 */

package com.jameswilliamson.teamlead;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;import android.widget.AdapterView;
import android.widget.GridView;
import java.io.IOException;import java.util.Timer;
import java.util.TimerTask;

public class ContextSwitchActivity extends AppCompatActivity
{
    /* Private member fields */
    private Workday m_UserWorkday;               /* The user workday model to depict on screen */
    private GridView m_TaskGrid;                 /* The grid of buttons (tasks) displayed to the user */
    private Activity m_ThisActivity;             /* A reference to this activity */
    private Timer m_ScreenUpdateTimer;           /* A timer that is used to update the UI */
    private TeamLeadApplication m_Application;   /* Reference to the application object */

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

        /* Show this activity on the device lock screen */
        getWindow().addFlags( WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED );

        /* Inflate layout */
        setContentView( R.layout.activity_context_switch );

        /* Set up toolbar */
        Toolbar m_Toolbar = (Toolbar)findViewById( R.id.context_switch_toolbar );
        m_Toolbar.setTitle( R.string.context_switch_activity_title );
        setSupportActionBar( m_Toolbar );

        /* Initialization of members */
        m_Application = (TeamLeadApplication)getApplication();
        m_UserWorkday = m_Application.getWorkdayModel();
        m_TaskGrid = (GridView)findViewById( R.id.context_switch_grid );
        m_ThisActivity = this;

        /* Set up the grid adapter */
        TaskButtonAdapter m_GridAdapter = new TaskButtonAdapter( this, m_UserWorkday );
        m_TaskGrid.setAdapter( m_GridAdapter );

        /* Register the grid view to display context menus */
        registerForContextMenu( m_TaskGrid );

        /* Assign listeners */
        m_TaskGrid.setOnItemClickListener( new gridClickHandler() );
    }

    /**
     * Called when the activity is visible and ready to start interacting with the user.
     */
    @Override
    public void onResume()
    {
        /* Call superclass implementation first */
        super.onResume();

        /* Retrieve stored refresh rate */
        int refreshRateMs = m_Application.getTaskTileRefreshRate();

        /* Set up the timer that will repaint the view */
        if( m_ScreenUpdateTimer != null )
        {
            m_ScreenUpdateTimer.cancel();
        }

        m_ScreenUpdateTimer = new Timer();
        m_ScreenUpdateTimer.scheduleAtFixedRate( new ActivityTimerTask( this ), 0, refreshRateMs );
    }

    /**
     * Called when the activity is no longer visible to the user.
     */
    public void onStop()
    {
        super.onStop();

        Log.d( "Info", "CONTEXT UI STOPPED" );

        try
        {
            /* Save the Workday data in case the application does not resume */
            m_Application.saveWorkday();
        }
        catch( IOException io_exception )
        {
            /* A problem occurred and the data could not be saved */
            // TODO: 4/23/2017 Log appropriately
            Log.d( "Info", "COULD NOT SAVE WORKDAY" );
            io_exception.printStackTrace();
        }
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
        inflater.inflate( R.menu.context_switch_toolbar_menu, menu );

        return( true );
    }

    /**
     * Called when the context menu for this view is being built. It is not safe to hold onto the menu after this
     * method returns.
     *
     * @param menu The context menu that is being built.
     * @param v The view for which the context menu is being built.
     * @param menuInfo Extra information about the item for which the context menu should be shown. This information
     *                 will vary depending on the class of v.
     */
    @Override
    public void onCreateContextMenu( ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo )
    {
        super.onCreateContextMenu( menu, v, menuInfo );
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;

        if( info.position < m_UserWorkday.getNumberOfUserTasks() )
        {
            /* Get the task associated with this context menu (only if it's a user-defined task) */
            Task selectedTask = m_UserWorkday.getTask( info.position );

            /* Inflate the menu resource */
            MenuInflater inflater = getMenuInflater();
            inflater.inflate( R.menu.task_button_context_menu, menu );

            /* Customize menu header */
            menu.setHeaderTitle( selectedTask.getTaskName() + " " +
                                 getString( R.string.task_context_menu_header_suffix ) );
        }
    }

    /**
     * Invoked whenever an item in a context menu is selected in order to perform the appropriate processing.
     *
     * @param item The context menu item that was selected.
     * @return Return false to allow normal context menu processing to proceed, true to consume it here.
     */
    @Override
    public boolean onContextItemSelected( MenuItem item )
    {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        boolean handled;

        switch( item.getItemId() )
        {
            case R.id.delete_task:
                m_UserWorkday.deleteTask( info.position );
                handled = true;
                break;

            case R.id.edit_task:
                handled = true;
                break;

            default:
                handled = super.onContextItemSelected( item );
                break;
        }

        return( handled );
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

            case R.id.reset:
                UserConfirmDialog userConfirmDialog = new UserConfirmDialog();
                userConfirmDialog.show( m_ThisActivity.getFragmentManager(), UserConfirmDialog.TAG );
                break;

            case R.id.view_log:
                startActivity( new Intent( m_ThisActivity, TaskLogActivity.class ) );
                break;

            case R.id.about:
                AboutDialog aboutDialog = new AboutDialog();
                aboutDialog.show( m_ThisActivity.getFragmentManager(), AboutDialog.TAG );
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

    /**
     * Dialog that is shown when a user's confirmation is required to reset all task data.
     */
    public static class UserConfirmDialog extends DialogFragment
    {
        /* Private constants */
        private final static String TAG = "Confirm action";

        /**
         * Builds the dialog's container.
         *
         * @param savedInstanceState The last saved instance state of the Fragment, or null if this is a freshly
         *                           created Fragment.
         * @return Returns a new Dialog instance to be displayed by the Fragment.
         */
        @Override
        public Dialog onCreateDialog( Bundle savedInstanceState )
        {
            /* Construct a dialog to inform the user that their task name cannot be accepted. */
            AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
            builder.setTitle( R.string.reset_task_data_dialog_title );

            /* The dialog body should convey why the task name is invalid */
            builder.setMessage( R.string.reset_task_data_dialog_message );

            /* Allows user to dismiss the dialog */
            builder.setPositiveButton( R.string.ok_button_label, new OkayButtonListener() );
            builder.setNegativeButton( R.string.cancel_button_label, new CancelButtonListener() );

            return( builder.create() );
        }

        /**
         * Button listener for the dialog fragment positive action.
         */
        private class OkayButtonListener implements DialogInterface.OnClickListener
        {
            /**
             * Called when the view has been clicked.
             *
             * @param dialog The dialog that received the click.
             * @param which The button that was clicked, or the position of the item clicked.
             */
            @Override
            public void onClick( DialogInterface dialog, int which )
            {
                ( (TeamLeadApplication)getActivity().getApplication() ).getWorkdayModel().resetWorkday();
            }
        }

        /**
         * Button listener for the dialog fragment negative action.
         */
        private class CancelButtonListener implements DialogInterface.OnClickListener
        {
            /**
             * Called when the view has been clicked.
             *
             * @param dialog The dialog that received the click.
             * @param which The button that was clicked, or the position of the item clicked.
             */
            @Override
            public void onClick( DialogInterface dialog, int which )
            {
                dialog.cancel();
            }
        }
    }
}
