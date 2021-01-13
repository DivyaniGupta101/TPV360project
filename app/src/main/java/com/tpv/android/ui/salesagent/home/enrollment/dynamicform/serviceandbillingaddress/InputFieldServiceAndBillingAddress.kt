package com.tpv.android.ui.salesagent.home.enrollment.dynamicform.serviceandbillingaddress


//import com.tpv.android.model.network.BillingAndServiceAddress
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.livinglifetechway.k4kotlin.core.androidx.color
import com.livinglifetechway.k4kotlin.core.onClick
import com.livinglifetechway.k4kotlin.core.orFalse
import com.livinglifetechway.k4kotlin.core.orZero
import com.livinglifetechway.k4kotlin.core.value
import com.tpv.android.R
import com.tpv.android.databinding.LayoutInputServiceAndBillingAddressBinding
import com.tpv.android.helper.addressComponents
import com.tpv.android.model.internal.AddressComponent
import com.tpv.android.model.network.DynamicFormResp
import com.tpv.android.ui.salesagent.home.HomeActivity
import com.tpv.android.ui.salesagent.home.enrollment.SetEnrollViewModel
import com.tpv.android.utils.AppConstant
import com.tpv.android.utils.copyTextDialog
import com.tpv.android.utils.enums.EnrollType
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


fun LayoutInputServiceAndBillingAddressBinding.setField(response: DynamicFormResp, listOfCopyTextForAddress: ArrayList<DynamicFormResp>) {

    val binding = this
    val context = binding.editBillingAddressLineOne.context

    binding.item = response

    // AutoPlace picker
    if (!Places.isInitialized()) {
        context?.let { Places.initialize(it, AppConstant.ADDRESSPICKER_KEY, Locale.US) }
    }

    binding.editBillingAddressLineOne.onClick {
        context.openPlacePicker(binding, false)
    }

    binding.editBillingZipcode.onClick {
        context.openPlacePicker(binding, false)
    }

    binding.editBillingCountry.onClick {
        context.openPlacePicker(binding, false)
    }

    binding.editServiceAddressLineOne.onClick {
        context.openPlacePicker(binding, true)
    }

    binding.editServiceZipcode.onClick {
        context.openPlacePicker(binding, true)
    }

    binding.editServiceCountry.onClick {
        context.openPlacePicker(binding, true)
    }

    binding.radioYes.onClick {
        handleBillingAddressField(binding, true)
    }


    binding.radioNo.onClick {
        handleBillingAddressField(binding, false)
    }

    binding.textServiceCopyText.onClick {
        context.copyTextDialog(
                list = listOfCopyTextForAddress,
                response = response,
                updateView =
                {
                    binding.invalidateAll()

                }
        )
    }

    binding.textBillingCopyText.onClick {
        context.copyTextDialog(
                isBilling = true,
                list = listOfCopyTextForAddress,
                response = response,
                updateView =
                {
                    binding.invalidateAll()

                }
        )
    }
    binding.editServiceUnit.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (binding.radioYes.isChecked) {
                binding.item?.values?.set(AppConstant.SERVICEUNIT, binding.editServiceUnit.value)
                handleBillingAddressField(binding, true)
            }
        }
    })
    binding.editServiceAddressLineTwo.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (binding.radioYes.isChecked) {
                binding.item?.values?.set(AppConstant.SERVICEADDRESS2, binding.editServiceAddressLineTwo.value)
                handleBillingAddressField(binding, true)
            }
        }
    })
}

