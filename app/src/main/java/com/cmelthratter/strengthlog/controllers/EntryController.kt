package com.cmelthratter.strengthlog.controllers

import android.widget.ArrayAdapter
import com.cmelthratter.strengthlog.models.Entry
import com.cmelthratter.strengthlog.util.DataObserver
import com.cmelthratter.strengthlog.util.JsonHandler

/**
 * Created by Cody Melthratter on 28.8.17. A class for
 * Controlling (creating, modifying, deleting) Entries.
 */

class EntryController(var entry: Entry,
                      var repsAdapter: ArrayAdapter<Int>,
                      var weightAdapter: ArrayAdapter<Float>,
                      var rpeAdapter: ArrayAdapter<Float>) {
    init {
        repsAdapter.registerDataSetObserver(DataObserver())
        weightAdapter.registerDataSetObserver(DataObserver())
        rpeAdapter.registerDataSetObserver(DataObserver())
    }

    fun addRpe(f : Float) {

        entry.rpe.add(f)
        rpeAdapter.notifyDataSetChanged()
    }



    fun addWeight(f: Float) {
        entry.weight.add(f)
        weightAdapter.notifyDataSetChanged()
    }


    fun addReps(i : Int) {
        entry.reps.add(i)
        repsAdapter.notifyDataSetChanged()
    }


    fun addNewSet(reps: Int, weight: Float, rpe: Float) {
        entry.reps.add(reps)
        entry.weight.add(weight)
        entry.rpe.add(rpe)

        repsAdapter.notifyDataSetChanged()
        weightAdapter.notifyDataSetChanged()
        rpeAdapter.notifyDataSetChanged()
    }


    fun setReps(index: Int, value: Int) {
        entry.reps[index] = value
        repsAdapter.notifyDataSetChanged()
    }

    fun setWeight(index: Int, value: Float) {
        entry.weight[index] = value
        weightAdapter.notifyDataSetChanged()
    }

    fun setRpe(index: Int, value: Float) {
        entry.rpe[index] = value
        rpeAdapter.notifyDataSetChanged()
    }

}
