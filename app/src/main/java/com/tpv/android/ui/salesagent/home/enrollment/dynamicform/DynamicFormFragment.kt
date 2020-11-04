package com.tpv.android.ui.salesagent.home.enrollment.dynamicform

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.location.LocationManager
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.widget.Autocomplete
import com.google.gson.reflect.TypeToken
import com.livinglifetechway.k4kotlin.core.androidx.hideKeyboard
import com.livinglifetechway.k4kotlin.core.onClick
import com.livinglifetechway.k4kotlin.core.orFalse
import com.livinglifetechway.k4kotlin.core.orZero
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import com.tpv.android.R
import com.tpv.android.databinding.*
import com.tpv.android.helper.OnBackPressCallBack
import com.tpv.android.model.network.DynamicFormResp
import com.tpv.android.model.network.OtherData
import com.tpv.android.model.network.ValidateLeadsDetailReq
import com.tpv.android.model.network.VelidateLeadsDetailResp
import com.tpv.android.network.error.AlertErrorHandler
import com.tpv.android.network.resources.Resource
import com.tpv.android.network.resources.apierror.APIError
import com.tpv.android.network.resources.extensions.ifSuccess
import com.tpv.android.ui.salesagent.home.HomeActivity
import com.tpv.android.ui.salesagent.home.TransparentActivity
import com.tpv.android.ui.salesagent.home.enrollment.SetEnrollViewModel
import com.tpv.android.ui.salesagent.home.enrollment.dynamicform.address.fillAddressFields
import com.tpv.android.ui.salesagent.home.enrollment.dynamicform.address.isValid
import com.tpv.android.ui.salesagent.home.enrollment.dynamicform.address.setField
import com.tpv.android.ui.salesagent.home.enrollment.dynamicform.checkbox.isValid
import com.tpv.android.ui.salesagent.home.enrollment.dynamicform.checkbox.setField
import com.tpv.android.ui.salesagent.home.enrollment.dynamicform.email.isValid
import com.tpv.android.ui.salesagent.home.enrollment.dynamicform.email.setField
import com.tpv.android.ui.salesagent.home.enrollment.dynamicform.fullname.isValid
import com.tpv.android.ui.salesagent.home.enrollment.dynamicform.fullname.setField
import com.tpv.android.ui.salesagent.home.enrollment.dynamicform.heading.setField
import com.tpv.android.ui.salesagent.home.enrollment.dynamicform.infotext.setField
import com.tpv.android.ui.salesagent.home.enrollment.dynamicform.label.setField
import com.tpv.android.ui.salesagent.home.enrollment.dynamicform.multilineedittext.isValid
import com.tpv.android.ui.salesagent.home.enrollment.dynamicform.multilineedittext.setField
import com.tpv.android.ui.salesagent.home.enrollment.dynamicform.phone.isValid
import com.tpv.android.ui.salesagent.home.enrollment.dynamicform.phone.setField
import com.tpv.android.ui.salesagent.home.enrollment.dynamicform.radiobutton.isValid
import com.tpv.android.ui.salesagent.home.enrollment.dynamicform.radiobutton.setField
import com.tpv.android.ui.salesagent.home.enrollment.dynamicform.serviceandbillingaddress.fillAddressFields
import com.tpv.android.ui.salesagent.home.enrollment.dynamicform.serviceandbillingaddress.isValid
import com.tpv.android.ui.salesagent.home.enrollment.dynamicform.serviceandbillingaddress.setField
import com.tpv.android.ui.salesagent.home.enrollment.dynamicform.singlelineedittext.isValid
import com.tpv.android.ui.salesagent.home.enrollment.dynamicform.singlelineedittext.setField
import com.tpv.android.ui.salesagent.home.enrollment.dynamicform.spinner.setField
import com.tpv.android.utils.*
import com.tpv.android.utils.enums.DynamicField
import kotlinx.coroutines.*

class DynamicFormFragment : Fragment(), OnBackPressCallBack {

    companion object {
        var REQUEST_GPS_SETTINGS = 1234
    }

