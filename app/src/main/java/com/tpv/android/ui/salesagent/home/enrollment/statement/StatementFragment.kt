package com.tpv.android.ui.salesagent.home.enrollment.statement


import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.github.gcacace.signaturepad.views.SignaturePad
import com.livinglifetechway.k4kotlin.core.hide
import com.livinglifetechway.k4kotlin.core.onClick
import com.livinglifetechway.k4kotlin.core.orZero
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
import com.tpv.android.ui.salesagent.home.enrollment.SetEnrollViewModel
import com.tpv.android.utils.*
import com.tpv.android.utils.enums.DynamicField
import com.tpv.android.utils.glide.GlideApp
import id.zelory.compressor.Compressor
import kotlinx.coroutines.launch
import java.io.File


class StatementFragment : Fragment() {

    private lateinit var mBinding: FragmentStatementBinding
    private lateinit var mViewModel: SetEnrollViewModel
    private var mSignImage: Bitmap? = null
    private var is_image:Boolean=false


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
        mBinding.errorHandler = AlertErrorHandler(mBinding.root)

        setupToolbar(mBinding.toolbar, getString(R.string.statement), showBackIcon = true)

        if (mViewModel.signature != null && mViewModel.isAgreeWithCondition) {
            mSignImage = mViewModel.signature
            GlideApp.with(this)
                    .asBitmap()
                    .load(resizedBitmap())
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
        if(mViewModel.upload_imagefile.isNotEmpty()){
            is_image=true
        }else{
            is_image=false
        }
        var liveData: LiveData<Resource<SaveLeadsDetailResp?, APIError>>? = null
        liveData = mViewModel.saveLeadDetail(SaveLeadsDetailReq(
                leadTempId = mViewModel.leadvelidationError?.leadTempId,
                formId = mViewModel.planId,
                fields = mViewModel.dynamicFormData,
                billingimage = is_image,
                other = OtherData(programId = android.text.TextUtils.join(",", mViewModel.programList.map { it.id }),
                        zipcode = mViewModel.zipcode)))
        liveData.observe(this, Observer {
            it?.ifSuccess {
                mViewModel.savedLeadResp = it
                if (mViewModel.recordingFile.isNotEmpty() && mViewModel.upload_imagefile.isNotEmpty()) {
                    saveRecordingApiCall()
                    lifecycleScope.launch {
                        val compressedImageFile = mViewModel.file_uploaded?.let {
                            it1 -> context?.let {
                            it2 -> Compressor.compress(it2, it1)
                        }
                        }
                        compressedImageFile?.let { it1 ->
                            saveBillingImageApiCall(it1)
                        }
                    }
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
    private fun saveBillingImageApiCall(file: File) {
        val liveData = File(mViewModel.upload_imagefile).toMultipartBody("media", "image/*")?.let {
            mViewModel.saveBillingImage(leadId = mViewModel.savedLeadResp?.id.toRequestBody(),
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
                    .load(resizedBitmap())
                    .into(mBinding.imageSign)
            dialog?.dismiss()
            setButtonEnable()
        }
    }

    private fun resizedBitmap(): Bitmap? {

        val bm = mSignImage
        val width = bm?.width.orZero()
        val height = bm?.height.orZero()
        val scaleWidth = (width * 3) / width
        val scaleHeight = (height * 3) / height
        // create a matrix for the manipulation
        val matrix = Matrix()
        // resize the bit map
        matrix.postScale(scaleWidth.toFloat(), scaleHeight.toFloat())
        // recreate the new Bitmap
        return bm?.let { Bitmap.createBitmap(it, 0, 0, width, height, matrix, false) }
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


