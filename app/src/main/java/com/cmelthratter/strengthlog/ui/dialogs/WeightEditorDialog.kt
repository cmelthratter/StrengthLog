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
import com.cmelthratter.strengthlog.ui.activities.RPE
import com.cmelthratter.strengthlog.ui.activities.WEIGHT


/**
 * Created by Cody Melthratter on 7/15/2017.
 * For receiving input for a new Lift in the LiftActivity
 */

class WeightEditorDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        log("Dialog created")
        val builder = AlertDialog.Builder(activity, R.style.DialogFragment)
        val title = TextView(context)
        val linf = LayoutInflater.from(this.context)
        val inflator = linf.inflate(R.layout.weight_editor_layout, null)
        title.setText(R.string.weight_editor_title)
        builder.setCustomTitle(title)
        builder.setView(inflator)

        builder.setMessage(R.string.weight_editor_desc)
                .setPositiveButton(R.string.lift_dialog_submit, DialogInterface.OnClickListener { _, _ ->
                    log("Positive button clicked")
                    val textInput = dialog.findViewById(R.id.weight_editor_editText) as EditText
                    try {
                        mListener!!.onDialogPositiveClick(textInput.text.toString().toFloat(), WEIGHT)
                    }catch(e : NumberFormatException) {
                        mListener!!.onDialogPositiveClick(0.0F, WEIGHT)
                    }
                })
                .setNegativeButton(R.string.lift_dialog_cancel, DialogInterface.OnClickListener { dialog, _ ->
                    // User cancelled the dialog
                    dialog.cancel()
                })
        // Create the AlertDialog object and return it
        return builder.create()

    }


    // Use this instance of the interface to deliver action events
    var mListener: RpeEditorDialog.EditorDialogListener? = null

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        log("Activity attached: $activity")
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = activity as RpeEditorDialog.EditorDialogListener
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException(activity.toString() + " must implement NoticeDialogListener")
        }

    }

    fun log(msg: String) {
        Log.i(LiftInputDialog::class.java.simpleName, msg)
    }

}