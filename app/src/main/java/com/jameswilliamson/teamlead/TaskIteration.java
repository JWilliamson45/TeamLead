/**
 * TaskIteration.java
 *
 * Models a single iteration of a task, which tracks the amount of time a specific activity is performed. A task
 * may have many iterations performed over the course of a workday, which may be contiguous or scattered.
 *
 * @author James Williamson
 * @version 0.3.0
 *
 * @formatter:off
 */

package com.jameswilliamson.teamlead;

import android.os.SystemClock;


class TaskIteration
{
    /* Private member fields */
    private Task m_Task;                           /* The associated task; this is an iteration of this task */
    private long m_StartTimestampMs;               /* A timestamp captured when the user switches to this task */
    private long m_TotalRuntimeMs;                 /* The total runtime of this iteration, when concluded */
    private boolean m_Active;                      /* Marks whether or not this iteration is currently active */

    /**
     * Constructs the TaskIteration object.
     *
     * @param task The task to which this iteration applies.
     */
    TaskIteration( Task task )
    {
        m_Task = task;
        m_StartTimestampMs = 0;
        m_TotalRuntimeMs = 0;
        m_Active = false;
    }

    /**
     * Get the task associated with this iteration.
     *
     * @return The associated task.
     */
    public Task getTask()
    {
        return( m_Task );
    }

    /**
     * Marks the start of this iteration, and begins recording time spent doing the task. Since subsequent iterations
     * of the task are tracked separately, calling this function more than once will return an error.
     *
     * @return An error code indicative of success or failure.
     */
    ErrorCode start()
    {
        ErrorCode taskErr = ErrorCode.ERR_NONE;

        if( ( m_Active == false ) && ( m_TotalRuntimeMs == 0 ) )
        {
            /* Capture a starting timestamp */
            m_StartTimestampMs = SystemClock.elapsedRealtime();

            /* Mark this iteration as active, and also mark the corresponding task as active as well */
            m_Active = true;
            m_Task.setAsActive();
        }
        else
        {
            /* Task is already active */
            taskErr = ErrorCode.ERR_TASK_ALREADY_STARTED;
        }

        return( taskErr );
    }

    /**
     * Marks the end of this iteration, and stops recording time spent doing the task. Calling this function more than
     * once will return an error.
     *
     * @return An error code indicative of success or failure.
     */
    ErrorCode end()
    {
        ErrorCode taskErr = ErrorCode.ERR_NONE;

        if( m_Active == true )
        {
            /* Add the time spent on the task to the running total */
            m_TotalRuntimeMs = ( SystemClock.elapsedRealtime() - m_StartTimestampMs );

            /* Mark this iteration as inactive, and also mark the corresponding task as inactive as well */
            m_Active = false;
            m_Task.setAsInactive();
        }
        else
        {
            /* Task is already inactive */
            taskErr = ErrorCode.ERR_TASK_ALREADY_STOPPED;
        }

        return( taskErr );
    }

    /**
     * Returns the runtime of the current iteration of the task.
     *
     * @return The current task runtime in milliseconds.
     */
    long getRuntimeMs()
    {
        long currentRuntimeMs;

        if( m_Active )
        {
            /* Get the time spent on the task since the start of the current iteration */
            currentRuntimeMs = ( SystemClock.elapsedRealtime() - m_StartTimestampMs );
        }
        else
        {
            currentRuntimeMs = m_TotalRuntimeMs;
        }

        return( currentRuntimeMs );
    }

    /**
     * Returns the string representation of the task iteration.
     *
     * @return The task iteration's name and runtime.
     */
    @Override public String toString()
    {
        return( m_Task.getTaskName() + ": " + Workday.convertMsToFormattedTimeString( getRuntimeMs() ) );
    }
}
