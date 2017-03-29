/**
 * TaskButtonAdapter.java
 *
 * Adapter that is used to inflate and update button views that populate the ContextSwitch GridView.
 *
 * @author James Williamson
 * @version 0.3.0
 *
 * @formatter:off
 */

package com.jameswilliamson.teamlead;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;


class TaskButtonAdapter extends BaseAdapter
{
    /* Private constants */
    private final float BUTTON_ALPHA_FADE_PCT = .25f;  /* Pct. to fade the task button color alpha on selection */

    /* Private member fields */
    private Workday m_Workday;                         /* Contains the list of tasks to be displayed on the GUI */
    private Activity m_Activity;                       /* The associated activity */

    /**
     * Constructs the task button adapter.
     *
     * @param a The associated activity.
     * @param workday The Workday object that contains the list of tasks to be displayed on the GridView.
     */
    TaskButtonAdapter( Activity a, Workday workday )
    {
        m_Activity = a;
        this.m_Workday = workday;
    }

    /**
     * Returns the number of items in the data set represented by this adapter.
     *
     * @return The item count.
     */
    @Override
    public int getCount()
    {
        return( m_Workday.getNumberOfTasks() );
    }

    /**
     * Returns the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem( int position )
    {
        if( position < m_Workday.getNumberOfTasks() )
        {
            return( m_Workday.getTask( position ) );
        }
        else
        {
            return( null );
        }
    }

    /**
     * Returns the row ID associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row ID we want.
     * @return The ID of the item at the specified position.
     */
    @Override
    public long getItemId( int position )
    {
        return( position );
    }

    /**
     * Returns the view that displays the data at the specified position in the data set. The view is inflated upon
     * first invocation from an XML file and populated with data from the associated Workday.
     *
     * @param position The position of the item within the adapter's data set of the item whose view we want.
     * @param convertView The old view to reuse, if possible.
     * @param parent The parent that this view will eventually be attached to.
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView( int position, View convertView, ViewGroup parent )
    {
        Button taskButton;
        int buttonColor;

        if( convertView == null )
        {
            /* Cannot reuse view, so inflate it from XML */
            LayoutInflater inflater = (LayoutInflater)m_Activity.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            View view = inflater.inflate( R.layout.task_button, null );
            taskButton = (Button)view.findViewById( R.id.task_button );
        }
        else
        {
            taskButton = (Button)convertView;
        }

        if( m_Workday.getTaskName( position ).equals( m_Activity.getString( R.string.add_task_label ) ) )
        {
            /*
             * The "add new" task should be drawn a bit differently since it performs a special function and
             * there is no timer.
             */
            taskButton.setText( m_Workday.getTaskName( position ) + "\n\n+" );
            buttonColor = Color.LTGRAY;
        }
        else if( m_Workday.getTaskName( position ).equals( m_Activity.getString( R.string.end_workday_label ) ) )
        {
            /*
             * The "end workday" task should be drawn a bit differently since it performs a special function and
             * there is no timer.
             */
            taskButton.setText( m_Workday.getTaskName( position ) + "\n\n" );
            buttonColor = Color.LTGRAY;
        }
        else
        {
            /* Update the button text, since the time value can change */
            taskButton.setText( m_Workday.getTaskName( position ) + "\n\n" + m_Workday.getTaskRuntime( position ) );

            /* If the task has a time limit, check to see whether or not it's been exceeded */
            if( m_Workday.isTaskLimitExceeded( position ) == true )
            {
                /* Over-budget on time; paint the task text color red to indicate this */
                taskButton.setTextColor( ContextCompat.getColor( m_Activity.getApplicationContext(),
                                                                 R.color.taskButtonTextAlarm ));
            }

            if( m_Workday.getTask( position ).isActive() )
            {
                /* User task button is selected */
                buttonColor = m_Workday.getTaskColor( position );

                /* Reduce the alpha value by a fixed percentage to indicate that it's clicked */
                int alpha = Color.alpha( buttonColor );
                alpha =- (int)( BUTTON_ALPHA_FADE_PCT * alpha );

                /* Reconstruct the color value */
                buttonColor = Color.argb( alpha, Color.red( buttonColor ), Color.green( buttonColor ),
                                          Color.blue( buttonColor ) );
            }
            else
            {
                /* User task button is not selected */
                buttonColor = m_Workday.getTaskColor( position );
            }
        }

        /* Update the button's color */
        taskButton.setBackgroundColor( buttonColor );

        return( taskButton );
    }
}
