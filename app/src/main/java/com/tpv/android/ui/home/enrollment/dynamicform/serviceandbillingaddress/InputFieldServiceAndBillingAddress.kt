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


fun LayoutInputServiceAndBillingAddressBinding.setField(resp: DynamicFormResp) {

    val binding = this
    val context = binding.editServiceAddress.context

    binding.item = resp

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

    binding.editServiceAddress.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (binding.radioYes.isChecked) {
                handleBothAddressField(binding, true)
            }
        }
    })

    binding.editServiceUnit.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (binding.radioYes.isChecked) {
                binding.editBillingUnit.value = binding.editServiceUnit.value
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
        binding.apply {
            editServiceAddress.value = addressComponent?.address.orEmpty()
            editServiceAddressLineOne.value = addressComponent?.addressLine1.orEmpty()
            editServiceAddressLineTwo.value = addressComponent?.addressLine2.orEmpty()
            editServiceZipcode.value = addressComponent?.zipcode.orEmpty()
            editServiceLatitude.value = addressComponent?.latitude.orEmpty()
            editServiceLongitude.value = addressComponent?.longitude.orEmpty()
            editServiceCountry.value = addressComponent?.country.orEmpty()
            editServiceCity.value = addressComponent?.city.orEmpty()
            editServiceState.value = addressComponent?.state.orEmpty()
        }
    } else {
        binding.apply {
            editBillingAddress.value = addressComponent?.address.orEmpty()
            editBillingAddressLineOne.value = addressComponent?.addressLine1.orEmpty()
            editBillingAddressLineTwo.value = addressComponent?.addressLine2.orEmpty()
            editBillingZipcode.value = addressComponent?.zipcode.orEmpty()
            editBillingLatitude.value = addressComponent?.latitude.orEmpty()
            editBillingLongitude.value = addressComponent?.longitude.orEmpty()
            editBillingCountry.value = addressComponent?.country.orEmpty()
            editBillingCity.value = addressComponent?.city.orEmpty()
            editBillingState.value = addressComponent?.state.orEmpty()
        }
    }

}

private fun handleBothAddressField(binding: LayoutInputServiceAndBillingAddressBinding, isSame: Boolean) {

    if (isSame) {
        binding.editBillingAddress.isEnabled = false
        binding.editBillingUnit.isEnabled = false
        binding.editBillingAddress?.value = binding.editServiceAddress?.value.orEmpty()
        binding.editBillingUnit?.value = binding.editServiceUnit?.value.orEmpty()
        binding.editBillingAddressLineOne?.value = binding.editServiceAddressLineOne?.value.orEmpty()
        binding.editBillingAddressLineTwo?.value = binding.editServiceAddressLineTwo?.value.orEmpty()
        binding.editBillingZipcode?.value = binding.editServiceZipcode?.value.orEmpty()
        binding.editBillingLatitude?.value = binding.editServiceLatitude?.value.orEmpty()
        binding.editBillingLongitude?.value = binding.editServiceLongitude?.value.orEmpty()
        binding.editBillingCity?.value = binding.editServiceCity?.value.orEmpty()
        binding.editBillingState?.value = binding.editServiceState?.value.orEmpty()
        binding.editBillingCountry?.value = binding.editServiceCountry?.value.orEmpty()

    } else {
        binding.editBillingAddress.isEnabled = true
        binding.editBillingUnit.isEnabled = true
        binding.editBillingAddress?.value = ""
        binding.editBillingUnit.value = ""
        binding.editBillingAddressLineOne?.value = ""
        binding.editBillingAddressLineTwo?.value = ""
        binding.editBillingZipcode?.value = ""
        binding.editBillingLatitude?.value = ""
        binding.editBillingLongitude?.value = ""
        binding.editBillingCity?.value = ""
        binding.editBillingState?.value = ""
        binding.editBillingCountry?.value = ""
    }
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

