/**
 * TaskLogActivity.java
 *
 * Android activity that is used to display the task history. This log shows the sequence of all task iterations
 * performed by the user and the time spent on each.
 *
 * @author James Williamson
 * @version 0.3.0
 *
 * @formatter:off
 */

package com.jameswilliamson.teamlead;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import java.util.Iterator;

public class TaskLogActivity extends AppCompatActivity
{
    /* Private members */
    private TextView m_TaskLog;                             /* A text field used to display the task log */

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
        setContentView( R.layout.activity_task_log );

        /* Set up toolbar */
        Toolbar m_Toolbar = (Toolbar)findViewById( R.id.task_log_toolbar );
        m_Toolbar.setTitle( R.string.task_log_activity_title );
        setSupportActionBar( m_Toolbar );

        /* Initialize text field */
        m_TaskLog = (TextView)findViewById( R.id.task_log );

        /* Output the data */
        writeLog();
    }

    /**
     * Writes the task log to the activity text view for the user to analyze.
     */
    private void writeLog()
    {
        /* Retrieve the list of tasks */
        Workday userWorkday = ((TeamLeadApplication)getApplication() ).getWorkdayModel();
        Iterator<TaskIteration> tasks = userWorkday.getTaskLogIterator();

        /* Set header */
        m_TaskLog.setText( getString( R.string.task_log_header ) );
        
        // TODO: 4/2/2017 Need to get the current iteration, too. This won't do that yet. 

        while( tasks.hasNext() )
        {
            m_TaskLog.append( tasks.next().toString() + "\n" );
        }
    }
}