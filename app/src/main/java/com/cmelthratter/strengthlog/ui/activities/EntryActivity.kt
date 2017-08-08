package com.cmelthratter.strengthlog.ui.activities

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.widget.*

import com.cmelthratter.strengthlog.R
import com.cmelthratter.strengthlog.ui.dialogs.EntryInputDialog
import com.cmelthratter.strengthlog.ui.dialogs.RepsEditorDialog
import com.cmelthratter.strengthlog.ui.dialogs.RpeEditorDialog
import com.cmelthratter.strengthlog.ui.dialogs.WeightEditorDialog
import com.cmelthratter.strengthlog.util.JsonHandler
import com.cmelthratter.strengthlog.util.POSITION_KEY


const val WEIGHT = 0
const val RPE = 1
/**
 * An activity for interacting with the list
 * of entries for a specified lift
 */
class EntryActivity : AppCompatActivity() , EntryInputDialog.EntryDialogListener, RepsEditorDialog.RepsEditorDialogListener, RpeEditorDialog.EditorDialogListener{

    val TAG = EntryActivity::class.java.simpleName

    lateinit var rpeList: ListView
    lateinit var repsList : ListView
    lateinit var weightList : ListView
    lateinit var dateLabel : TextView
    lateinit var currentLift : Lift
    lateinit var currentEntry : Entry
    lateinit var rpeAdapter: ArrayAdapter<Float>
    lateinit var repsAdapter : ArrayAdapter<Int>
    lateinit var weightAdapter : ArrayAdapter<Float>
    lateinit var jsonHandler : JsonHandler
    var selectedPosition : Int = -1


    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry)
        jsonHandler = JsonHandler()
        currentLift = LiftActivity.currentLift
        val entryPosition = intent.getIntExtra(POSITION_KEY, 0)
        currentEntry = currentLift.entries[entryPosition]

        val fab = findViewById(R.id.entry_fab) as FloatingActionButton
        rpeList = findViewById(R.id.rpe_listView) as ListView
        repsList = findViewById(R.id.reps_listView) as ListView
        weightList = findViewById(R.id.weight_listView) as ListView
        dateLabel = findViewById(R.id.date_text_label) as TextView
        val toolbar = findViewById(R.id.entry_toolbar) as Toolbar
        toolbar.title = LiftActivity.currentLift.name
        setSupportActionBar(toolbar)
        if(currentEntry.rpe.size != currentEntry.reps.size) {
            currentEntry.rpe.clear()
            currentEntry.rpe.addAll(currentEntry.weight)
        }


        repsList.setOnItemClickListener { parent, view, position, id ->

            val dialog = RepsEditorDialog()
            dialog.show(fragmentManager, "RepsEditorDialog")
            selectedPosition = position
        }

        weightList.setOnItemClickListener { parent, view, position, id ->
            val dialog = WeightEditorDialog()
            dialog.show(fragmentManager, "WeightEditorDialog")
            selectedPosition = position
        }

        rpeList.setOnItemClickListener { parent, view, position, id ->
            val dialog = RpeEditorDialog()
            dialog.show(fragmentManager, "RpeEditorDialog")
            selectedPosition = position
        }

        rpeAdapter = ArrayAdapter<Float>(this, android.R.layout.simple_list_item_1, currentEntry.rpe)
        repsAdapter = ArrayAdapter<Int>(this, android.R.layout.simple_list_item_1, currentEntry.reps)
        weightAdapter = ArrayAdapter<Float>(this, android.R.layout.simple_list_item_1, currentEntry.weight)

        rpeList.adapter = rpeAdapter
        repsList.adapter = repsAdapter
        weightList.adapter = weightAdapter

        dateLabel.text = String.format(getString(R.string.date_label),
                currentEntry.getFormattedDate())
        log("$currentEntry")
        log("$entryPosition")
        fab.setOnClickListener { view ->
            var repsPlaceholder = 0
            var weightPlaceholder = 0.0F
            var rpePlaceHolder = 0.0F
            if (currentEntry.reps.isNotEmpty()) repsPlaceholder = currentEntry.reps.last()
            if (currentEntry.weight.isNotEmpty()) weightPlaceholder = currentEntry.weight.last()
            if (currentEntry.rpe.isNotEmpty()) rpePlaceHolder = currentEntry.rpe.last()
            val dialog = EntryInputDialog(repsPlaceholder, weightPlaceholder, rpePlaceHolder)
            dialog.show(fragmentManager, "EntryInputDialog")

        }
    }

    override fun onBackPressed() {

        super.onBackPressed()
    }


    override fun onDialogPositiveClick(reps: Int?, weight: Float?, rpe: Float?) {

        if (reps == null)
            repsAdapter.add(0)
        else
            repsAdapter.add(reps)
        if (weight == null)
            weightAdapter.add(0.0F)
        else
            weightAdapter.add(weight)
        if (rpe == null)
            rpeAdapter.add(0.0F)
        else
            rpeAdapter.add(rpe)

        jsonHandler.writeLifts()

    }
    override fun onDialogPositiveClick(newVal: Int) {

        currentEntry.reps[selectedPosition] = newVal
        repsAdapter.notifyDataSetChanged()
        jsonHandler.writeLifts()
    }


    override fun onDialogPositiveClick(newVal: Float, type: Int) {
        when(type) {
            WEIGHT -> {
             currentEntry.weight[selectedPosition] = newVal
             weightAdapter.notifyDataSetChanged()
             jsonHandler.writeLifts()
            }
            RPE -> {
                currentEntry.rpe[selectedPosition] = newVal
                weightAdapter.notifyDataSetChanged()
                jsonHandler.writeLifts()
            }
        }
    }

    override fun onDialogNegativeClick() {
        toast("Canceled input")
    }

    private fun log(msg : String) {
        Log.i(TAG, msg)
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

}
