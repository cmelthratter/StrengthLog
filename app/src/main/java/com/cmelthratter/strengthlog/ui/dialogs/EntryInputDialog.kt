package com.cmelthratter.strengthlog.ui.dialogs

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.TextView
import com.cmelthratter.strengthlog.R

/**
 * Created by Cody Melthratter on 7/19/2017.
 * For recieving input for a new set in an entry
 */


class EntryInputDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        log("Dialog created")
        val builder = AlertDialog.Builder(activity, R.style.DialogFragment)
        val title = TextView(context)
        val linf = LayoutInflater.from(this.context)
        val inflator = linf.inflate(R.layout.entry_input_layout, null)
        title.setText(R.string.entry_input_title)
        builder.setCustomTitle(title)
        builder.setView(inflator)

        builder.setMessage(R.string.entry_input_desc)
                .setPositiveButton(R.string.lift_dialog_submit, DialogInterface.OnClickListener { _, _ ->
                    log("Positive button clicked")
                    val reps = this@EntryInputDialog.dialog.findViewById(R.id.reps_editText) as EditText
                    val weight = this@EntryInputDialog.dialog.findViewById(R.id.weight_editText) as EditText
                    mListener!!.onDialogPositiveClick(reps.text.toString().toInt(), weight.text.toString().toFloat() )

                })
                .setNegativeButton(R.string.lift_dialog_cancel, DialogInterface.OnClickListener { dialog, _ ->
                    // User cancelled the dialog
                    dialog.cancel()
                })
        // Create the AlertDialog object and return it
        return builder.create()

    }

    interface EntryDialogListener {
        fun onDialogPositiveClick(reps : Int?, weight: Float?)
        fun onDialogNegativeClick()
    }

    // Use this instance of the interface to deliver action events
    var mListener: EntryDialogListener? = null

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        log("Activity attached: $activity")
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = activity as EntryDialogListener
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException(activity.toString() + " must implement NoticeDialogListener")
        }

    }

    fun log(msg: String) {
        Log.i(LiftInputDialog::class.java.simpleName, msg)
    }
}