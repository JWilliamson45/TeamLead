/**
 * AddNewTaskActivity.java
 *
 * Android activity that is used to gather user input for the creation of a new task. Once complete, the task can be
 * added to the ContextSwitch activity and tracked.
 *
 * @author James Williamson
 * @version 0.3.0
 *
 * @formatter:off
 */

package com.jameswilliamson.teamlead;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddNewTaskActivity extends AppCompatActivity
{
    /* Private member fields */
    private Activity m_ThisActivity;           /* A reference to this activity */
    private EditText m_AddTaskNameField;       /* Holds the user-specified task name */

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
        setContentView( R.layout.activity_add_new_task );

        /* Set up toolbar */
        Toolbar m_Toolbar = (Toolbar)findViewById( R.id.add_new_task_toolbar );
        m_Toolbar.setTitle( R.string.add_new_task_activity_title );
        setSupportActionBar( m_Toolbar );

        /* Initialization of members */
        m_ThisActivity = this;

        /* Get references to key layout elements */
        Button m_AddTaskButton = (Button)findViewById( R.id.add_task_button );
        m_AddTaskNameField = (EditText)findViewById( R.id.enter_task_name_field );

        /* Set up listeners */
        m_AddTaskButton.setOnClickListener( new AddTaskButtonListener() );
    }

    /**
     * Private class for handling the button that adds the new user task.
     */
    private class AddTaskButtonListener implements View.OnClickListener
    {
        /**
         * Called when the view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick( View v )
        {
            int userTaskNameLength = m_AddTaskNameField.getText().length();

            if( userTaskNameLength == 0 )
            {
                /* Don't allow tasks with empty string names */
                String displayMsg = getResources().getString( R.string.invalid_task_name_zero_dialog_msg );

                /* Pass message to error dialog and show it to the user */
                Bundle dialogBundle = new Bundle();
                dialogBundle.putString( "message", displayMsg );

                InvalidTaskNameDialog invalidTaskDialog = new InvalidTaskNameDialog();
                invalidTaskDialog.setArguments( dialogBundle );
                invalidTaskDialog.show( m_ThisActivity.getFragmentManager(), InvalidTaskNameDialog.TAG );
            }
            else if( userTaskNameLength > Task.TASK_NAME_CHARS_MAX )
            {
                /* Don't allow tasks with names longer than the allowable limit */
                String displayMsg = String.format( getResources().getString(
                        R.string.invalid_task_name_length_dialog_msg ), Task.TASK_NAME_CHARS_MAX );

                /* Pass message to error dialog and show it to the user */
                Bundle dialogBundle = new Bundle();
                dialogBundle.putString( "message", displayMsg );

                InvalidTaskNameDialog invalidTaskDialog = new InvalidTaskNameDialog();
                invalidTaskDialog.setArguments( dialogBundle );
                invalidTaskDialog.show( m_ThisActivity.getFragmentManager(), InvalidTaskNameDialog.TAG );
            }
            else
            {
                /* Task name is good; add it to the workday */
                Task createdTask = new Task( m_AddTaskNameField.getText().toString() );
                ( (TeamLeadApplication)getApplication() ).getWorkdayModel().addTask( createdTask );

                /* Return to the ContextSwitch screen */
                startActivity( new Intent( m_ThisActivity, ContextSwitchActivity.class ) );
            }
        }
    }

    /**
     * Dialog that can be shown to the user to alert them of invalid input.
     */
    public static class InvalidTaskNameDialog extends DialogFragment
    {
        /* Private constants */
        private final static String TAG = "Invalid Task";

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
            builder.setTitle( R.string.invalid_task_dialog_title );

            /* The dialog body should convey why the task name is invalid */
            builder.setMessage( getArguments().getString( "message" ) );

            /* Allows user to dismiss the dialog */
            builder.setPositiveButton( R.string.ok_button_label, new CloseDialogButtonListener() );

            return( builder.create() );
        }

        /**
         * Button listener for the dialog fragment.
         */
        private class CloseDialogButtonListener implements DialogInterface.OnClickListener
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
