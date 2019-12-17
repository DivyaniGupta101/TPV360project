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
import com.livinglifetechway.k4kotlin.core.value
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


fun LayoutInputAddressBinding.setField(resp: DynamicFormResp) {

    val binding = this
    val context = binding.editAddress.context

    binding.item = resp

    // AutoPlace picker
    if (!Places.isInitialized()) {
        context?.let { Places.initialize(it, AppConstant.ADDRESSPICKER_KEY, Locale.US) }
    }

    binding.editAddress.onClick {
        if (context is HomeActivity) {
            HomeActivity.ADDRESS_REQUEST_CODE = HomeActivity.ADDRESS_REQUEST_CODE + item?.id.orZero()
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, addressField)
                    .setCountry(AppConstant.PLACE_COUNTRY)
                    .build(context)
            binding.root.findFragment<Fragment>().startActivityForResult(intent, HomeActivity.ADDRESS_REQUEST_CODE)
        }
    }

}

fun LayoutInputAddressBinding.fillAddressFields(fillAddressFields: Place?) {
    val binding = this
    val addressComponent = fillAddressFields?.let { addressComponents(it) }
    binding.apply {
        editAddress.value = addressComponent?.address.orEmpty()
        editAddressLineOne.value = addressComponent?.addressLine1.orEmpty()
        editAddressLineTwo.value = addressComponent?.addressLine2.orEmpty()
        editZipcode.value = addressComponent?.zipcode.orEmpty()
        editLatitude.value = addressComponent?.latitude.orEmpty()
        editLongitude.value = addressComponent?.longitude.orEmpty()
        editCountry.value = addressComponent?.country.orEmpty()
        editCity.value = addressComponent?.city.orEmpty()
        editState.value = addressComponent?.state.orEmpty()
    }
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