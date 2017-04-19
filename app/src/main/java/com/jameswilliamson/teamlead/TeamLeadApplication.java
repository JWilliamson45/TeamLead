/**
 * TeamLeadApplication.java
 *
 * Represents the common state of the TeamLead application.
 *
 * @author James Williamson
 *
 * @formatter:off
 */

package com.jameswilliamson.teamlead;

import android.app.Application;
import android.support.v4.content.ContextCompat;

public class TeamLeadApplication extends Application
{
    /* Private member fields */
    private Workday m_UserWorkday;                           /* Models a user's workday */
    private int m_TaskTileRefreshRateMs;                     /* A refresh rate preference for updating the UI */

    /* Public constants */
    public static final int REFRESH_RATE_100_MS = 100;       /* Used to repaint the UI every tenth-second */
    public static final int REFRESH_RATE_500_MS = 500;       /* Used to repaint the UI every half-second */
    public static final int REFRESH_RATE_1000_MS = 1000;     /* Used to repaint the UI every second */
    public static final int REFRESH_RATE_5000_MS = 5000;     /* Used to repaint the UI every five seconds */

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
        Task SampleTask1 = new Task( "Code" );
        SampleTask1.setTaskColor( getResources().getColor( R.color.taskButtonDefaultBlueInactive ) );
        m_UserWorkday.addTask( SampleTask1 );

        /* TODO: 4/18/2017 Read preference data from appropriate location on startup */
        m_TaskTileRefreshRateMs = REFRESH_RATE_100_MS;
    }

    /**
     * Sets the new task tile refresh rate.
     *
     * @param refreshRateMs The task tile refresh rate to use, in milliseconds.
     */
    public synchronized void setTaskTileRefreshRate( int refreshRateMs )
    {
        m_TaskTileRefreshRateMs = refreshRateMs;
    }

    /**
     * Returns the selected task tile refresh rate.
     *
     * @return The selected task tile refresh rate, in milliseconds.
     */
    public synchronized int getTaskTileRefreshRate()
    {
        return( m_TaskTileRefreshRateMs );
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
}
