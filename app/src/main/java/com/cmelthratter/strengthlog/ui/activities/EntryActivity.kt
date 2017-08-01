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
import com.cmelthratter.strengthlog.ui.dialogs.WeightEditorDialog
import com.cmelthratter.strengthlog.util.JsonHandler
import com.cmelthratter.strengthlog.util.POSITION_KEY
import java.text.SimpleDateFormat
import java.util.*

/**
 * An activity for interacting with the list
 * of entries for a specified lift
 */
class EntryActivity : AppCompatActivity() , EntryInputDialog.EntryDialogListener, RepsEditorDialog.RepsEditorDialogListener, WeightEditorDialog.WeightEditorDialogListener{

    val TAG = EntryActivity::class.java.simpleName

    lateinit var setsList : ListView
    lateinit var repsList : ListView
    lateinit var weightList : ListView
    lateinit var dateLabel : TextView
    lateinit var currentLift : Lift
    lateinit var currentEntry : Entry
    lateinit var setsAdapter: ArrayAdapter<Int>
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
        setsList = findViewById(R.id.sets_listView) as ListView
        repsList = findViewById(R.id.reps_listView) as ListView
        weightList = findViewById(R.id.weight_listView) as ListView
        dateLabel = findViewById(R.id.date_text_label) as TextView
        val toolbar = findViewById(R.id.entry_toolbar) as Toolbar
        toolbar.title = LiftActivity.currentLift.name
        setSupportActionBar(toolbar)


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

        setsAdapter = ArrayAdapter<Int>(this, android.R.layout.simple_list_item_1, currentEntry.sets)
        repsAdapter = ArrayAdapter<Int>(this, android.R.layout.simple_list_item_1, currentEntry.reps)
        weightAdapter = ArrayAdapter<Float>(this, android.R.layout.simple_list_item_1, currentEntry.weight)

        setsList.adapter = setsAdapter
        repsList.adapter = repsAdapter
        weightList.adapter = weightAdapter

        dateLabel.text = String.format(getString(R.string.date_label),
                currentEntry.getFormattedDate())
        log("$currentEntry")
        log("$entryPosition")
        fab.setOnClickListener { view ->
            var repsPlaceholder = 0
            var weightPlaceholder = 0.0F
            if (currentEntry.reps.isNotEmpty()) repsPlaceholder = currentEntry.reps.last()
            if (currentEntry.weight.isNotEmpty()) weightPlaceholder = currentEntry.weight.last()
            val dialog = EntryInputDialog(repsPlaceholder, weightPlaceholder)
            dialog.show(fragmentManager, "EntryInputDialog")

        }
    }

    override fun onBackPressed() {

        super.onBackPressed()
    }


    override fun onDialogPositiveClick(reps: Int?, weight: Float?) {
        setsAdapter.add(currentEntry.sets.size + 1)

        if (reps == null) repsAdapter.add(0)
        else repsAdapter.add(reps)
        if (weight == null) weightAdapter.add(0.0F)
        else weightAdapter.add(weight)
        jsonHandler.writeLifts()

    }
    override fun onDialogPositiveClick(newReps: Int) {
        currentEntry.reps[selectedPosition] = newReps
        repsAdapter.notifyDataSetChanged()
        jsonHandler.writeLifts()
    }

    override fun onDialogPositiveClick(newWeight: Float) {
        currentEntry.weight[selectedPosition] = newWeight
        weightAdapter.notifyDataSetChanged()
        jsonHandler.writeLifts()
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
