package com.tpv.android.ui.home.enrollment.statement


import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.github.gcacace.signaturepad.views.SignaturePad
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.livinglifetechway.k4kotlin.core.hide
import com.livinglifetechway.k4kotlin.core.onClick
import com.livinglifetechway.k4kotlin.core.orFalse
import com.livinglifetechway.k4kotlin.core.orZero
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import com.tpv.android.R
import com.tpv.android.databinding.DialogSignatureBinding
import com.tpv.android.databinding.FragmentStatementBinding
import com.tpv.android.model.internal.DialogText
import com.tpv.android.model.network.OtherData
import com.tpv.android.model.network.SaveLeadsDetailReq
import com.tpv.android.model.network.SaveLeadsDetailResp
import com.tpv.android.network.error.AlertErrorHandler
import com.tpv.android.network.resources.Resource
import com.tpv.android.network.resources.apierror.APIError
import com.tpv.android.network.resources.extensions.ifSuccess
import com.tpv.android.ui.home.TransparentActivity
import com.tpv.android.ui.home.enrollment.SetEnrollViewModel
import com.tpv.android.ui.home.enrollment.dynamicform.DynamicFormFragment.Companion.REQUEST_GPS_SETTINGS
import com.tpv.android.utils.*
import com.tpv.android.utils.enums.DynamicField
import com.tpv.android.utils.glide.GlideApp
import kotlinx.coroutines.*
import java.io.File


class StatementFragment : Fragment() {

