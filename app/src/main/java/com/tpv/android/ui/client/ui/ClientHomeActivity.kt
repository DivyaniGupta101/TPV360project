package com.tpv.android.ui.client.ui

import android.content.Context
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
import com.livinglifetechway.k4kotlin.core.onClick
import com.livinglifetechway.k4kotlin.core.startActivity
import com.livinglifetechway.k4kotlin.databinding.setBindingView
import com.tpv.android.R
import com.tpv.android.databinding.ActivityClientHomeBinding
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
import com.tpv.android.utils.enums.ClientMenuItem
import com.tpv.android.utils.navigateSafe

class ClientHomeActivity : AppCompatActivity() {
    lateinit var mBinding: ActivityClientHomeBinding
    lateinit var mNavController: NavController
    private lateinit var navigationHostFragment: NavHostFragment
    lateinit var mViewModel: ClientHomeViewModel
    var mLastSelectedItem = ClientMenuItem.DASHBOARD.value


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = setBindingView(R.layout.activity_client_home)
        mBinding.lifecycleOwner = this
        mViewModel = ViewModelProviders.of(this).get(ClientHomeViewModel::class.java)
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
                MenuItems(getDrawable(R.drawable.ic_register_white_32dp), getString(R.string.reports)),
                MenuItems(getDrawable(R.drawable.ic_menu_logout_white_32dp), getString(R.string.log_out))
        )

        mBinding.navMenu.currentSelected = mLastSelectedItem

        mBinding.navMenu?.layoutDashboard?.parentContainer?.onClick {
            menuItemSelection(ClientMenuItem.DASHBOARD.value)
            closeDrawer()
            mNavController.navigateSafe(R.id.action_global_clientDashBoardFragment)
        }

        mBinding.navMenu?.layoutProfile?.parentContainer?.onClick {
            menuItemSelection(ClientMenuItem.PROFILE.value)
            closeDrawer()
            mNavController.navigateSafe(R.id.action_global_clientProfileFragment)
        }

        mBinding.navMenu?.layoutReports?.parentContainer?.onClick {
            menuItemSelection(ClientMenuItem.REPORTS.value)
            closeDrawer()
//            mNavController.navigateSafe(R.id.action_global_clockTimeFragment)
        }


        mBinding.navMenu?.layoutLogout?.parentContainer?.onClick {
            mBinding.navMenu.currentSelected = ClientMenuItem.LOGOUT.value
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


    private fun Context.logOutApiCall() {
        val liveData = mViewModel.logout()
        liveData.observe(this@ClientHomeActivity, Observer {
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

    private fun getProfileApiCall() {
        mViewModel.getProfile().observe(this, Observer {
            it.ifSuccess { userDetail ->
                mBinding.navMenu.item = Pref.user
            }
        })
    }

    /**
     * close drawer
     */
    private fun closeDrawer() = mBinding.drawerLayout.closeDrawer(GravityCompat.END)

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

}