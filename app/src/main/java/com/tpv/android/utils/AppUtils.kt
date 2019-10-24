package com.tpv.android.utils

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.livinglifetechway.k4kotlin.core.onClick
import com.livinglifetechway.k4kotlin.core.show
import com.tpv.android.R
import com.tpv.android.ui.home.HomeActivity


fun Fragment.setupToolbar(
        toolbarContainer: View?,
        title: String = "",
        showMenuIcon: Boolean = false,
        showBackIcon: Boolean = false,
        backIconClickListener: (() -> Unit)? = null
) {

    when (activity) {
        is HomeActivity -> {

            val titleText = toolbarContainer?.findViewById<TextView>(R.id.textToolbarTitle)
            val backImage = toolbarContainer?.findViewById<ImageView>(R.id.imageToolbarBack)
            val menuImage = toolbarContainer?.findViewById<ImageView>(R.id.imageToolbarMenu)

            titleText?.setText(title)

            if (showBackIcon) {
                backImage?.show()
                backImage?.onClick {
                    Navigation.findNavController(toolbarContainer).navigateUp()
                }
            }

            if (showMenuIcon) {
                menuImage?.show()
                menuImage?.onClick {
                    (activity as HomeActivity).openMenu()
                }
            }
        }
    }


}