    private lateinit var mBinding: FragmentStatementBinding
    private lateinit var mViewModel: SetEnrollViewModel
    private var mSignImage: Bitmap? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_statement, container, false)
        mBinding.lifecycleOwner = this
        activity?.let { mViewModel = ViewModelProviders.of(it).get(SetEnrollViewModel::class.java) }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
    }

    private fun initialize() {
        setupToolbar(mBinding.toolbar, getString(R.string.statement), showBackIcon = true)

        if (mViewModel.signature != null && mViewModel.isAgreeWithCondition) {
            mSignImage = mViewModel.signature
            GlideApp.with(this)
                    .asBitmap()
                    .load(mViewModel.signature)
                    .into(mBinding.imageSign)

            mBinding.textTapToOpen.hide()
            mBinding.checkContract.isChecked = mViewModel.isAgreeWithCondition
            setButtonEnable()
        }


        mBinding.errorHandler = AlertErrorHandler(mBinding.root)

        //Get value for phonenumber
        if (mViewModel.dynamicFormData.filter { it.type == DynamicField.PHONENUMBER.type }.isNotEmpty()) {
            mBinding.phone = mViewModel.dynamicFormData.find { it.type == DynamicField.PHONENUMBER.type && it.meta?.isPrimary == true }?.values?.get(AppConstant.VALUE) as String

        }

        //Get value for Customer Name
        if (mViewModel.dynamicFormData.filter { it.type == DynamicField.FULLNAME.type }.isNotEmpty()) {
            val fullNameResponse = mViewModel.dynamicFormData.find { it.type == DynamicField.FULLNAME.type && it.meta?.isPrimary == true }?.values
            mBinding.name = fullNameResponse?.get(AppConstant.FIRSTNAME) as String + " " + fullNameResponse.get(AppConstant.LASTNAME) as String
        }
        mBinding.checkContract.setOnCheckedChangeListener { buttonView, isChecked ->
            setButtonEnable()
        }

        //If geo location is enable then
        //Get current location and get zipcode
        //After that compare with previous selected(stored) zipcode
        //If same then saveAllDetails related to leads else show Dialog for zipcode does not mathch.
        //Else direct saveAllDetails.
        mBinding.btnNext.onClick {

            mViewModel.isAgreeWithCondition = mBinding.checkContract.isChecked
            mViewModel.signature = mSignImage
            saveCustomerDataApiCall()
        }

        mBinding.imageSign.onClick {
            signatureDialog()
        }

        mBinding.textTapToOpen.onClick {
            signatureDialog()
        }
    }

    /**
     * Call API for save customer data
     * But before thet check if planId is DUEL FUEL then parameters will be change than GAS or ELECTRIC
     * On success of saveCustomerDataApiCall api, call saveContract API
     * Also check if recording is not empty then call save recording API else call save Signature API
     */
    private fun saveCustomerDataApiCall() {

        var liveData: LiveData<Resource<SaveLeadsDetailResp?, APIError>>? = null
        liveData = mViewModel.saveLeadDetail(SaveLeadsDetailReq(
                formId = mViewModel.planId,
                fields = mViewModel.dynamicFormData,
                other = OtherData(programId = android.text.TextUtils.join(",", mViewModel.programList.map { it.id }),
                        zipcode = mViewModel.zipcode)))
        liveData.observe(this, Observer {
            it?.ifSuccess {
                mViewModel.savedLeadResp = it


                if (mViewModel.recordingFile.isNotEmpty()) {
                    saveRecordingApiCall()
                } else {
                    saveSignatureApiCall()
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
                File(mViewModel.recordingFile).toMultipartBody("media", "audio/*")?.let {
                    mViewModel.saveMedia(leadId = mViewModel.savedLeadResp?.id.toRequestBody(),
                            mediaFile = it, lng = mViewModel.location?.longitude.toString().toRequestBody(),
                            lat = mViewModel.location?.latitude.toString().toRequestBody())
                }
        liveData?.observe(this, Observer {
            it?.ifSuccess {
                saveSignatureApiCall()
            }
        })

        mBinding.resource = liveData as LiveData<Resource<Any, APIError>>
    }

    /**
     * Save signatureImage
     */
    private fun saveSignatureApiCall() {

        val liveData = context?.bitmapToFile(changeBitmapColor(mSignImage, Color.BLACK)).toMultipartBody("media", "image/png")?.let {
            mViewModel.saveMedia(leadId = mViewModel.savedLeadResp?.id.toRequestBody(),
                    mediaFile = it, lat = mViewModel.location?.latitude.toString().toRequestBody(),
                    lng = mViewModel.location?.longitude.toString().toRequestBody())
        }
        liveData?.observe(this, Observer {
            it.ifSuccess {
                Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_statementFragment_to_successFragment)
            }
        })

        mBinding.resource = liveData as LiveData<Resource<Any, APIError>>

    }

    /**
     * if checkBox is selected and signature image both are available then only nextButton is enabled
     */
    private fun setButtonEnable() {
        mBinding.btnNext.isEnabled = if (mBinding.checkContract.isChecked && mSignImage != null) true else false
    }


    private fun signatureDialog() {
        val binding = DataBindingUtil.inflate<DialogSignatureBinding>(layoutInflater, R.layout.dialog_signature, null, false)
        val dialog = context?.let { AlertDialog.Builder(it) }
                ?.setView(binding.root)?.show()

        binding.item = DialogText("", "",
                getString(R.string.save),
                getString(R.string.cancel))

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.signatureView.setOnSignedListener(object : SignaturePad.OnSignedListener {
            override fun onStartSigning() {
            }

            override fun onClear() {
            }

            override fun onSigned() {
                binding.btnSave.isEnabled = true
            }
        })

        binding?.textClear?.onClick {
            binding.btnSave.isEnabled = false
            binding.signatureView.clear()
        }

        binding?.btnCancel?.onClick {
            dialog?.dismiss()
        }


        binding?.btnSave?.onClick {
            mBinding.textTapToOpen.hide()
            mSignImage = binding.signatureView.transparentSignatureBitmap
            GlideApp.with(context)
                    .asBitmap()
                    .load(mSignImage)
                    .into(mBinding.imageSign)
            dialog?.dismiss()
            setButtonEnable()
        }
    }

    fun changeBitmapColor(sourceBitmap: Bitmap?, color: Int): Bitmap? {
        val resultBitmap = sourceBitmap?.copy(sourceBitmap.getConfig(), true);
        val paint = Paint();
        val filter = LightingColorFilter(color, 1);
        paint.setColorFilter(filter);
        resultBitmap?.let {
            val canvas = Canvas(resultBitmap);
            canvas.drawBitmap(resultBitmap, 0f, 0f, paint);
        }
        return resultBitmap;
    }
}


