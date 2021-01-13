package com.tpv.android.utils

import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.InputFilter
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.textfield.TextInputEditText
import com.livinglifetechway.k4kotlin.core.androidx.color
import com.livinglifetechway.k4kotlin.core.hide
import com.livinglifetechway.k4kotlin.core.orFalse
import com.livinglifetechway.k4kotlin.core.orZero
import com.livinglifetechway.k4kotlin.core.show
import com.tpv.android.R
import com.tpv.android.helper.formatDate
import com.tpv.android.network.error.ErrorHandler
import com.tpv.android.network.resources.Resource
import com.tpv.android.network.resources.apierror.APIError
import com.tpv.android.utils.enums.ClientLeadStatus
import com.tpv.android.utils.enums.LeadStatus
import com.tpv.android.utils.textdrawable.TextDrawable
import java.text.SimpleDateFormat
import java.util.*


object BindingAdapter {

    /**
     * binding adapter for apply colorTint on image
     */
    @JvmStatic
    @BindingAdapter("dynamicTintColor")
    fun setDynamicTintColor(imageView: ImageView, color: Int) {
        imageView.setColorFilter(
                ContextCompat.getColor(imageView.context, color),
                android.graphics.PorterDuff.Mode.MULTIPLY
        )
    }


    /**
     * binding adapter for set image url in imageView if url is null or empty then show placeholder image in imageView
     */
    @JvmStatic
    @BindingAdapter(value = ["url", "placeholder"], requireAll = false)
    fun loadImage(imageView: ImageView, url: String?, placeHolder: Drawable?) {
        // make sure url is valid

        if (!url.isNullOrEmpty()) {
            setImage(imageView, url, placeHolder)
        } else {
            imageView.setImageDrawable(placeHolder)
        }
    }

    private fun setImage(
            imageView: ImageView,
            url: Any?,
            placeHolder: Drawable?
    ) {
        val glide = Glide.with(imageView.context)
                .load(url)

        if (placeHolder != null)
            glide.apply(RequestOptions().placeholder(placeHolder))

        glide.into(imageView)
    }

    /**
     * change text color and text as per @param status value
     */
    @JvmStatic
    @BindingAdapter("leadStatus")
    fun setLeadStatus(textView: TextView, status: String?) {
        val context = textView.context
        when (status) {
            LeadStatus.PENDING.value -> {
                textView.text = context?.getString(R.string.pending)
                textView.setTextColor(context.color(R.color.colorPendingText))
            }
            LeadStatus.VERIFIED.value -> {
                textView.text = context?.getString(R.string.verified)
                textView.setTextColor(context.color(R.color.colorVerifiedText))
            }

            LeadStatus.DECLINED.value -> {
                textView.text = context?.getString(R.string.declined)
                textView.setTextColor(context.color(R.color.colorDeclinedText))
            }

            LeadStatus.DISCONNECTED.value -> {
                textView.text = context?.getString(R.string.disconnected)
                textView.setTextColor(context.color(R.color.colorDisconnectedText))
            }

            LeadStatus.CANCELLED.value -> {
                textView.text = context?.getString(R.string.cancelled)
                textView.setTextColor(context.color(R.color.colorCancelledText))
            }
            LeadStatus.EXPIRED.value -> {
                textView.text = context?.getString(R.string.expired)
                textView.setTextColor(context.color(R.color.colorSecondaryDarkText))
            }
            ClientLeadStatus.PENDING.value -> {
                textView.text = context?.getString(R.string.pending)
                textView.setTextColor(context.color(R.color.colorPendingText))
            }
            ClientLeadStatus.VERIFIED.value -> {
                textView.text = context?.getString(R.string.verified)
                textView.setTextColor(context.color(R.color.colorVerifiedText))
            }

            ClientLeadStatus.DECLINED.value -> {
                textView.text = context?.getString(R.string.declined)
                textView.setTextColor(context.color(R.color.colorDeclinedText))
            }

            ClientLeadStatus.DISCONNECTED.value -> {
                textView.text = context?.getString(R.string.disconnected)
                textView.setTextColor(context.color(R.color.colorDisconnectedText))
            }

            ClientLeadStatus.CANCELLED.value -> {
                textView.text = context?.getString(R.string.cancelled)
                textView.setTextColor(context.color(R.color.colorCancelledText))
            }
            ClientLeadStatus.EXPIRED.value -> {
                textView.text = context?.getString(R.string.expired)
                textView.setTextColor(context.color(R.color.colorSecondaryDarkText))
            }
            ClientLeadStatus.SELFVERIFIED.value -> {
                textView.text = context?.getString(R.string.self_verify)
                textView.setTextColor(context.color(R.color.colorPinkText))
            }
        }
    }


