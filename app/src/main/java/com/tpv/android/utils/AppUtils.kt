package com.tpv.android.utils

import android.os.Bundle
import android.util.Log
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.navigation.*
import com.livinglifetechway.k4kotlin.core.hide
import com.livinglifetechway.k4kotlin.core.invisible
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
            } else {
                backImage?.hide()
            }

            val menuImage = toolbarContainer.imageToolbarMenu
            if (showMenuIcon) {
                menuImage?.show()
                (activity as HomeActivity).unLockSwipeModeMenu()
                menuImage?.onClick {
                    (activity as HomeActivity).openMenu()
                }
            } else {
                (activity as HomeActivity).lockSwipeModeMenu()
                menuImage?.invisible()
            }
        }
    }


}

fun Fragment.setItemSelection(item: Int) {
    when (activity) {
        is HomeActivity -> {
            (activity as HomeActivity).handleItemMenu(item)
        }
    }
}

fun Fragment.updateProfileInMenu() {
    when (activity) {
        is HomeActivity -> {
            (activity as HomeActivity).setMenuProfileData()
        }
    }
}

enum class Plan(val value: String) {
    DUALFUEL("dualfuel"),
    GASFUEL("gasfuel"),
    ELECTRICFUEL("electricityfuel")
}

enum class LeadStatus(val value: String) {
    PENDING("pending"),
    VERIFIED("verified"),
    DECLINED("decline"),
    HANGUP("hangup")
}

/**
 * check requested action in current destination of navigation controller
 */
fun NavController.navigateSafe(
        @IdRes resId: Int,
        args: Bundle? = null,
        navOptions: NavOptions? = null,
        navExtras: Navigator.Extras? = null
) {
    try {
        val action = currentDestination?.getAction(resId)
        if (action != null) navigate(resId, args, navOptions, navExtras)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * check requested action in current destination of navigation controller
 */
fun NavController.navigateSafe(
        navDirections: NavDirections
) {
    try {
        val action = currentDestination?.getAction(navDirections.actionId)
        if (action != null) navigate(navDirections.actionId, navDirections.arguments, null, null)
    } catch (e: Exception) {
        Log.e("TAG", e.message)
        e.printStackTrace()
    }
}