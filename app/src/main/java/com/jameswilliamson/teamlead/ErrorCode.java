/**
 * ErrorCode.java
 *
 * Error codes that may be returned from various subsystems within the TeamLead application.
 *
 * @author James Williamson
 * @version 0.1.0
 */

package com.jameswilliamson.teamlead;


public enum ErrorCode
{
    ERR_NONE( 0, "No error" ),
    ERR_TASK_ALREADY_STARTED( 1, "The selected task is already started" ),
    ERR_TASK_ALREADY_STOPPED( 2, "The selected task is already stopped" );

    // Enumeration constants
    private final int m_errCode;
    private final String m_Description;

    /**
     * Constructs the enum and assigns it the given code and description.
     *
     * @param errCode The unique error code handle
     * @param description A string description of the associated error
     */
    private ErrorCode( int errCode, String description )
    {
        m_errCode = errCode;
        m_Description = description;
    }

    /**
     * Returns the string description of the associated error.
     *
     * @return The error string description
     */
    public String getDescription()
    {
        return( m_Description );
    }

    /**
     * Returns the unique error code handle.
     *
     * @return The error code handle
     */
    public int getCode()
    {
        return( m_errCode );
    }

    /**
     * Provides a string representation of the error enum.
     *
     * @return A string containing the error code number and string description
     */
    @Override
    public String toString()
    {
        return( "[" + m_errCode + "] " + m_Description );
    }
}