package com.cmelthratter.strengthlog.ui.dialogs

import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.content.DialogInterface
import android.app.AlertDialog
import android.widget.EditText
import android.widget.TextView

import com.cmelthratter.strengthlog.R
import android.app.Activity
import android.util.Log
import android.view.LayoutInflater



/**
 * Created by Cody Melthratter on 7/15/2017.
 * For receiving input for a new Lift in the LiftActivity
 */

class RepsEditorDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        log("Dialog created")
        val builder = AlertDialog.Builder(activity, R.style.DialogFragment)
        val title = TextView(context)
        val linf = LayoutInflater.from(this.context)
        val inflator = linf.inflate(R.layout.reps_editor_layout, null)
        title.setText(R.string.reps_editor_title)
        builder.setCustomTitle(title)
        builder.setView(inflator)

        builder.setMessage(R.string.reps_editor_desc)
                .setPositiveButton(R.string.lift_dialog_submit, DialogInterface.OnClickListener { _, _ ->
                    log("Positive button clicked")
                    val textInput = dialog.findViewById(R.id.reps_editor_editText) as EditText
                    mListener!!.onDialogPositiveClick(textInput.text.toString().toInt())

                })
                .setNegativeButton(R.string.lift_dialog_cancel, DialogInterface.OnClickListener { dialog, _ ->
                    // User cancelled the dialog
                    dialog.cancel()
                })
        // Create the AlertDialog object and return it
        return builder.create()

    }

    interface RepsEditorDialogListener {
        fun onDialogPositiveClick(newReps : Int)
        fun onDialogNegativeClick()
    }

    // Use this instance of the interface to deliver action events
    var mListener: RepsEditorDialogListener? = null

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        log("Activity attached: $activity")
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = activity as RepsEditorDialogListener
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException(activity.toString() + " must implement NoticeDialogListener")
        }

    }

    fun log(msg: String) {
        Log.i(LiftInputDialog::class.java.simpleName, msg)
    }

}