    /**
     * shows the view if resource data is empty
     */
    @JvmStatic
    @BindingAdapter("showIfEmptyDataCheck", "showEmptyText")
    fun showIfEmptyDataCheck(container: View, resource: Resource<*, APIError>?, isShow: Boolean = true) {
        val data = resource?.data
        if (data is List<*> && data.size.orZero() == 0 && resource.state == Resource.State.SUCCESS) {
            if (isShow) {
                container.show()
            }
        } else {
            container.hide()
        }
    }

    /**
     * set date 'MM-dd-yyyy HH:mm:ss to dd/MM/yyyy'
     */
    @JvmStatic
    @BindingAdapter("setDate")
    fun setDate(textView: TextView, date: String?) {
        textView.text = SimpleDateFormat("MM-dd-yyyy HH:mm:ss", Locale.US).parse(date).formatDate("dd / MM / yyyy")

    }


    /**
     * set time 'MM-dd-yyyy HH:mm:ss to hh:mm:ss'
     */
    @JvmStatic
    @BindingAdapter("time")
    fun setTimeFormate(textView: TextView, time: String?) {
        textView.text = SimpleDateFormat("MM-dd-yyyy HH:mm:ss", Locale.US).parse(time).formatDate("hh:mm:ss")
    }


    /**
     * Api error handler binding adaptor
     */
    @JvmStatic
    @BindingAdapter(value = ["resource", "errorHandler", "showEmptyText"], requireAll = false)
    fun handleErrors(view: View, resource: Resource<*, APIError>?, errorHandler: ErrorHandler?, isShow: Boolean = true) {
        resource?.let {
            if (resource.state == Resource.State.ERROR) {
                if (errorHandler == null) {
                    if (view is TextView) {
                        view.text = resource.errorData?.message
                        if (isShow) {
                            view.show()
                        } else {
                            view.hide()
                        }
                    }
                } else {
                    view.hide()
                    errorHandler.onError(resource)
                }
            }
        }
    }


    /**
     * binding adapter for hide/show views.
     * @param shouldBeVisible is true then show the view and if false then hide the view.
     */
    @JvmStatic
    @BindingAdapter("visibleIf")
    fun setVisibleIf(view: View, shouldBeVisible: Boolean?) {
        if (shouldBeVisible.orFalse()) {
            view.show()
        } else {
            view.hide()
        }
    }

    /**
     * binding adapter for show name's letter in image while url is blank or null
     */
    @JvmStatic
    @BindingAdapter(value = ["url", "name"], requireAll = false)
    fun loadImageOrTextDrawable(image: ImageView, url: String?, name: String?) {

        val letterImage: String? = name?.split(" ")?.joinToString("") { it.take(1).toUpperCase() }
        val drawable = TextDrawable.builder()
                .beginConfig()
                .height(200)
                .width(200)
                .textColor(image.context?.color(R.color.colorProfileImageText).orZero())
                .fontSize(56)
                .useFont(Typeface.DEFAULT_BOLD)
                .bold()
                .endConfig()
                .buildRect(letterImage, image.context?.color(R.color.colorProfileImageBg).orZero())
//            image.setImageDrawable(drawable)
        setImage(image, url, drawable)
    }

