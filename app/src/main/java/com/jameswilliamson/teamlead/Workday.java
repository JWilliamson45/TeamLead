/**
 * Workday.java
 *
 * Characterizes a workday within the "ContextSwitch" feature; a workday consists of several iterations of a specific
 * number of tasks. If a user invokes a context switch, the current task is stopped and logged, and a new task is
 * marked as active. When the workday is complete, the user can analyze the data collected by the various context
 * switches.
 *
 * @author James Williamson
 * @version 0.2.0
 *
 * @formatter:off
 */

package com.jameswilliamson.teamlead;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;

public class Workday implements Serializable
{
    // Public constants
    public static final String ADD_TASK_LABEL = "Add\n\n+";   // The label to be shown for the "add new" task button

    // Private constants
    private final String TIMER_FIELD_FORMAT = "00";           // Format used for presenting task time to the user
    private final int MS_PER_SEC = 1000;                      // Conversion constant for seconds <-> milliseconds
    private final int SEC_PER_MIN = 60;                       // Conversion constant for seconds <-> minutes
    private final int MIN_PER_HOUR = 60;                      // Conversion constant for minutes <-> hours

    // Private member fields
    private ArrayList<Task> m_Tasks;                   // A set of all m_Tasks created by the user
    private ArrayDeque<TaskIteration> m_TaskLog;       // A sequence of task iterations that compose the entire workday
    private TaskIteration m_ActiveIteration;           // The currently active task iteration
    private DecimalFormat m_TimeFormatter;             // Formats task time for presentation to the user

    /**
     * Constructs the Workday object.
     */
    public Workday()
    {
        // Initialize members
        m_Tasks = new ArrayList<Task>();
        m_TaskLog = new ArrayDeque<TaskIteration>();
        m_ActiveIteration = null;
        m_TimeFormatter = new DecimalFormat( TIMER_FIELD_FORMAT );

        // Create the "add new" task; a special type of task that is only used to define a new custom user task
        m_Tasks.add( new Task( ADD_TASK_LABEL ) );
    }

    /**
     * Adds a new task type that may be performed throughout the Workday.
     *
     * @param newTask A new task to be performed
     * @return An error code indicative of success or failure
     */
    public ErrorCode addTask( Task newTask )
    {
        ErrorCode taskErr = ErrorCode.ERR_NONE;

        if( m_Tasks.contains( newTask ) == false )
        {
            // Add the new task to the second to last index; the "add new" task should always be last
            m_Tasks.add( m_Tasks.size() - 1, newTask );
        }
        else
        {
            taskErr = ErrorCode.ERR_TASK_DUPLICATE;
        }

        return( taskErr );
    }

    /**
     * Returns the number of user-defined tasks that currently make up a Workday.
     *
     * @return The number of user-defined tasks added to the Workday
     */
    public int getNumberOfUserTasks()
    {
        // TODO: 2/18/2017 Consider adding a "user-defined" field to the Task to make this scalable
        return( m_Tasks.size() - 1 );
    }

    /**
     * Returns the total number of tasks in the workday, including "special tasks," if applicable.
     * 
     * @return The number of all tasks in the Workday
     */
    public int getNumberOfTasks()
    {
        return( m_Tasks.size() );
    }

    /**
     * Returns the name of the task at the specified index.
     *
     * @param taskIndex The index of the task
     * @return The name of the task
     */
    public String getTaskName( int taskIndex )
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
     * @param taskIndex The index of the task
     * @return The specified task
     */
    public Task getTask( int taskIndex )
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
     * @param newTaskIndex The index of the task to begin
     * @return An error code indicative of success or failure
     */
    public ErrorCode contextSwitch( int newTaskIndex )
    {
        ErrorCode taskErr = ErrorCode.ERR_NONE;

        if( newTaskIndex < ( m_Tasks.size() - 1 ) )
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
    public void endWorkday()
    {
        endTask();

        // TODO: 2/12/2017 Add feature that creates a graph using the task log.
    }

    /**
     * Returns the total amount of time spent performing the task, in "hh:mm:ss" format.
     *
     * @param taskIndex The index of the task to query
     * @return The total amount of time spent performing the task, in UI-friendly hh:mm:ss format
     */
    public String getTaskRuntime( int taskIndex )
    {
        String timeSpent = "00:00:00";

        // Boundary check
        if( taskIndex < m_Tasks.size() )
        {
            // If no task is active, cannot return an elapsed time
            if( m_ActiveIteration != null )
            {
                // Get the task associated with the index and query the runtime
                Task task = m_Tasks.get( taskIndex );
                long taskRuntimeMs = task.getRuntimeMs();

                if( m_ActiveIteration.getTask().equals( task ) )
                {
                    // The task being queried is currently active, so add the up-to-date time to the total
                    taskRuntimeMs += m_ActiveIteration.getRuntimeMs();
                }

                // Convert total millisecond task time to hours, minutes, and seconds
                int seconds = (int)( taskRuntimeMs / MS_PER_SEC );
                int minutes = ( seconds / SEC_PER_MIN );
                int hours = ( minutes / MIN_PER_HOUR );

                // Adjust for remainder so that hh:mm:ss reported to user is cohesive
                seconds = ( seconds % SEC_PER_MIN );
                minutes = ( minutes % MIN_PER_HOUR );

                // Build string and return to user
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
     * Begins performing the task identified by the given index.
     *
     * @param taskIndex The index of the task to begin
     */
    private void beginTask( int taskIndex )
    {
        // Create a new iteration of the task and mark it as active
        m_ActiveIteration = new TaskIteration( m_Tasks.get( taskIndex ) );

        // Begin tracking time spent on the task
        m_ActiveIteration.start();
    }

    /**
     * Ends the task currently being performed.
     */
    private void endTask()
    {
        if( m_ActiveIteration != null )
        {
            // Stop the iteration
            m_ActiveIteration.end();

            // Add the iteration's runtime to the task's running total
            long iterationRuntimeMs = m_ActiveIteration.getRuntimeMs();
            m_ActiveIteration.getTask().addRuntimeMs( iterationRuntimeMs );

            // Add the stopped active iteration to the queue
            m_TaskLog.add( m_ActiveIteration );
        }
    }
}
