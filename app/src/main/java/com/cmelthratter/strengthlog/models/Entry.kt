package com.cmelthratter.strengthlog.models

import android.os.Parcel
import android.os.Parcelable
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Cody Melthratter on 18.8.17.
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

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Entry> {
        override fun createFromParcel(parcel: Parcel): Entry = Entry(parcel)

        override fun newArray(size: Int): Array<Entry?> = arrayOfNulls(size)

    }

    /**
     * returns a formatted date to be displayed in the
     * entry page
     */
    fun getFormattedDate(): String = SimpleDateFormat("EEE, MMM dd, yyyy", Locale.US).format(this.date)

    override fun toString(): String {
        val sb = StringBuilder(SimpleDateFormat("MM-dd-yy", Locale.US).format(date)).append(":\n")
        if (weight.all { i -> i == weight[0] } && reps.all { i -> reps[0] == i } && rpe.all { i -> i == rpe[0]} || reps.size == 1) {
            if (rpe.isNotEmpty())
                if (weight[0] > 0 && rpe[0] > 0)
                    sb.append("\t\t\t${reps[0]}x${reps.size}x${weight[0]} @ ${rpe[0]}\n")
                else if (weight[0] > 0 && rpe[0] == 0f)
                    sb.append("\t\t\t${reps[0]}x${reps.size}x${weight[0]}\n")
                else if (weight[0] == 0f && rpe[0] > 0)
                    sb.append("\t\t\t${reps[0]}x${reps.size} @ ${rpe[0]}\n")
                else if (weight[0] == 0.0f  && rpe[0] == 0f)
                    sb.append("\t\t\t${reps[0]}x${reps.size}")
        } else {
            var eqRepsSum = 1
            for (i in 0 until rpe.size) {
                if(i == rpe.size - 1) {
                    if (reps[i] == reps[i - 1] && weight[i] == weight[i - 1] && rpe[i] == rpe[i - 1])
                        if (weight[i] > 0)
                            sb.append("\t\t${reps[i]}x${eqRepsSum++}x${weight[i]} @ ${rpe[i]}\n")
                        else
                            sb.append("\t\t${reps[i]}x${eqRepsSum++} @ ${rpe[i]}\n")
                    else
                        if (weight[i] > 0)
                            sb.append("\t\t${reps[i]}x1x${weight[i]} @ ${rpe[i]}\n")
                        else
                            sb.append("\t\t${reps[i]}x1 @ ${rpe[i]}\n")

                } else if((reps[i] == reps[i + 1]) && (weight[i + 1] == weight[i]) && rpe[i] == rpe[i + 1]) {
                    eqRepsSum++
                } else {

                    if (weight[i] > 0)
                        sb.append("\t\t${reps[i]}x${eqRepsSum}x${weight[i]} @ ${rpe[i]}\n")
                    else
                        sb.append("\t\t${reps[i]}x${eqRepsSum} @ ${rpe[i]}\n")
                    eqRepsSum = 1
                }
            }
        }
        return sb.toString()
    }
}