    /**
     * binding adapter for highlight selected menu item and hide/show views.
     * @param isSelected give the information about view should highlight or not.
     */
    @JvmStatic
    @BindingAdapter("selectMenuItem")
    fun setMenuSelection(view: View, isSelected: Boolean) {
        if (isSelected) {
            if (view is FrameLayout) {
                view.setBackgroundColor(view.context.color(R.color.colorMenuLightHighLight))
            } else {
                view.show()
            }
        } else {
            if (view is FrameLayout) {
                view.background = null
            } else {
                view.hide()
            }

        }
    }

    @JvmStatic
    @BindingAdapter("selectButtonClick")
    fun setButtonClick(view: View, isClicked: Boolean) {
        if (isClicked) {
            if (view is TextView) {
                view.background = view.context?.getDrawable(R.drawable.button_background_state)
                view.setTextColor(view.context?.color(R.color.colorButtonText).orZero())
            }
        } else {
            if (view is TextView) {
                if (view.isEnabled) {
                    view.setTextColor(view.context?.color(R.color.colorCancelButtonBorder).orZero())
                } else {
                    view.setTextColor(view.context?.color(R.color.colorEditTextBorder).orZero())
                }
                view.background = view.context?.getDrawable(R.drawable.button_border_background_state)
            }
        }
    }


