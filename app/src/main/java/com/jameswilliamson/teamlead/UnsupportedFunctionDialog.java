/**
 * UnsupportedFunctionDialog.java
 *
 * Used to alert a user of an attempt at an unsupported operation.
 *
 * @author James Williamson
 * @version 0.2.0
 *
 * @formatter:off
 */

package com.jameswilliamson.teamlead;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

public class UnsupportedFunctionDialog extends DialogFragment
{
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
        /* Construct a dialog to inform the user that the operation is unsupported. */
        AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );

        builder.setTitle( getResources().getString( R.string.invalid_function_dialog_title ) );
        builder.setMessage( getResources().getString( R.string.invalid_function_dialog_msg ) );

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
