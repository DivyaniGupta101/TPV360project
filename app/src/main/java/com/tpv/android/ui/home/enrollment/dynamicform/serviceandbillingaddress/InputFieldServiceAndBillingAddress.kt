package com.tpv.android.ui.home.enrollment.dynamicform.serviceandbillingaddress


import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.livinglifetechway.k4kotlin.core.onClick
import com.livinglifetechway.k4kotlin.core.orZero
import com.livinglifetechway.k4kotlin.core.value
import com.tpv.android.R
import com.tpv.android.databinding.LayoutInputServiceAndBillingAddressBinding
import com.tpv.android.helper.addressComponents
import com.tpv.android.model.network.DynamicFormResp
import com.tpv.android.ui.home.HomeActivity
import com.tpv.android.utils.AppConstant
import com.tpv.android.utils.validation.EmptyValidator
import com.tpv.android.utils.validation.TextInputValidationErrorHandler
import com.tpv.android.utils.validation.Validator
import java.util.*


private var addressField =
        Arrays.asList(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS_COMPONENTS,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG)


fun LayoutInputServiceAndBillingAddressBinding.setField(response: DynamicFormResp) {

    val binding = this
    val context = binding.editServiceAddress.context

    binding.item = response

    // AutoPlace picker
    if (!Places.isInitialized()) {
        context?.let { Places.initialize(it, AppConstant.ADDRESSPICKER_KEY, Locale.US) }
    }

    binding.editServiceAddress.onClick {
        context.openPlacePicker(binding, true)
    }

    binding.editBillingAddress.onClick {
        context.openPlacePicker(binding, false)
    }

    binding.radioYes.onClick {
        handleBothAddressField(binding, true)
    }


    binding.radioNo.onClick {
        handleBothAddressField(binding, false)
    }

    binding.editServiceUnit.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (binding.radioYes.isChecked) {
                binding.item?.values?.serviceUnit = binding.editServiceUnit.value
                handleBothAddressField(binding, true)
            }
        }
    })
}

fun LayoutInputServiceAndBillingAddressBinding.isValid(context: Context?): Boolean {
    val binding = this
    return Validator(TextInputValidationErrorHandler()) {
        addValidate(
                binding.editServiceAddress,
                EmptyValidator(),
                context?.getString(R.string.enter_service_address)
        )
        addValidate(
                binding.editServiceUnit,
                EmptyValidator(),
                context?.getString(R.string.enter_service_unit)
        )
        addValidate(
                binding.editBillingAddress,
                EmptyValidator(),
                context?.getString(R.string.enter_billing_address)
        )
        addValidate(
                binding.editBillingUnit,
                EmptyValidator(),
                context?.getString(R.string.enter_billing_unit)
        )
    }.validate()
}


fun LayoutInputServiceAndBillingAddressBinding.fillAddressFields(fillAddressFields: Place?, isServiceAddress: Boolean) {

    val binding = this
    val addressComponent = fillAddressFields?.let { addressComponents(it) }

    if (isServiceAddress) {
        binding.item?.values?.apply {
            serviceAddress = addressComponent?.address.orEmpty()
            serviceAddress1 = addressComponent?.addressLine1.orEmpty()
            serviceAddress2 = addressComponent?.addressLine2.orEmpty()
            serviceZipcode = addressComponent?.zipcode.orEmpty()
            serviceLat = addressComponent?.latitude.orEmpty()
            serviceLng = addressComponent?.longitude.orEmpty()
            serviceCountry = addressComponent?.country.orEmpty()
            serviceCountry = addressComponent?.city.orEmpty()
            serviceState = addressComponent?.state.orEmpty()
        }
        handleBothAddressField(binding, true)
    } else {
        binding.item?.values?.apply {
            billingAddress = addressComponent?.address.orEmpty()
            billingAddress1 = addressComponent?.addressLine1.orEmpty()
            billingAddress2 = addressComponent?.addressLine2.orEmpty()
            billingZipcode = addressComponent?.zipcode.orEmpty()
            billingLat = addressComponent?.latitude.orEmpty()
            billingLng = addressComponent?.longitude.orEmpty()
            billingCountry = addressComponent?.country.orEmpty()
            billingCity = addressComponent?.city.orEmpty()
            billingState = addressComponent?.state.orEmpty()
        }
    }
    binding.invalidateAll()

}

/**
 * If billing address and service address are same then save all values of service address fields in billing address fields
 * Else set blank values in all billing address fields
 */
private fun handleBothAddressField(binding: LayoutInputServiceAndBillingAddressBinding, isSame: Boolean) {

    if (isSame) {
        binding.editBillingAddress.isEnabled = false
        binding.editBillingUnit.isEnabled = false
        binding.item?.values?.apply {
            isAddressSame = isSame
            billingAddress = serviceAddress
            billingUnit = serviceUnit
            billingAddress1 = serviceAddress1
            billingAddress2 = serviceAddress2
            billingZipcode = serviceZipcode
            billingLat = serviceLat
            billingLng = serviceLng
            billingCity = serviceCity
            billingState = serviceState
            billingCountry = serviceCountry
        }
    } else {
        binding.editBillingAddress.isEnabled = true
        binding.editBillingUnit.isEnabled = true
        binding.item?.values?.apply {
            isAddressSame = isSame
            billingAddress = ""
            billingUnit = ""
            billingAddress1 = ""
            billingAddress2 = ""
            billingZipcode = ""
            billingLat = ""
            billingLng = ""
            billingCity = ""
            billingState = ""
            billingCountry = ""
        }
    }
    binding.invalidateAll()

}

private fun Context.openPlacePicker(binding: LayoutInputServiceAndBillingAddressBinding, isServiceAddress: Boolean) {
    val context = this

    if (context is HomeActivity) {
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, addressField)
                .setCountry(AppConstant.PLACE_COUNTRY)
                .build(context)

        if (isServiceAddress) {
            HomeActivity.SERVICE_ADDRESS_REQUEST_CODE = HomeActivity.SERVICE_ADDRESS_REQUEST_CODE + binding.item?.id.orZero()
            binding.root.findFragment<Fragment>().startActivityForResult(intent, HomeActivity.SERVICE_ADDRESS_REQUEST_CODE)
        } else {
            HomeActivity.BILLING_ADDRESS_REQUEST_CODE = HomeActivity.BILLING_ADDRESS_REQUEST_CODE + binding.item?.id.orZero()
            binding.root.findFragment<Fragment>().startActivityForResult(intent, HomeActivity.BILLING_ADDRESS_REQUEST_CODE)
        }
    }
}

