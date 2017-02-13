package com.jameswilliamson.teamlead;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;

public class ContextSwitchActivity extends AppCompatActivity
{
    Workday m_Workday;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_context_switch );

        m_Workday = new Workday();

        m_Workday.addTask( new Task( "Code" ) );
        m_Workday.addTask( new Task( "Design" ) );
        m_Workday.addTask( new Task( "Test" ) );
        m_Workday.addTask( new Task( "Plan" ) );
        m_Workday.addTask( new Task( "Debug" ) );

        TaskButtonAdapter gridAdapter = new TaskButtonAdapter( this, m_Workday );

        GridView grid = (GridView) findViewById( R.id.context_switch_grid );
        grid.setAdapter( gridAdapter );
        grid.setOnItemClickListener( new gridClickHandler() );
        Log.d( "INFO", "Tasks are ready" );

        // TODO: 2/12/2017 Add documentation to this class, and make a thread that repaints the UI - then test! 
    }

    private class gridClickHandler implements AdapterView.OnItemClickListener
    {
        @Override
        public void onItemClick( AdapterView<?> parent, View view, int position, long id )
        {
            m_Workday.contextSwitch( position );

            Log.d( "INFO", "Clicked button " + position );
        }
    }
}
