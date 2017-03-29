/**
 * Task.java
 *
 * Models an individual task that a team leader may perform in a typical workday.
 *
 * @author James Williamson
 * @version 0.3.0
 *
 * @formatter:off
 */

package com.jameswilliamson.teamlead;

import android.support.annotation.ColorInt;

class Task
{
    /* Private constants */
    @ColorInt private final int NO_COLOR = 0xFF000000; /* No color; opaque black */

    /* Public constants */
    static final int TASK_NAME_CHARS_MAX = 8;          /* The maximum allowable length of a task name */

    /* Private member fields */
    private String m_TaskName;                         /* The name of the task */
    private boolean m_TaskActive;                      /* Marks whether or not this task is currently active */
    private long m_TotalTaskRuntimeMs;                 /* Total time spent on all previous iterations of the task */
    @ColorInt private int m_TaskColor;                 /* The color code assigned to the task for display on the UI */
    private long m_TaskTimeLimitMs;                    /* A user-defined limit on time to spend on the task */

    /**
     * Constructs a new task and gives it a name.
     *
     * @param taskName The name of the task.
     */
    Task( String taskName )
    {
        m_TaskName = taskName;
        m_TaskActive = false;
        m_TotalTaskRuntimeMs = 0;
        m_TaskColor = NO_COLOR;
        m_TaskTimeLimitMs = 0;
    }

    /**
     * Sets a new name for the task.
     *
     * @param taskName The new name for the task.
     */
    public void setTaskName( String taskName )
    {
        m_TaskName = taskName;
    }

    /**
     * Returns the name of the task.
     *
     * @return The name of the task.
     */
    String getTaskName()
    {
        return( m_TaskName );
    }

    /**
     * Marks the task as currently active.
     */
    void setAsActive()
    {
        m_TaskActive = true;
    }

    /**
     * Marks the task as currently inactive.
     */
    void setAsInactive()
    {
        m_TaskActive = false;
    }

    /**
     * Returns whether or not the task is active.
     *
     * @return True if the task is active, false otherwise.
     */
    boolean isActive()
    {
        return( m_TaskActive );
    }

    /**
     * Adds the given millisecond count to the total overall runtime of the task.
     *
     * @param runtimeMs The amount of runtime to add to the task, in milliseconds.
     */
    void addRuntimeMs( long runtimeMs )
    {
        m_TotalTaskRuntimeMs += runtimeMs;
    }

    /**
     * Returns the total runtime of all previous iterations of the task.
     *
     * @return The runtime of all previous iterations of the task, in milliseconds.
     */
    long getRuntimeMs()
    {
        return( m_TotalTaskRuntimeMs );
    }

    /**
     * Assigns a new color code to the task.
     *
     * @param newColor The new color code to associate with the task.
     */
    void setTaskColor( int newColor )
    {
        m_TaskColor = newColor;
    }

    /**
     * Returns the color code associated with the task.
     *
     * @return The task's color code.
     */
    int getTaskColor()
    {
        return( m_TaskColor );
    }

    /**
     * Sets a new user-defined limit for time to spend on the task. This may be used for alarm applications. If
     * the new value is zero, it means that no limit is specified.
     *
     * @param hours The hours component of the limit.
     * @param minutes The minutes component of the limit.
     * @param seconds The seconds component of the limit.
     */
    void setTaskTimeLimit( int hours, int minutes, int seconds )
    {
        /* Boundary checks */
        if( ( hours < Workday.HOURS_PER_DAY ) &&
            ( minutes < Workday.MINS_PER_HOUR ) &&
            ( seconds < Workday.SECS_PER_MIN ) )
        {
            /* Convert hours to milliseconds */
            long taskLimitMs = hours * Workday.MINS_PER_HOUR * Workday.SECS_PER_MIN * Workday.MS_PER_SEC;

            /* Convert minutes to milliseconds */
            taskLimitMs += minutes * Workday.SECS_PER_MIN * Workday.MS_PER_SEC;

            /* Convert seconds to milliseconds */
            taskLimitMs += seconds * Workday.MS_PER_SEC;

            setTaskTimeLimit( taskLimitMs );
        }
    }

    /**
     * Sets a new user-defined limit for time to spend on the task. This may be used for alarm applications. If
     * the new value is zero, it means that no limit is specified.
     *
     * @param newLimitMs The new task time limit.
     */
    void setTaskTimeLimit( long newLimitMs )
    {
        m_TaskTimeLimitMs = newLimitMs;
    }

    /**
     * Returns the set limit for time to spend on the task.
     *
     * @return The assigned task time limit.
     */
    long getTaskTimeLimit()
    {
        return( m_TaskTimeLimitMs );
    }

    /**
     * Determines if the Task is equal to another given Task.
     *
     * @param obj The other object to test for equality.
     * @return True if the Tasks are equal, false otherwise.
     */
    @Override
    public boolean equals( Object obj )
    {
        boolean isEqual = false;

        if( obj != null )
        {
            if( obj instanceof Task )
            {
                Task otherTask = (Task)obj;

                if( m_TaskName.equals( otherTask.getTaskName() ) )
                {
                    isEqual = true;
                }
            }
        }

        return( isEqual );
    }

    /**
     * Returns the hash code of the Task object.
     *
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode()
    {
        int result = 716;

        /* Factor in task's name (the string hash code) */
        result = 37 * result + m_TaskName.hashCode();

        return( result );
    }
}
