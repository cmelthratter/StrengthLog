package com.cmelthratter.strengthlog.ui.activities

import android.Manifest
import android.app.DialogFragment
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast

import com.cmelthratter.strengthlog.R
import com.cmelthratter.strengthlog.ui.dialogs.LiftInputDialog
import com.cmelthratter.strengthlog.ui.dialogs.LiftInputDialog.LiftDialogListener
import com.cmelthratter.strengthlog.util.JsonHandler
import com.cmelthratter.strengthlog.util.LIFTS_KEY
import com.cmelthratter.strengthlog.models.Lift

import kotlin.collections.ArrayList
import android.support.v7.widget.Toolbar
import android.view.*
import com.cmelthratter.strengthlog.controllers.LiftController
import com.cmelthratter.strengthlog.ui.dialogs.DeleteConfirmDialog
import java.io.FileNotFoundException

const val VIEW = 0
const val EDIT =  1
const val DELETE = 2

class LiftActivity : AppCompatActivity(), LiftDialogListener, DeleteConfirmDialog.DeleteDialogListener {
    /**
     * for holding the data transferred between activities.
     * Transferring it as Parcelable resulted in
     * memory allocation errors.
     */
    companion object {

        var currentLift: Lift? = Lift("null")
        var liftList: kotlin.collections.ArrayList<Lift>? = arrayListOf()
    }

    private lateinit var listView: ListView
    private lateinit var arrayAdapter: ArrayAdapter<Lift>
    private var permissionGranted = false
    private lateinit var prefs: SharedPreferences
    private var choiceMode = VIEW
    private var currentMenuItem : MenuItem? = null
    private lateinit var liftController : LiftController

    val TAG = LiftActivity::class.java.simpleName

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.default_menu, menu)

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
                JsonHandler.writeLifts(true)
                toast("Backing up lifts data file..")
        }
            else -> return super.onOptionsItemSelected(item)
        }
        return false
    }

    /**
     * handling positive response from the new Lift dialog
     */
    override fun onDialogPositiveClick(newLift: String, isNewLift: Boolean) {
        log("positive click: $newLift")
        if (isNewLift)
            liftController.addLift(newLift)
        else
            liftController.changeLiftName(currentLift!!, newLift)

        choiceMode = VIEW

        if (currentMenuItem != null)
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
            LiftActivity.currentLift = LiftActivity.liftList!![position]
            when(choiceMode) {
                VIEW -> {
                    val intent = Intent(this@LiftActivity, EntryListActivity::class.java)
                    //intent.putExtra(CURRENT_LIFT_KEY, liftList[position])

                    startActivity(intent)
                }
                EDIT -> {
                    val liftDialog: LiftInputDialog = LiftInputDialog(false, LiftActivity.currentLift!!.name)
                    liftDialog.onAttach(this as Context)
                    liftDialog.show(fragmentManager, "LiftInputDialog")
                }
                DELETE -> {
                    val deleteDialog = DeleteConfirmDialog()
                    deleteDialog.onAttach(this as Context)
                    deleteDialog.show(fragmentManager, "DeleteConfirmDialog")

                }
            }
        }
        fab.setOnClickListener(View.OnClickListener { view ->

            val liftDialog: LiftInputDialog = LiftInputDialog()
            liftDialog.onAttach(this)
            liftDialog.show(fragmentManager, "LiftInputDialog")
        })
        liftController = LiftController(liftList!!, arrayAdapter)
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
        liftController.removeLift(currentLift)
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
        LiftActivity.liftList = liftController.liftList as ArrayList<Lift>
    }

    override fun onDestroy() {
        super.onDestroy()
        log("Activity destroyed")
    }



    /**
     * calls readLifts() in the JsonHandler
     * and then updates the ArrayAdapter
     */
    private fun loadLifts() {
        try {
            if (liftList!!.isNotEmpty()) return
            if (!permissionGranted) throw FileNotFoundException("Read/Write permissions not granted.")
            LiftActivity.liftList!!.addAll(JsonHandler.readLifts())
            liftController = LiftController(liftList!!, arrayAdapter)
            arrayAdapter.notifyDataSetChanged()
        }catch (e : Exception) {
            Log.e(TAG, "" + e.message)
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
