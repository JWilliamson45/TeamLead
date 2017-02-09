/**
 * TaskButtonAdapter.java
 *
 * TODO: descripton
 *
 * @author James Williamson
 * @version 0.1.0
 */

package com.jameswilliamson.teamlead;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import java.util.ArrayList;


public class TaskButtonAdapter extends BaseAdapter
{
    // Private member fields
    private ArrayList<Task> tasks;                     // The list of tasks to be displayed on the GUI
    private Context context;                           // The associated context

    /**
     * Constructs the task button adapter.
     *
     * @param c The application context
     * @param tasks The list of tasks to be displayed on the GridView
     */
    public TaskButtonAdapter( Context c, ArrayList<Task> tasks )
    {
        context = c;
        this.tasks = tasks;
    }

    @Override
    public int getCount()
    {
        return( tasks.size() );
    }

    @Override
    public Object getItem( int position )
    {
        if( position < tasks.size() )
        {
            return( tasks.get( position ) );
        }
        else
        {
            return( null );
        }
    }

    @Override
    public long getItemId( int position )
    {
        return( position );
    }

    @Override
    public View getView( int position, View convertView, ViewGroup parent )
    {
        Button taskButton;

        if( convertView == null )
        {
            // // TODO: 2/8/2017 Verify this works as expected; should it be run from another thread?
            LayoutInflater inflater = (LayoutInflater)context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            View view = inflater.inflate( R.layout.task_button, null );
            taskButton = (Button)view.findViewById( R.id.task_button );
        }
        else
        {
            taskButton = (Button)convertView;
        }

        Task taskView = tasks.get( position );
        taskButton.setText( taskView.getTaskName() + "\n\n" + taskView.getTotalTaskRuntime() );

        return( taskButton );
    }
}
