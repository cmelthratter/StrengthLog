package com.cmelthratter.strengthlog.ui.activities

import android.annotation.TargetApi
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast

import com.cmelthratter.strengthlog.R
import com.cmelthratter.strengthlog.controllers.EntryListController
import com.cmelthratter.strengthlog.models.Entry
import com.cmelthratter.strengthlog.ui.dialogs.DeleteConfirmDialog
import com.cmelthratter.strengthlog.util.*
import java.util.*


class EntryListActivity : AppCompatActivity(), DeleteConfirmDialog.DeleteDialogListener {
    override fun onDialogPositiveClick() {
        entryListController.removeEntry(currentPosition)
        choiceMode = VIEW
    }

    override fun onDialogNegativeClick() {
        //do nothing
    }

    lateinit var entryListView : ListView
    lateinit var arrayAdapter : ArrayAdapter<Entry>
    lateinit var entryListController : EntryListController
    var currentPosition = 0
    var choiceMode = VIEW
    var currentItem : MenuItem? = null

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.default_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection

        when (item.itemId) {
            R.id.edit -> {
                currentItem = item
                choiceMode = EDIT
                toast("Choose an entry to edit its date")
                currentItem!!.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
                return true
            } R.id.delete -> {
            currentItem = item
                choiceMode = DELETE
                toast("Choose an entry to delete")
                currentItem!!.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
                return true
            } R.id.backup -> {
                JsonHandler.writeLifts(true)
                toast("Backing up lifts data file..")
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }


    @TargetApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry_list)
        val toolbar = findViewById(R.id.entry_list_toolbar) as Toolbar

        toolbar.title = String.format(Locale.US, getString(R.string.title_activity_entry_list), LiftActivity.currentLift!!.name)
        setSupportActionBar(toolbar)

        entryListView = findViewById(R.id.entry_list_view) as ListView
        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, LiftActivity.currentLift!!.entries)
        arrayAdapter.setNotifyOnChange(true)
        entryListView.adapter = arrayAdapter
        val fab = findViewById(R.id.fab) as FloatingActionButton

        entryListView.setOnItemClickListener { parent, view, position, id ->
            currentPosition = position
            when (choiceMode) {

                VIEW -> {
                    val intent = Intent(this, EntryActivity::class.java)
                    intent.putExtra(POSITION_KEY, position)
                    JsonHandler.writeLifts()
                    startActivity(intent)
                }
                EDIT -> {
                    val datePickerDialog = DatePickerDialog(this, R.style.AppTheme_AppBarOverlay)
                    datePickerDialog.setOnDateSetListener { view, year, month, dayOfMonth ->
                        val calendar = GregorianCalendar(year, month, dayOfMonth)
                        LiftActivity.currentLift!!.entries[position].date = Date(calendar.timeInMillis)
                        arrayAdapter.notifyDataSetChanged()

                        choiceMode = VIEW
                        currentItem!!.setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT)
                    }

                    datePickerDialog.setTitle(R.string.edit_date_title)

                    datePickerDialog.show()
                }
                DELETE -> {
                    val deleteDialog = DeleteConfirmDialog()
                    deleteDialog.onAttach(this)
                    deleteDialog.show(fragmentManager, "DeleteConfirmDialog")
                    currentItem!!.setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT)
                }
            }
        }

        entryListController = EntryListController(LiftActivity.currentLift!!.entries, arrayAdapter)
        fab.setOnClickListener(View.OnClickListener { view ->
            entryListController.addEntry()
            Toast.makeText(this, "New entry added for ${LiftActivity.currentLift}", Toast.LENGTH_SHORT).show()
        })
    }

    override fun onStart() {
        super.onStart()
        log("Activity started")
        arrayAdapter.notifyDataSetChanged()
    }


    private fun log(msg: String) {
        Log.i(LiftActivity::class.java.simpleName, msg)
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
