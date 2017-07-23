package com.cmelthratter.strengthlog.ui.activities

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast

import com.cmelthratter.strengthlog.R
import com.cmelthratter.strengthlog.util.*
import java.util.*

class EntryListActivity : AppCompatActivity() {

    lateinit var entryListView : ListView
    lateinit var arrayAdapter : ArrayAdapter<Entry>
    lateinit var jsonHandler : JsonHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry_list)
        jsonHandler = JsonHandler()
        val toolbar = findViewById(R.id.toolbar) as Toolbar

        toolbar.title = String.format(Locale.US, getString(R.string.title_activity_entry_list), LiftActivity.currentLift.name)
        setSupportActionBar(toolbar)

        entryListView = findViewById(R.id.entry_list_view) as ListView
        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, LiftActivity.currentLift.entries)
        arrayAdapter.setNotifyOnChange(true)
        entryListView.adapter = arrayAdapter
        val fab = findViewById(R.id.fab) as FloatingActionButton

        entryListView.setOnItemClickListener { parent, view, position, id ->
            var intent = Intent(this, EntryActivity::class.java)
            //intent.putExtra(CURRENT_LIFT_KEY, currentLift)
            intent.putExtra(POSITION_KEY, position)
            //intent.putExtra(CURRENT_ENTRY_KEY, currentLift.entries[position])
            jsonHandler.writeLifts()
            startActivity(intent)
        }
        fab.setOnClickListener(View.OnClickListener { view ->
            addEntry()
            Toast.makeText(this, "New entry added for ${LiftActivity.currentLift}", Toast.LENGTH_SHORT).show()
        })
    }


    private fun addEntry() {

        val entry = Entry()

        arrayAdapter.add(entry)
        jsonHandler.writeLifts()
        log("adding entry: $entry, ${LiftActivity.currentLift.entries}")
    }

    private fun log(msg: String) {
        Log.i(LiftActivity::class.java.simpleName, msg)
    }

}
