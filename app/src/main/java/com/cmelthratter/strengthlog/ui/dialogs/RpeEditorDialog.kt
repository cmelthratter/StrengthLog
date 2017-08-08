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
import android.widget.TextView
import com.cmelthratter.strengthlog.R
import com.cmelthratter.strengthlog.ui.activities.RPE

/**
 * Created by Cody Melthratter on 8/8/2017. A simple
 * dialog for editing the RPE of an entry
 */
class RpeEditorDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        log("Dialog created")
        val builder = AlertDialog.Builder(activity, R.style.DialogFragment)
        val title = TextView(context)
        val linf = LayoutInflater.from(this.context)
        val inflator = linf.inflate(R.layout.rpe_editor, null)
        title.setText(R.string.rpe_editor_title)
        builder.setCustomTitle(title)
        builder.setView(inflator)


        builder.setMessage(R.string.rpe_editor_desc)
                .setPositiveButton(R.string.lift_dialog_submit, DialogInterface.OnClickListener { _, _ ->
                    log("Positive button clicked")
                    val textInput = dialog.findViewById(R.id.rpe_editor_editText) as EditText

                    try {
                        mListener!!.onDialogPositiveClick(textInput.text.toString().toFloat(), RPE)

                    } catch(e: NumberFormatException) {
                        mListener!!.onDialogPositiveClick(0.0F, RPE)
                    }
                })
                .setNegativeButton(R.string.lift_dialog_cancel, DialogInterface.OnClickListener { dialog, _ ->
                    // User cancelled the dialog
                    dialog.cancel()
                })
        // Create the AlertDialog object and return it
        return builder.create()

    }

    interface EditorDialogListener {
        fun onDialogPositiveClick(newVal : Float, type: Int)
        fun onDialogNegativeClick()
    }


    // Use this instance of the interface to deliver action events
    var mListener: EditorDialogListener? = null

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        log("Activity attached: $activity")
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = activity as EditorDialogListener
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException(activity.toString() + " must implement RpeEditorDialogListener")
        }

    }

    fun log(msg: String) {
        Log.i(LiftInputDialog::class.java.simpleName, msg)
    }

}