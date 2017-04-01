/**
 * Workday.java
 *
 * Characterizes a workday within the "ContextSwitch" feature; a workday consists of several iterations of a specific
 * number of tasks. If a user invokes a context switch, the current task is stopped and logged, and a new task is
 * marked as active. When the workday is complete, the user can analyze the data collected by the various context
 * switches.
 *
 * @author James Williamson
 * @version 0.3.0
 *
 * @formatter:off
 */

package com.jameswilliamson.teamlead;

import android.content.Context;

import java.text.DecimalFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;

public class Workday
{
    /* Private constants */
    private static final String TIMER_FIELD_FORMAT = "00"; /* Format used for presenting task time to the user */
    private static final int NUM_SPECIAL_TASKS = 2;        /* The number of "special tasks" in the Workday */

    /* Private member fields */
    private Context m_Context;                             /* The associated application context */
    private ArrayList<Task> m_Tasks;                       /* A set of all m_Tasks created by the user */
    private ArrayDeque<TaskIteration> m_TaskLog;           /* Sequence of task iterations that constitute the workday */
    private TaskIteration m_ActiveIteration;               /* The currently active task iteration */
    private DecimalFormat m_TimeFormatter;                 /* Formats task time for presentation to the user */

    /* Public constants */
    public static final int MS_PER_SEC = 1000;             /* Conversion constant for seconds <-> milliseconds */
    public static final int SECS_PER_MIN = 60;             /* Conversion constant for seconds <-> minutes */
    public static final int MINS_PER_HOUR = 60;            /* Conversion constant for minutes <-> hours */
    public static final int HOURS_PER_DAY = 24;            /* Conversion constant for hours <-> days */

    /**
     * Constructs the Workday object.
     *
     * @param c The associated application context.
     */
    Workday( Context c )
    {
        /* Initialize members */
        m_Context = c;
        m_Tasks = new ArrayList<>();
        m_TaskLog = new ArrayDeque<>();
        m_ActiveIteration = null;
        m_TimeFormatter = new DecimalFormat( TIMER_FIELD_FORMAT );

        /* Create the "add new" task; a special type of task that is only used to define a new custom user task */
        m_Tasks.add( new Task( m_Context.getString( R.string.add_task_label ) ) );

        /* Create the "end workday" task; a special type of task used to end the workday and generate output */
        m_Tasks.add( new Task( m_Context.getString( R.string.end_workday_label ) ) );
    }

    /**
     * Adds a new task type that may be performed throughout the Workday.
     *
     * @param newTask A new task to be performed.
     * @return An error code indicative of success or failure.
     */
    ErrorCode addTask( Task newTask )
    {
        ErrorCode taskErr = ErrorCode.ERR_NONE;

        if( m_Tasks.contains( newTask ) == false )
        {
            /*
             * Add the new task to the list of user-defined tasks, but rather than inserting them at the very end of
             * the list, insert them before the "special tasks" so that they are shown last on the UI.
             */
            m_Tasks.add( m_Tasks.size() - NUM_SPECIAL_TASKS, newTask );
        }
        else
        {
            taskErr = ErrorCode.ERR_TASK_DUPLICATE;
        }

        return( taskErr );
    }

    /**
     * Removes the given task from the Workday.
     *
     * @param task A task to remove from the Workday.
     * @return An error code indicative of success or failure.
     */
    ErrorCode deleteTask( Task task )
    {
        ErrorCode taskErr = ErrorCode.ERR_NONE;

        if( m_Tasks.contains( task ) == true )
        {
            /* Remove the task from the model */
            m_Tasks.remove( task );
        }
        else
        {
            taskErr = ErrorCode.ERR_TASK_INVALID;
        }

        return( taskErr );
    }

    /**
     * Returns the number of user-defined tasks that currently make up a Workday.
     *
     * @return The number of user-defined tasks added to the Workday.
     */
    public int getNumberOfUserTasks()
    {
        return( m_Tasks.size() - NUM_SPECIAL_TASKS );
    }

