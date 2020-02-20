package com.tpv.android.ui.home.enrollment.success


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.CompoundButton
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.livinglifetechway.k4kotlin.core.onClick
import com.tpv.android.R
import com.tpv.android.databinding.FragmentSuccessBinding
import com.tpv.android.helper.OnBackPressCallBack
import com.tpv.android.model.network.SuccessReq
import com.tpv.android.network.error.AlertErrorHandler
import com.tpv.android.network.resources.Resource
import com.tpv.android.network.resources.apierror.APIError
import com.tpv.android.network.resources.extensions.ifSuccess
import com.tpv.android.ui.home.enrollment.SetEnrollViewModel
import com.tpv.android.utils.navigateSafe
import com.tpv.android.utils.setupToolbar

class SuccessFragment : Fragment(), OnBackPressCallBack {

    lateinit var mBinding: FragmentSuccessBinding
    lateinit var mViewModel: SetEnrollViewModel
    private var mVerificationType: ArrayList<String> = ArrayList()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_success, container, false)
        mBinding.lifecycleOwner = this
        activity?.let { mViewModel = ViewModelProviders.of(it).get(SetEnrollViewModel::class.java) }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
    }

    override fun onResume() {
        super.onResume()
    }


    /**
     * On click of backButton remove stored Data
     */
    override fun handleOnBackPressed(): Boolean {
        mViewModel.clearSavedData()
        return true
    }

    private fun initialize() {
        setupToolbar(mBinding.toolbar, getString(R.string.success))

        mBinding.errorHandler = AlertErrorHandler(mBinding.root)
        mBinding.item = mViewModel.savedLeadResp

        mVerificationType.add(getString(R.string.email))
        mVerificationType.add(getString(R.string.phone))

        mBinding.checkBoxEmail.setOnCheckedChangeListener { buttonView, isChecked ->
            getSelectedCheckBoxValue(isChecked, buttonView)
        }

        mBinding.checkBoxPhone.setOnCheckedChangeListener { buttonView, isChecked ->
            getSelectedCheckBoxValue(isChecked, buttonView)
        }

        mBinding.btnVerify.onClick {
            selfVerificationApiCall()
        }

        mBinding.textBackToDashBoard.onClick {
            mViewModel.clearSavedData()
            Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_successFragment_to_dashBoardFragment)
        }
    }

    /**
     * Check if @param isChecked is true then add @param buttonView's text in mVerificationType list
     * Else remove from mVerificationType list
     * Also check if list is empty then verify button should not be enabled
     */
    private fun getSelectedCheckBoxValue(isChecked: Boolean, buttonView: CompoundButton?) {
        if (isChecked) {
            mVerificationType.add(buttonView?.text.toString())
            mBinding.btnVerify.isEnabled = true

        } else {
            if (mVerificationType.isNotEmpty()) {
                mVerificationType.remove(mVerificationType.find { it == buttonView?.text.toString() })
            }

            mBinding.btnVerify.isEnabled = mVerificationType.isNotEmpty()

        }

    }

    private fun selfVerificationApiCall() {

        val liveData = mViewModel.selfVerification(SuccessReq(verificationType = mVerificationType.joinToString(separator = ","), leadId = mViewModel.savedLeadResp?.id))
        liveData.observe(this, Observer {
            it?.ifSuccess {
                mViewModel.clearSavedData()
                Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_successFragment_to_dashBoardFragment)
            }
        })

        mBinding.resource = liveData as LiveData<Resource<Any, APIError>>
    }
}
