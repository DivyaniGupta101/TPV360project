package com.tpv.android.ui.home.enrollment.form.electricdetailform


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.livinglifetechway.k4kotlin.core.androidx.hideKeyboard
import com.livinglifetechway.k4kotlin.core.onClick
import com.livinglifetechway.k4kotlin.core.value
import com.tpv.android.R
import com.tpv.android.databinding.FragmentElectricDetailFormBinding
import com.tpv.android.helper.addressComponents
import com.tpv.android.model.network.CustomerData
import com.tpv.android.ui.home.enrollment.SetEnrollViewModel
import com.tpv.android.utils.AppConstant
import com.tpv.android.utils.enums.Plan
import com.tpv.android.utils.navigateSafe
import com.tpv.android.utils.setupToolbar
import com.tpv.android.utils.validation.EmptyValidator
import com.tpv.android.utils.validation.TextInputValidationErrorHandler
import com.tpv.android.utils.validation.Validator
import java.util.*

class ElectricDetailFormFragment : Fragment() {
    private lateinit var mBinding: FragmentElectricDetailFormBinding
    private lateinit var mViewModel: SetEnrollViewModel
    private var mSelectedRadioButton = "No"
    private var mCustomerData: CustomerData = CustomerData()
    private var addressField =
            Arrays.asList(
                    Place.Field.ID,
                    Place.Field.NAME,
                    Place.Field.ADDRESS_COMPONENTS,
                    Place.Field.ADDRESS,
                    Place.Field.LAT_LNG)
    private var AUTOCOMPLETE_REQUEST_CODE_BILLING = 1
    private var AUTOCOMPLETE_REQUEST_CODE_SERVICE = 2


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_electric_detail_form, container, false)
        activity?.let { mViewModel = ViewModelProviders.of(it).get(SetEnrollViewModel::class.java) }
        return mBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
    }

    private fun initialize() {
        setupToolbar(mBinding.toolbar, getString(R.string.customer_data), showBackIcon = true)
        mBinding.item = mViewModel.customerData
        mBinding.viewModel = mViewModel

        if (mViewModel.customerDataList.isNotEmpty()) {
            mBinding.item = mViewModel.customerDataList.find { it?.planType == Plan.ELECTRICFUEL.value }
        }

        // AutoPlace picker
        if (!Places.isInitialized()) {
            context?.let { Places.initialize(it, AppConstant.ADDRESSPICKER_KEY, Locale.US) }
        }

        mBinding.radioYes?.onClick()
        {
            mBinding.layoutBillingAddress.editAddress.isEnabled = false
            mBinding.layoutBillingAddress.editUnit.isEnabled = false
            mBinding.layoutBillingAddress.editAddress?.value = mBinding.layoutServiceAddress?.editAddress?.value.orEmpty()
            mBinding.layoutBillingAddress.editUnit?.value = mBinding.layoutServiceAddress?.editUnit?.value.orEmpty()
            mBinding.layoutBillingAddress?.editAddressLineOne?.value = mBinding.layoutServiceAddress?.editAddressLineOne?.value.orEmpty()
            mBinding.layoutBillingAddress?.editAddressLineTwo?.value = mBinding.layoutServiceAddress?.editAddressLineTwo?.value.orEmpty()
            mBinding.layoutBillingAddress?.editZipcode?.value = mBinding.layoutServiceAddress?.editZipcode?.value.orEmpty()
            mBinding.layoutBillingAddress?.editLatitude?.value = mBinding.layoutServiceAddress?.editLatitude?.value.orEmpty()
            mBinding.layoutBillingAddress?.editLongitude?.value = mBinding.layoutServiceAddress?.editLongitude?.value.orEmpty()
            mBinding.layoutBillingAddress?.editCity?.value = mBinding.layoutServiceAddress?.editCity?.value.orEmpty()
            mBinding.layoutBillingAddress?.editState?.value = mBinding.layoutServiceAddress?.editState?.value.orEmpty()
            mBinding.layoutBillingAddress?.editCountry?.value = mBinding.layoutServiceAddress?.editCountry?.value.orEmpty()
        }

        // Billing address,Zipcode will not same Service Address,Service Zipcode respectively
        // And Billing address and zipcode will be editable
        mBinding.radioNo?.onClick()
        {
            mBinding.layoutBillingAddress.editAddress.isEnabled = true
            mBinding.layoutBillingAddress.editUnit.isEnabled = true
            mBinding.layoutBillingAddress.editUnit.value = ""
            mBinding.layoutBillingAddress.editAddress?.value = ""
            mBinding.layoutBillingAddress?.editAddressLineOne?.value = ""
            mBinding.layoutBillingAddress?.editAddressLineTwo?.value = ""
            mBinding.layoutBillingAddress?.editZipcode?.value = ""
            mBinding.layoutBillingAddress?.editLatitude?.value = ""
            mBinding.layoutBillingAddress?.editLongitude?.value = ""
            mBinding.layoutBillingAddress?.editCity?.value = ""
            mBinding.layoutBillingAddress?.editState?.value = ""
            mBinding.layoutBillingAddress?.editCountry?.value = ""
        }

        mBinding.layoutServiceAddress.editUnit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (mBinding.radioYes.isChecked) {
                    mBinding.layoutBillingAddress.editUnit.value = mBinding.layoutServiceAddress.editUnit.value
                }
            }

        })

        mBinding.layoutServiceAddress.editAddress.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (mBinding.radioYes.isChecked) {
                    mBinding.layoutBillingAddress.editAddress.isEnabled = false
                    mBinding.layoutBillingAddress.editUnit.isEnabled = false
                    mBinding.layoutBillingAddress.editAddress?.value = mBinding.layoutServiceAddress?.editAddress?.value.orEmpty()
                    mBinding.layoutBillingAddress.editUnit?.value = mBinding.layoutServiceAddress?.editUnit?.value.orEmpty()
                    mBinding.layoutBillingAddress?.editAddressLineOne?.value = mBinding.layoutServiceAddress?.editAddressLineOne?.value.orEmpty()
                    mBinding.layoutBillingAddress?.editAddressLineTwo?.value = mBinding.layoutServiceAddress?.editAddressLineTwo?.value.orEmpty()
                    mBinding.layoutBillingAddress?.editZipcode?.value = mBinding.layoutServiceAddress?.editZipcode?.value.orEmpty()
                    mBinding.layoutBillingAddress?.editLatitude?.value = mBinding.layoutServiceAddress?.editLatitude?.value.orEmpty()
                    mBinding.layoutBillingAddress?.editLongitude?.value = mBinding.layoutServiceAddress?.editLongitude?.value.orEmpty()
                    mBinding.layoutBillingAddress?.editCity?.value = mBinding.layoutServiceAddress?.editCity?.value.orEmpty()
                    mBinding.layoutBillingAddress?.editState?.value = mBinding.layoutServiceAddress?.editState?.value.orEmpty()
                    mBinding.layoutBillingAddress?.editCountry?.value = mBinding.layoutServiceAddress?.editCountry?.value.orEmpty()
                }
            }
        })

        mBinding.layoutServiceAddress.editAddress?.onClick {
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, addressField)
                    .setCountry("US")
                    .build(context)
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE_SERVICE)
        }

        mBinding.layoutBillingAddress.editAddress?.onClick {
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, addressField)
                    .setCountry("US")
                    .build(context)
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE_BILLING)
        }

        mBinding.radioGroup.setOnCheckedChangeListener()
        { group, checkedId ->
            mSelectedRadioButton = group.findViewById<RadioButton>(checkedId).text.toString()
        }


        mBinding.btnNext.onClick()
        {
            if (isValid()) {
                hideKeyboard()
                setValueInViewModel()
                Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_electricDetailFormFragment_to_clientInfoFragment)
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                AUTOCOMPLETE_REQUEST_CODE_BILLING -> {
                    val place = Autocomplete.getPlaceFromIntent(data!!)
                    val addressComponent = addressComponents(place)

                    mBinding.layoutBillingAddress.editAddressLineOne.value = addressComponent?.addressLine1.orEmpty()
                    mBinding.layoutBillingAddress.editAddressLineTwo.value = addressComponent?.addressLine2.orEmpty()
                    mBinding.layoutBillingAddress.editZipcode.value = addressComponent?.zipcode.orEmpty()
                    mBinding.layoutBillingAddress.editLatitude.value = addressComponent?.latitude.orEmpty()
                    mBinding.layoutBillingAddress.editLongitude.value = addressComponent?.longitude.orEmpty()
                    mBinding.layoutBillingAddress.editCountry.value = addressComponent?.country.orEmpty()
                    mBinding.layoutBillingAddress.editCity.value = addressComponent?.city.orEmpty()
                    mBinding.layoutBillingAddress.editState.value = addressComponent?.state.orEmpty()
                    mBinding.layoutBillingAddress.editUnit.value = addressComponent?.unit.orEmpty()
                    mBinding.layoutBillingAddress.editAddress.value = addressComponent?.address.orEmpty()
                }

                AUTOCOMPLETE_REQUEST_CODE_SERVICE -> {
                    val place = Autocomplete.getPlaceFromIntent(data!!)
                    val addressComponent = addressComponents(place)

                    mBinding.layoutServiceAddress.editAddressLineOne.value = addressComponent?.addressLine1.orEmpty()
                    mBinding.layoutServiceAddress.editAddressLineTwo.value = addressComponent?.addressLine2.orEmpty()
                    mBinding.layoutServiceAddress.editZipcode.value = addressComponent?.zipcode.orEmpty()
                    mBinding.layoutServiceAddress.editLatitude.value = addressComponent?.latitude.orEmpty()
                    mBinding.layoutServiceAddress.editLongitude.value = addressComponent?.longitude.orEmpty()
                    mBinding.layoutServiceAddress.editCountry.value = addressComponent?.country.orEmpty()
                    mBinding.layoutServiceAddress.editCity.value = addressComponent?.city.orEmpty()
                    mBinding.layoutServiceAddress.editState.value = addressComponent?.state.orEmpty()
                    mBinding.layoutServiceAddress.editUnit.value = addressComponent?.unit.orEmpty()
                    mBinding.layoutServiceAddress.editAddress.value = addressComponent?.address.orEmpty()

                }
            }
        }
    }

    private fun isValid(): Boolean {
        return Validator(TextInputValidationErrorHandler()) {
            addValidate(
                    mBinding.layoutServiceAddress.editAddress,
                    EmptyValidator(),
                    getString(R.string.enter_service_address)
            )
            addValidate(
                    mBinding.layoutServiceAddress.editUnit,
                    EmptyValidator(),
                    getString(R.string.enter_service_unit)
            )
            addValidate(
                    mBinding.layoutBillingAddress.editAddress,
                    EmptyValidator(),
                    getString(R.string.enter_billing_address)
            )
            addValidate(
                    mBinding.layoutBillingAddress.editUnit,
                    EmptyValidator(),
                    getString(R.string.enter_billing_unit)
            )
            addValidate(
                    mBinding.editBillingFirstName,
                    EmptyValidator(),
                    getString(R.string.enter_billing_first_name)
            )
            addValidate(
                    mBinding.editBillingLastName,
                    EmptyValidator(),
                    getString(R.string.enter_billing_last_name)
            )
            addValidate(
                    mBinding.editAccountNumber,
                    EmptyValidator(),
                    getString(R.string.enter_account_number)
            )
        }.validate()
    }

    /**
     * Check planType, if It's DuelFuel then parameters will be different than ELECTRIC Fuel
     * And set as per parameters required in Model class
     */
    private fun setValueInViewModel() {

        mCustomerData.apply {
            billingFirstName = mBinding.editBillingFirstName.value
            billingMiddleInitial = mBinding.editBillingMiddleName.value
            billingLastName = mBinding.editBillingLastName.value
            accountNumber = mBinding.editAccountNumber.value

            billingAddress = mBinding.layoutBillingAddress.editAddress.value
            serviceAddress = mBinding.layoutServiceAddress.editAddress.value

            billingUnit = mBinding.layoutBillingAddress.editUnit.value
            serviceUnit = mBinding.layoutServiceAddress.editUnit.value

            billingAddressLine1 = mBinding.layoutBillingAddress.editAddressLineOne.value
            serviceAddressLine1 = mBinding.layoutServiceAddress.editAddressLineOne.value

            billingAddressLine2 = mBinding.layoutBillingAddress.editAddressLineOne.value
            serviceAddressLine2 = mBinding.layoutServiceAddress.editAddressLineOne.value

            billingZipcode = mBinding.layoutBillingAddress.editZipcode.value
            serviceZipcode = mBinding.layoutServiceAddress.editZipcode.value

            billingLatitude = mBinding.layoutBillingAddress.editLatitude.value
            serviceLatitude = mBinding.layoutServiceAddress.editLatitude.value

            billingLongitude = mBinding.layoutBillingAddress.editLongitude.value
            serviceLongitude = mBinding.layoutServiceAddress.editLongitude.value

            billingCity = mBinding.layoutBillingAddress.editCity.value
            serviceCity = mBinding.layoutServiceAddress.editCity.value

            billingState = mBinding.layoutBillingAddress.editState.value
            serviceState = mBinding.layoutServiceAddress.editState.value

            billingCountry = mBinding.layoutBillingAddress.editCountry.value
            serviceCountry = mBinding.layoutServiceAddress.editCountry.value

            isAddressSame = if (mSelectedRadioButton.equals(getString(R.string.yes))) true else false

            planType = Plan.ELECTRICFUEL.value
        }

        mViewModel.customerDataList.add(mCustomerData)

        mViewModel.customerData.apply {

            when (mViewModel.planType) {

                Plan.ELECTRICFUEL.value -> {
                    billingFirstName = mBinding.editBillingFirstName.value
                    billingMiddleInitial = mBinding.editBillingMiddleName.value
                    billingLastName = mBinding.editBillingLastName.value
                    billingAddress = mBinding.layoutBillingAddress.editAddress.value
                    billingZip = mBinding.layoutBillingAddress.editZipcode.value
                    isTheBillingAddressTheSameAsTheServiceAddress = mSelectedRadioButton
                    billingAddress2 = ""
                    serviceAddress = mBinding.layoutServiceAddress.editAddress.value
                    serviceAddress2 = ""
                    serviceZip = mBinding.layoutServiceAddress.editZipcode.value
                    accountNumber = mBinding.editAccountNumber.value
                    billingCity = "Amherst"
                    billingState = "MA"
                    serviceState = "MA"
                    serviceCity = "Amherst"
                }

                Plan.DUALFUEL.value -> {
                    electricBillingFirstName = mBinding.editBillingFirstName.value
                    electricBillingMiddleInitial = mBinding.editBillingMiddleName.value
                    electricBillingLastName = mBinding.editBillingLastName.value
                    electricBillingAddress = mBinding.layoutBillingAddress.editAddress.value
                    electricBillingAddress2 = ""
                    electricBillingCity = "Amherst"
                    electricBillingState = "MA"
                    electricBillingZip = mBinding.layoutBillingAddress.editZipcode.value
                    serviceAddress = mBinding.layoutServiceAddress.editAddress.value
                    serviceAddress2 = ""
                    serviceCity = "Amherst"
                    serviceState = "MA"
                    serviceZip = mBinding.layoutServiceAddress.editZipcode.value
                    electricAccountNumber = mBinding.editAccountNumber.value
                }
            }
        }
    }
}
