package com.cmelthratter.strengthlog.models

import android.os.Parcel
import android.os.Parcelable
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by cmelt on 18.8.17.
 */
/**
 * A class for representing an Entry for a given lift,
 * with a Date, a list of sets corresponding to a set of reps and a set
 * of weights
 */
class Entry(var date: Date = Date(),
            var rpe: ArrayList<Float> = arrayListOf(),
            var reps: ArrayList<Int> = arrayListOf(),
            var weight: ArrayList<Float> = arrayListOf()) : Parcelable {
    constructor(parcel: Parcel) : this(
            Date(parcel.readLong())) {
        val rpeArray: FloatArray? = null
        val weightArray: FloatArray? = null
        val repsArray: IntArray? = null

        parcel.readFloatArray(rpeArray)
        parcel.readIntArray(repsArray)
        parcel.readFloatArray(weightArray)

        rpe.addAll(rpeArray!!.toTypedArray())
        weight.addAll(weightArray!!.toTypedArray())
        reps.addAll(repsArray!!.toTypedArray())
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(date.time)
        parcel.writeFloatArray(rpe.toFloatArray())
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
        val sb = StringBuilder(SimpleDateFormat("MM-dd-yy", Locale.US).format(date)).append(":\n")
        if (weight.all { i -> i == weight[0] } && reps.all { i -> reps[0] == i } || reps.size == 1) {
            if (rpe.isNotEmpty())
                sb.append("\t\t\t${reps[0]}x${reps.size}x${weight[0]} @ ${rpe[0]}")
        } else {
            var eqRepsSum = 1
            for (i in 0 until rpe.size) {
                if(i == rpe.size - 1) {
                    if (reps[i] == reps[i - 1] && weight[i] == weight[i - 1] && rpe[i] == rpe[i - 1])
                        sb.append("\t\t${reps[i]}x${eqRepsSum++}x${weight[i]} @ ${rpe[i]}")
                    else
                        sb.append("\t\t${reps[i]}x1x${weight[i]} @ ${rpe[i]}")

                } else if((reps[i] == reps[i + 1]) && (weight[i + 1] == weight[i]) && rpe[i] == rpe[i + 1]) {
                    eqRepsSum++
                } else {

                    sb.append("\t\t${reps[i]}x${eqRepsSum}x${weight[i]} @ ${rpe[i]}\n")
                    eqRepsSum = 1
                }
            }

        }
        return sb.toString()
    }
}