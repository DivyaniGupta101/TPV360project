package com.tpv.android.ui.home.enrollment.statement


import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.location.LocationManager
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
import com.tpv.android.model.network.ContractReq
import com.tpv.android.model.network.OtherData
import com.tpv.android.model.network.SaveLeadsDetailReq
import com.tpv.android.model.network.SaveLeadsDetailResp
import com.tpv.android.network.error.AlertErrorHandler
import com.tpv.android.network.resources.Resource
import com.tpv.android.network.resources.apierror.APIError
import com.tpv.android.network.resources.extensions.ifSuccess
import com.tpv.android.ui.home.TransparentActivity
import com.tpv.android.ui.home.enrollment.SetEnrollViewModel
import com.tpv.android.utils.*
import com.tpv.android.utils.enums.DynamicField
import com.tpv.android.utils.glide.GlideApp
import kotlinx.coroutines.*
import java.io.File


class StatementFragment : Fragment() {
    companion object {
        var REQUEST_GPS_SETTINGS = 1234
    }

    private lateinit var mBinding: FragmentStatementBinding
    private lateinit var mViewModel: SetEnrollViewModel
    private var mSignImage: Bitmap? = null
    private var location: Location? = null
    private var locationManager: LocationManager? = null

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

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

    /**
     * Get result from MAIN ACTIVITY onActivityResult method
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_GPS_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                createLocationRequest()
            }
        }

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

        locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (mViewModel.dynamicFormData.filter { it.type == DynamicField.PHONENUMBER.type }.isNotEmpty()) {
            mBinding.phone = mViewModel.dynamicFormData.find { it.type == DynamicField.PHONENUMBER.type && it.meta?.isPrimary == true }?.values?.get(AppConstant.VALUE) as String

        }
        if(mViewModel.dynamicFormData.filter { it.type == DynamicField.FULLNAME.type  }.isNotEmpty()){
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

            if (AppConstant.GEO_LOCATION_ENABLE) {
                getLocation()
            } else {
                saveCustomerDataApiCall()
            }
        }

        mBinding.imageSign.onClick {
            signatureDialog()
        }

        mBinding.textTapToOpen.onClick {
            signatureDialog()
        }
    }

    /**
     * Get user current location
     * Check location permission
     * Also check gps is enabled
     * Then checkRadius else show error message
     */
    private fun getLocation() = runWithPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION) {
        uiScope.launch {
            location = context?.let { LocationHelper.getLastKnownLocation(it) }

            if (location == null) {
                startActivityForResult(Intent(context, TransparentActivity::class.java), TransparentActivity.REQUEST_CHECK_SETTINGS)
                //   createLocationRequest()
            } else {
                if (locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER).orFalse()) {
                    checkRadius(location?.latitude, location?.longitude)
                } else {
                    context?.infoDialog(subTitleText = getString(R.string.msg_gps_location))
                }
            }
        }

    }

    /**
     * create location request and check gps dialog is enabled or not
     */
    private fun createLocationRequest() {
        val locationRequest = LocationRequest.create()?.apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = locationRequest?.let {
            LocationSettingsRequest.Builder()
                    .addLocationRequest(it)
        }
        val client: SettingsClient? = context?.let { LocationServices.getSettingsClient(it) }
        val task: Task<LocationSettingsResponse>? = client?.checkLocationSettings(builder?.build())

        task?.addOnSuccessListener { locationSettingsResponse ->
            // All location settings are satisfied. The client can initialize
            // location requests here.
            // ...
            uiScope.launch {
                var count = 0
                for (i in 1..3) {
                    location = context?.let { LocationHelper.getLastKnownLocation(it) }
                    count += 1
                    if (location != null) {
                        checkRadius(location?.latitude, location?.longitude)
                        break
                    } else {
                        if (count < 3) {
                            delay(500)
                        } else {
                            context?.infoDialog(subTitleText = getString(R.string.msg_location))
                        }
                    }
                }
            }
        }

        task?.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(activity,
                            TransparentActivity.REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    /**
     * Call API for save customer data
     * But before thet chaek if planId is DUEL FUEL then parameters will be change than GAS or ELECTRIC
     * On success of saveCustomerDataApiCall api, call saveContract API
     * Also check if recording is not empty then call save recording API else call save Signature API
     */
    private fun saveCustomerDataApiCall() {

        var liveData: LiveData<Resource<SaveLeadsDetailResp?, APIError>>? = null
        liveData = mViewModel.saveLeadDetail(SaveLeadsDetailReq(
                formId = mViewModel.planId,
                fields = mViewModel.dynamicFormData,
                other = OtherData(programId = android.text.TextUtils.join(",", mViewModel.selectedUtilityList.map { it.utid }),
                        zipcode = mViewModel.zipcode)))
        liveData.observe(this, Observer {
            it?.ifSuccess {
                mViewModel.savedLeadResp = it

                mViewModel.saveContract(contractReq = ContractReq(mViewModel.savedLeadResp?.id))

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
                    mViewModel.saveMedia(mViewModel.savedLeadResp?.id.toRequestBody(),
                            it)
                }
        liveData?.observe(this, Observer {
            it?.ifSuccess {
                saveSignatureApiCall()
            }
        })

        mBinding.resource = liveData as LiveData<Resource<Any, APIError>>
    }

    /**
     * save SignatureImage
     */
    private fun saveSignatureApiCall() {

        val liveData = context?.bitmapToFile(mSignImage).toMultipartBody("media", "image/jpeg")?.let {
            mViewModel.saveMedia(mViewModel.savedLeadResp?.id.toRequestBody(),
                    it)
        }
        liveData?.observe(this, Observer {
            it.ifSuccess {
                Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_statementFragment_to_successFragment)
            }
        })

        mBinding.resource = liveData as LiveData<Resource<Any, APIError>>

    }

    /**
     * if checkBox is selected and signature image both  are available then only nextButton is enabled
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
            mSignImage = binding.signatureView.signatureBitmap
            GlideApp.with(context)
                    .asBitmap()
                    .load(mSignImage)
                    .into(mBinding.imageSign)
            dialog?.dismiss()
            setButtonEnable()
        }
    }

    private fun checkRadius(latitude: Double?, longitude: Double?) {
        try {
            var lat = ""
            var lng = ""
            val result = FloatArray(1)
            val response = mViewModel.dynamicFormData.find { (it.type == DynamicField.ADDRESS.type || it.type == DynamicField.BOTHADDRESS.type) && it.meta?.isPrimary == true }
            when (response?.type) {
                DynamicField.ADDRESS.type -> {
                    lat = response.values.get(AppConstant.LAT) as String
                    lng = response.values.get(AppConstant.LNG) as String
                }
                DynamicField.BOTHADDRESS.type -> {
                    lat = response.values.get(AppConstant.SERVICELAT) as String
                    lng = response.values.get(AppConstant.SERVICELNG) as String
                }

            }

            Location.distanceBetween(lat.toDouble().orZero(),
                    lng.toDouble().orZero()
                    , latitude.orZero(), longitude.orZero(), result)

            if (result.isNotEmpty()) {
                if (result[0] < AppConstant.GEO_LOCATION_RADIOUS.toDouble() || result[0] == AppConstant.GEO_LOCATION_RADIOUS.toFloat()) {
                    saveCustomerDataApiCall()
                } else {
                    context?.infoDialog(subTitleText = getString(R.string.msg_zipcode_not_match))
                }
            }
        } catch (e: IllegalArgumentException) {
        }
    }


}


