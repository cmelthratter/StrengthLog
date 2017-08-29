package com.cmelthratter.strengthlog.controllers

import com.cmelthratter.strengthlog.models.Entry
import com.cmelthratter.strengthlog.ui.activities.LiftActivity

/**
 * Created by Cody Melthratter on 28.8.17.
 * This class will be responsible for controlling (creating, modifying, deleting)
 * the list of Entries.
 */

class EntryListController(var entries: ArrayList<Entry>) {



    fun addEntry() {
        entries.add(Entry())
    }

    fun removeEntry(index : Int) {
        entries.removeAt(index)
    }

    fun removeEntry(entry : Entry) {
        entries.remove(entry)
    }

}