package com.jameswilliamson.teamlead;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;

import java.util.ArrayList;

public class ContextSwitchActivity extends AppCompatActivity
{
    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_context_switch );

        ArrayList<Task> tasks = new ArrayList<Task>();

        tasks.add( new Task( "Code" ) );
        tasks.add( new Task( "Design" ) );
        tasks.add( new Task( "Test" ) );
        tasks.add( new Task( "Plan" ) );
        tasks.add( new Task( "Debug" ) );

        tasks.get(0).begin();

        TaskButtonAdapter gridAdapter = new TaskButtonAdapter( this, tasks );

        GridView grid = (GridView) findViewById( R.id.context_switch_grid );
        grid.setAdapter( gridAdapter );
    }
}
