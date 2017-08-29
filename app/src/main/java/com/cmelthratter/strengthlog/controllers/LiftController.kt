package com.cmelthratter.strengthlog.controllers

import com.cmelthratter.strengthlog.models.Lift
import com.cmelthratter.strengthlog.ui.activities.LiftActivity

/**
 * Created by Cody Melthratter on 28.8.17. A class for
 * controlling (creating, modifying, removing) Lifts and
 * A Lift List.
 */
class LiftController(var liftList : List<Lift>) {


    /**
     * Changes the lift name of the Lift object
     * @param lift the Lift to change
     * @param newName  the new name to change the lift name to
     */
    fun changeLiftName(lift: Lift, newName: String) {
        liftList[LiftActivity.liftList!!.indexOf(lift)].name = newName
    }

    /**
     * adds a new lift with the specified name to the
     * lift list, the ArrayAdapter, and writes the new list
     * to a files
     * @param liftName the name of the lift to add
     */
    fun addLift(liftName: String) {

        val lift = Lift(liftName)
        LiftActivity.liftList!!.add(lift)
    }

    /**
     * removes the specified lift from the lift list,
     * ArrayAdapter, and writes the new lift list out
     * to the json file
     * @param lift the Lift to remove from the list
     */
    fun removeLift(lift: Lift?) {

        LiftActivity.liftList!!.remove(lift)
    }

}