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
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater



/**
 * Created by Cody Melthratter on 7/15/2017.
 * For receiving input for a new Lift in the LiftActivity
 */

class LiftInputDialog(val newLift: Boolean = true, val placeholder: String = "") : DialogFragment() {
    lateinit var textInput : EditText
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        log("Dialog created")
        val builder = AlertDialog.Builder(activity, R.style.DialogFragment)
        val title = TextView(context)
        val linf = LayoutInflater.from(this.context)
        val inflator = linf.inflate(R.layout.lift_dialog, null)

        if (newLift) title.setText(R.string.lift_input_dialog_title)
        else title.text = getString(R.string.edit_lift_title)
        builder.setCustomTitle(title)
        builder.setView(inflator)


        builder.setMessage(R.string.lift_input_dialog_message)
                .setPositiveButton(R.string.lift_dialog_submit, DialogInterface.OnClickListener { _, _ ->
                    log("Positive button clicked")
                    if (textInput != null)
                        mListener!!.onDialogPositiveClick(textInput.text.toString(), newLift)

                })
                .setNegativeButton(R.string.lift_dialog_cancel, DialogInterface.OnClickListener { dialog, _ ->
                    // User cancelled the dialog
                    dialog.cancel()
                })
        // Create the AlertDialog object and return it
        return builder.create()

    }

    override fun onStart() {
        super.onStart()
        textInput = this.dialog.findViewById(R.id.lift_input) as EditText
        textInput.text = Editable.Factory.getInstance().newEditable(String.format(getString(R.string.placeholder), placeholder))
        textInput.setTextColor(context.getColor(R.color.actionBarText))
    }

    interface LiftDialogListener {
        fun onDialogPositiveClick(newLift : String, isNewLift : Boolean)
        fun onDialogNegativeClick(dialog: DialogFragment)
    }

    // Use this instance of the interface to deliver action events
    var mListener: LiftDialogListener? = null

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        log("Activity attached: $activity")
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = activity as LiftDialogListener
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException(activity.toString() + " must implement NoticeDialogListener")
        }

    }

    fun log(msg: String) {
        Log.i(LiftInputDialog::class.java.simpleName, msg)
    }

}