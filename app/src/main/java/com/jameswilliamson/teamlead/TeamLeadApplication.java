/**
 * TeamLeadApplication.java
 *
 * Represents the common state of the TeamLead application.
 *
 * @author James Williamson
 * @version 0.3.0
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
