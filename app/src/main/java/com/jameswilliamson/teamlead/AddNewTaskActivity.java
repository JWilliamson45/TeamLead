/**
 * AddNewTaskActivity.java
 *
 * Android activity that is used to gather user input for the creation of a new task. Once complete, the task can be
 * added to the ContextSwitch activity and tracked.
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
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;

public class AddNewTaskActivity extends AppCompatActivity
{
    /* Private constants */
    private int MIN_COLOR_VALUE = 0;           /* The minimum value allowed for a color value */
    private int MAX_COLOR_VALUE = 255;         /* The maximum value allowed for a color value */
    private int RED_COLOR_DEFAULT = 72;        /* Default value for red component of task button color */
    private int GREEN_COLOR_DEFAULT = 134;     /* Default value for green component of task button color */
    private int BLUE_COLOR_DEFAULT = 206;      /* Default value for blue component of task button color */
    private int ALPHA_DEFAULT = 255;           /* Default value for alpha component of task button color */

    /* Private member fields */
    private Activity m_ThisActivity;           /* A reference to this activity */
    private EditText m_AddTaskNameField;       /* Holds the user-specified task name */
    private NumberPicker m_HourLimitPicker;    /* Picker used to select hour component of task limit */
    private NumberPicker m_MinuteLimitPicker;  /* Picker used to select minute component of task limit */
    private NumberPicker m_SecondLimitPicker;  /* Picker used to select second component of task limit */
    private NumberPicker m_RedColorPicker;     /* Picker used to select red color component of task button color */
    private NumberPicker m_GreenColorPicker;   /* Picker used to select green color component of task button color */
    private NumberPicker m_BlueColorPicker;    /* Picker used to select blue color component of task button color */
    private Button m_AddTaskButton;            /* The button used to complete addition of the task */

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
        m_AddTaskNameField = (EditText)findViewById( R.id.enter_task_name_field );
        m_HourLimitPicker = (NumberPicker)findViewById( R.id.enter_task_hour_limit_picker );
        m_MinuteLimitPicker = (NumberPicker)findViewById( R.id.enter_task_minute_limit_picker );
        m_SecondLimitPicker = (NumberPicker)findViewById( R.id.enter_task_second_limit_picker );
        m_RedColorPicker = (NumberPicker)findViewById( R.id.enter_task_red_color_picker );
        m_GreenColorPicker = (NumberPicker)findViewById( R.id.enter_task_green_color_picker );
        m_BlueColorPicker = (NumberPicker)findViewById( R.id.enter_task_blue_color_picker );
        m_AddTaskButton = (Button)findViewById( R.id.add_task_button );

        /* Configure time picker elements so user can assign a task time limit */
        m_HourLimitPicker.setMinValue( 0 );
        m_HourLimitPicker.setMaxValue( Workday.HOURS_PER_DAY - 1 );
        m_HourLimitPicker.setValue( 0 );

        m_MinuteLimitPicker.setMinValue( 0 );
        m_MinuteLimitPicker.setMaxValue( Workday.MINS_PER_HOUR - 1 );
        m_MinuteLimitPicker.setValue( 0 );

        m_SecondLimitPicker.setMinValue( 0 );
        m_SecondLimitPicker.setMaxValue( Workday.SECS_PER_MIN - 1 );
        m_SecondLimitPicker.setValue( 0 );

        /* Configure color picker elements so user can select a custom color */
        m_RedColorPicker.setMinValue( MIN_COLOR_VALUE );
        m_RedColorPicker.setMaxValue( MAX_COLOR_VALUE );
        m_RedColorPicker.setValue( RED_COLOR_DEFAULT );

        m_GreenColorPicker.setMinValue( MIN_COLOR_VALUE );
        m_GreenColorPicker.setMaxValue( MAX_COLOR_VALUE );
        m_GreenColorPicker.setValue( GREEN_COLOR_DEFAULT );

        m_BlueColorPicker.setMinValue( MIN_COLOR_VALUE );
        m_BlueColorPicker.setMaxValue( MAX_COLOR_VALUE );
        m_BlueColorPicker.setValue( BLUE_COLOR_DEFAULT );

        /* Set up listeners */
        m_AddTaskButton.setOnClickListener( new AddTaskButtonListener() );

        ColorPickerListener colorPickerListener = new ColorPickerListener();
        m_RedColorPicker.setOnValueChangedListener( colorPickerListener );
        m_GreenColorPicker.setOnValueChangedListener( colorPickerListener );
        m_BlueColorPicker.setOnValueChangedListener( colorPickerListener );
    }

    /**
     * Private class for handling changes to the color component number pickers.
     */
    private class ColorPickerListener implements NumberPicker.OnValueChangeListener
    {
        /**
         * Invoked upon a change of the picker's current value.
         *
         * @param picker The NumberPicker associated with this listener.
         * @param oldVal The previous value.
         * @param newVal The new value.
         */
        public void onValueChange( NumberPicker picker, int oldVal, int newVal )
        {
            int buttonColor = Color.argb( ALPHA_DEFAULT, m_RedColorPicker.getValue(), m_GreenColorPicker.getValue(),
                                          m_BlueColorPicker.getValue() );

            /* Dynamically change the color of the add button to preview the color for the user */
            m_AddTaskButton.setBackgroundColor( buttonColor );
        }
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
            boolean displayErrorDialog = false;
            String errorDialogMessage = null;

            /* Evaluate the length of the proposed task name */
            if( userTaskNameLength == 0 )
            {
                /* Don't allow tasks with empty string names */
                errorDialogMessage = getResources().getString( R.string.invalid_task_name_zero_dialog_msg );
                displayErrorDialog = true;
            }
            else if( userTaskNameLength > Task.TASK_NAME_CHARS_MAX )
            {
                /* Don't allow tasks with names longer than the allowable limit */
                errorDialogMessage = String.format( getResources().getString(
                        R.string.invalid_task_name_length_dialog_msg ), Task.TASK_NAME_CHARS_MAX );
                displayErrorDialog = true;
            }
            else
            {
                /* Name is good; create a new task object */
                Task createdTask = new Task( m_AddTaskNameField.getText().toString() );

                /* Apply limit information (zero values mean no time limit is assigned to the task) */
                createdTask.setTaskTimeLimit( m_HourLimitPicker.getValue(), m_MinuteLimitPicker.getValue(),
                                              m_SecondLimitPicker.getValue() );

                /* Apply task color code information */
                createdTask.setTaskColor( Color.argb( ALPHA_DEFAULT, m_RedColorPicker.getValue(),
                                                      m_GreenColorPicker.getValue(), m_BlueColorPicker.getValue() ) );

                /* Add the task to the workday model */
                Workday userWorkday = ( (TeamLeadApplication)getApplication() ).getWorkdayModel();

                if( userWorkday.addTask( createdTask ) == ErrorCode.ERR_TASK_DUPLICATE )
                {
                    /* Duplicate task - inform the user that it cannot be added */
                    errorDialogMessage = getResources().getString( R.string.invalid_task_duplicate_dialog_msg );
                    displayErrorDialog = true;
                }
            }

            /* Based on input, either display an error dialog, or return to the ContextSwitch UI */
            if( displayErrorDialog == true )
            {
                /* Package up the appropriate message so it can be passed to the error dialog */
                Bundle dialogBundle = new Bundle();
                dialogBundle.putString( "message", errorDialogMessage );

                /* Build and display the dialog fragment */
                InvalidTaskNameDialog invalidTaskDialog = new InvalidTaskNameDialog();
                invalidTaskDialog.setArguments( dialogBundle );
                invalidTaskDialog.show( m_ThisActivity.getFragmentManager(), InvalidTaskNameDialog.TAG );
            }
            else
            {
                /* Success */
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
        public final static String TAG = "Invalid Task";

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
