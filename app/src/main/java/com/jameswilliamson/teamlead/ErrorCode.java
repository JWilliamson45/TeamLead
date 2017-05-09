/**
 * ErrorCode.java
 *
 * Error codes that may be returned from various subsystems within the TeamLead application.
 *
 * @author James Williamson
 *
 * @formatter:off
 */

package com.jameswilliamson.teamlead;


enum ErrorCode
{
    ERR_NONE( 0, "No error" ),
    ERR_TASK_ALREADY_STARTED( 1, "The selected task is already started" ),
    ERR_TASK_ALREADY_STOPPED( 2, "The selected task is already stopped" ),
    ERR_TASK_INVALID( 3, "The specified task is invalid" ),
    ERR_TASK_DUPLICATE( 4, "The specified task has already been created" ),
    ERR_MEDIA_UNAVAILABLE( 5, "Unable to write file; media is busy or unavailable" ),
    ERR_FILE_WRITE( 6, "Unable to write file; a problem occurred with the operation" ),
    ERR_FILE_READ( 7, "Unable to read file; a problem occurred with the operation" );

    /* Enumeration constants */
    private final int m_errCode;
    private final String m_Description;

    /**
     * Constructs the enum and assigns it the given code and description.
     *
     * @param errCode The unique error code handle.
     * @param description A string description of the associated error.
     */
    ErrorCode( int errCode, String description )
    {
        m_errCode = errCode;
        m_Description = description;
    }

    /**
     * Returns the string description of the associated error.
     *
     * @return The error string description.
     */
    public String getDescription()
    {
        return( m_Description );
    }

    /**
     * Returns the unique error code handle.
     *
     * @return The error code handle.
     */
    public int getCode()
    {
        return( m_errCode );
    }

    /**
     * Provides a string representation of the error enum.
     *
     * @return A string containing the error code number and string description.
     */
    @Override
    public String toString()
    {
        return( "[" + m_errCode + "] " + m_Description );
    }
}
