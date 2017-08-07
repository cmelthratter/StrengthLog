package com.cmelthratter.strengthlog.ui.activities

import android.Manifest
import android.app.DialogFragment
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import android.widget.AbsListView.MultiChoiceModeListener;

import com.cmelthratter.strengthlog.R
import com.cmelthratter.strengthlog.ui.dialogs.LiftInputDialog
import com.cmelthratter.strengthlog.ui.dialogs.LiftInputDialog.LiftDialogListener
import com.cmelthratter.strengthlog.util.JsonHandler
import com.cmelthratter.strengthlog.util.LIFTS_KEY

import java.text.SimpleDateFormat
import java.util.*


import kotlin.collections.ArrayList
import android.R.menu
import android.support.v7.widget.Toolbar
import android.view.*
import android.widget.AbsListView
import com.cmelthratter.strengthlog.ui.dialogs.DeleteConfirmDialog

const val VIEW = 0
const val EDIT =  1
const val DELETE = 2


/**
 * A class for representing a tracked lift,
 * with a name and daily entries
 */
class Lift (var name: String,
        var entries : ArrayList<Entry> = arrayListOf()) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString()){
                parcel.setDataPosition(1)
                val array : Array<Entry> = parcel.createTypedArray(Entry.CREATOR)

                entries.addAll(array)

            }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(name)
        dest?.writeTypedArray(entries.toTypedArray(), 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Lift> {
        override fun createFromParcel(parcel: Parcel): Lift {
            return Lift(parcel)
        }

        override fun newArray(size: Int): Array<Lift?> {
            return arrayOfNulls(size)
        }
    }

    override fun toString() : String {
        return name
    }

    fun getDates() : MutableList<String>{
        return entries.map { entry -> SimpleDateFormat("MM:dd:yy", Locale.US).format(entry.date).toString() }.toMutableList()
    }

}

/**
 * A class for representing an Entry for a given lift,
 * with a Date, a list of sets corresponding to a set of reps and a set
 * of weights
 */
