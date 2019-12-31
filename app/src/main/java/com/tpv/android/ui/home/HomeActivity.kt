package com.tpv.android.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.livinglifetechway.k4kotlin.core.hide
import com.livinglifetechway.k4kotlin.core.onClick
import com.livinglifetechway.k4kotlin.core.show
import com.livinglifetechway.k4kotlin.core.startActivity
import com.livinglifetechway.k4kotlin.databinding.setBindingView
import com.tpv.android.BuildConfig
import com.tpv.android.R
import com.tpv.android.databinding.ActivityHomeBinding
import com.tpv.android.helper.OnBackPressCallBack
import com.tpv.android.helper.Pref
import com.tpv.android.model.internal.DialogText
import com.tpv.android.model.internal.MenuItems
import com.tpv.android.network.error.AlertErrorHandler
import com.tpv.android.network.resources.Resource
import com.tpv.android.network.resources.apierror.APIError
import com.tpv.android.network.resources.extensions.ifFailure
import com.tpv.android.network.resources.extensions.ifSuccess
import com.tpv.android.ui.auth.AuthActivity
import com.tpv.android.utils.actionDialog
import com.tpv.android.utils.enums.MenuItem
import com.tpv.android.utils.navigateSafe


class HomeActivity : AppCompatActivity() {

    companion object {
        var ADDRESS_REQUEST_CODE = 5000
        var SERVICE_ADDRESS_REQUEST_CODE = 5000
        var BILLING_ADDRESS_REQUEST_CODE = 5000
    }

    lateinit var mBinding: ActivityHomeBinding
    lateinit var mNavController: NavController
    lateinit var mViewModel: HomeViewModel
    var mLastSelectedItem = MenuItem.DASHBOARD.value
    private lateinit var navigationHostFragment: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = setBindingView(R.layout.activity_home)
        mBinding.lifecycleOwner = this
        mViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        mNavController = Navigation.findNavController(this, R.id.navHostFragment)
        navigationHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        initialize()
    }

    private fun initialize() {

        mBinding.errorHandler = AlertErrorHandler(mBinding.root)

        setProfileData()

        mBinding.navMenu.menutItemList = arrayListOf(
                MenuItems(getDrawable(R.drawable.ic_menu_dashboard_white), getString(R.string.dashboard)),
                MenuItems(getDrawable(R.drawable.ic_menu_profile_white), getString(R.string.profile)),
                MenuItems(getDrawable(R.drawable.ic_register_white_32dp), getString(R.string.start_enrollment)),
                MenuItems(getDrawable(R.drawable.ic_logout_white_32dp), getString(R.string.log_out)))

        mBinding.navMenu.currentSelected = mLastSelectedItem

        if (BuildConfig.DEBUG) {
            mBinding.navMenu?.textSettings?.show()
        } else {
            mBinding.navMenu?.textSettings?.hide()
        }

        mBinding.navMenu?.layoutDashboard?.parentContainer?.onClick {
            menuItemSelection(MenuItem.DASHBOARD.value)
            closeDrawer()
            mNavController.navigateSafe(R.id.action_global_dashboardFragment)
        }

        mBinding.navMenu?.layoutProfile?.parentContainer?.onClick {
            menuItemSelection(MenuItem.PROFILE.value)
            closeDrawer()
            mNavController.navigateSafe(R.id.action_global_profileFragment)
        }

        mBinding.navMenu?.layoutSetEnroll?.parentContainer?.onClick {
            menuItemSelection(MenuItem.ENROLL.value)
            closeDrawer()
            mNavController.navigateSafe(R.id.action_global_commodityFragment)
        }

        mBinding.navMenu?.textSettings?.onClick {
            closeDrawer()
            startActivityForResult(Intent(this@HomeActivity, SettingsActivity::class.java), SettingsActivity.REQUEST_SETTINGS)
        }

        mBinding.navMenu?.layoutLogout?.parentContainer?.onClick {
            mBinding.navMenu.currentSelected = MenuItem.LOGOUT.value
            closeDrawer()
            actionDialog(DialogText(getString(R.string.log_out),
                    getString(R.string.msg_log_out),
                    getString(R.string.yes),
                    getString(R.string.cancel)), setOnDismissListener = {
                mBinding.navMenu.currentSelected = mLastSelectedItem
            }, setOnPositiveBtnClickLisener = {
                context.logOutApiCall()
            })
        }
    }

    private fun getProfileApiCall() {
        mViewModel.getProfile().observe(this, Observer {
            it.ifSuccess { userDetail ->
                mBinding.navMenu.item = Pref.user
            }
        })
    }

    private fun Context.logOutApiCall() {
        val liveData = mViewModel.logout()
        liveData.observe(this@HomeActivity, Observer {
            it?.ifSuccess {
                this.startActivity<AuthActivity>()
                finish()
            }
            it?.ifFailure { throwable, errorData ->
                this.startActivity<AuthActivity>()
                finish()
            }

        })

        mBinding.resource = liveData as LiveData<Resource<Any, APIError>>
    }

    /**
     * get stored profile data and set in menu item
     */
    fun setProfileData() {
        if (Pref.user == null) {
            getProfileApiCall()
        } else {
            mBinding.navMenu.item = Pref.user
        }
    }

    /**
     * set slideMenuItem selection as per @param:item value
     */
    fun menuItemSelection(item: String) {
        mLastSelectedItem = item
        mBinding.navMenu.currentSelected = item
    }

    /**
     * open slide menu
     */
    fun openMenu() = mBinding.drawerLayout.openDrawer(GravityCompat.END)


    /**
     * lock swipe to open menu
     */
    fun lockSwipeModeMenu() = mBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)


    /**
     * unlock swipe to open menu
     */
    fun unLockSwipeModeMenu() = mBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)


    override fun onBackPressed() {
        if (mBinding.drawerLayout.isDrawerOpen(GravityCompat.END)) {
            closeDrawer()
        } else {
            val fragment = navigationHostFragment.childFragmentManager.fragments.first()
            if ((fragment is OnBackPressCallBack)) fragment.handleOnBackPressed()
            super.onBackPressed()
        }

    }

    /**
     * close drawer
     */
    private fun closeDrawer() = mBinding.drawerLayout.closeDrawer(GravityCompat.END)

}
