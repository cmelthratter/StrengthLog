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
import java.util.*

/**
 * Created by Cody Melthratter on 7/19/2017.
 * For recieving input for a new set in an entry
 */

//TODO: fix this
class EntryInputDialog () : DialogFragment() {
    val TAG = LiftInputDialog::class.java.simpleName
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
                    val textVal = text.editText!!.text.toString()
                    log(textVal)
                    var weightVal = 0.0F
                    var rpeVal = 0.0F
                    var repsVal = 0
                    var setsVal = 0
                    try {
                        if (!textVal.matches(Regex("([0-9]*x){0,2}[0-9]+@[0-9]")))
                            throw InputMismatchException("Invalid input")


                        if (!textVal.contains('x') && textVal.contains('@')) {//this format: [weight]@[RPE]; sets and reps == 1
                            val weightAndRPE = textVal.split('@')
                            weightVal = weightAndRPE[0].toFloat()
                            rpeVal = weightAndRPE[1].toFloat()
                            repsVal = 1
                            setsVal = 1

                        } else {
                            var tokens = textVal.split('x')

                            //TODO: allow user to choose the notation in settings menu
                            if (tokens.size == 3) {//This format: [weight]x[reps]x[sets]@[RPE]
                                weightVal = tokens[0].toFloat()
                                repsVal = tokens[1].toInt()
                                val setsAndRPE = tokens[2].split('@')
                                setsVal = setsAndRPE[0].toInt()
                                rpeVal = setsAndRPE[1].toFloat()
                            } else if (tokens.size == 2) {//This format: [weight]x[reps]@[RPE]; sets == 1
                                weightVal = tokens[0].toFloat()
                                val repsAndRPE = tokens[1].split('@')
                                repsVal = repsAndRPE[0].toInt()
                                rpeVal = repsAndRPE[1].toFloat()
                                setsVal = 1
                            } else if (tokens.size > 3 || tokens.isEmpty()) {
                                throw InputMismatchException("invalid input")
                            }
                        }
                    } catch (e: InputMismatchException) {
                        Log.e(TAG, e.message)
                        toast("Invalid input")
                    }
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
        Log.i(TAG, msg)
    }
    /** displays a short Toast message
    * @param msg the message to display
    */
    private fun toast(msg: String) {
        Toast.makeText(this.context, msg, Toast.LENGTH_SHORT).show()
    }

}