fun LayoutInputServiceAndBillingAddressBinding.isValid(context: Context?): Boolean {
    val binding = this
    return if (binding.item?.isAddressSame.orFalse()) {

        binding.textInputBillingAddressLineOne.error = null
        binding.editBillingAddressLineOne.error = null

        binding.textInputServiceAddressLineOne.error = null
        binding.editServiceAddressLineOne.error = null

        Validator(TextInputValidationErrorHandler()) {
            addValidate(
                    binding.editServiceAddressLineOne,
                    EmptyValidator(),
                    context?.getString(R.string.enter_service_address)
            )

        }.validate()
    } else {
        Validator(TextInputValidationErrorHandler()) {
            addValidate(
                    binding.editServiceAddressLineOne,
                    EmptyValidator(),
                    context?.getString(R.string.enter_service_address)
            )
            addValidate(
                    binding.editBillingAddressLineOne,
                    EmptyValidator(),
                    context?.getString(R.string.enter_billing_address)
            )
        }.validate()
    }
}

/**
 * Get values from addressPicker and set in model class
 */
fun LayoutInputServiceAndBillingAddressBinding.fillAddressFields(fillAddressFields: Place?, isServiceAddress: Boolean, mViewModel: SetEnrollViewModel) {

    val binding = this
    val addressComponent = fillAddressFields?.let { addressComponents(it) }

    if (binding.item?.meta?.isPrimary == true) {
        if (mViewModel.selectionType == EnrollType.STATE.value) {
            if (isServiceAddress) {
                if (mViewModel.selectedState?.state == addressComponent?.stateSortName && addressComponent?.zipcode?.isNotBlank().orFalse()) {
                    bindServiceAddressField(binding, addressComponent)
                } else {
                    binding.editBillingAddressLineOne.context.infoDialog(
                            subTitleText = binding.editBillingCountry.context.getString(R.string.state_not_match)
                    )
                }
            } else {
                if (!isServiceAddress) {
                    bindBillingAddressField(binding, addressComponent)
                }
            }

        } else {
            if (isServiceAddress) {
                if (mViewModel.zipcode == addressComponent?.zipcode) {
                    bindServiceAddressField(binding, addressComponent)
                } else {
                    binding.editBillingAddressLineOne.context.infoDialog(
                            subTitleText = binding.editBillingCountry.context.getString(R.string.zipcode_not_match)
                    )
                }
            } else {
                if (!isServiceAddress) {
                    bindBillingAddressField(binding, addressComponent)
                }
            }
        }

    } else {
        if (isServiceAddress) {
            bindServiceAddressField(binding, addressComponent)
        } else {
            bindBillingAddressField(binding, addressComponent)
        }
    }
}

private fun bindBillingAddressField(binding: LayoutInputServiceAndBillingAddressBinding, addressComponent: AddressComponent?) {
    binding.item?.values?.set(AppConstant.BILLINGADDRESS1, addressComponent?.addressLine1.orEmpty())
    binding.item?.values?.set(AppConstant.BILLINGCOUNTY, addressComponent?.county.orEmpty())
    binding.item?.values?.set(AppConstant.BILLINGADDRESS2, addressComponent?.addressLine2.orEmpty())
    binding.item?.values?.set(AppConstant.BILLINGZIPCODE, addressComponent?.zipcode.orEmpty())
    binding.item?.values?.set(AppConstant.BILLINGLAT, addressComponent?.latitude.orEmpty())
    binding.item?.values?.set(AppConstant.BILLINGLNG, addressComponent?.longitude.orEmpty())
    binding.item?.values?.set(AppConstant.BILLINGCOUNTRY, addressComponent?.country.orEmpty())
    binding.item?.values?.set(AppConstant.BILLINGCITY, addressComponent?.city.orEmpty())
    binding.item?.values?.set(AppConstant.BILLINGSTATE, addressComponent?.state.orEmpty())
    binding.invalidateAll()
}

