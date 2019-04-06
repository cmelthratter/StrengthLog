package com.cmelthratter.strengthlog.ui.dialogs

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.support.v4.app.DialogFragment
import android.content.DialogInterface
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.cmelthratter.strengthlog.R
import com.cmelthratter.strengthlog.ui.activities.LiftActivity

/**
 * Created by Cody Melthratter on 7/19/2017.
 * For recieving input for a new set in an entry
 */

//TODO: fix this
class EntryInputDialog () : DialogFragment() {

    lateinit var text: TextInputLayout
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        log("Dialog created")
        val builder = AlertDialog.Builder(activity, R.style.DialogFragment)
        val title = TextView(context)
        val linf = LayoutInflater.from(this.context)
        val inflator = linf.inflate(R.layout.entry_input_layout_alt, null)
        title.setText(R.string.entry_input_title)
        builder.setCustomTitle(title)
        builder.setView(inflator)

        builder.setMessage(R.string.entry_input_desc)
                .setPositiveButton(R.string.lift_dialog_submit, DialogInterface.OnClickListener { _, _ ->
                    log("Positive button clicked")
                    val textVal = text.editText.toString()
                    if (!textVal.matches(Regex("([0-9]x)*([0-9]x)*[0-9]+@[0-9]"))) {
                        log("invalid input")
                        toast("Invalid input")
                    }
                    val numX = textVal.count({c -> c == 'x'})
                    var setsIndex = -1
                    if (numX == 2)
                        setsIndex =  textVal.indexOf ('x' )

                    var repsIndex = -1
                    if (numX > 0)
                        textVal.indexOf('x', setsIndex + 1)
                    var weightIndex = 0
                    if (numX > 0)
                        weightIndex = textVal.indexOf('x', repsIndex + 1)
                    val atSymbol = textVal.indexOf('@')
                    var setsVal = 1
                    if (numX == 2)
                        setsVal = textVal.substring(0, repsIndex - 1).toInt()
                    var repsVal = 1
                    if (numX > 0)
                        repsVal = textVal.substring(repsIndex + 1, weightIndex - 1).toInt()
                    val weightVal = textVal.substring(weightIndex + 1, atSymbol - 1).toFloat()
                    val rpeVal = textVal.substring(atSymbol + 1).toFloat()
                    mListener!!.onDialogPositiveClick(repsVal, setsVal, weightVal, rpeVal)

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
        text = this.dialog.findViewById(R.id.entry_input_string)


    }

    interface EntryDialogListener {
        fun onDialogPositiveClick(reps : Int?, sets : Int?, weight: Float?, rpe: Float?)
        fun onDialogNegativeClick()
    }

    // Use this instance of the interface to deliver action events
    var mListener: EntryDialogListener? = null

   //TODO: refactor this to use (remove?)
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        log("Activity attached: $activity")
        try {
            mListener = activity as EntryDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$activity must implement NoticeDialogListener")
        }

    }

    fun log(msg: String) {
        Log.i(LiftInputDialog::class.java.simpleName, msg)
    }
    /** displays a short Toast message
    * @param msg the message to display
    */
    private fun toast(msg: String) {
        Toast.makeText(this.context, msg, Toast.LENGTH_SHORT).show()
    }

}