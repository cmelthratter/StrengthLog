package com.cmelthratter.strengthlog.util

import android.util.Log
import com.cmelthratter.strengthlog.models.Lift
import java.io.File
import java.io.PrintWriter

/**
 * Created by Cody Melthratter on 10/8/2017.
 * Runnable object for handling the exporting of formatted .CSV files.
 */

class CsvExporter(val list : List<Lift>) : Runnable{

    val TAG = CsvExporter::class.java.simpleName
    /**
     * Writes the Lift list to a CSV file.
     * @param list the list to write
     * @return the File object created by this function
     */
    private fun writeCsvFile(list : List<Lift>) {
        var file : File? = null
        try {

            file = File(CSV_FILE_NAME)
            file.createNewFile()
            file.setWritable(true)
            val writer = PrintWriter(file)
            writer.println(CSV_HEADERS)
            for (lift in list) {
                for (entry in lift.entries)
                    for (i in 0 until entry.reps.size) {
                        println("$lift,${entry.getFormattedDate()},${entry.reps[i]},${entry.weight[i]},${entry.rpe[i]}")
                    }

                writer.println()
            }


        } catch (e : Exception) {
                Log.e(TAG, e.message)
        }

    }

    override fun run() {
        writeCsvFile(list)
    }


    private fun log(msg: String) {
        Log.i(TAG, msg)
    }
}