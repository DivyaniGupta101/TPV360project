package com.tpv.android.ui.salesagent.home.enrollment.dynamicform.address

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import com.tpv.android.model.internal.AddressComponent
import com.tpv.android.model.network.DynamicFormResp
import com.tpv.android.ui.salesagent.home.HomeActivity
import com.tpv.android.ui.salesagent.home.enrollment.SetEnrollViewModel
import com.tpv.android.utils.AppConstant
import com.tpv.android.utils.BindingAdapter.setAllCaps
import com.tpv.android.utils.copyTextDialog
import com.tpv.android.utils.infoDialog
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


fun LayoutInputAddressBinding.setField(response: DynamicFormResp, listOfCopyTextForAddress: ArrayList<DynamicFormResp>) {
    val binding = this
    val context = binding.editUnit.context

    binding.item = response
    binding.item?.meta?.isAllCaps?.orFalse()?.let { setAllCaps(binding.editAddressLineOne, it) }
    binding.item?.meta?.isAllCaps?.orFalse()?.let { setAllCaps(binding.editAddressLineTwo, it) }
    binding.item?.meta?.isAllCaps?.orFalse()?.let { setAllCaps(binding.editCountry, it) }
    binding.item?.meta?.isAllCaps?.orFalse()?.let { setAllCaps(binding.editZipcode, it) }
    binding.item?.meta?.isAllCaps?.orFalse()?.let { setAllCaps(binding.editUnit, it) }

    binding.textCopyFrom.onClick {
        context.copyTextDialog(
                list = listOfCopyTextForAddress,
                response = response,
                updateView =
                {
                    binding.invalidateAll()

                }
        )
    }

    // AutoPlace picker
    if (!Places.isInitialized()) {
        context?.let { Places.initialize(it, AppConstant.ADDRESSPICKER_KEY, Locale.US) }
    }

    binding.editAddressLineOne.onClick {
        context.openPlacePicker(binding)
    }
    binding.editCountry.onClick {
        context.openPlacePicker(binding)
    }
    binding.editZipcode.onClick {
        context.openPlacePicker(binding)
    }

    binding.editCountry.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (binding.item?.meta?.isAllCaps.orFalse()) {
                updateCapitalizeValue(AppConstant.CITY)
                updateCapitalizeValue(AppConstant.COUNTY)
            }
        }

        override fun afterTextChanged(s: Editable?) {
        }

        private fun updateCapitalizeValue(key: String) {
            binding.item?.values?.set(key, (binding.item?.values?.get(key) as String).toUpperCase(Locale.ROOT))
        }

    })
    binding.editZipcode.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (binding.item?.meta?.isAllCaps.orFalse()) {
                updateCapitalizeValue(AppConstant.ZIPCODE)
                updateCapitalizeValue(AppConstant.COUNTRY)
                updateCapitalizeValue(AppConstant.STATE)
            }
        }

        override fun afterTextChanged(s: Editable?) {
        }

        private fun updateCapitalizeValue(key: String) {
            binding.item?.values?.set(key, (binding.item?.values?.get(key) as String).toUpperCase(Locale.ROOT))
        }

    })

}

/**
 * Get value from addressPicker response and set in model class
 */
fun LayoutInputAddressBinding.fillAddressFields(fillAddressFields: Place?, mViewModel: SetEnrollViewModel) {
    val binding = this
    val addressComponent = fillAddressFields?.let { addressComponents(it) }


    if (binding.item?.meta?.isPrimary == true) {
        if (mViewModel.zipcode == addressComponent?.zipcode.orEmpty()) {
            bindAddressField(binding, addressComponent)
        } else {
            binding.editCountry.context.infoDialog(
                    subTitleText = binding.editCountry.context.getString(R.string.zipcode_not_match))
        }
    } else {
        bindAddressField(binding, addressComponent)
    }

}

private fun bindAddressField(binding: LayoutInputAddressBinding, addressComponent: AddressComponent?) {
    binding.item?.values?.set(AppConstant.ADDRESS1, addressComponent?.addressLine1.orEmpty())
    binding.item?.values?.set(AppConstant.COUNTY, addressComponent?.county.orEmpty())
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
                    binding.editAddressLineOne,
                    EmptyValidator(),
                    context?.getString(R.string.enter_address)
            )
        }.validate()
    } else {
        return true
    }
}

/**
 * Open address picker
 */
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