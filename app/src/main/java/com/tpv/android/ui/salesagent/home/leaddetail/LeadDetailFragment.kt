package com.tpv.android.ui.salesagent.home.leaddetail


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.reflect.TypeToken
import com.livinglifetechway.k4kotlin.core.orFalse
import com.tpv.android.R
import com.tpv.android.databinding.*
import com.tpv.android.model.network.DynamicFormResp
import com.tpv.android.model.network.Option
import com.tpv.android.network.error.AlertErrorHandler
import com.tpv.android.network.resources.Resource
import com.tpv.android.network.resources.apierror.APIError
import com.tpv.android.network.resources.extensions.ifSuccess
import com.tpv.android.utils.AppConstant
import com.tpv.android.utils.enums.DynamicField
import com.tpv.android.utils.setupToolbar

class LeadDetailFragment : Fragment() {

    private lateinit var mBinding: FragmentLeadDetailBinding
    private lateinit var mViewModel: LeadDetailViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_lead_detail, container, false)
        mBinding.lifecycleOwner = this
        mViewModel = ViewModelProviders.of(this).get(LeadDetailViewModel::class.java)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
    }

    private fun initialize() {

        setupToolbar(mBinding.toolbar, getString(R.string.lead_details), showBackIcon = true)
        getLeadDetailApiCall()
    }

    /**
     * Get leadDetail data and inflate in views
     */
    private fun getLeadDetailApiCall() {
        mBinding.errorHandler = AlertErrorHandler(mBinding.root)
        val liveData = mViewModel.getLeadDetail(arguments?.let { LeadDetailFragmentArgs.fromBundle(it) }?.item)
        liveData.observe(this, Observer {
            it?.ifSuccess {
                it?.forEach { response ->
                    inflateViews(response)
                }
            }

        })

        mBinding.resource = liveData as LiveData<Resource<Any, APIError>>
    }

    private fun inflateViews(response: DynamicFormResp) {

        when (response.type) {
            DynamicField.FULLNAME.type -> {
                setFullNameField(response)
            }
            DynamicField.PHONENUMBER.type -> {
                setSingleField(response)
            }
            DynamicField.BOTHADDRESS.type -> {
                setBothAddress(response)
            }
            DynamicField.ADDRESS.type -> {
                setAddress(response)
            }
            DynamicField.SELECTBOX.type -> {
                setSelectionField(response)
            }
            DynamicField.CHECKBOX.type -> {
                setSelectionField(response)
            }
            DynamicField.RADIO.type -> {
                setSelectionField(response)
            }
            DynamicField.EMAIL.type -> {
                setSingleField(response)
            }
            DynamicField.TEXTAREA.type -> {
                setSingleField(response)
            }
            DynamicField.TEXTBOX.type -> {
                setSingleField(response)
            }
            DynamicField.HEADING.type -> {
                setHeadingField(response)
            }
            DynamicField.LABEL.type -> {
                setLabelField(response)
            }
            DynamicField.SEPARATE.type -> {
                setSeparateField()
            }
        }
    }

    /**
     * Inflate view for both billing and service address
     */
    private fun setBothAddress(response: DynamicFormResp) {
        setServiceAddress(response)
    }

    /**
     * Inflate service address view
     */
    private fun setServiceAddress(response: DynamicFormResp) {
        val binding = DataBindingUtil.inflate<LayoutOutputServiceAndBillingAddressBinding>(layoutInflater,
                R.layout.layout_output_service_and_billing_address,
                mBinding.leadDetailContainer,
                true)

        setBillingAddress(response, binding)

        val unitNumber = response.values[AppConstant.SERVICEUNIT] as String?
        val addressLineOne = response.values[AppConstant.SERVICEADDRESS1] as String?
        val addressLineTwo = response.values[AppConstant.SERVICEADDRESS2] as String?
        val city = response.values[AppConstant.SERVICECITY] as String?
        val state = response.values[AppConstant.SERVICESTATE] as String?
        val zipcode = response.values[AppConstant.SERVICEZIPCODE] as String?
        val country = response.values[AppConstant.SERVICECOUNTRY] as String?

        var text = ""
        if (unitNumber?.isNotEmpty().orFalse()) {
            text = unitNumber + "\n"
        }
        if (addressLineOne?.isNotEmpty().orFalse()) {
            text += addressLineOne + "\n"
        }

        if (addressLineTwo?.isNotEmpty().orFalse()) {
            text += addressLineTwo + "\n"
        }
        if (city?.isNotEmpty().orFalse()) {
            text += "$city,"
        }
        if (state?.isNotEmpty().orFalse()) {
            text += "$state,"
        }
        if (zipcode?.isNotEmpty().orFalse()) {
            text += zipcode + "\n"
        }
        if (country?.isNotEmpty().orFalse()) {
            text += country + "\n"
        }
        binding.serviceValue.text = text
    }

    /**
     * Set value for billing address
     */
    private fun setBillingAddress(response: DynamicFormResp, binding: LayoutOutputServiceAndBillingAddressBinding) {

        val unitNumber = response.values[AppConstant.BILLINGUNIT] as String?
        val addressLineOne = response.values[AppConstant.BILLINGADDRESS1] as String?
        val addressLineTwo = response.values[AppConstant.BILLINGADDRESS2] as String?
        val city = response.values[AppConstant.BILLINGCITY] as String?
        val state = response.values[AppConstant.BILLINGSTATE] as String?
        val zipcode = response.values[AppConstant.BILLINGZIPCODE] as String?
        val country = response.values[AppConstant.BILLINGCOUNTRY] as String?

        var text = ""
        if (unitNumber?.isNotEmpty().orFalse()) {
            text = unitNumber + "\n"
        }
        if (addressLineOne?.isNotEmpty().orFalse()) {
            text = text + addressLineOne + "\n"
        }

        if (addressLineTwo?.isNotEmpty().orFalse()) {
            text += addressLineTwo + "\n"
        }
        if (city?.isNotEmpty().orFalse()) {
            text += "$city,"
        }
        if (state?.isNotEmpty().orFalse()) {
            text += "$state,"
        }
        if (zipcode?.isNotEmpty().orFalse()) {
            text += zipcode + "\n"
        }
        if (country?.isNotEmpty().orFalse()) {
            text += country + "\n"
        }
        binding.billingValue.text = text
    }

    /**
     * Inflate view for label
     */
    private fun setLabelField(response: DynamicFormResp) {
        val binding = DataBindingUtil.inflate<LayoutInputLabelBinding>(layoutInflater,
                R.layout.layout_input_label,
                mBinding.leadDetailContainer,
                true)

        binding.item = response.label

    }

    /**
     * Inflate view for heading
     */
    private fun setHeadingField(response: DynamicFormResp) {
        val binding = DataBindingUtil.inflate<LayoutInputHeadingBinding>(layoutInflater,
                R.layout.layout_input_heading,
                mBinding.leadDetailContainer,
                true)
        binding.item = response

    }

    /**
     * Inflate view for separate or divider
     */
    private fun setSeparateField() {
        DataBindingUtil.inflate<LayoutOutputSeparateBinding>(layoutInflater,
                R.layout.layout_output_separate,
                mBinding.leadDetailContainer,
                true)
    }

    /**
     * Inflate view for address
     */
    private fun setAddress(response: DynamicFormResp) {

        val binding = DataBindingUtil.inflate<LayoutOutputTextFieldsBinding>(layoutInflater,
                R.layout.layout_output_text_fields,
                mBinding.leadDetailContainer,
                true)

        val unitNumber = response.values[AppConstant.UNIT] as String?
        val addressLineOne = response.values[AppConstant.ADDRESS1] as String?
        val addressLineTwo = response.values[AppConstant.ADDRESS2] as String?
        val city = response.values[AppConstant.CITY] as String?
        val state = response.values[AppConstant.STATE] as String?
        val zipcode = response.values[AppConstant.ZIPCODE] as String?
        val country = response.values[AppConstant.COUNTRY] as String?

        var text = ""
        if (unitNumber?.isNotEmpty().orFalse()) {
            text = unitNumber + "\n"
        }
        if (addressLineOne?.isNotEmpty().orFalse()) {
            text += addressLineOne + "\n"
        }
        if (addressLineTwo?.isNotEmpty().orFalse()) {
            text += addressLineTwo + "\n"
        }
        if (city?.isNotEmpty().orFalse()) {
            text += "$city,"
        }
        if (state?.isNotEmpty().orFalse()) {
            text += "$state,"
        }
        if (zipcode?.isNotEmpty().orFalse()) {
            text += zipcode + "\n"
        }
        if (country?.isNotEmpty().orFalse()) {
            text += country + "\n"
        }
        response.leadDetailText = text
        response.label = getString(R.string.address)
        binding.item = response
    }

    /**
     * Inflate view for selection fields value(radioButton,checkBox,spinner)
     */
    private fun setSelectionField(response: DynamicFormResp) {
        val binding = DataBindingUtil.inflate<LayoutOutputTextFieldsBinding>(layoutInflater,
                R.layout.layout_output_text_fields,
                mBinding.leadDetailContainer,
                true)
        val list: ArrayList<Option> = ArrayList()
        val option = response.values[AppConstant.OPTIONS] as ArrayList<LinkedTreeMap<String, Any>>
        option.forEach {
            val json = Gson().toJson(it)
            list.add(Gson().fromJson(json, object : TypeToken<Option>() {}.type))
        }
        response.leadDetailText = android.text.TextUtils.join(",", list.filter { it.selected == true }.map { it.option })

        binding.item = response
    }

    /**
     * Inflate view for single line field
     */
    private fun setSingleField(response: DynamicFormResp) {
        val binding = DataBindingUtil.inflate<LayoutOutputTextFieldsBinding>(layoutInflater,
                R.layout.layout_output_text_fields,
                mBinding.leadDetailContainer,
                true)
        response.leadDetailText = response.values[AppConstant.VALUE] as String?
        binding.item = response
    }

    /**
     * Inflate view for full name
     */
    private fun setFullNameField(response: DynamicFormResp) {

        val binding = DataBindingUtil.inflate<LayoutOutputTextFieldsBinding>(layoutInflater,
                R.layout.layout_output_text_fields,
                mBinding.leadDetailContainer,
                true)

        var text: String? = ""
        text = response.values.get(AppConstant.FIRSTNAME) as String?
        if ((response.values.get(AppConstant.MIDDLENAME) as String?)?.isNotEmpty().orFalse()) {
            text += " " + response.values[AppConstant.MIDDLENAME] as String?
        }
        text += " " + response.values.get(AppConstant.LASTNAME) as String?

        response.leadDetailText = text
        binding.item = response
    }

}
