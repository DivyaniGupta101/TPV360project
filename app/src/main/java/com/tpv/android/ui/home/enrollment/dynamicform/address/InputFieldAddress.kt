package com.tpv.android.ui.home.enrollment.dynamicform.address

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.livinglifetechway.k4kotlin.core.onClick
import com.livinglifetechway.k4kotlin.core.orFalse
import com.livinglifetechway.k4kotlin.core.orZero
import com.tpv.android.R
import com.tpv.android.databinding.LayoutInputAddressBinding
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


fun LayoutInputAddressBinding.setField(response: DynamicFormResp) {

    val binding = this
    val context = binding.editAddress.context

    binding.item = response

    // AutoPlace picker
    if (!Places.isInitialized()) {
        context?.let { Places.initialize(it, AppConstant.ADDRESSPICKER_KEY, Locale.US) }
    }

    binding.editAddress.onClick {
        context.openPlacePicker(binding)
    }

}

fun LayoutInputAddressBinding.fillAddressFields(fillAddressFields: Place?) {
    val binding = this
    val addressComponent = fillAddressFields?.let { addressComponents(it) }
    binding.item?.address = addressComponent?.address.orEmpty()
    binding.item?.values?.set(AppConstant.ADDRESS1, addressComponent?.addressLine1.orEmpty())
    binding.item?.values?.set(AppConstant.ADDRESS2, addressComponent?.addressLine2.orEmpty())
    binding.item?.values?.set(AppConstant.ZIPCODE, addressComponent?.zipcode.orEmpty())
    binding.item?.values?.set(AppConstant.LAT, addressComponent?.latitude.orEmpty())
    binding.item?.values?.set(AppConstant.LNG, addressComponent?.longitude.orEmpty())
    binding.item?.values?.set(AppConstant.COUNTRY, addressComponent?.country.orEmpty())
    binding.item?.values?.set(AppConstant.CITY, addressComponent?.city.orEmpty())
    binding.item?.values?.set(AppConstant.STATE, addressComponent?.state.orEmpty())
    binding.invalidateAll()
}

fun LayoutInputAddressBinding.isValid(context: Context?): Boolean {
    val binding = this
    return if (binding.item?.validations?.required.orFalse()) {
        Validator(TextInputValidationErrorHandler()) {
            addValidate(
                    binding.editAddress,
                    EmptyValidator(),
                    context?.getString(R.string.enter_address)

            )
            addValidate(
                    binding.editUnit,
                    EmptyValidator(),
                    context?.getString(R.string.enter_unit)
            )
        }.validate()
    } else {
        return true
    }
}

private fun Context.openPlacePicker(binding: LayoutInputAddressBinding) {
    val context = this
    if (context is HomeActivity) {
        HomeActivity.ADDRESS_REQUEST_CODE = HomeActivity.ADDRESS_REQUEST_CODE + binding.item?.id.orZero()
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, addressField)
                .setCountry(AppConstant.PLACE_COUNTRY)
                .build(context)
        binding.root.findFragment<Fragment>().startActivityForResult(intent, HomeActivity.ADDRESS_REQUEST_CODE)
    }
}