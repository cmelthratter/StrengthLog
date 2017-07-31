package com.cmelthratter.strengthlog.ui.dialogs

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import com.cmelthratter.strengthlog.R

/**
 * Created by Cody Melthratter on 7/31/2017.
 * Simple dialog to confirm the deletion of
 * a Lift or Entry
 */

const val LIFT = "Lift"
const val ENTRY = "Entry"

class DeleteConfirmDialog(val toDelete: String = LIFT) : DialogFragment() {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        log("Dialog created")
        val builder = AlertDialog.Builder(activity, R.style.DialogFragment)
        val title = TextView(context)
        val linf = LayoutInflater.from(this.context)

        title.text = (String.format(getString(R.string.delete_dialog_title), toDelete))
        title.setBackgroundColor(context.getColor(R.color.colorBackground))
        title.setTextColor(context.getColor(R.color.actionBarText))
        builder.setCustomTitle(title)

        builder.setMessage(String.format(getString(R.string.delete_dialog_desc, toDelete)))
                .setPositiveButton(R.string.lift_dialog_submit, DialogInterface.OnClickListener { _, _ ->
                    log("Positive button clicked")
                    mListener!!.onDialogPositiveClick()

                })
                .setNegativeButton(R.string.lift_dialog_cancel, DialogInterface.OnClickListener { dialog, _ ->
                    // User cancelled the dialog
                    mListener!!.onDialogNegativeClick()
                    dialog.cancel()
                })
        // Create the AlertDialog object and return it
        return builder.create()

    }

    interface DeleteDialogListener {
        fun onDialogPositiveClick()
        fun onDialogNegativeClick()
    }

    // Use this instance of the interface to deliver action events
    var mListener: DeleteDialogListener? = null

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        log("Activity attached: $activity")
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = activity as DeleteDialogListener
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException(activity.toString() + " must implement NoticeDialogListener")
        }

    }

    fun log(msg: String) {
        Log.i(LiftInputDialog::class.java.simpleName, msg)
    }
}