    /**
     * Returns the total number of tasks in the workday, including "special tasks," if applicable.
     * 
     * @return The number of all tasks in the Workday.
     */
    int getNumberOfTasks()
    {
        return( m_Tasks.size() );
    }

    /**
     * Returns the name of the task at the specified index.
     *
     * @param taskIndex The index of the task.
     * @return The name of the task.
     */
    String getTaskName( int taskIndex )
    {
        String taskName = "n/a";

        if( taskIndex < m_Tasks.size() )
        {
            taskName = m_Tasks.get( taskIndex ).getTaskName();
        }

        return( taskName );
    }

    /**
     * Returns the task at the specified index.
     *
     * @param taskIndex The index of the task.
     * @return The specified task.
     */
    Task getTask( int taskIndex )
    {
        Task task = null;

        if( taskIndex < m_Tasks.size() )
        {
            task = m_Tasks.get( taskIndex );
        }

        return( task );
    }

    /**
     * Starts performing a new task, specified by the given index. If a different task is currently being performed,
     * it is saved on the internal task log for later analysis, and the new task is marked as active.
     *
     * @param newTaskIndex The index of the task to begin.
     * @return An error code indicative of success or failure.
     */
    ErrorCode contextSwitch( int newTaskIndex )
    {
        ErrorCode taskErr = ErrorCode.ERR_NONE;

        /* Cannot perform context switch to a special task */
        if( newTaskIndex < ( m_Tasks.size() - NUM_SPECIAL_TASKS ) )
        {
            endTask();
            beginTask( newTaskIndex );
        }
        else
        {
            taskErr = ErrorCode.ERR_TASK_INVALID;
        }

        return( taskErr );
    }

    /**
     * Ends the workday, which stops the active task and creates visual output for analysis of the time spent.
     */
    void endWorkday()
    {
        endTask();

        // TODO: 4/1/2017 Save or export the data before clearing it.
    }

    /**
     * Returns the total amount of time spent performing the task, in "hh:mm:ss" format.
     *
     * @param taskIndex The index of the task to query.
     * @return The total amount of time spent performing the task, in UI-friendly hh:mm:ss format.
     */
    String getTaskRuntime( int taskIndex )
    {
        String timeSpent = "00:00:00";

        /* Boundary check */
        if( taskIndex < m_Tasks.size() )
        {
            /* If no task is active, cannot return an elapsed time */
            if( m_ActiveIteration != null )
            {
                /* Get the task associated with the index and query the runtime */
                Task task = m_Tasks.get( taskIndex );
                long taskRuntimeMs = this.getTotalTaskRuntimeMs( taskIndex );

                /* Convert total millisecond task time to hours, minutes, and seconds */
                int seconds = (int)( taskRuntimeMs / MS_PER_SEC );
                int minutes = ( seconds / SECS_PER_MIN );
                int hours = ( minutes / MINS_PER_HOUR );

                /* Adjust for remainder so that hh:mm:ss reported to user is cohesive */
                seconds = ( seconds % SECS_PER_MIN );
                minutes = ( minutes % MINS_PER_HOUR );

                /* Build string and return to user */
                timeSpent = m_TimeFormatter.format( hours );
                timeSpent = timeSpent.concat( ":" );
                timeSpent = timeSpent.concat( m_TimeFormatter.format( minutes ) );
                timeSpent = timeSpent.concat( ":" );
                timeSpent = timeSpent.concat( m_TimeFormatter.format( seconds ) );
            }
        }

        return( timeSpent );
    }

    /**
     * Returns the percentage of the Workday that was allocated to the given task.
     *
     * @param taskIndex The index of the task to query.
     * @return The percentage of the day spent on the specified task.
     */
    double getTaskPercentage( int taskIndex )
    {
        double percentage = 0.0;

        /* Boundary check */
        if( taskIndex < m_Tasks.size() )
        {
            double totalTaskRuntimeTally = 0.0;
            double specifiedTaskRuntime = (double)getTotalTaskRuntimeMs( taskIndex );

            /* Count up total task runtime, including only user tasks */
            for( int index = 0; index < getNumberOfUserTasks(); index++ )
            {
                totalTaskRuntimeTally += getTotalTaskRuntimeMs( index );
            }

            percentage = ( specifiedTaskRuntime / totalTaskRuntimeTally ) * 100.0;
        }

        return( percentage );
    }

