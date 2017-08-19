package com.cmelthratter.strengthlog.models

import android.os.Parcel
import android.os.Parcelable
import java.text.SimpleDateFormat
import java.util.*

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
