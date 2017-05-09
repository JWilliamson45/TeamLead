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
import android.util.Log;

import java.io.IOException;

public class TeamLeadApplication extends Application
{
    /* Private member fields */
    private Workday m_UserWorkday;                           /* Models a user's workday */
    private FileManager m_FileManager;                       /* Used to save/load workday into persistent storage */
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

        /* Instantiate the application's file manager */
        m_FileManager = new FileManager( this.getApplicationContext() );

        /* Initialize the user workday data */
        initializeWorkday();

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

    /**
     * Saves the application's current Workday model to persistent storage.
     *
     * @throws IOException If the saving of the Workday to persistent storage fails.
     */
    public synchronized void saveWorkday() throws IOException
    {
        /* TODO: debug this feature */
         //m_FileManager.saveWorkday( m_UserWorkday, false );
    }

    /**
     * Initialize the Workday data model. If the application was stopped/killed before the user properly "ended"
     * the workday, the application will attempt to recover the content from a file saved to the system.
     */
    private void initializeWorkday()
    {
        try
        {
            /* Check to see if there is a Workday model saved to the filesystem */
            if( m_FileManager.preservedWorkdayExists() == true )
            {
                /* There is a saved active Workday record - restore it */
                m_UserWorkday = m_FileManager.loadWorkday( null, false );

                /* Now, delete the file; it will be written again later if necessary */
                m_FileManager.deletePreservedWorkday();
                Log.d( "Info", "WORKDAY FILE LOADED SUCCESSFULLY" );
            }
            else
            {
                /* Nothing to load, so create a new Workday */
                m_UserWorkday = new Workday( this );
                Log.d( "Info", "CREATED A NEW WORKDAY" );
            }
        }
        catch( ClassNotFoundException cnf_exception )
        {
            /* A problem occurred and the data could not be loaded */
            // TODO: 4/23/2017 Log appropriately
            m_FileManager.deletePreservedWorkday();
        }
        catch( IOException io_exception )
        {
            /* A problem occurred and the data could not be loaded */
            // TODO: 4/23/2017 Log appropriately
            m_FileManager.deletePreservedWorkday();
        }
    }
}
