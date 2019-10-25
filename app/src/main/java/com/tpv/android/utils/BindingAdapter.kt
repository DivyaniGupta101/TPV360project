package com.tpv.android.utils

import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.livinglifetechway.k4kotlin.core.androidx.color
import com.tpv.android.R

object BindingAdapter {

    @JvmStatic
    @BindingAdapter("dynamicTintColor")
    fun setDynamicTintColor(imageView: ImageView, color: Int) {
        imageView.setColorFilter(
                ContextCompat.getColor(imageView.context, color),
                android.graphics.PorterDuff.Mode.MULTIPLY
        )
    }


    @JvmStatic
    @BindingAdapter("leadStatus")
    fun setLeadStatus(textView: TextView, status: String) {
        val context = textView.context
        when (status) {
            AppConstant.PENDING -> {
                textView.setText(context?.getString(R.string.pending))
                textView.setTextColor(context.color(R.color.colorPendingText))
            }
            AppConstant.VERIFIED -> {
                textView.setText(context?.getString(R.string.verified))
                textView.setTextColor(context.color(R.color.colorVerifiedText))
            }

            AppConstant.DECLINED -> {
                textView.setText(context?.getString(R.string.decline))
                textView.setTextColor(context.color(R.color.colorDecliendText))
            }

            AppConstant.HANGUP -> {
                textView.setText(context?.getString(R.string.hang_up))
                textView.setTextColor(context.color(R.color.colorHangUpText))
            }
        }
    }
}