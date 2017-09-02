package com.cmelthratter.strengthlog.util

import android.database.DataSetObserver
import android.os.Environment
import android.util.Log
import com.cmelthratter.strengthlog.models.Lift
import com.cmelthratter.strengthlog.ui.activities.LiftActivity
import com.google.gson.Gson
import java.io.*
import java.util.*

val file : File = File(Environment.getExternalStorageDirectory().absolutePath + JSON_FILE_NAME)
val backupFile : File = File(Environment.getExternalStorageDirectory().absolutePath + JSON_FILE_NAME + BACKUP_FILE_EXT)
val gson : Gson = Gson()
val TAG = JsonHandler::class.java.simpleName

/**
 * Created by Cody Melthratter on 7/20/2017.
 * For reading and writing json strings to a file
 */
object JsonHandler  {

        /**
         * reads the lift from file and sets the Global Lift List
         * to the result from file
         */
        fun readLifts() : Array<out Lift>  {

            log("reading lifts")
            var reader: Scanner? = null
            try {
                if (!file.exists()) {
                    throw FileNotFoundException("No file to read")
                }
                file.setReadable(true)
                reader = Scanner(file, "UTF-8")
                val data = reader.nextLine()
                val array = gson.fromJson(data, arrayOf<Lift>()::class.java)
                log("loadedlist: $array")
                return array
            } catch (e: Exception) {
                Log.e(TAG, "" + e.message)
                return arrayOf()
            } finally {
                reader!!.close()
            }

        }

        /**
         * writes the lifts contained in the LiftActivity companion object
         * @param backup true if writing to backup file, false otherwise
         */
        fun writeLifts(backup : Boolean = false) {

            var writer: PrintWriter? = null
            try {
                if (backup) writer = PrintWriter(FileOutputStream(backupFile, false))
                else writer = PrintWriter(FileOutputStream(file, false))
                log("writing lifts")
                if (!file.exists()) file.createNewFile()
                file.setWritable(true)
                val data = gson.toJson(LiftActivity.liftList!!.toArray())
                log("Writing string: $data")
                writer.println(data)
            } catch (e: Exception) {
                Log.e(TAG, e.message)
            } finally {
                writer!!.close()
            }

        }



    private fun log(msg: String) {
            Log.i(TAG, msg)
    }

}

/**
 * Simple class that allows us to create a new DataObserver that just
 * calls JsonHandler.writeLifts() when the observed dataSet changes.
 * This insures that no exceptions are thrown due to duplicate
 * registers.
 */
class DataObserver : DataSetObserver() {
    override fun onChanged() {
        super.onChanged()
        JsonHandler.writeLifts()
    }

    override fun onInvalidated() {
        super.onInvalidated()
    }
}