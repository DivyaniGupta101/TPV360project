package com.tpv.android.model

import android.graphics.drawable.Drawable

data class DashBoardItem(var icon: Drawable? = null, var title: String? = "", var statusCount: String? = "-", var statusType: String? = "")