package com.tpv.android.ui.home

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.livinglifetechway.k4kotlin.core.androidx.color
import com.livinglifetechway.k4kotlin.core.hide
import com.livinglifetechway.k4kotlin.core.onClick
import com.livinglifetechway.k4kotlin.core.show
import com.livinglifetechway.k4kotlin.core.startActivity
import com.livinglifetechway.k4kotlin.databinding.setBindingView
import com.tpv.android.R
import com.tpv.android.databinding.ActivityHomeBinding
import com.tpv.android.databinding.DialogLogoutBinding
import com.tpv.android.helper.Pref
import com.tpv.android.network.error.AlertErrorHandler
import com.tpv.android.network.resources.Resource
import com.tpv.android.network.resources.apierror.APIError
import com.tpv.android.network.resources.extensions.ifFailure
import com.tpv.android.network.resources.extensions.ifSuccess
import com.tpv.android.ui.auth.AuthActivity
import com.tpv.android.utils.navigateSafe


class HomeActivity : AppCompatActivity() {


    companion object {
        var DASHBOARD = 1
        var PROFILE = 2
        var ENROLL = 3
        var LOGOUT = 4
    }

    lateinit var mBinding: ActivityHomeBinding
    lateinit var mNavController: NavController
    lateinit var mViewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = setBindingView(R.layout.activity_home)
        mBinding.lifecycleOwner = this
        mViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        mNavController = Navigation.findNavController(this, R.id.navHostFragment)


        handleStatusBarColor()



        mBinding.errorHandler = AlertErrorHandler(mBinding.root)

        if (Pref.user == null) {
            mViewModel.getProfile().observe(this, Observer {
                it.ifSuccess { userDetail ->
                    mBinding.navMenu.item = Pref.user
                }
            })
        } else {
            setMenuProfileData()
        }

        handleItemMenu(DASHBOARD)

        mBinding.navMenu?.dashboardContainer?.onClick {
            handleItemMenu(DASHBOARD)
            mNavController.navigateSafe(R.id.action_global_dashboardFragment)
        }

        mBinding.navMenu?.profileContainer?.onClick {
            handleItemMenu(PROFILE)
            mNavController.navigateSafe(R.id.action_global_profileFragment)
        }

        mBinding.navMenu?.enrollmentContainer?.onClick {
            handleItemMenu(ENROLL)
            mNavController.navigateSafe(R.id.action_global_plansListFragment)
        }

        mBinding.navMenu?.logoutContainer?.onClick {
            handleItemMenu(LOGOUT)
        }
    }

    private fun handleStatusBarColor() {
        val window = getWindow()

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this@HomeActivity, R.color.colorStatusBarColor))

    }

    fun setMenuProfileData() {
        mBinding.navMenu.item = Pref.user
    }

    fun handleItemMenu(item: Int) {

        mBinding.navMenu?.darkViewDashBoard?.hide()
        mBinding.navMenu?.dashboardContainer?.background = null
        mBinding.navMenu?.darkViewProfile?.hide()
        mBinding.navMenu?.profileContainer?.background = null
        mBinding.navMenu?.darkViewEnrollment?.hide()
        mBinding.navMenu?.enrollmentContainer?.background = null
        mBinding.navMenu?.darkViewLogout?.hide()
        mBinding.navMenu?.logoutContainer?.background = null

        when (item) {
            DASHBOARD -> {
                mBinding.navMenu?.darkViewDashBoard?.show()
                mBinding.navMenu?.dashboardContainer?.setBackgroundColor(this.color(R.color.colorMenuLightHighLight))
                mBinding.drawerLayout.closeDrawer(GravityCompat.END)
            }

            PROFILE -> {
                mBinding.navMenu?.darkViewProfile?.show()
                mBinding.navMenu?.profileContainer?.setBackgroundColor(this.color(R.color.colorMenuLightHighLight))
                mBinding.drawerLayout.closeDrawer(GravityCompat.END)
            }

            ENROLL -> {
                mBinding.navMenu?.darkViewEnrollment?.show()
                mBinding.navMenu?.enrollmentContainer?.setBackgroundColor(this.color(R.color.colorMenuLightHighLight))
                mBinding.drawerLayout.closeDrawer(GravityCompat.END)
            }

            LOGOUT -> {
                mBinding.drawerLayout.closeDrawer(GravityCompat.END)
                mBinding.navMenu?.logoutContainer?.setBackgroundColor(this.color(R.color.colorMenuLightHighLight))
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
            val liveData = mViewModel.logout()
            liveData.observe(this@HomeActivity, Observer {
                it?.ifSuccess {
                    context.startActivity<AuthActivity>()
                    finish()
                }
                it?.ifFailure { throwable, errorData ->
                    context.startActivity<AuthActivity>()
                    finish()
                }

            })

            mBinding.resource = liveData as LiveData<Resource<Any, APIError>>
            dialog.dismiss()


        }
    }

    fun openMenu() {
        mBinding.drawerLayout.openDrawer(GravityCompat.END)
    }


    fun lockSwipeModeMenu() {
        mBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    fun unLockSwipeModeMenu() {
        mBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }

    override fun onBackPressed() {
        if (mBinding.drawerLayout.isDrawerOpen(GravityCompat.END)) {
            mBinding.drawerLayout.closeDrawer(GravityCompat.END)
        } else {
            super.onBackPressed()

        }

    }

}