    @JvmStatic
    @BindingAdapter(value = ["currentPage", "pageNumber"], requireAll = true)
    fun handleHighlightOfIndicator(textView: TextView, currentPage: Int, pageNumber: Int) {
        if (currentPage == pageNumber) {
            textView.background = textView.context.getDrawable(R.drawable.bg_selected_page_indicator)
        } else {
            textView.background = textView.context.getDrawable(R.drawable.bg_unselected_page_indicator)
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["primaryValue", "secondaryText", "secondaryValue"], requireAll = true)

    fun setBracketText(editText: EditText, primaryValue: String?, secondaryText: String?, secondaryValue: String?) {
        if (primaryValue?.isNotEmpty().orFalse() && secondaryValue?.isNotEmpty().orFalse()) {
            val text = "$primaryValue ($secondaryText : $secondaryValue)"
            editText.setText(text)
        }

    }


    @JvmStatic
    @BindingAdapter("editTextMaxLength")
    fun editTextMaxLength(editText: EditText, length: Int) {
        if (length != 0) {
            val arrayOfInputFilters = arrayOfNulls<InputFilter>(1)
            arrayOfInputFilters[0] = InputFilter.LengthFilter(length)
            editText.filters = arrayOfInputFilters
        }
    }

    @JvmStatic
    @BindingAdapter("allCaps")
    fun setAllCaps(editText: EditText, isAllCaps: Boolean) {
        if (isAllCaps) {
            editText.filters = arrayOf<InputFilter>(InputFilter.AllCaps())
        }
    }

    @JvmStatic
    @BindingAdapter("county", "city")
    fun editTextCombineValues(editText: EditText, county: String?, city: String?) {
        var value: String? = ""

        if (city?.isNotEmpty().orFalse()) {
            if (value?.isNotEmpty().orFalse()) {
                value = "$value, $city"
            } else {
                value = city
            }
        }
        if (county?.isNotEmpty().orFalse()) {
            if (value?.isNotEmpty().orFalse()) {
                value = "$value, $county"
            } else {
                value = county
            }
        }

        editText.setText(value)
    }

    @JvmStatic
    @BindingAdapter("country", "state", "zipcode")
    fun combineCountryStateZipcodeValues(editText: EditText, country: String?, state: String?, zipcode: String?) {
        var value: String? = ""

        if (state?.isNotEmpty().orFalse()) {
            if (value?.isNotEmpty().orFalse()) {
                value = "$value, $state"
            } else {
                value = state
            }
        }
        if (zipcode?.isNotEmpty().orFalse()) {
            if (value?.isNotEmpty().orFalse()) {
                value = "$value, $zipcode"
            } else {
                value = zipcode
            }
        }

        if (country?.isNotEmpty().orFalse()) {
            if (value?.isNotEmpty().orFalse()) {
                value = "$value, $country"
            } else {
                value = country
            }
        }
        editText.setText(value)
    }


    @JvmStatic
    @BindingAdapter(value = ["firstName", "middleName", "lastName"], requireAll = true)
    fun setCombineFullName(textView: TextView, firstName: String?, middleName: String?, lastName: String?) {
        var value: String? = ""
        if (firstName?.isNotEmpty().orFalse()) {
            value = firstName
        }
        if (middleName?.isNotEmpty().orFalse()) {
            value = "$value  $middleName"
        }
        if (lastName?.isNotEmpty().orFalse()) {
            value = "$value  $lastName"
        }

        textView.text = value
    }

    @JvmStatic
    @BindingAdapter("city", "state")
    fun textViewCombineValues(textView: TextView, city: String?, state: String?) {
        var value: String = ""
        if (city?.isNotEmpty().orFalse()) {
            value = city.orEmpty()
        }
        if (state?.isNotEmpty().orFalse()) {
            if (value.isNotEmpty()) {
                value = "$value, $state"
            } else {
                value = state.orEmpty()
            }
        }
        textView.setText(value)
    }


    @JvmStatic
    @BindingAdapter(value = ["county", "unit", "addressLine1", "addressLine2", "city", "state", "zipcode", "country"], requireAll = true)
    fun addressCombineValues(textView: TextView,
                             county: String?,
                             unit: String?, addressLine1: String?,
                             addressLine2: String?, city: String?,
                             state: String?, zipcode: String?, country: String?) {
        var address: String = ""

//        if (unit?.isNotEmpty().orFalse()) {
//            address = unit.orEmpty()
//        }

        if (addressLine1?.isNotEmpty().orFalse()) {
            if (address.isNotEmpty()) {
                address = address + ", " + addressLine1.orEmpty()
            } else {
                address = addressLine1.orEmpty()
            }
        }

        if (addressLine2?.isNotEmpty().orFalse()) {
            if (address.isNotEmpty()) {
                address = address + ", " + addressLine2.orEmpty()
            } else {
                address = addressLine2.orEmpty()
            }
        }



        if (city?.isNotEmpty().orFalse()) {
            if (address.isNotEmpty()) {
                address = address + ", " + city.orEmpty()
            } else {
                address = city.orEmpty()
            }
        }

        if (county?.isNotEmpty().orFalse()) {
            if (address.isNotEmpty()) {
                address = address + ", " + county.orEmpty()
            } else {
                address = county.orEmpty()
            }
        }

        if (state?.isNotEmpty().orFalse()) {
            if (address.isNotEmpty()) {
                address = address + ", " + state.orEmpty()
            } else {
                address = state.orEmpty()
            }
        }

        if (zipcode?.isNotEmpty().orFalse()) {
            if (address.isNotEmpty()) {
                address = address + ", " + zipcode.orEmpty()
            } else {
                address = zipcode.orEmpty()
            }
        }

        if (country?.isNotEmpty().orFalse()) {
            if (address.isNotEmpty()) {
                address = address + ", " + country.orEmpty()
            } else {
                address = country.orEmpty()
            }
        }
        textView.text = address
    }

    @JvmStatic
    @BindingAdapter("reportData")
    fun reportTextData(textView: TextView, data: String?) {
        if (data.isNullOrEmpty()) {
            textView.setText(" " + " - ")
        } else {
            textView.setText(" " + data)
        }
    }

    @JvmStatic
    @BindingAdapter("applyTint")
    fun setTintColor(editText: TextInputEditText, imageView: Drawable) {

        imageView.setColorFilter(
                ContextCompat.getColor(editText.context, R.color.colorMenuDarkHighLight),
                android.graphics.PorterDuff.Mode.MULTIPLY
        )

        editText.setCompoundDrawablesWithIntrinsicBounds(null, null, imageView, null)
    }

}

