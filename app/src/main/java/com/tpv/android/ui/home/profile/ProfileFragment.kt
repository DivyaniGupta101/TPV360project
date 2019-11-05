package com.tpv.android.ui.home.profile


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.tpv.android.R
import com.tpv.android.databinding.FragmentProfileBinding
import com.tpv.android.network.error.AlertErrorHandler
import com.tpv.android.network.resources.APIError
import com.tpv.android.network.resources.Resource
import com.tpv.android.network.resources.ifSuccess
import com.tpv.android.ui.home.HomeActivity
import com.tpv.android.utils.setItemSelection
import com.tpv.android.utils.setupToolbar

/**
 * A simple [Fragment] subclass.
 */
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
        setupToolbar(mBinding.toolbar, getString(R.string.profile), false, true)
        getUserProfile()
    }

    private fun getUserProfile() {

        mBinding.errorHandler = AlertErrorHandler(mBinding.root)
        val liveData = mViewModel.getProfile()
        liveData.observe(viewLifecycleOwner, Observer {
            it.ifSuccess {
                mBinding.item = it

            }
        })
        mBinding.resource = liveData as LiveData<Resource<Any, APIError>>

    }

    override fun onResume() {
        super.onResume()
        setItemSelection(HomeActivity.PROFILE)
    }
}
