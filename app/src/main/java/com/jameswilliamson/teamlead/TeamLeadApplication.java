/**
 * TeamLeadApplication.java
 *
 * Represents the common state of the TeamLead application.
 *
 * @author James Williamson
 * @version 0.2.0
 *
 * @formatter:off
 */

package com.jameswilliamson.teamlead;

import android.app.Application;
import android.support.v4.content.ContextCompat;

public class TeamLeadApplication extends Application
{
    /* Private member fields */
    private Workday m_UserWorkday;             /* Models a user's workday */
    private int m_ActiveButtonColor;           /* Setting for the color of a selected task button */
    private int m_InactiveButtonColor;         /* Setting for the color of an unselected task button */

    /**
     * Specifies tasks to be performed once the TeamLead application is started.
     */
    @Override
    public void onCreate()
    {
        super.onCreate();

        /* TODO: 2/18/2017 Attempt to read workday data from saved file; only create new if there's nothing to load */
        m_UserWorkday = new Workday( this );

        /* Create sample tasks */
        m_UserWorkday.addTask( new Task( "Code" ) );
        m_UserWorkday.addTask( new Task( "Design" ) );
        m_UserWorkday.addTask( new Task( "Test" ) );
        m_UserWorkday.addTask( new Task( "Plan" ) );
        m_UserWorkday.addTask( new Task( "Debug" ) );

        /* Apply preferences */
        // TODO: 3/1/2017 Load these from preferences - don't hard code  
        m_ActiveButtonColor = ContextCompat.getColor( getApplicationContext(), R.color.taskButtonBlueActive );
        m_InactiveButtonColor = ContextCompat.getColor( getApplicationContext(), R.color.taskButtonBlueInactive );
    }

    /**
     * Retrieves the user workday data model.
     *
     * @return The user workday object.
     */
    public synchronized Workday getWorkdayModel()
    {
        return( m_UserWorkday );
    }

    /**
     * Assigns new colors to the task buttons for both active and inactive state.
     *
     * @param activeColor The color to paint the button when it is selected by the user.
     * @param inactiveColor The color to paint the button when it is not selected by the user.
     */
    public synchronized void setTaskButtonColors( int activeColor, int inactiveColor )
    {
        m_ActiveButtonColor = activeColor;
        m_InactiveButtonColor = inactiveColor;
    }

    /**
     * Returns the color assigned to an active task button.
     *
     * @return The color of an active task button.
     */
    public synchronized int getTaskButtonActiveColor()
    {
        return( m_ActiveButtonColor );
    }

    /**
     * Returns the color assigned to an inactive task button.
     *
     * @return The color of an inactive task button.
     */
    public synchronized int getTaskButtonInactiveColor()
    {
        return( m_InactiveButtonColor );
    }
}
