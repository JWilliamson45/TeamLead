/**
 * FileManager.java
 *
 * TODO: this file is incomplete
 *
 * @author James Williamson
 *
 * @formatter:off
 */

package com.jameswilliamson.teamlead;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;

public class FileManager
{
    /* Private member fields */
    private Context m_AppContext;              /* The associated application context */
    private File m_Directory;                  /* The directory where files used by the application are stored */
    private File m_ActiveWorkdayFile;          /* A file used to store a Workday model in case something goes wrong */

    /**
     * Constructs the file manager.
     *
     * @param appContext The associated application context.
     */
    public FileManager( Context appContext )
    {
        m_AppContext = appContext;
        m_Directory = Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_DOCUMENTS );

        /* The "active workday" file will always reside in the documents folder with the same handle */
        m_ActiveWorkdayFile = new File( m_Directory, m_AppContext.getResources().
                getString( R.string.active_workday_file_name ) );
    }

    /**
     * Saves the given Workday data to a file. Workday files should be saved under two conditions:
     * 1) Workday is complete and needs to be archived on the device storage
     * 2) The Workday is active but the application closed or was killed, and the data should be recovered when the
     *    app is restarted
     *
     * @param workday The Workday model to write to a file.
     * @param completed True if the Workday is completed (inactive), false if it is in progress (active).
     * @throws IOException If the write operation fails.
     */
    public void saveWorkday( Workday workday, boolean completed ) throws IOException
    {
        if( Environment.getExternalStorageState().equals( Environment.MEDIA_MOUNTED ) == false )
        {
            /* Unable to write file - storage is unavailable */
            throw new IOException();
        }
        else
        {
            File workdayFile;

            if( completed == true )
            {
                /* The Workday was properly concluded, so save the file using a generated handle */
                workdayFile = new File( m_Directory, createWorkdayFileName() );
            }
            else
            {
                /* The workday is in progress, so save the file using a special handle that indicates this */
                workdayFile = m_ActiveWorkdayFile;
            }

            if( workdayFile.mkdirs() == false )
            {
                /* Unable to make the requisite directory for storage */
                throw new IOException();
            }
            else
            {
                /* Write the Workday data to the file */
                writeWorkdayDataToFile( workdayFile, workday );
                Log.d( "Info", "WORKDAY FILE CREATED AT: " + workdayFile.getAbsolutePath() );
            }
        }
    }

    /**
     * Loads the given Workday data from the file. Workday files can be read in two scenarios:
     * 1) The user wishes to browse archived Workday records.
     * 2) The app crashed/closed and the user wants to retrieve the saved Workday.
     *
     * @param fileName The name of the file to read, if applicable.
     * @param completed True if the Workday was completed normally, false if it was used to save state of an active
     *                  Workday. If false, the fileName argument is not used.
     * @return The Workday object read from the file.
     * @throws IOException If the read operation fails or the file does not exist.
     * @throws ClassNotFoundException If the class of the serialized Workday cannot be found.
     */
    public Workday loadWorkday( String fileName, boolean completed ) throws IOException, ClassNotFoundException
    {
        Workday workday;

        if( completed == true )
        {
            // TODO: 4/23/2017 Implement the feature that allows users to retrieve archived workday records.
            workday = null;
        }
        else
        {
            workday = readActiveWorkdayFromFile();
        }

        return( workday );
    }

    /**
     * Checks to see if an preserved (active) Workday file exists on the filesystem.
     *
     * @return True if there is a preserved Workday file available to read, false otherwise.
     */
    public boolean preservedWorkdayExists()
    {
        return( m_ActiveWorkdayFile.exists() );
    }

    /**
     * Deletes the preserved (active) Workday file, if one exists.
     *
     * @return True if the deletion was successful, false otherwise.
     */
    public boolean deletePreservedWorkday()
    {
        return( m_ActiveWorkdayFile.delete() );
    }

    /**
     * Writes the Workday data to the given file.
     *
     * @param workdayDataFile The file that will hold the Workday data.
     * @param workday The Workday model to write to the file.
     * @throws IOException If the write operation fails.
     */
    private void writeWorkdayDataToFile( File workdayDataFile, Workday workday ) throws IOException
    {
        if( ( workdayDataFile != null ) && ( workday != null ) )
        {
            /* Set up the streams required */
            FileOutputStream fileStream = new FileOutputStream( workdayDataFile, false );
            ObjectOutputStream objectStream = new ObjectOutputStream( fileStream );

            /* Write the serializable workday object */
            objectStream.writeObject( workday );

            /* Clean up */
            objectStream.close();
            fileStream.close();
        }
        else
        {
            /* If the file or Workday are null, just treat it as an IO Exception */
            throw new IOException();
        }
    }

    /**
     * Attempts to read the active Workday file, so that the Workday state may be restored.
     *
     * @return The restored Workday object for application use.
     * @throws IOException If the read operation fails or the file does not exist.
     * @throws ClassNotFoundException If the class of the serialized Workday cannot be found.
     */
    private Workday readActiveWorkdayFromFile() throws IOException, ClassNotFoundException
    {
        /* Set up the streams required */
        FileInputStream fileStream = new FileInputStream( m_ActiveWorkdayFile );
        ObjectInputStream objectStream = new ObjectInputStream( fileStream );

        /* Read the serializable workday object */
        Workday restoredWorkday = (Workday)objectStream.readObject();

        /* Clean up */
        objectStream.close();
        fileStream.close();

        return( restoredWorkday );
    }

    /**
     * Generates a file name for the Workday data file, based on the current date.
     *
     * @return The file name, formatted as "TL_Workday_MM_DD_YYYY"
     */
    private String createWorkdayFileName()
    {
        Calendar calendar = Calendar.getInstance();
        String fileName = m_AppContext.getResources().getString( R.string.completed_workday_file_prefix );

        /* Name file using date information */
        fileName.concat( calendar.MONTH + "_" + calendar.DAY_OF_MONTH + "_" + calendar.YEAR + ".txt" );

        return( fileName );
    }
}
