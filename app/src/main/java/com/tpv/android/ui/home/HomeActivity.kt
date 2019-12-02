package com.tpv.android.ui.home

import android.content.Intent
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
import androidx.navigation.fragment.NavHostFragment
import com.livinglifetechway.k4kotlin.core.onClick
import com.livinglifetechway.k4kotlin.core.startActivity
import com.livinglifetechway.k4kotlin.databinding.setBindingView
import com.tpv.android.R
import com.tpv.android.databinding.ActivityHomeBinding
import com.tpv.android.databinding.DialogLogoutBinding
import com.tpv.android.helper.OnBackPressCallBack
import com.tpv.android.helper.Pref
import com.tpv.android.model.DialogText
import com.tpv.android.model.MenuItems
import com.tpv.android.network.error.AlertErrorHandler
import com.tpv.android.network.resources.Resource
import com.tpv.android.network.resources.apierror.APIError
import com.tpv.android.network.resources.extensions.ifFailure
import com.tpv.android.network.resources.extensions.ifSuccess
import com.tpv.android.ui.auth.AuthActivity
import com.tpv.android.ui.home.enrollment.statement.StatementFragment
import com.tpv.android.utils.MenuItem
import com.tpv.android.utils.navigateSafe


class HomeActivity : AppCompatActivity() {

    companion object {
        var REQUEST_CHECK_SETTINGS = 1234
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
        navigationHostFragment =
                supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment

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

        mBinding.navMenu.menutItemList = arrayListOf(
                MenuItems(getDrawable(R.drawable.ic_menu_dashboard_white), getString(R.string.dashboard)),
                MenuItems(getDrawable(R.drawable.ic_menu_profile_white), getString(R.string.profile)),
                MenuItems(getDrawable(R.drawable.ic_register_white_32dp), getString(R.string.start_enrollment)),
                MenuItems(getDrawable(R.drawable.ic_logout_white_32dp), getString(R.string.log_out)))

        handleItemMenu(MenuItem.DASHBOARD.value)

        mBinding.navMenu?.layoutDashboard?.parentContainer?.onClick {
            mBinding.navMenu.currentSelected = MenuItem.DASHBOARD.value
            mLastSelectedItem = MenuItem.DASHBOARD.value
            mBinding.drawerLayout.closeDrawer(GravityCompat.END)
            mNavController.navigateSafe(R.id.action_global_dashboardFragment)
        }

        mBinding.navMenu?.layoutProfile?.parentContainer?.onClick {
            mBinding.navMenu.currentSelected = MenuItem.PROFILE.value
            mLastSelectedItem = MenuItem.PROFILE.value
            mBinding.drawerLayout.closeDrawer(GravityCompat.END)
            mNavController.navigateSafe(R.id.action_global_profileFragment)
        }

        mBinding.navMenu?.layoutSetEnroll?.parentContainer?.onClick {
            mBinding.navMenu.currentSelected = MenuItem.ENROLL.value
            mLastSelectedItem = MenuItem.ENROLL.value
            mBinding.drawerLayout.closeDrawer(GravityCompat.END)
            mNavController.navigateSafe(R.id.action_global_commodityFragment)
        }

        mBinding.navMenu?.layoutLogout?.parentContainer?.onClick {
            mBinding.navMenu.currentSelected = MenuItem.LOGOUT.value
            mBinding.drawerLayout.closeDrawer(GravityCompat.END)
            openLogOutDialog()
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

    fun handleItemMenu(item: String) {
        mLastSelectedItem = item
        mBinding.navMenu.currentSelected = item
    }

    private fun openLogOutDialog() {
        val binding = DataBindingUtil.inflate<DialogLogoutBinding>(layoutInflater, R.layout.dialog_logout, null, false)
        val dialog = AlertDialog.Builder(this@HomeActivity)
                .setView(binding.root).show()

        binding.item = DialogText(getString(R.string.log_out),
                getString(R.string.msg_log_out),
                getString(R.string.yes),
                getString(R.string.cancel))

        dialog?.setOnDismissListener {
            mBinding.navMenu.currentSelected = mLastSelectedItem
        }
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
            val fragment = navigationHostFragment.childFragmentManager.fragments.first()
            if ((fragment is OnBackPressCallBack)) fragment.handleOnBackPressed()
            super.onBackPressed()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == StatementFragment.REQUEST_CHECK_SETTINGS) {
            StatementFragment().onActivityResult(requestCode, resultCode, data);
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

}
