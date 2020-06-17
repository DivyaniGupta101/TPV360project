package com.tpv.android.ui.salesagent.home.profile


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.tpv.android.R
import com.tpv.android.databinding.FragmentProfileBinding
import com.tpv.android.helper.Pref
import com.tpv.android.network.resources.extensions.ifSuccess
import com.tpv.android.utils.enums.MenuItem
import com.tpv.android.utils.setItemSelection
import com.tpv.android.utils.setupToolbar
import com.tpv.android.utils.updateProfileInMenu

class ProfileFragment : Fragment() {

    lateinit var mBinding: FragmentProfileBinding
    private lateinit var mViewModel: ProfileViewModel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        mBinding.lifecycleOwner = this
        mViewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
    }

    override fun onResume() {
        super.onResume()
        setItemSelection(MenuItem.PROFILE.value)
    }

    private fun initialize() {
        setupToolbar(mBinding.toolbar, getString(R.string.profile), true, true)

        mBinding.item = Pref.user

        getProfileApiCall()
    }

    private fun getProfileApiCall() {

        mViewModel.getProfile().observe(viewLifecycleOwner, Observer {
            it.ifSuccess {
                updateProfileInMenu()
            }
        })
    }
}
