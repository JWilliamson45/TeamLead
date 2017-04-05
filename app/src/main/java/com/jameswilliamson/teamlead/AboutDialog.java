/**
 * About.java
 *
 * Displays a dialog containing information about the application.
 *
 * @author James Williamson
 *
 * @formatter:off
 */

package com.jameswilliamson.teamlead;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

public class AboutDialog extends DialogFragment
{
    /* Private constants */
    public final static String TAG = "About";

    /**
     * Builds the dialog's container.
     *
     * @param savedInstanceState The last saved instance state of the Fragment, or null if this is a freshly
     *                           created Fragment.
     * @return Returns a new Dialog instance to be displayed by the Fragment.
     */
    @Override
    public Dialog onCreateDialog( Bundle savedInstanceState )
    {
        AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );

        builder.setTitle( getResources().getString( R.string.about_dialog_title ) );

        /* Display author and version information */
        builder.setMessage( getResources().getString( R.string.about_dialog_author ) + " " +
                            getResources().getString( R.string.app_author ) + "\n" +
                            getResources().getString( R.string.about_dialog_version ) + ": " +
                            getResources().getString( R.string.app_version ) + "\n\n" +
                            getResources().getString( R.string.app_url ) );

        /* Allows user to dismiss the dialog */
        builder.setPositiveButton( R.string.ok_button_label, new CloseDialogButtonListener() );

        return( builder.create() );
    }

    /**
     * Button listener for the dialog fragment.
     */
    private class CloseDialogButtonListener implements DialogInterface.OnClickListener
    {
        /**
         * Called when the view has been clicked.
         *
         * @param dialog The dialog that received the click.
         * @param which The button that was clicked, or the position of the item clicked.
         */
        @Override
        public void onClick( DialogInterface dialog, int which )
        {
            dialog.cancel();
        }
    }
}
