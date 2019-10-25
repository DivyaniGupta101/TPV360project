package com.tpv.android.ui.home

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.livinglifetechway.k4kotlin.core.hide
import com.livinglifetechway.k4kotlin.core.onClick
import com.livinglifetechway.k4kotlin.core.show
import com.livinglifetechway.k4kotlin.core.startActivity
import com.livinglifetechway.k4kotlin.databinding.setBindingView
import com.tpv.android.R
import com.tpv.android.databinding.ActivityHomeBinding
import com.tpv.android.ui.auth.AuthActivity
import kotlinx.android.synthetic.main.dialog_logout.view.*

class HomeActivity : AppCompatActivity() {


    lateinit var mBinding: ActivityHomeBinding
        lateinit var mNavController: NavController
    private var DASHBOARD = 1
    private var PROFILE = 2
    private var ENROLL = 3
    private var LOGOUT = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = setBindingView(R.layout.activity_home)
        mNavController = Navigation.findNavController(this, R.id.navHostFragment)
        mBinding.navMenu?.dashboardContainer?.onClick {
            handleItemMenu(DASHBOARD)
        }

        mBinding.navMenu?.profileContainer?.onClick {
            handleItemMenu(PROFILE)
        }

        mBinding.navMenu?.enrollmentContainer?.onClick {
            handleItemMenu(ENROLL)
        }

        mBinding.navMenu?.logoutContainer?.onClick {
            handleItemMenu(LOGOUT)
        }
    }


    private fun handleItemMenu(item: Int) {

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
                mNavController.navigate(R.id.action_global_dashboardFragment)
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
                mBinding.navMenu?.darkViewLogout?.show()
                mBinding.navMenu?.lightViewLogout?.show()
                mBinding.drawerLayout.closeDrawer(GravityCompat.END)
                openLogOutDialog()
            }
        }

    }

    private fun openLogOutDialog() {
        val view = LayoutInflater.from(this@HomeActivity).inflate(R.layout.dialog_logout, null)
        val dialog = AlertDialog.Builder(this@HomeActivity)
                .setView(view).show()

        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

        view.btnCancel.onClick {
            dialog.dismiss()
        }
        view.btnYes.onClick {
            dialog.dismiss()
            context.startActivity<AuthActivity>()
            finish()
        }
    }

    fun openMenu() {
        mBinding.drawerLayout.openDrawer(GravityCompat.END)
    }

}
