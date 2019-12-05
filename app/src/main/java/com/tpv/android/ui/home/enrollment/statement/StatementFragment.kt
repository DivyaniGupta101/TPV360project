package com.tpv.android.ui.home.enrollment.statement


import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Geocoder
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
import com.tpv.android.databinding.DialogErrorBinding
import com.tpv.android.databinding.DialogSignatureBinding
import com.tpv.android.databinding.FragmentStatementBinding
import com.tpv.android.helper.Pref
import com.tpv.android.model.ContractReq
import com.tpv.android.model.DialogText
import com.tpv.android.model.SaveLeadsDetailReq
import com.tpv.android.model.SaveLeadsDetailResp
import com.tpv.android.network.error.AlertErrorHandler
import com.tpv.android.network.resources.Resource
import com.tpv.android.network.resources.apierror.APIError
import com.tpv.android.network.resources.extensions.ifSuccess
import com.tpv.android.ui.home.HomeActivity
import com.tpv.android.ui.home.enrollment.SetEnrollViewModel
import com.tpv.android.utils.*
import com.tpv.android.utils.enums.Plan
import com.tpv.android.utils.glide.GlideApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import java.util.*


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

        mBinding.errorHandler = AlertErrorHandler(mBinding.root)

        locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        mBinding.item = mViewModel.customerData

        mBinding.checkContract.setOnCheckedChangeListener { buttonView, isChecked ->
            setButtonEnable()
        }

        //Get current location and get zipcode
        //After that compare with previous selected(stored) zipcode
        //If same then saveAllDetails related to leads else show Dialog for zipcode does not mathch.
        mBinding.btnNext.onClick {
            getLocation()
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
     * If last location time is less than 2 minutes then it will give you location else show UnMatchZipcodeDialog
     */
    private fun getLocation() = runWithPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION) {
        uiScope.launch {
            location = context?.let { LocationHelper.getLastKnownLocation(it) }
            val currentTimeStamp = System.currentTimeMillis()
            val diffTime = currentTimeStamp.minus(location?.time.orZero())

            if (location == null) {
                createLocationRequest()
            } else {
                if (locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER).orFalse() && diffTime <= AppConstant.LOCATION_EXPIRED_TIMEOUT) {
                    getZipCodedFromLocation(location)
                } else {
                    unMatchZipcodeDialog()
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
                location = context?.let { LocationHelper.getLastKnownLocation(it) }
                getZipCodedFromLocation(location)
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
                            HomeActivity.REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    /**
     * Get zipcode from current or last location and check if it's match with stored Zipcode then able to call all saveLeadsAPI
     * Else call api getnearByZipcode
     */
    private fun getZipCodedFromLocation(location: Location?) {
        location?.let {
            val geoCoder = Geocoder(context, Locale.getDefault())
            val addresses = geoCoder.getFromLocation(it.latitude.orZero(), it.longitude.orZero(), 1)
            val postalCode = addresses.find { it.postalCode == mViewModel.zipcode?.zipcode }

            if (postalCode != null) {
                saveCustomerDataApiCall()
            } else {
                getNearByZipCodesListApiCall(it.latitude.orZero().toString(), it.longitude.orZero().toString())
            }
        }
    }

    /**
     * Using current or last location get list of nearByZipcode
     * Also Check if selected zipcode match with any zipcode from list then call all saveLeadsAPI else show no zipcode match dialog
     */
    private fun getNearByZipCodesListApiCall(lat: String?, lng: String?) {
        val liveData = mViewModel.getNearByZipCodes(lat = lat, lng = lng)
        liveData.observe(this, Observer {
            it.ifSuccess { list ->
                val postalCode = list?.find { it.postalCode == mViewModel.zipcode?.zipcode }
                if (postalCode != null) {
                    saveCustomerDataApiCall()
                } else {
                    unMatchZipcodeDialog()
                }
            }

        })

    }

    /**
     * Call API for save customer data
     * But before thet chaek if planType is DUEL FUEL then parameters will be change than GAS or ELECTRIC
     * On success of saveCustomerDataApiCall api, call saveContract API
     * Also check if recording is not empty then call save recording API else call save Signature API
     */
    private fun saveCustomerDataApiCall() {
        var liveData: LiveData<Resource<SaveLeadsDetailResp?, APIError>>? = null
        when (mViewModel.planType) {
            Plan.DUALFUEL.value -> {
                liveData = mViewModel.saveLeadDetail(SaveLeadsDetailReq(
                        clientid = Pref.user?.clientId.toString(),
                        commodity = mViewModel.planType,
                        gasutilityId = mViewModel.utilitiesList.find { it?.commodity == Plan.GASFUEL.value }?.utid.toString(),
                        gasprogramid = mViewModel.programList.find { it.utilityType == Plan.GASFUEL.value }?.id,
                        electricutilityId = mViewModel.utilitiesList.find { it?.commodity == Plan.ELECTRICFUEL.value }?.utid.toString(),
                        electricprogramid = mViewModel.programList.find { it.utilityType == Plan.ELECTRICFUEL.value }?.id,
                        fields = arrayListOf(mViewModel.customerData),
                        zipcode = mViewModel.zipcode?.zipcode)
                )
            }
            else -> {
                liveData = mViewModel.saveLeadDetail(SaveLeadsDetailReq(
                        clientid = Pref.user?.clientId.toString(),
                        commodity = mViewModel.planType,
                        programId = mViewModel.programList.get(0).id,
                        utilityId = mViewModel.utilitiesList.get(0)?.utid.toString(),
                        zipcode = mViewModel.zipcode?.zipcode,
                        fields = arrayListOf(mViewModel.customerData)))
            }
        }

        liveData.observe(this, Observer {
            it?.ifSuccess {
                mViewModel.savedLeadDetail = it

                mViewModel.saveContract(contractReq = ContractReq(mViewModel.savedLeadDetail?.id))

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
                    mViewModel.saveMedia(mViewModel.savedLeadDetail?.id.toRequestBody(),
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
            mViewModel.saveMedia(mViewModel.savedLeadDetail?.id.toRequestBody(),
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


//        if (mSignImage != null) {
//            binding.btnSave.isEnabled = true
//            binding.signatureView.signatureBitmap = mSignImage
//        }
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

    private fun unMatchZipcodeDialog() {
        val binding = DataBindingUtil.inflate<DialogErrorBinding>(layoutInflater, R.layout.dialog_error, null, false)
        val dialog = context?.let { AlertDialog.Builder(it) }
                ?.setView(binding.root)?.show()

        binding.item = getString(R.string.msg_zipcode_not_match)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding?.btnYes?.onClick {
            dialog?.dismiss()
        }

    }
}


