package com.tpv.android.ui.salesagent.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.livinglifetechway.k4kotlin.core.*
import com.livinglifetechway.k4kotlin.databinding.setBindingView
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
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
import com.tpv.android.ui.salesagent.NotificationForegroundService
import com.tpv.android.utils.*
import com.tpv.android.utils.enums.MenuItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


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
    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)
    private var locationManager: LocationManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = setBindingView(R.layout.activity_home)
        mBinding.lifecycleOwner = this
        mViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        mNavController = Navigation.findNavController(this, R.id.navHostFragment)
        navigationHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        initialize()
//        supportFragmentManager.transact {
//            replace(R.id.homeMainContainer, FilePickerFragment())
//        }
    }

    private fun initialize() {

        mBinding.errorHandler = AlertErrorHandler(mBinding.root)

        getCurrentActivity()
        setProfileData()

        mBinding.navMenu.menutItemList = arrayListOf(
                MenuItems(getDrawable(R.drawable.ic_menu_dashboard_white), getString(R.string.dashboard)),
                MenuItems(getDrawable(R.drawable.ic_menu_profile_white), getString(R.string.profile)),
                MenuItems(getDrawable(R.drawable.ic_register_white_32dp), getString(R.string.start_enrollment)),
                MenuItems(getDrawable(R.drawable.ic_menu_email_white_32dp), getString(R.string.support)),
                MenuItems(getDrawable(R.drawable.ic_menu_white_access_time_32dp), getString(R.string.time_clock)),
                MenuItems(getDrawable(R.drawable.ic_menu_logout_white_32dp), getString(R.string.log_out)))

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

        mBinding.navMenu?.layoutTimeClock?.parentContainer?.onClick {
            menuItemSelection(MenuItem.TIMECLOCK.value)
            closeDrawer()
            mNavController.navigateSafe(R.id.action_global_clockTimeFragment)
        }

        mBinding.navMenu?.layoutSupport?.parentContainer?.onClick {
            menuItemSelection(MenuItem.SUPPORT.value)
            closeDrawer()
            mNavController.navigateSafe(R.id.action_global_supportFragment)
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
            }, setOnPositiveBanClickListener = {
                context.logOutApiCall()
            })
        }

    }

    private fun getCurrentActivity() {
        getLocation()
        val liveData = mViewModel.getCurrentActivity()
        liveData.observe(this, Observer {
            it.ifSuccess {
                if (it?.isClockIn.orFalse()) {
                    startForeGroundService()
                }

            }
        })
        mBinding.resource = liveData as LiveData<Resource<Any, APIError>>
    }

    private fun startForeGroundService() {
        val intent = Intent(this, NotificationForegroundService::class.java)
        intent.putExtra(NotificationForegroundService.LOCATIONKEY, mViewModel.location)
        this.startService(intent)
    }

    private fun stopForeGroundService() {
        val intent = Intent(this, NotificationForegroundService::class.java)
        intent.action = NotificationForegroundService.STOPACTION
        this?.startService(intent)
    }

    private fun getProfileApiCall() {
        mViewModel.getProfile().observe(this, Observer {
            it.ifSuccess { userDetail ->
                mBinding.navMenu.item = Pref.user
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (!Screenshot.allow)
            this?.window?.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
    }

    private fun Context.logOutApiCall() {
        val liveData = mViewModel.logout()
        liveData.observe(this@HomeActivity, Observer {
            it?.ifSuccess {
                stopForeGroundService()
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

    @SuppressLint("MissingPermission")
    private fun getLocation() = runWithPermissions(
            *checkPermission()
    ) {
        uiScope.launch {
            locationManager = this@HomeActivity.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            mViewModel.location = this@HomeActivity.let { LocationHelper.getLastKnownLocation(it) }

            if (mViewModel.location == null) {
                startActivityForResult(Intent(this@HomeActivity, TransparentActivity::class.java), TransparentActivity.REQUEST_CHECK_SETTINGS)
            } else {
                if (!locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER).orFalse()) {
                    this@HomeActivity.infoDialog(subTitleText = getString(R.string.msg_gps_location))
                }
            }
        }
    }

}