    /**
     * Checks to see if a particular task limit has been exceeded.
     *
     * @param taskIndex The index of the task.
     * @return True if the task has exceeded it's configured limit time, false otherwise.
     */
    boolean isTaskLimitExceeded( int taskIndex )
    {
        boolean limitExceeded = false;

        /* Boundary check */
        if( taskIndex < m_Tasks.size() )
        {
            long taskTimeLimit = m_Tasks.get( taskIndex ).getTaskTimeLimit();

            if( taskTimeLimit != 0 )
            {
                /* Non-zero limit; compare it to current runtime */
                if( this.getTotalTaskRuntimeMs( taskIndex ) > taskTimeLimit )
                {
                    limitExceeded = true;
                }
            }
        }

        return( limitExceeded );
    }

    /**
     * Assigns a new color code to the specified task.
     *
     * @param taskIndex The index of the task.
     * @param newColor The new color code to associate with the task.
     */
    void setTaskColor( int taskIndex, int newColor )
    {
        /* Boundary check */
        if( taskIndex < m_Tasks.size() )
        {
            m_Tasks.get( taskIndex ).setTaskColor( newColor );
        }
    }

    /**
     * Returns the color code associated with the specified task.
     *
     * @param taskIndex The index of the task.
     * @return The task's color code.
     */
    int getTaskColor( int taskIndex )
    {
        /* Boundary check */
        if( taskIndex < m_Tasks.size() )
        {
            return( m_Tasks.get( taskIndex ).getTaskColor() );
        }
        else
        {
            return( 0 );
        }
    }

    /**
     * Clears the task log from the workday and resets all timing data to zero. The list of user tasks is left
     * intact.
     */
    private void resetWorkday()
    {
        /* Clear the task log */
        m_TaskLog.clear();

        /* Reset all timing data */
        for( Task task : m_Tasks )
        {
            task.resetRuntime();
        }
    }

    /**
     * Begins performing the task identified by the given index.
     *
     * @param taskIndex The index of the task to begin.
     */
    private void beginTask( int taskIndex )
    {
        /* Create a new iteration of the task and mark it as active */
        m_ActiveIteration = new TaskIteration( m_Tasks.get( taskIndex ) );

        /* Begin tracking time spent on the task */
        m_ActiveIteration.start();
    }

    /**
     * Ends the task currently being performed.
     */
    private void endTask()
    {
        if( m_ActiveIteration != null )
        {
            /* Stop the iteration */
            if( m_ActiveIteration.end() != ErrorCode.ERR_TASK_ALREADY_STOPPED )
            {
                /* Add the iteration's runtime to the task's running total */
                long iterationRuntimeMs = m_ActiveIteration.getRuntimeMs();
                m_ActiveIteration.getTask().addRuntimeMs( iterationRuntimeMs );

                /* Add the stopped active iteration to the queue */
                m_TaskLog.add( m_ActiveIteration );
            }
        }
    }

    /**
     * Returns the total task runtime, in milliseconds. If the task is currently active, the current iteration is
     * included as part of the calculation.
     *
     * @param taskIndex The index of the task.
     * @return The total runtime of the task, in milliseconds.
     */
    private long getTotalTaskRuntimeMs( int taskIndex )
    {
        long taskRuntimeMs = m_Tasks.get( taskIndex ).getRuntimeMs();

        if( m_ActiveIteration != null )
        {
            if( m_ActiveIteration.getTask().equals( m_Tasks.get( taskIndex ) ) )
            {
                /* The task being queried is currently active, so add the up-to-date time to the total */
                taskRuntimeMs += m_ActiveIteration.getRuntimeMs();
            }
        }

        return( taskRuntimeMs );
    }
}
