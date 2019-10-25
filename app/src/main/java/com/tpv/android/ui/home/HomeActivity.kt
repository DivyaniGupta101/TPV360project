package com.tpv.android.ui.home

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.livinglifetechway.k4kotlin.core.hide
import com.livinglifetechway.k4kotlin.core.onClick
import com.livinglifetechway.k4kotlin.core.show
import com.livinglifetechway.k4kotlin.core.startActivity
import com.livinglifetechway.k4kotlin.databinding.setBindingView
import com.tpv.android.R
import com.tpv.android.databinding.ActivityHomeBinding
import com.tpv.android.databinding.DialogLogoutBinding
import com.tpv.android.ui.auth.AuthActivity

class HomeActivity : AppCompatActivity() {


    companion object {
        var DASHBOARD = 1
        var PROFILE = 2
        var ENROLL = 3
        var LOGOUT = 4
    }

    lateinit var mBinding: ActivityHomeBinding
    lateinit var mNavController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = setBindingView(R.layout.activity_home)
        mNavController = Navigation.findNavController(this, R.id.navHostFragment)

        mBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

        handleItemMenu(DASHBOARD)

        mBinding.navMenu?.dashboardContainer?.onClick {
            handleItemMenu(DASHBOARD)
            mNavController.navigate(R.id.action_global_dashboardFragment)
        }

        mBinding.navMenu?.profileContainer?.onClick {
            handleItemMenu(PROFILE)
            mNavController.navigate(R.id.action_global_profileFragment)
        }

        mBinding.navMenu?.enrollmentContainer?.onClick {
            handleItemMenu(ENROLL)
        }

        mBinding.navMenu?.logoutContainer?.onClick {
            handleItemMenu(LOGOUT)
        }
    }


    fun handleItemMenu(item: Int) {

        mBinding.navMenu?.darkViewDashBoard?.hide()
        mBinding.navMenu?.lightViewDashBoard?.hide()
        mBinding.navMenu?.darkViewProfile?.hide()
        mBinding.navMenu?.lightViewProfile?.hide()
        mBinding.navMenu?.darkViewEnrollment?.hide()
        mBinding.navMenu?.lightViewEnrollment?.hide()
        mBinding.navMenu?.darkViewLogout?.hide()
        mBinding.navMenu?.lightViewLogout?.hide()

        when (item) {
            DASHBOARD -> {
                mBinding.navMenu?.darkViewDashBoard?.show()
                mBinding.navMenu?.lightViewDashBoard?.show()
                mBinding.drawerLayout.closeDrawer(GravityCompat.END)
            }

            PROFILE -> {
                mBinding.navMenu?.darkViewProfile?.show()
                mBinding.navMenu?.lightViewProfile?.show()
                mBinding.drawerLayout.closeDrawer(GravityCompat.END)
            }

            ENROLL -> {
                mBinding.navMenu?.darkViewEnrollment?.show()
                mBinding.navMenu?.lightViewEnrollment?.show()
                mBinding.drawerLayout.closeDrawer(GravityCompat.END)
            }

            LOGOUT -> {
                mBinding.drawerLayout.closeDrawer(GravityCompat.END)
                openLogOutDialog()
            }
        }

    }

    private fun openLogOutDialog() {
        val binding = DataBindingUtil.inflate<DialogLogoutBinding>(layoutInflater, R.layout.dialog_logout, null, false)
        val dialog = AlertDialog.Builder(this@HomeActivity)
                .setView(binding.root).show()

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding?.btnCancel?.onClick {
            dialog.dismiss()
        }
        binding?.btnYes?.onClick {
            dialog.dismiss()
            context.startActivity<AuthActivity>()
            finish()
        }
    }

    fun openMenu() {
        mBinding.drawerLayout.openDrawer(GravityCompat.END)
    }

}
