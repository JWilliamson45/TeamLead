/**
 * Task.java
 *
 * Models an individual task that a team leader may perform in a typical workday.
 *
 * @author James Williamson
 * @version 0.2.0
 */

package com.jameswilliamson.teamlead;


public class Task
{
    // Private member fields
    private String m_TaskName;                         // The name of the task
    private boolean m_TaskActive;                      // Marks whether or not this task is currently active
    private long m_TotalTaskRuntimeMs;                 // Total time spent on all previous iterations of the task

    /**
     * Constructs a new task and gives it a name.
     *
     * @param taskName The name of the task
     */
    public Task( String taskName )
    {
        m_TaskName = taskName;
        m_TaskActive = false;
        m_TotalTaskRuntimeMs = 0;
    }

    /**
     * Sets a new name for the task.
     *
     * @return The new name for the task
     */
    public void setTaskName( String taskName )
    {
        m_TaskName = taskName;
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
     * Marks the task as currently active.
     */
    public void setAsActive()
    {
        m_TaskActive = true;
    }

    /**
     * Marks the task as currently inactive.
     */
    public void setAsInactive()
    {
        m_TaskActive = false;
    }

    /**
     * Returns whether or not the task is active.
     *
     * @return True if the task is active, false otherwise
     */
    public boolean isActive()
    {
        return( m_TaskActive );
    }

    /**
     * Adds the given millisecond count to the total overall runtime of the task.
     *
     * @param runtimeMs The amount of runtime to add to the task, in milliseconds
     */
    public void addRuntimeMs( long runtimeMs )
    {
        m_TotalTaskRuntimeMs += runtimeMs;
    }

    /**
     * Returns the total runtime of all previous iterations of the task.
     *
     * @return The runtime of all previous iterations of the task, in milliseconds
     */
    public long getRuntimeMs()
    {
        return( m_TotalTaskRuntimeMs );
    }

    /**
     * Determines if the Task is equal to another given Task.
     *
     * @param obj The other object to test for equality
     * @return True if the Tasks are equal, false otherwise
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
     * @return A hash code value for this object
     */
    @Override
    public int hashCode()
    {
        int result = 716;

        // Factor in task's name (the string hash code)
        result = 37 * result + m_TaskName.hashCode();

        return( result );
    }
}