class Entry(var date: Date = Date(),
                 var sets: ArrayList<Int> = arrayListOf(),
                 var reps: ArrayList<Int> = arrayListOf(),
                 var weight: ArrayList<Float> = arrayListOf()) : Parcelable {
    constructor(parcel: Parcel) : this(
            Date(parcel.readLong())) {
        val setsArray: IntArray? = null
        val weightArray: FloatArray? = null
        val repsArray: IntArray? = null

        parcel.readIntArray(setsArray)
        parcel.readIntArray(repsArray)
        parcel.readFloatArray(weightArray)

        sets.addAll(setsArray!!.toTypedArray())
        weight.addAll(weightArray!!.toTypedArray())
        reps.addAll(repsArray!!.toTypedArray())
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(date.time)
        parcel.writeIntArray(sets.toIntArray())
        parcel.writeIntArray(reps.toIntArray())
        parcel.writeFloatArray(weight.toFloatArray())
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Entry> {
        override fun createFromParcel(parcel: Parcel): Entry {
            return Entry(parcel)
        }

        override fun newArray(size: Int): Array<Entry?> {
            return arrayOfNulls(size)
        }
    }

    /**
     * returns a formatted date to be displayed in the
     * entry page
     */
    fun getFormattedDate(): String {
        return SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.US).format(this.date)
    }

    /**
     * returns a properly formatted string representing a Lift entry
     * depending on the reps and weight for each set
     */
    override fun toString(): String {
        val sb = StringBuilder(SimpleDateFormat("MM-dd-yy", Locale.US).format(date)).append(":")
        if (weight.all { i -> i == weight[0] } && reps.all { i -> reps[0] == i }) {
            if (sets.isNotEmpty())
                sb.append("\t\t\t${reps[0]}x${sets.size}x${weight[0]}")
        } else {
            var eqRepsSum = 1
            for (i in 0 until sets.size) {
                if(i == sets.size - 1) {
                    //if (reps[i] == reps[i - 1] && weight[i] == weight[i - 1])
                        sb.append("\t\t${eqRepsSum++}x${reps[i]}x${weight[i]}")

                } else if((reps[i] == reps[i + 1]) && (weight[i + 1] == weight[i])) {
                    eqRepsSum++
                } else {

                    sb.append("\t\t${eqRepsSum}x${reps[i]}x${weight[i]}")
                    if (i < sets.size - 1) sb.append(", ")
                    if (i % 3 == 0 && i > 1) sb.append("\n")
                    eqRepsSum = 1
                }
            }

        }
        return sb.toString()
    }
}

    class LiftActivity : AppCompatActivity(), LiftDialogListener, DeleteConfirmDialog.DeleteDialogListener {
        /**
         * for holding the data transferred between activities.
         * Transferring it as Parcelable resulted in
         * memory allocation errors.
         */
        companion object {

            var currentLift: Lift = Lift("null")
            var liftList: ArrayList<Lift> = arrayListOf()
        }

        private lateinit var listView: ListView
        private lateinit var arrayAdapter: ArrayAdapter<Lift>
        private var permissionGranted = false
        private lateinit var prefs: SharedPreferences
        private lateinit var jsonHandler: JsonHandler
        private var choiceMode = VIEW
        private var currentMenuItem : MenuItem? = null

        override fun onCreateOptionsMenu(menu: Menu): Boolean {
            val inflater = menuInflater
            inflater.inflate(R.menu.menu, menu)

            return true
        }
        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            // Handle item selection
            currentMenuItem = item
            if (item.itemId != R.id.backup)
                item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
            when (item.itemId) {
                R.id.edit -> {
                    choiceMode = EDIT

                    toast("Choose a lift to edit its name")
                    return true
                } R.id.delete -> {
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

        val TAG = LiftActivity::class.java.simpleName
        /**
         * handling positive response from the new Lift dialog
         */
        override fun onDialogPositiveClick(newLift: String, isNewLift: Boolean) {
            log("positive click: $newLift")
            if (isNewLift)
                addLift(newLift)
            else
                changeLiftName(currentLift, newLift)

            choiceMode = VIEW
            currentMenuItem!!.setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT)
        }

        /**
         * handling negative response from the new Lift dialog
         */
        override fun onDialogNegativeClick(dialog: DialogFragment) {
            Toast.makeText(baseContext, "Canceled", Toast.LENGTH_SHORT).show()
            choiceMode = VIEW
            currentMenuItem!!.setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT)
        }


        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            log("Activity created")
            setContentView(R.layout.activity_lift)
            listView = this.findViewById(R.id.lift_list) as ListView
            prefs = getSharedPreferences(LIFTS_KEY, 0)
            val fab = findViewById(R.id.fab_lift) as FloatingActionButton
            val toolbar = findViewById(R.id.toolbar) as Toolbar
            arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, LiftActivity.liftList)
            arrayAdapter.setNotifyOnChange(true)
            listView.adapter = arrayAdapter

            requestPermissions()
            listView.setOnItemClickListener { parent, view, position, id ->
                LiftActivity.currentLift = LiftActivity.liftList[position]
                when(choiceMode) {
                    VIEW -> {
                        val intent = Intent(this@LiftActivity, EntryListActivity::class.java)
                        //intent.putExtra(CURRENT_LIFT_KEY, liftList[position])

                        startActivity(intent)
                    }
                    EDIT -> {
                        val liftDialog: LiftInputDialog = LiftInputDialog(false, LiftActivity.currentLift.name)
                        liftDialog.onAttach(this)
                        liftDialog.show(fragmentManager, "LiftInputDialog")
                    }
                    DELETE -> {
                        val deleteDialog = DeleteConfirmDialog()
                        deleteDialog.onAttach(this)
                        deleteDialog.show(fragmentManager, "DeleteConfirmDialog")

                    }
                }
            }
            fab.setOnClickListener(View.OnClickListener { view ->

                val liftDialog: LiftInputDialog = LiftInputDialog()
                liftDialog.onAttach(this)
                liftDialog.show(fragmentManager, "LiftInputDialog")
            })
        }

        /**
         * handles call backs from the permission request
         */
        override fun onRequestPermissionsResult(requestCode: Int,
                                                permissions: Array<String>, grantResults: IntArray) {
            log("requestCode $requestCode, permissions: $permissions, grantResults: $grantResults")
            when (requestCode) {
                0 -> {
                    if (grantResults.isNotEmpty()
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        permissionGranted = true
                        log("Permisson granted: $permissionGranted")
                        jsonHandler = JsonHandler()
                        loadLifts()
                    } else
                        toast("Request failed")

                    return
                }
            }
        }

        override fun onDialogNegativeClick() {
            toast("Canceled delete")
            choiceMode = VIEW

            currentMenuItem!!.setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT)
        }

        override fun onDialogPositiveClick() {
            removeLift(currentLift)
            choiceMode = VIEW

            currentMenuItem!!.setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT)
        }


        override fun onStart() {
            super.onStart()
            log("Activity started")
        }

        override fun onResume() {
            super.onResume()
            log("Activity Resumed")
        }

        override fun onStop() {
            log("Activity stopped")
            super.onStop()
        }

        override fun onDestroy() {
            super.onDestroy()
            log("Activity destroyed")
        }

        /**
         * Changes the lift name of the Lift object
         * @param lift the Lift to change
         * @param newName  the new name to change the lift name to
         */
        fun changeLiftName(lift: Lift, newName: String) {
            liftList[liftList.indexOf(lift)].name = newName
            arrayAdapter.notifyDataSetChanged()
        }

        /**
         * adds a new lift with the specified name to the
         * lift list, the ArrayAdapter, and writes the new list
         * to a files
         * @param liftName the name of the lift to add
         */
        private fun addLift(liftName: String) {
            log("Adding lift: $liftName")
            val lift = Lift(liftName)
            liftList.add(lift)
            arrayAdapter.notifyDataSetChanged()
            jsonHandler.writeLifts()
        }

        /**
         * removes the specified lift from the lift list,
         * ArrayAdapter, and writes the new lift list out
         * to the json file
         * @param lift the Lift to remove from the list
         */
        private fun removeLift(lift: Lift) {
            log("removing lift $lift")
            liftList.remove(lift)
            arrayAdapter.notifyDataSetChanged()
            jsonHandler.writeLifts()
        }

        /**
         * calls readLifts() in the JsonHandler
         * and then updates the ArrayAdapter
         */
        private fun loadLifts() {
            if (liftList.isEmpty()) {
                jsonHandler.readLifts()
                arrayAdapter.notifyDataSetChanged()
            }
        }

        /**
         * checks read write permissions from the user,
         * requests them if not granted
         */

        private fun requestPermissions() {
            log("requesting permissions")
            if (ContextCompat.checkSelfPermission(applicationContext,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(applicationContext,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE),
                        0)

            } else {
                permissionGranted = true
                jsonHandler = JsonHandler()
                loadLifts()
            }
        }

        /**
         * displays a short Toast message
         * @param msg the message to display
         */
        private fun toast(msg: String) {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }

        /**
         * logs a message to the android monitor with the info tag
         * @param msg the message to log
         */
        private fun log(msg: String) {
            Log.i(TAG, msg)
        }
}
