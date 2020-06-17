package com.tpv.android.ui.client.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.livinglifetechway.k4kotlin.databinding.setBindingView
import com.tpv.android.R
import com.tpv.android.databinding.ActivityClientHomeBinding
import com.tpv.android.helper.Pref
import com.tpv.android.model.internal.MenuItems
import com.tpv.android.network.error.AlertErrorHandler
import com.tpv.android.network.resources.extensions.ifSuccess
import com.tpv.android.utils.enums.ClientMenuItem

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
                MenuItems(getDrawable(R.drawable.ic_register_white_32dp), getString(R.string.reports))
        )

        mBinding.navMenu.currentSelected = mLastSelectedItem


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
}