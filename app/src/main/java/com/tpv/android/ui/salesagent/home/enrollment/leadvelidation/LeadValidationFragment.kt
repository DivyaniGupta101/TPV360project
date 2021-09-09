package com.tpv.android.ui.salesagent.home.enrollment.leadvelidation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.livinglifetechway.k4kotlin.core.onClick
import com.ravikoradiya.liveadapter.LiveAdapter
import com.tpv.android.BR
import com.tpv.android.R
import com.tpv.android.databinding.FragmentLeadValidationBinding
import com.tpv.android.databinding.ItemLeadValidationBinding
import com.tpv.android.helper.OnBackPressCallBack
import com.tpv.android.model.network.CancelLeadReq
import com.tpv.android.model.network.LeadVelidationError
import com.tpv.android.network.error.AlertErrorHandler
import com.tpv.android.network.resources.Resource
import com.tpv.android.network.resources.apierror.APIError
import com.tpv.android.network.resources.extensions.ifSuccess
import com.tpv.android.ui.salesagent.home.enrollment.SetEnrollViewModel
import com.tpv.android.ui.salesagent.home.enrollment.dynamicform.DynamicFormFragment
import com.tpv.android.utils.AppConstant
import com.tpv.android.utils.navigateSafe
import com.tpv.android.utils.setupToolbar

class LeadValidationFragment : Fragment(), OnBackPressCallBack {
    private lateinit var mBinding: FragmentLeadValidationBinding
    private lateinit var mViewModel: SetEnrollViewModel
    private var mViewGroup: ViewGroup? = null
    companion object{
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_lead_validation, container, false)
        mBinding.lifecycleOwner = this
        activity?.let { mViewModel = ViewModelProviders.of(it).get(SetEnrollViewModel::class.java) }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
    }

    private fun initialize() {

        mBinding.errorHandler = AlertErrorHandler(mBinding.root)

        val title = if (mViewModel.leadvelidationError?.errors?.size == 1) {
            getString(R.string.enroll_triggered_alert)
        } else {
            getString(R.string.enroll_triggered_alerts)
        }

        setupToolbar(mBinding.toolbar, "Alert!", showBackIcon = true,backIconClickListener = {
            if(mViewModel.addenrollement==true){
                mViewModel.custome_toolbar_clicked=true

            }
            mViewModel.customerback=true
            mViewModel.add_enrollement_value=mViewModel.secondclick
        })

        mBinding.title = title

        LiveAdapter(mViewModel.leadvelidationError?.errors, BR.item)
                .map<LeadVelidationError, ItemLeadValidationBinding>(R.layout.item_lead_validation)
                .into(mBinding.errorList)

        mBinding.btnCancel?.onClick {
            val liveData = mViewModel.cancelLeadDetail(mViewModel.leadvelidationError?.leadTempId
                    ?: "0",
                    CancelLeadReq(
                            source = AppConstant.ALERT
                    ))
            liveData.observe(this@LeadValidationFragment, Observer {
                it?.ifSuccess {
                    mViewModel.clearSavedData()
                    Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_leadVelidationFragment_to_dashBoardFragment)
                }
            })

            mBinding.resource = liveData as LiveData<Resource<Any, APIError>>

        }

        mBinding.btnYes?.onClick {
            navigateToInfo()
        }
    }

    private fun navigateToInfo() {
        Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_leadVelidationFragment_to_clientInfoFragment)
    }

    override fun handleOnBackPressed(): Boolean {
        mViewModel.customerback=true
        Log.e("backpressed", DynamicFormFragment.back_pressed.toString())
        mViewModel.add_enrollement_value=mViewModel.secondclick
        Log.e("addenrollement",mViewModel.add_enrollement_value.toString())
        if(mViewModel.addenrollement==true){
            mViewModel.custome_toolbar_clicked=true

        }
        return true
    }
}
