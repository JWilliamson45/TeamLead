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

import android.os.Environment;

import java.io.File;
import java.util.Calendar;

public class FileManager
{
    /**
     * Saves the given Workday data to a file.
     *
     * @param workday The Workday model to write to a file.
     * @return
     */
    public ErrorCode saveWorkdayToFile( Workday workday )
    {
        ErrorCode err;

        if( Environment.getExternalStorageState().equals( Environment.MEDIA_MOUNTED ) == false )
        {
            /* Unable to write file - storage is unavailable */
            err = ErrorCode.ERR_MEDIA_UNAVAILABLE;
        }
        else
        {
            File documentsDirectory = Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_DOCUMENTS );
            File workdayFile = new File( documentsDirectory, createWorkdayFileName() );

            if( workdayFile.mkdirs() == false )
            {
                /* Unable to make the requisite directory for storage */
                err = ErrorCode.ERR_FILE_WRITE;
            }
            else
            {
                /* Write the Workday data to the file */
                err = writeWorkdayDataToFile( workdayFile, workday );
            }
        }

        return( err );
    }

    /**
     * Writes the Workday data to the given file.
     *
     * @param workdayDataFile The file that will hold the Workday data.
     * @param workday The Workday model to write to the file.
     * @return An error code indicative of success or failure.
     */
    private ErrorCode writeWorkdayDataToFile( File workdayDataFile, Workday workday )
    {
        ErrorCode err = ErrorCode.ERR_NONE;

        /* TODO - finish implementation */

        return( err );
    }

    /**
     * Generates a file name for the Workday data file, based on the current date.
     *
     * @return The file name, formatted as "TL_Workday_MM_DD_YYYY"
     */
    private String createWorkdayFileName()
    {
        Calendar calendar = Calendar.getInstance();
        String fileName = "TL_Workday_";

        /* Name file using date information */
        fileName.concat( calendar.MONTH + "_" + calendar.DAY_OF_MONTH + "_" + calendar.YEAR + ".txt" );

        return( fileName );
    }
}
