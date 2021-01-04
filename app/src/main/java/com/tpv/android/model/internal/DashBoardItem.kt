package com.tpv.android.model.internal

import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

data class DashBoardItem(var icon: Drawable? = null, var title: String? = "", var statusCount: String? = "-", var statusType: String? = "", var isSelfVerifiedEnable: Boolean = false) : Serializable, Parcelable {
    constructor(parcel: Parcel) : this(
            TODO("icon"),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readByte() != 0.toByte()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(statusCount)
        parcel.writeString(statusType)
        parcel.writeByte(if (isSelfVerifiedEnable) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DashBoardItem> {
        override fun createFromParcel(parcel: Parcel): DashBoardItem {
            return DashBoardItem(parcel)
        }

        override fun newArray(size: Int): Array<DashBoardItem?> {
            return arrayOfNulls(size)
        }
    }
}