    private lateinit var mBinding: FragmentDynamicFormBinding
    private lateinit var mViewModel: SetEnrollViewModel
    private var bindingList: ArrayList<Any> = ArrayList()
    private var totalPage: Int = 1
    private var hasNext: Boolean = false
    private var currentPage = 1

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    private var locationManager: LocationManager? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_dynamic_form, container, false)

        mBinding.lifecycleOwner = this
        activity?.let { mViewModel = ViewModelProviders.of(it).get(SetEnrollViewModel::class.java) }

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
    }

    override fun handleOnBackPressed(): Boolean {
        saveOldData()
        return true
    }

    fun initialize() {

        locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        mBinding.errorHandler = AlertErrorHandler(mBinding.root)

        mBinding.leadValidationErrorHandler = AlertErrorHandler(mBinding.root, false) {
            mViewModel.clearSavedData()
            Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_dynamicFormFragment_to_dashBoardFragment)
        }

        currentPage = arguments?.let { DynamicFormFragmentArgs.fromBundle(it) }?.item.orZero()
        totalPage = mViewModel.duplicatePageMap?.size.orZero()

        setupToolbar(mBinding.toolbar, getString(R.string.customer_data), showBackIcon = true, backIconClickListener = {
            saveOldData()
        })

        //Check next page is Available or not
        hasNext = if (currentPage == totalPage) {
            false
        } else {
            currentPage < totalPage.orZero()
        }

        //Inflate all the views
        inflateViews()

        mBinding.btnNext.onClick {

            hideKeyboard()

            val validList: ArrayList<Boolean> = ArrayList()
            bindingList.forEach { view ->
                validList.add(checkValid(view))
            }

            //Check if all validation is true then check have next page then
            //Load this page again else sent to client info screen
            if (!validList.contains(false)) {
                mViewModel.formPageMap?.set(currentPage, mViewModel.duplicatePageMap?.copy(object : TypeToken<DynamicFormResp>() {}.type)?.get(currentPage).orEmpty())
                if (hasNext) {
                    currentPage += 1
                    Navigation.findNavController(mBinding.root).navigateSafe(DynamicFormFragmentDirections.actionDynamicFormFragmentSelf(currentPage))
                } else {
                    mViewModel.dynamicFormData.clear()
                    for (key in 1..mViewModel.formPageMap?.size.orZero()) {
                        mViewModel.dynamicFormData.addAll(mViewModel.formPageMap?.get(key).orEmpty())
                    }
                    getLocation()
                }
            }
        }
    }

    private fun navigateNext() {
        if (mViewModel.leadvelidationError?.errors.isNullOrEmpty()) {
            Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_dynamicFormFragment_to_clientInfoFragment)
        } else {
            Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_dynamicFormFragment_to_leadVelidationFragment)
        }
    }

    /**
     * Call API for save customer data
     * But before thet check if planId is DUEL FUEL then parameters will be change than GAS or ELECTRIC
     * On success of saveCustomerDataApiCall api, call saveContract API
     * Also check if recording is not empty then call save recording API else call save Signature API
     */
    private fun validateCustomerDataApiCall(latitude: Double?, longitude: Double?) {
        var lat = ""
        var lng = ""

        if (!AppConstant.CURRENT_GEO_LOCATION) {
            lat = latitude.toString()
            lng = longitude.toString()
        } else {
            val response = mViewModel.dynamicFormData.find { (it.type == DynamicField.ADDRESS.type || it.type == DynamicField.BOTHADDRESS.type) && it.meta?.isPrimary == true }
            when (response?.type) {
                DynamicField.ADDRESS.type -> {
                    lat = response.values?.get(AppConstant.LAT) as String
                    lng = response.values?.get(AppConstant.LNG) as String
                }
                DynamicField.BOTHADDRESS.type -> {
                    lat = response.values?.get(AppConstant.SERVICELAT) as String
                    lng = response.values?.get(AppConstant.SERVICELNG) as String
                }
            }
        }

        var liveData: LiveData<Resource<VelidateLeadsDetailResp?, APIError>>? = null
        liveData = mViewModel.validateLeadDetail(ValidateLeadsDetailReq(
                agentLat = lat,
                agentLng = lng,
                formId = mViewModel.planId,
                fields = mViewModel.dynamicFormData,
                geoLocationSettingOn = AppConstant.CURRENT_GEO_LOCATION,
                other = OtherData(programId = TextUtils.join(",", mViewModel.programList.map { it.id }),
                        zipcode = mViewModel.zipcode)))
        liveData.observe(this, Observer {
            it?.ifSuccess {
                mViewModel.leadvelidationError = it
                navigateNext()
            }
        })

        mBinding.leadValidationResource = liveData as LiveData<Resource<Any, APIError>>
    }

    /**
     * Replace updated values with old values
     */
    private fun saveOldData() {
        mViewModel.duplicatePageMap?.set(currentPage, mViewModel.formPageMap?.copy(object : TypeToken<DynamicFormResp>() {}.type)?.get(currentPage).orEmpty())
    }

    /**
     * Check validation of inflated views
     */
    private fun checkValid(view: Any): Boolean {
        return when (view) {
            is LayoutInputFullNameBinding -> {
                view.isValid(context)
            }
            is LayoutInputSingleLineEditTextBinding -> {
                view.isValid(context)
            }
            is LayoutInputPhoneNumberBinding -> {
                view.isValid(context)
            }
            is LayoutInputAddressBinding -> {
                view.isValid(context)
            }
            is LayoutInputServiceAndBillingAddressBinding -> {
                view.isValid(context)
            }
            is LayoutInputMultiLineEditTextBinding -> {
                view.isValid(context)
            }
            is LayoutInputRadioButtonBinding -> {
                view.isValid(context)
            }
            is LayoutInputCheckBoxBinding -> {
                view.isValid(context)
            }
            is LayoutInputEmailAddressBinding -> {
                view.isValid(context)
            }
            else -> {
                return true
            }
        }

    }

    /**
     * Inflate different view on basis of type
     */
    private fun inflateViews() {

        //Handle page indicator view
        for (pageNumber in 1..totalPage.orZero()) {
            val binding = DataBindingUtil.inflate<LayoutHighlightIndicatorBinding>(layoutInflater,
                    R.layout.layout_highlight_indicator,
                    mBinding.indicatorContainer,
                    true)
            binding.currentPage = currentPage
            binding.pageNumber = pageNumber
        }


        mViewModel.duplicatePageMap?.get(currentPage)?.forEach { response ->
            when (response.type) {
                DynamicField.FULLNAME.type -> {
                    setFieldsOfFullName(response)
                }
                DynamicField.TEXTBOX.type -> {
                    setFieldsOfSinglLineEditText(response)
                }
                DynamicField.EMAIL.type -> {
                    setFieldsOfEmailAddress(response)
                }
                DynamicField.PHONENUMBER.type -> {
                    setFieldsOfPhoneNumber(response)
                }
                DynamicField.HEADING.type -> {
                    setFieldsOfHeading(response)
                }
                DynamicField.LABEL.type -> {
                    setFieldsOfLabel(response)
                }
                DynamicField.ADDRESS.type -> {
                    setFieldsOfAddress(response)
                }
                DynamicField.BOTHADDRESS.type -> {
                    setFieldOfBillingAndServiceAddress(response)
                }
                DynamicField.TEXTAREA.type -> {
                    setFieldOfMultiLineEditText(response)
                }
                DynamicField.RADIO.type -> {
                    setFieldOfRadioButton(response)
                }
                DynamicField.CHECKBOX.type -> {
                    setFieldOfCheckBox(response)
                }
                DynamicField.SELECTBOX.type -> {
                    setFieldOfSpinner(response)
                }
                DynamicField.TEXT.type -> {
                    setFieldOfMessageInfo(response)
                }
            }
        }

    }

    /**
     * Inflate email address view
     */
    private fun setFieldsOfEmailAddress(response: DynamicFormResp) {
        val binding = DataBindingUtil.inflate<LayoutInputEmailAddressBinding>(layoutInflater,
                R.layout.layout_input_email_address,
                mBinding.fieldContainer,
                true)
        binding.setField(response, mViewModel, mBinding, getListOfCopyText(response))
        bindingList.add(binding)
    }

    /**
     * Inflate single line edit text view
     */
    private fun setFieldsOfSinglLineEditText(response: DynamicFormResp) {
        val binding = DataBindingUtil.inflate<LayoutInputSingleLineEditTextBinding>(layoutInflater,
                R.layout.layout_input_single_line_edit_text,
                mBinding.fieldContainer,
                true)

        binding.setField(response)
        bindingList.add(binding)
    }

    /**
     * Inflate full name view
     */
    private fun setFieldsOfFullName(response: DynamicFormResp) {
        val binding = DataBindingUtil.inflate<LayoutInputFullNameBinding>(layoutInflater,
                R.layout.layout_input_full_name,
                mBinding.fieldContainer,
                true)
        val list: ArrayList<DynamicFormResp> = ArrayList()
        if (binding.item?.meta?.isAllowCopy.orFalse()) {
            for (i in 1..totalPage) {
                mViewModel.duplicatePageMap?.get(i).orEmpty().forEach {
                    if (binding.item?.type == it.type && binding.item?.id != it.id) {
                        list.add(it)
                    }
                }
            }
            binding.setField(response, list)
            bindingList.add(binding)
        }
    }

    /**
     * Inflate heading view
     */
    private fun setFieldsOfHeading(response: DynamicFormResp) {
        val binding = DataBindingUtil.inflate<LayoutInputHeadingBinding>(layoutInflater,
                R.layout.layout_input_heading,
                mBinding.fieldContainer,
                true)

        binding.setField(response)
        bindingList.add(binding)
    }

    /**
     * Inflate label view
     */
    private fun setFieldsOfLabel(response: DynamicFormResp) {
        val binding = DataBindingUtil.inflate<LayoutInputLabelBinding>(layoutInflater,
                R.layout.layout_input_label,
                mBinding.fieldContainer,
                true)

        binding.setField(response.label.orEmpty())
        bindingList.add(binding)
    }

    /**
     * Inflate phoneNumber view
     */
    private fun setFieldsOfPhoneNumber(response: DynamicFormResp) {
        val binding = DataBindingUtil.inflate<LayoutInputPhoneNumberBinding>(layoutInflater,
                R.layout.layout_input_phone_number,
                mBinding.fieldContainer,
                true)

        binding.setField(response, mViewModel, mBinding)
        bindingList.add(binding)
    }

    /**
     * Inflate address view
     */
    private fun setFieldsOfAddress(response: DynamicFormResp) {
        val binding = DataBindingUtil.inflate<LayoutInputAddressBinding>(layoutInflater,
                R.layout.layout_input_address,
                mBinding.fieldContainer,
                true)
        binding.setField(response)
        bindingList.add(binding)
    }

    /**
     * Inflate billing and service address view
     */
    private fun setFieldOfBillingAndServiceAddress(response: DynamicFormResp) {
        val binding = DataBindingUtil.inflate<LayoutInputServiceAndBillingAddressBinding>(layoutInflater,
                R.layout.layout_input_service_and_billing_address,
                mBinding.fieldContainer,
                true)
        binding.setField(response)
        bindingList.add(binding)
    }

    /**
     * Inflate multi line edit text view
     */
    private fun setFieldOfMultiLineEditText(response: DynamicFormResp) {
        val binding = DataBindingUtil.inflate<LayoutInputMultiLineEditTextBinding>(layoutInflater,
                R.layout.layout_input_multi_line_edit_text,
                mBinding.fieldContainer,
                true)

        binding.setField(response)
        bindingList.add(binding)
    }

    /**
     * Inflate radio button view
     */
    private fun setFieldOfRadioButton(response: DynamicFormResp) {
        val binding = DataBindingUtil.inflate<LayoutInputRadioButtonBinding>(layoutInflater,
                R.layout.layout_input_radio_button,
                mBinding.fieldContainer,
                true)

        binding.setField(response)
        bindingList.add(binding)
    }

    /**
     * Inflate check box view
     */
    private fun setFieldOfCheckBox(response: DynamicFormResp) {
        val binding = DataBindingUtil.inflate<LayoutInputCheckBoxBinding>(layoutInflater,
                R.layout.layout_input_check_box,
                mBinding.fieldContainer,
                true)

        binding.setField(response)
        bindingList.add(binding)

    }

    /**
     * Inflate spinner(dropdown) view
     */
    private fun setFieldOfSpinner(response: DynamicFormResp) {
        val binding = DataBindingUtil.inflate<LayoutInputSpinnerBinding>(layoutInflater,
                R.layout.layout_input_spinner,
                mBinding.fieldContainer,
                true)

        binding.setField(response)
        bindingList.add(binding)
    }

    /**
     * Inflate messageInfo in view
     */
    private fun setFieldOfMessageInfo(response: DynamicFormResp) {
        val binding = DataBindingUtil.inflate<LayoutInputTextInfoBinding>(layoutInflater,
                R.layout.layout_input_text_info,
                mBinding.fieldContainer,
                true)

        binding.setField(response)
        bindingList.add(binding)
    }

    /**
     * Get user current location
     * Check location permission
     * Also check gps is enabled
     * Then checkRadius else show error message
     */
    private fun getLocation() = runWithPermissions(*getListOfLocationPermission()) {
        uiScope.launch {
            mViewModel.location = context?.let { LocationHelper.getLastKnownLocation(it) }

            if (mViewModel.location == null) {
                startActivityForResult(Intent(context, TransparentActivity::class.java), TransparentActivity.REQUEST_CHECK_SETTINGS)
                //   createLocationRequest()
            } else {
                if (locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER).orFalse()) {
                    validateCustomerDataApiCall(mViewModel.location?.latitude, mViewModel.location?.longitude)
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
                    mViewModel.location = context?.let { LocationHelper.getLastKnownLocation(it) }
                    count += 1
                    if (mViewModel.location == null) {
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

    private fun getListOfCopyText(dynamicFormResp: DynamicFormResp): ArrayList<DynamicFormResp> {
        val list: ArrayList<DynamicFormResp> = ArrayList()
        if (dynamicFormResp.meta?.isAllowCopy.orFalse()) {
            for (i in 1..totalPage) {
                mViewModel.duplicatePageMap?.get(i).orEmpty().forEachIndexed { index, it ->
                    if (dynamicFormResp.type == it.type && dynamicFormResp.id != it.id) {
                        mViewModel.duplicatePageMap?.get(i)?.get(index)?.let { it1 -> list.add(it1) }
                    }
                }
            }
        }
        return list;
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == REQUEST_GPS_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                createLocationRequest()
            }
        }

        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                when (requestCode) {
                    HomeActivity.ADDRESS_REQUEST_CODE -> {
                        val id = HomeActivity.ADDRESS_REQUEST_CODE - 5000
                        HomeActivity.ADDRESS_REQUEST_CODE = 5000

                        //Get binding from list if layout is address then check id is same
                        // Then set addressComponent in addressFields
                        bindingList.forEach { binding ->
                            when (binding) {
                                is LayoutInputAddressBinding -> {
                                    if (binding.item?.id?.equals(id).orFalse()) {
                                        binding.fillAddressFields(data?.let { Autocomplete.getPlaceFromIntent(it) }, mViewModel)
                                    }
                                }
                            }
                        }
                    }
                    HomeActivity.BILLING_ADDRESS_REQUEST_CODE -> {
                        val id = HomeActivity.BILLING_ADDRESS_REQUEST_CODE - 5000
                        HomeActivity.BILLING_ADDRESS_REQUEST_CODE = 5000

                        //Get binding from list if layout is serviceAndBillingAddress then check id is same
                        // Then set addressComponent in billingAddressFields
                        bindingList.forEach { binding ->
                            when (binding) {
                                is LayoutInputServiceAndBillingAddressBinding -> {
                                    if (binding.item?.id?.equals(id).orFalse()) {
                                        binding.fillAddressFields(data.let { Autocomplete.getPlaceFromIntent(it) }, false, mViewModel)
                                    }
                                }
                            }
                        }
                    }
                    HomeActivity.SERVICE_ADDRESS_REQUEST_CODE -> {
                        val id = HomeActivity.SERVICE_ADDRESS_REQUEST_CODE - 5000
                        HomeActivity.SERVICE_ADDRESS_REQUEST_CODE = 5000

                        //Get binding from list if layout is serviceAndBillingAddress then check id is same
                        // Then set addressComponent in serviceAddressFields
                        bindingList.forEach { binding ->
                            when (binding) {
                                is LayoutInputServiceAndBillingAddressBinding -> {
                                    if (binding.item?.id?.equals(id).orFalse()) {
                                        binding.fillAddressFields(data.let { Autocomplete.getPlaceFromIntent(it) }, true, mViewModel)
                                    }
                                }
                            }
                        }
                    }
                    else -> super.onActivityResult(requestCode, resultCode, data)
                }
            }
        }
    }
}
