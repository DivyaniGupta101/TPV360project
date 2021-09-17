package com.tpv.android.ui.salesagent.home.enrollment.signatureVerification

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
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
import com.livinglifetechway.k4kotlin.*
import com.tpv.android.R
import com.tpv.android.databinding.FragmentSignatureVerificationBinding
import com.tpv.android.helper.OnBackPressCallBack
import com.tpv.android.model.internal.DialogText
import com.tpv.android.model.network.*
import com.tpv.android.network.error.AlertErrorHandler
import com.tpv.android.network.resources.Resource
import com.tpv.android.network.resources.apierror.APIError
import com.tpv.android.network.resources.extensions.ifSuccess
import com.tpv.android.ui.salesagent.home.dashboard.DashBoardFragment
import com.tpv.android.ui.salesagent.home.enrollment.SetEnrollViewModel
import com.tpv.android.ui.salesagent.home.enrollment.dynamicform.DynamicFormFragment
import com.tpv.android.ui.salesagent.home.enrollment.programs.ElectricListingFragment
import com.tpv.android.utils.*
import com.tpv.android.utils.enums.DynamicField
import kotlinx.android.synthetic.main.fragment_signature_verification.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class SignatureVerificationFragment : Fragment(), OnBackPressCallBack {
    lateinit var mBinding: FragmentSignatureVerificationBinding
    lateinit var mViewModel: SignatureVerificationViewModel
    private lateinit var mSetEnrollViewModel: SetEnrollViewModel
    private var mVerificationType: ArrayList<String> = ArrayList()
    private var handler: Handler = Handler()
    private var is_image:Boolean=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_signature_verification, container, false)
        mBinding.lifecycleOwner = this
        mViewModel = ViewModelProviders.of(this).get(SignatureVerificationViewModel::class.java)
        activity?.let { mSetEnrollViewModel = ViewModelProviders.of(it).get(SetEnrollViewModel::class.java)
        }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initalize()
    }

    private fun initalize() {
        setupToolbar(mBinding.toolbar, getString(R.string.e_signature))
        mBinding.errorHandler = AlertErrorHandler(mBinding.root)
        if(mSetEnrollViewModel.dynamicSettings?.le_client_enrollment_type.orFalse()){
                if (DynamicFormFragment.image_upload==1) {
                setupToolbar(mBinding.toolbar, "Confirm Enrollement")
                mBinding.heading.text="Please submit to proceed the enrollment"
                    mBinding.heading.textSize=resources.getDimension(R.dimen._5sdp)
                mBinding.checkBoxPhone.visibility=View.GONE
                mBinding.checkBoxEmail.visibility=View.GONE
                mBinding.btnSubmit.isEnabled=true
                mBinding.btnSendLink.visibility=View.GONE
            }


        }

        mBinding.checkBoxEmail.setOnCheckedChangeListener { buttonView, isChecked ->
            getSelectedCheckBoxValue(isChecked, buttonView)
        }

        mBinding.checkBoxPhone.setOnCheckedChangeListener { buttonView, isChecked ->
            getSelectedCheckBoxValue(isChecked, buttonView)
        }


        mBinding.btnSubmit.onClick {
            saveCustomerDataApiCall()
        }

        mBinding.btnCancel.onClick {

            context?.actionDialog(
                    DialogText(getString(R.string.cancel_enrollment),
                            getString(R.string.enroll_cancel),
                            getString(R.string.yes),
                            getString(R.string.no)),
                    setOnPositiveBanClickListener = {
                        cancelLeadAPICall()
                    }
            )
        }
        mBinding.btnSendLink.onClick {
            sendLinkAPICall()
        }

        handler = Handler(Looper.getMainLooper())
        handler.post(object : Runnable {
            override fun run() {
                verifySignatureAPICall()
                handler.postDelayed(this, 10000)
            }

            private fun verifySignatureAPICall() {
                val liveDataVerifySignature = mViewModel.verifySignature(verifySignatureReq = VerifySignatureReq(
                        mSetEnrollViewModel.parent_id
                ))
                liveDataVerifySignature.observeForever(Observer {
                    it?.ifSuccess {
                        if (it?.isVerificationSignature.orFalse()) {
                            mBinding.btnSubmit.isEnabled = true
                            handler.removeCallbacks(this)
                        }
                    }
                })

            }
        })

    }



    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
    }

    private fun sendLinkAPICall() {

        val liveData = mViewModel.sendSignature(SendSignatureLinkReq(
                mode = mVerificationType.joinToString(separator = ","),
                tmpLeadId = mSetEnrollViewModel.parent_id
        ))
           liveData.observe(this@SignatureVerificationFragment, Observer { resources ->
            resources?.ifSuccess {
                getSelectedCheckBoxValue(false, mBinding.checkBoxEmail)
                getSelectedCheckBoxValue(false, mBinding.checkBoxPhone)
                toastNow(it?.message.orEmpty())

            }
        })


    }

    private fun cancelLeadAPICall() {
        val liveData = mViewModel.cancelEnrollLead(mSetEnrollViewModel.leadvelidationError?.leadTempId.orEmpty(),
                cancelLeadReq = CancelLeadReq(source = AppConstant.E_SIGNATURE))
        liveData.observe(this, Observer {
            it?.ifSuccess {
                mSetEnrollViewModel.clearSavedData()
                ElectricListingFragment.onback=false
                DynamicFormFragment.back_pressed=false
                Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_signatureVerificationFragment_to_dashBoardFragment)
            }

        })
        mBinding.resource = liveData as LiveData<Resource<Any, APIError>>

    }

    private fun getSelectedCheckBoxValue(isChecked: Boolean, buttonView: CompoundButton?) {
        if (isChecked) {
            mVerificationType.add(buttonView?.text.toString().toLowerCase(Locale.ROOT))
        } else {
            if (mVerificationType.isNotEmpty()) {
                mVerificationType.remove(mVerificationType.find { it == buttonView?.text.toString().toLowerCase(Locale.ROOT) })
            }

        }
        mBinding.btnSendLink.isEnabled = mVerificationType.isNotEmpty()
    }

    /**
     * Call API for save customer data
     * But before thet check if planId is DUEL FUEL then parameters will be change than GAS or ELECTRIC
     * On success of saveCustomerDataApiCall api, call saveContract API
     * Also check if recording is not empty then call save recording API else call save Signature API
     */
    private fun saveCustomerDataApiCall() {

        var zipcode = ""
        if (mSetEnrollViewModel.zipcode.isEmpty()) {
            zipcode = mSetEnrollViewModel.dynamicFormData.find { it.type == DynamicField.BOTHADDRESS.type && it.meta?.isPrimary == true }?.values?.get(AppConstant.SERVICEZIPCODE) as String
        } else {
            zipcode = mSetEnrollViewModel.zipcode
        }
        if(mSetEnrollViewModel.upload_imagefile.isNotEmpty()){
            is_image=true
        }else{
            is_image=false
        }
        mBinding.btnSubmit.isEnabled=false
        var liveData: LiveData<Resource<SaveLeadsDetailResp?, APIError>>? = null
        if( mSetEnrollViewModel.programid.isEmpty()){
            liveData = mSetEnrollViewModel.saveLeadDetail(SaveLeadsDetailReq(
                    leadTempId = mSetEnrollViewModel.parent_id,
                    formId = mSetEnrollViewModel.planId,
                    agent_ipaddress = DashBoardFragment.sAddr,
                    fields = mSetEnrollViewModel.dynamicFormData,
                    billingimage = is_image,
                    other = OtherData(programId = TextUtils.join(",", mSetEnrollViewModel.programList.map { it.id }),
                            zipcode = zipcode,enrollmentUsing = mSetEnrollViewModel.selectionType)))
        }else{

            liveData = mSetEnrollViewModel.saveLeadDetail(SaveLeadsDetailReq(
                    leadTempId = mSetEnrollViewModel.parent_id,
                    formId = mSetEnrollViewModel.planId,
                    agent_ipaddress = DashBoardFragment.sAddr,
                    fields = mSetEnrollViewModel.dynamicFormData,
                    billingimage = is_image,
                    other = OtherData(programId = mSetEnrollViewModel.programid,
                            zipcode = zipcode,enrollmentUsing = mSetEnrollViewModel.selectionType)))
        }

        liveData.observe(this, Observer {
            it?.ifSuccess {
                mSetEnrollViewModel.savedLeadResp = it
                if (mSetEnrollViewModel.recordingFile.isNotEmpty() && mSetEnrollViewModel.upload_imagefile.isNotEmpty()) {
                    saveRecordingApiCall()
                    mSetEnrollViewModel.file_uploaded?.let { it1 -> saveBillingImageApiCall(it1) }

                } else {
                    if(mSetEnrollViewModel.upload_imagefile.isNotEmpty()){
                        saveRecordingApiCall()
                        mSetEnrollViewModel.file_uploaded?.let { it1 -> saveBillingImageApiCall(it1) }


                    }

                    Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_signatureVerificationFragment_to_successFragment)
                }
            }
        })

        mBinding.resource = liveData as LiveData<Resource<Any, APIError>>
    }

    /**
     * Save recording file
     * And on success callBack call saveSinature API
     */
    private fun saveRecordingApiCall() {
        val liveData =
                File(mSetEnrollViewModel.recordingFile).toMultipartBody("media", "audio/*")?.let {
                    mSetEnrollViewModel.saveMedia(leadId = mSetEnrollViewModel.savedLeadResp?.id.toRequestBody(),
                            mediaFile = it, lng = mSetEnrollViewModel.location?.longitude.toString().toRequestBody(),
                            lat = mSetEnrollViewModel.location?.latitude.toString().toRequestBody())

                }
        liveData?.observe(this, Observer {
            it?.ifSuccess {
                Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_signatureVerificationFragment_to_successFragment)

            }
        })

        mBinding.resource = liveData as LiveData<Resource<Any, APIError>>
    }


    private fun saveBillingImageApiCall(file: File) {
        val liveData = File(mSetEnrollViewModel.upload_imagefile).toMultipartBody("media", "image/*")?.let {
            mSetEnrollViewModel.saveBillingImage(leadId = mSetEnrollViewModel.savedLeadResp?.id.toRequestBody(),
                    mediaFile = it, lng = mSetEnrollViewModel.location?.longitude.toString().toRequestBody(),
                    lat = mSetEnrollViewModel.location?.latitude.toString().toRequestBody())
        }
        liveData?.observe(this, Observer {
            it?.ifSuccess {
                Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_signatureVerificationFragment_to_successFragment)

            }
        })

        mBinding.resource = liveData as LiveData<Resource<Any, APIError>>
    }

    override fun handleOnBackPressed(): Boolean {
        mSetEnrollViewModel.customerback=true
        return true
    }


}




