package com.cmelthratter.strengthlog.ui.activities

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem

import android.widget.*

import com.cmelthratter.strengthlog.R
import com.cmelthratter.strengthlog.models.Lift
import com.cmelthratter.strengthlog.models.Entry
import com.cmelthratter.strengthlog.ui.dialogs.*
import com.cmelthratter.strengthlog.ui.dialogs.DeleteConfirmDialog.DeleteDialogListener
import com.cmelthratter.strengthlog.util.JsonHandler
import com.cmelthratter.strengthlog.util.POSITION_KEY


const val WEIGHT = 0
const val RPE = 1
/**
 * An activity for interacting with the list
 * of entries for a specified lift
 */
class EntryActivity : AppCompatActivity() , EntryInputDialog.EntryDialogListener,
        RepsEditorDialog.RepsEditorDialogListener,
        RpeEditorDialog.EditorDialogListener,
        DeleteDialogListener{
    override fun onDialogPositiveClick() {
        currentEntry.rpe.removeAt(selectedPosition)
        currentEntry.reps.removeAt(selectedPosition)
        currentEntry.weight.removeAt(selectedPosition)
        choiceMode = EDIT
        currentMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
        rpeAdapter.notifyDataSetChanged()
        repsAdapter.notifyDataSetChanged()
        weightAdapter.notifyDataSetChanged()
       toast("Removed set #$selectedPosition")
    }

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
    lateinit var currentMenuItem : MenuItem
    var choiceMode : Int = EDIT

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.entry_menu, menu)

        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        currentMenuItem = item
        if (item.itemId != R.id.backup)
            item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        when (item.itemId) {
           R.id.delete -> {
            choiceMode = DELETE
            toast("Choose a lift to delete it" )
            return true
        } R.id.backup -> {
            jsonHandler.writeLifts(true)
            toast("Backing up lifts data file..")
        }
            else -> return super.onOptionsItemSelected(item)
        }
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry)
        currentLift = LiftActivity.currentLift!!
        val entryPosition = intent.getIntExtra(POSITION_KEY, 0)
        currentEntry = currentLift.entries[entryPosition]

        val fab = findViewById<FloatingActionButton>(R.id.entry_fab)
        rpeList = findViewById(R.id.rpe_listView)
        repsList = findViewById(R.id.reps_listView)
        weightList = findViewById(R.id.weight_listView)
        dateLabel = findViewById(R.id.date_text_label)
        val toolbar = findViewById<Toolbar>(R.id.entry_toolbar)
        toolbar.title = LiftActivity.currentLift!!.name
        setSupportActionBar(toolbar)
        if(currentEntry.rpe.size != currentEntry.reps.size) {
            currentEntry.rpe.clear()
            currentEntry.rpe.addAll(currentEntry.weight)
        }


        repsList.setOnItemClickListener { _, _, position, _ ->

            when (choiceMode) {
                EDIT -> {
                    val dialog = RepsEditorDialog()
                    dialog.show(fragmentManager, "RepsEditorDialog")
                    selectedPosition = position
                }
                DELETE -> {
                    val dialog = DeleteConfirmDialog()
                    dialog.show(fragmentManager, "DeleteConfirmDialog")
                    selectedPosition = position
                }

            }
        }

        weightList.setOnItemClickListener { _, _, position, _ ->
            when (choiceMode) {
                EDIT -> {
                    val dialog = WeightEditorDialog()
                    dialog.show(fragmentManager, "WeightEditorDialog")
                    selectedPosition = position
                }
                DELETE -> {
                    val dialog = DeleteConfirmDialog()
                    dialog.show(fragmentManager, "DeleteConfirmDialog")
                    selectedPosition = position
                }
            }
        }

        rpeList.setOnItemClickListener { _, _, position, _ ->
            when (choiceMode) {
                EDIT -> {
                    val dialog = RpeEditorDialog()
                    dialog.show(fragmentManager, "RpeEditorDialog")
                    selectedPosition = position
                }
                DELETE -> {
                    val dialog = DeleteConfirmDialog()
                    dialog.show(fragmentManager, "DeleteConfirmDialog")
                    selectedPosition = position
                }
            }
        }

        rpeAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, currentEntry.rpe)
        repsAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, currentEntry.reps)
        weightAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, currentEntry.weight)

        rpeList.adapter = rpeAdapter
        repsList.adapter = repsAdapter
        weightList.adapter = weightAdapter

        dateLabel.text = String.format(getString(R.string.date_label),
                currentEntry.getFormattedDate())
        log("$currentEntry")
        log("$entryPosition")
        fab.setOnClickListener {

            val dialog = EntryInputDialog()
            dialog.show(supportFragmentManager, "EntryInputDialog")

        }
    }

    override fun onBackPressed() {

        super.onBackPressed()
    }


    override fun onDialogPositiveClick(reps: Int?, sets: Int?, weight: Float?, rpe: Float?) {
        for (i in 0 until sets!!) {
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
        }

        //JsonHandler.writeLifts()

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