private fun bindServiceAddressField(binding: LayoutInputServiceAndBillingAddressBinding, addressComponent: AddressComponent?) {
    binding.item?.values?.set(AppConstant.SERVICEADDRESS1, addressComponent?.addressLine1.orEmpty())
    binding.item?.values?.set(AppConstant.SERVICECOUNTY, addressComponent?.county.orEmpty())
    binding.item?.values?.set(AppConstant.SERVICEADDRESS2, addressComponent?.addressLine2.orEmpty())
    binding.item?.values?.set(AppConstant.SERVICEZIPCODE, addressComponent?.zipcode.orEmpty())
    binding.item?.values?.set(AppConstant.SERVICELAT, addressComponent?.latitude.orEmpty())
    binding.item?.values?.set(AppConstant.SERVICELNG, addressComponent?.longitude.orEmpty())
    binding.item?.values?.set(AppConstant.SERVICECOUNTRY, addressComponent?.country.orEmpty())
    binding.item?.values?.set(AppConstant.SERVICECITY, addressComponent?.city.orEmpty())
    binding.item?.values?.set(AppConstant.SERVICESTATE, addressComponent?.state.orEmpty())
//    handleBillingAddressField(binding, binding.item?.isAddressSame.orFalse())
    binding.invalidateAll()
}

/**
 * If billing address and service address are same then save all values of service address fields in billing address fields
 * Else set blank values in all billing address fields
 */
private fun handleBillingAddressField(binding: LayoutInputServiceAndBillingAddressBinding, isSame: Boolean) {

    val context = binding.editBillingAddressLineOne.context
    binding.item?.isAddressSame = isSame

    if (isSame) {
        binding.item?.values?.set(AppConstant.BILLINGUNIT, binding.item?.values?.getValue(AppConstant.SERVICEUNIT).toString())
        binding.item?.values?.set(AppConstant.BILLINGCOUNTY, binding.item?.values?.getValue(AppConstant.SERVICECOUNTY).toString())
        binding.item?.values?.set(AppConstant.BILLINGADDRESS1, binding.item?.values?.getValue(AppConstant.SERVICEADDRESS1).toString())
        binding.item?.values?.set(AppConstant.BILLINGADDRESS2, binding.item?.values?.getValue(AppConstant.SERVICEADDRESS2).toString())
        binding.item?.values?.set(AppConstant.BILLINGZIPCODE, binding.item?.values?.getValue(AppConstant.SERVICEZIPCODE).toString())
        binding.item?.values?.set(AppConstant.BILLINGLAT, binding.item?.values?.getValue(AppConstant.SERVICELAT).toString())
        binding.item?.values?.set(AppConstant.BILLINGLNG, binding.item?.values?.getValue(AppConstant.SERVICELNG).toString())
        binding.item?.values?.set(AppConstant.BILLINGCOUNTRY, binding.item?.values?.getValue(AppConstant.SERVICECOUNTRY).toString())
        binding.item?.values?.set(AppConstant.BILLINGCITY, binding.item?.values?.getValue(AppConstant.SERVICECITY).toString())
        binding.item?.values?.set(AppConstant.BILLINGSTATE, binding.item?.values?.getValue(AppConstant.SERVICESTATE).toString())
        binding.editBillingUnit.setTextColor(context?.color(R.color.colorSecondaryText).orZero())
        binding.editBillingAddressLineTwo.setTextColor(context?.color(R.color.colorSecondaryText).orZero())
    } else {
        binding.item?.values?.set(AppConstant.BILLINGUNIT, "")
        binding.item?.values?.set(AppConstant.BILLINGCOUNTY, "")
        binding.item?.values?.set(AppConstant.BILLINGADDRESS1, "")
        binding.item?.values?.set(AppConstant.BILLINGADDRESS2, "")
        binding.item?.values?.set(AppConstant.BILLINGZIPCODE, "")
        binding.item?.values?.set(AppConstant.BILLINGLAT, "")
        binding.item?.values?.set(AppConstant.BILLINGLNG, "")
        binding.item?.values?.set(AppConstant.BILLINGCOUNTRY, "")
        binding.item?.values?.set(AppConstant.BILLINGCITY, "")
        binding.item?.values?.set(AppConstant.BILLINGSTATE, "")
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

