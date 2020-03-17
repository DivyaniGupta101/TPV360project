package com.tpv.android.ui.home.enrollment.leadvelidation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.livinglifetechway.k4kotlin.core.onClick
import com.ravikoradiya.liveadapter.LiveAdapter
import com.tpv.android.BR
import com.tpv.android.R
import com.tpv.android.databinding.FragmentLeadVelidationBinding
import com.tpv.android.databinding.ItemLeadVelidationBinding
import com.tpv.android.model.network.LeadVelidationError
import com.tpv.android.network.resources.extensions.ifSuccess
import com.tpv.android.ui.home.enrollment.SetEnrollViewModel
import com.tpv.android.utils.navigateSafe
import com.tpv.android.utils.setupToolbar

class LeadVelidationFragment : Fragment() {
    private lateinit var mBinding: FragmentLeadVelidationBinding
    private lateinit var mViewModel: SetEnrollViewModel
    private var mViewGroup: ViewGroup? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_lead_velidation, container, false)
        mBinding.lifecycleOwner = this
        activity?.let { mViewModel = ViewModelProviders.of(it).get(SetEnrollViewModel::class.java) }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
    }

    private fun initialize() {

        val title = if (mViewModel.leadvelidationError?.errors?.size == 1) {
            "This enrollment triggered the following alert:"
        } else {
            "This enrollment triggered the following alerts:"
        }

        setupToolbar(mBinding.toolbar, "Alert!", showBackIcon = true)

        mBinding.title = title

        LiveAdapter(mViewModel.leadvelidationError?.errors, BR.item)
                .map<LeadVelidationError, ItemLeadVelidationBinding>(R.layout.item_lead_velidation)
                .into(mBinding.errorList)

        mBinding.btnCancel?.onClick {
            mViewModel.cancelLeadDetail(mViewModel.leadvelidationError?.leadTempId
                    ?: "0").observe(this@LeadVelidationFragment, Observer {
                it?.ifSuccess {
                    mViewModel.clearSavedData()
                    Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_leadVelidationFragment_to_dashBoardFragment)
                }
            })
        }

        mBinding.btnYes?.onClick {
            navigateToInfo()
        }
    }

    private fun navigateToInfo() {
        Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_leadVelidationFragment_to_clientInfoFragment)
    }
}
