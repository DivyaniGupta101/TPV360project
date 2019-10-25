package com.tpv.android.utils

import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.livinglifetechway.k4kotlin.core.onClick
import com.livinglifetechway.k4kotlin.core.show
import com.tpv.android.databinding.ToolbarBinding
import com.tpv.android.ui.home.HomeActivity


fun Fragment.setupToolbar(
        toolbarContainer: ToolbarBinding,
        title: String = "",
        showMenuIcon: Boolean = false,
        showBackIcon: Boolean = false,
        backIconClickListener: (() -> Unit)? = null
) {

    when (activity) {
        is HomeActivity -> {

            toolbarContainer.textToolbarTitle.text = title

            val backImage = toolbarContainer.imageToolbarBack
            if (showBackIcon) {
                backImage?.show()
                backImage?.onClick {
                    Navigation.findNavController(toolbarContainer.root).navigateUp()
                }
            }

            val menuImage = toolbarContainer.imageToolbarMenu
            if (showMenuIcon) {
                menuImage?.show()
                menuImage?.onClick {
                    (activity as HomeActivity).openMenu()
                }
            }
        }
    }


}