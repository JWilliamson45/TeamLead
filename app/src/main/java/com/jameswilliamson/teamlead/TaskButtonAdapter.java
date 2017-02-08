package com.jameswilliamson.teamlead;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import java.util.ArrayList;

/**
 * Created by JEWilli1 on 2/6/2017.
 */

public class TaskButtonAdapter extends BaseAdapter
{
    // Private member fields
    private ArrayList<Task> tasks;                     // The list of tasks to be displayed on the GUI
    private Context context;                           // The associated context

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

        // TODO: 2/6/2017 make this an inflated button instead to decouple UI from logic

        if( convertView == null )
        {
            Task task = tasks.get( position );

            taskButton = new Button( context );
            taskButton.setText( task.getTaskName() + "\n\n" + task.getTimeSpent() );
            taskButton.setBackgroundColor( Color.BLUE );
        }
        else
        {
            taskButton = (Button)convertView;
        }

        return( taskButton );
    }
}
