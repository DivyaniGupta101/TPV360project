package com.tpv.android.utils

import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter

object BindingAdapter {

    @JvmStatic
    @BindingAdapter("dynamicTintColor")
    fun setDynamicTintColor(imageView: ImageView, color: Int) {
        imageView.setColorFilter(
                ContextCompat.getColor(imageView.context, color),
                android.graphics.PorterDuff.Mode.MULTIPLY
        )
    }
}