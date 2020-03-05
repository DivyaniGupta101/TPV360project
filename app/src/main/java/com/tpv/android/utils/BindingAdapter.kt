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
    fun setLeadStatus(textView: TextView, status: String) {
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
    @BindingAdapter(value = ["resource", "errorHandler"], requireAll = true)
    fun handleErrors(view: View, resource: Resource<*, APIError>?, errorHandler: ErrorHandler?) {
        resource?.let {
            if (resource.state == Resource.State.ERROR) {
                if (errorHandler == null) {
                    if (view is TextView) {
                        view.text = resource.errorData?.message
                        view.show()
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
        if (url.isNullOrBlank()) {

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
            image.setImageDrawable(drawable)
        } else {
            setImage(image, url, null)
        }
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
    @BindingAdapter(value = ["currentPage", "pageNumber"], requireAll = true)
    fun handleHighlightOfIndicator(textView: TextView, currentPage: Int, pageNumber: Int) {
        if (currentPage == pageNumber) {
            textView.background = textView.context.getDrawable(R.drawable.bg_selected_page_indicator)
        } else {
            textView.background = textView.context.getDrawable(R.drawable.bg_unselected_page_indicator)
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
    @BindingAdapter("city", "state", "zipcode")
    fun editTextCombineValues(editText: EditText, city: String, state: String, zipcode: String) {
        var value: String = ""
        if (city.isNotEmpty()) {
            value = city
        }
        if (state.isNotEmpty()) {
            if (value.isNotEmpty()) {
                value = "$value, $state"
            } else {
                value = state
            }
        }
        if (zipcode.isNotEmpty()) {
            if (value.isNotEmpty()) {
                value = "$value, $zipcode"
            } else {
                value = zipcode
            }
        }
        editText.setText(value)
    }


    @JvmStatic
    @BindingAdapter(value = ["firstName", "middleName", "lastName"], requireAll = true)
    fun setCombineFullName(textView: TextView, firstName: String?, lastName: String?, middleName: String?) {
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
    @BindingAdapter(value = ["unit", "addressLine1", "addressLine2", "city", "state", "zipcode", "country"], requireAll = true)
    fun addressCombineValues(textView: TextView,
                             unit: String?, addressLine1: String?,
                             addressLine2: String?, city: String?,
                             state: String?, zipcode: String?, country: String?) {
        var address: String = ""

        if (unit?.isNotEmpty().orFalse()) {
            address = unit.orEmpty()
        }

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

        textView.setText(address)
    }
}

