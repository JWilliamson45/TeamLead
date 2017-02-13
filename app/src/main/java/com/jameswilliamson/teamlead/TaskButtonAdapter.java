/**
 * TaskButtonAdapter.java
 *
 * Adapter that is used to inflate and update button views that populate the ContextSwitch GridView.
 *
 * @author James Williamson
 * @version 0.2.0
 */

package com.jameswilliamson.teamlead;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;


public class TaskButtonAdapter extends BaseAdapter
{
    // Private member fields
    private Workday m_Workday;                         // Contains the list of tasks to be displayed on the GUI
    private Context m_Context;                         // The associated context

    /**
     * Constructs the task button adapter.
     *
     * @param c The application context
     * @param workday The Workday object that contains the list of tasks to be displayed on the GridView
     */
    public TaskButtonAdapter( Context c, Workday workday )
    {
        m_Context = c;
        this.m_Workday = workday;
    }

    /**
     * Returns the number of items in the data set represented by this adapter.
     *
     * @return The item count
     */
    @Override
    public int getCount()
    {
        return( m_Workday.getNumberOfTasks() );
    }

    /**
     * Returns the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's data set
     * @return The data at the specified position
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
     * @param position The position of the item within the adapter's data set whose row ID we want
     * @return The ID of the item at the specified position
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
     * @param position The position of the item within the adapter's data set of the item whose view we want
     * @param convertView The old view to reuse, if possible
     * @param parent The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position
     */
    @Override
    public View getView( int position, View convertView, ViewGroup parent )
    {
        Button taskButton;

        if( convertView == null )
        {
            // Cannot reuse view, so inflate it from XML
            LayoutInflater inflater = (LayoutInflater)m_Context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            View view = inflater.inflate( R.layout.task_button, null );
            taskButton = (Button)view.findViewById( R.id.task_button );
        }
        else
        {
            taskButton = (Button)convertView;
        }

        // Update the button text, since the time value can change
        taskButton.setText( m_Workday.getTaskName( position ) + "\n\n" + m_Workday.getTaskRuntime( position ) );

        return( taskButton );
    }
}
