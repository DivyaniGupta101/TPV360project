package com.tpv.android.ui.home.enrollment.success


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.tpv.android.model.CustomerData
import com.tpv.android.model.SuccessReq
import com.tpv.android.network.error.AlertErrorHandler
import com.tpv.android.network.resources.Resource
import com.tpv.android.network.resources.apierror.APIError
import com.tpv.android.network.resources.extensions.ifSuccess
import com.tpv.android.ui.home.enrollment.SetEnrollViewModel
import com.tpv.android.utils.navigateSafe
import com.tpv.android.utils.setupToolbar

class SuccessFragment : Fragment() {

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

        mBinding.errorHandler = AlertErrorHandler(mBinding.root)
        setupToolbar(mBinding.toolbar, getString(R.string.success))
        mBinding.item = mViewModel.savedLeadDetail

        mVerificationType.add(getString(R.string.email))
        mVerificationType.add(getString(R.string.phone))

        mBinding.checkBoxEmail.setOnCheckedChangeListener { buttonView, isChecked ->
            getSelectedCheckBoxValue(isChecked, buttonView)
        }

        mBinding.checkBoxPhone.setOnCheckedChangeListener { buttonView, isChecked ->
            getSelectedCheckBoxValue(isChecked, buttonView)
        }

        mBinding.btnVerify.onClick {
            selfVerificationCall()
        }



        mBinding.textBackToDashBoard.onClick {
            removeStoredData()
            Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_successFragment_to_dashBoardFragment)
        }
    }

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

    private fun selfVerificationCall() {

        val liveData = mViewModel.selfVerification(SuccessReq(verificationType = mVerificationType.joinToString(separator = ","), leadId = mViewModel.savedLeadDetail?.id))

        liveData.observe(this, Observer {
            it?.ifSuccess {
                removeStoredData()
                Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_successFragment_to_dashBoardFragment)
            }
        })

        mBinding.resource = liveData as LiveData<Resource<Any, APIError>>


    }

    private fun removeStoredData() {
        mViewModel.utilitiesList.clear()
        mViewModel.planType = ""
        mViewModel.zipcode = null
        mViewModel.programList.clear()
        mViewModel.customerData = CustomerData()
        mViewModel.savedLeadDetail = null
        mViewModel.recordingFile = ""
        mViewModel.isElectricServiceAddressSame = false
        mViewModel.isGasServiceAddressSame = false
        mViewModel.relationShipList.clear()
    }

    override fun handleOnBackPressed(): Boolean {
        removeStoredData()
        return true
    }

}
