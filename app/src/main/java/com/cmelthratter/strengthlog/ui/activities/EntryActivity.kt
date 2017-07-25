package com.cmelthratter.strengthlog.ui.activities

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast

import com.cmelthratter.strengthlog.R
import com.cmelthratter.strengthlog.ui.dialogs.EntryInputDialog
import com.cmelthratter.strengthlog.ui.dialogs.RepsEditorDialog
import com.cmelthratter.strengthlog.ui.dialogs.WeightEditorDialog
import com.cmelthratter.strengthlog.util.JsonHandler
import com.cmelthratter.strengthlog.util.POSITION_KEY
import java.util.*

/**
 * An activity for interacting with the list
 * of entries for a specified lift
 */
class EntryActivity : AppCompatActivity() , EntryInputDialog.EntryDialogListener, RepsEditorDialog.RepsEditorDialogListener, WeightEditorDialog.WeightEditorDialogListener{

    lateinit var setsList : ListView
    lateinit var repsList : ListView
    lateinit var weightList : ListView
    lateinit var liftLabel : TextView
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
        val position = intent.getIntExtra(POSITION_KEY, 0)
        currentEntry = currentLift.entries[position]

        val fab = findViewById(R.id.entry_fab) as FloatingActionButton
        setsList = findViewById(R.id.sets_listView) as ListView
        repsList = findViewById(R.id.reps_listView) as ListView
        weightList = findViewById(R.id.weight_listView) as ListView
        liftLabel = findViewById(R.id.lift_text_label) as TextView
        dateLabel = findViewById(R.id.date_textView) as TextView

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

        liftLabel.text = currentLift.name
        val date = Calendar.getInstance()
        date.time = currentEntry.date

        dateLabel.text = currentEntry.getFormattedDate()
        fab.setOnClickListener { view ->
            val dialog = EntryInputDialog()
            dialog.show(fragmentManager, "EntryInputDialog")

        }
    }


    override fun onDialogPositiveClick(reps: Int, weight: Float) {
        setsAdapter.add(currentEntry.sets.size + 1)
        repsAdapter.add(reps)
        weightAdapter.add(weight)
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

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

}
