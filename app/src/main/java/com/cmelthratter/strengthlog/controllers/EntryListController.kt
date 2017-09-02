package com.cmelthratter.strengthlog.controllers

import android.database.DataSetObserver
import android.widget.ArrayAdapter
import com.cmelthratter.strengthlog.models.Entry
import com.cmelthratter.strengthlog.util.DataObserver
import com.cmelthratter.strengthlog.util.JsonHandler

/**
 * Created by Cody Melthratter on 28.8.17.
 * This class will be responsible for controlling (creating, modifying, deleting)
 * the list of Entries.
 */

class EntryListController(var entries: ArrayList<Entry>,
                          var adapter: ArrayAdapter<Entry>) {
    init {
        adapter.registerDataSetObserver(DataObserver())
    }



    fun addEntry() {
        adapter.insert(Entry(), 0)
        adapter.notifyDataSetChanged()
    }

    fun removeEntry(index : Int) {
        entries.removeAt(index)
        adapter.notifyDataSetChanged()
    }

    fun removeEntry(entry : Entry) {
        entries.remove(entry)
    }

}