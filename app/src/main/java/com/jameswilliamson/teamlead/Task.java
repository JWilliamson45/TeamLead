/**
 * Task.java
 *
 * Models an individual task that a team leader may perform in a typical workday.
 *
 * @author James Williamson
 * @version 0.1.0
 */

package com.jameswilliamson.teamlead;

import android.os.SystemClock;
import java.text.DecimalFormat;


public class Task
{
    // Private constants
    private final String TIMER_FIELD_FORMAT = "00";    // Format used for presenting task time to the user
    private final int MS_PER_SEC = 1000;               // Conversion constant for seconds <-> milliseconds
    private final int SEC_PER_MIN = 60;                // Conversion constant for seconds <-> minutes
    private final int MIN_PER_HOUR = 60;               // Conversion constant for minutes <-> hours

    // Private member fields
    private String m_TaskName;                         // The name of the task
    private boolean m_TaskActive;                      // Marks whether or not this task is currently active
    private long m_TaskStartMs;                        // A timestamp captured when the user switches to this task
    private long m_TimeSpentMs;                        // The total time spent performing the task, in milliseconds
    private DecimalFormat m_TimeFormatter;             // Formats task time for presentation to the user

    /**
     * Constructs a new task and gives it a name.
     * @param taskName The name of the task
     */
    public Task( String taskName )
    {
        m_TaskName = taskName;
        m_TaskActive = false;
        m_TaskStartMs = 0;
        m_TimeSpentMs = 0;
        m_TimeFormatter = new DecimalFormat( TIMER_FIELD_FORMAT );
    }

    /**
     * Returns the name of the task.
     *
     * @return The name of the task
     */
    public String getTaskName()
    {
        return( m_TaskName );
    }

    /**
     * Marks a switch to the task, and begins recording time spent doing the task.
     *
     * @return An error code indicative of success or failure
     */
    public ErrorCode begin()
    {
        ErrorCode taskErr = ErrorCode.ERR_NONE;

        if( m_TaskActive == false )
        {
            m_TaskStartMs = SystemClock.elapsedRealtime();
            m_TaskActive = true;
        }
        else
        {
            // Task is already active
            taskErr = ErrorCode.ERR_TASK_ALREADY_STARTED;
        }

        return( taskErr );
    }

    /**
     * Marks a switch off the task, and stops recording time spent doing the task. The total time spent on the task
     * for the current duration is tallied and stored.
     *
     * @return An error code indicative of success or failure
     */
    public ErrorCode end()
    {
        ErrorCode taskErr = ErrorCode.ERR_NONE;

        if( m_TaskActive == true )
        {
            // Add the time spent on the task to the running total
            long taskStopMs = SystemClock.elapsedRealtime();
            m_TimeSpentMs += ( taskStopMs - m_TaskStartMs );
            m_TaskActive = false;
        }
        else
        {
            // Task is already inactive
            taskErr = ErrorCode.ERR_TASK_ALREADY_STOPPED;
        }

        return( taskErr );
    }

    /**
     * Returns the total amount of time spent performing the task, in "hh:mm:ss" format.
     *
     * @return The total number of time spent performing the task, in UI-friendly hh:mm:ss format
     */
    public String getTimeSpent()
    {
        String timeSpent = "";

        // Convert total millisecond task time to hours, minutes, and seconds
        int seconds = (int)( m_TimeSpentMs / MS_PER_SEC );
        int minutes = ( seconds / SEC_PER_MIN );
        int hours = ( minutes / MIN_PER_HOUR );

        // Adjust for remainder so that hh:mm:ss reported to user is cohesive
        seconds = ( seconds % SEC_PER_MIN );
        minutes = ( minutes % MIN_PER_HOUR );

        // Build string and return to user
        timeSpent = timeSpent.concat( m_TimeFormatter.format( hours ).toString() );
        timeSpent = timeSpent.concat( ":" );
        timeSpent = timeSpent.concat( m_TimeFormatter.format( minutes ).toString() );
        timeSpent = timeSpent.concat( ":" );
        timeSpent = timeSpent.concat( m_TimeFormatter.format( seconds ).toString() );

        return( timeSpent );
    }
}
