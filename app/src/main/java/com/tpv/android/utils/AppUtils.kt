package com.tpv.android.utils

import android.Manifest.permission
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.annotation.IdRes
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.*
import com.google.gson.Gson
import com.livinglifetechway.k4kotlin.core.androidx.hideKeyboard
import com.livinglifetechway.k4kotlin.core.hide
import com.livinglifetechway.k4kotlin.core.invisible
import com.livinglifetechway.k4kotlin.core.onClick
import com.livinglifetechway.k4kotlin.core.show
import com.ravikoradiya.liveadapter.LiveAdapter
import com.tpv.android.BR
import com.tpv.android.R
import com.tpv.android.databinding.*
import com.tpv.android.model.internal.DialogText
import com.tpv.android.model.network.DynamicFormResp
import com.tpv.android.ui.client.ui.ClientHomeActivity
import com.tpv.android.ui.salesagent.home.HomeActivity
import com.tpv.android.utils.BindingAdapter.addressCombineValues
import com.tpv.android.utils.BindingAdapter.setCombineFullName
import com.tpv.android.utils.enums.DynamicField
import kotlinx.android.synthetic.main.dialog_copy_text.*
import kotlinx.android.synthetic.main.toolbar.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap

/**
 * handle toolbar text and image hide/show and clickListener
 * also handle function invoke for back and skip button
 */
fun Fragment.setupToolbar(
        toolbarContainer: ToolbarBinding,
        title: String = "",
        showMenuIcon: Boolean = false,
        showBackIcon: Boolean = false,
        showSubTitle: Boolean = false,
        subTitleText: String = "",
        subTitleClickListener: (() -> Unit)? = null,
        backIconClickListener: (() -> Unit)? = null
) {

    toolbarContainer.textToolbarTitle.text = title

    val backImage = toolbarContainer.imageToolbarBack
    val subTitle = toolbarContainer.textSkip
    val menuImage = toolbarContainer.imageToolbarMenu

    if (subTitleText.isNotEmpty()) {
        subTitle.setText(subTitleText)
    }

    if (showBackIcon) {
        backImage?.show()
        backImage?.onClick {
            hideKeyboard()
            backIconClickListener?.invoke()
            Navigation.findNavController(toolbarContainer.root).navigateUp()
        }
    } else {
        backImage?.hide()
    }

    if (showSubTitle) {
        subTitle?.show()
        menuImage?.hide()
        subTitle?.onClick {
            subTitleClickListener?.invoke()
        }

    } else {
        textSkip?.invisible()
    }
    when (activity) {
        is HomeActivity -> {
            if (showMenuIcon) {
                menuImage?.show()
                subTitle?.hide()
                (activity as HomeActivity).unLockSwipeModeMenu()
                menuImage?.onClick {
                    (activity as HomeActivity).openMenu()
                }
            } else {
                (activity as HomeActivity).lockSwipeModeMenu()
                menuImage?.invisible()
            }
        }
        is ClientHomeActivity -> {
            if (showMenuIcon) {
                menuImage?.show()
                subTitle?.hide()
                (activity as ClientHomeActivity).unLockSwipeModeMenu()
                menuImage?.onClick {
                    (activity as ClientHomeActivity).openMenu()
                }
            } else {
                (activity as ClientHomeActivity).lockSwipeModeMenu()
                menuImage?.invisible()
            }
        }
    }
}

fun getListOfLocationPermission(): Array<String> {
    val list: ArrayList<String> = arrayListOf(permission.ACCESS_FINE_LOCATION,
            permission.ACCESS_COARSE_LOCATION)

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
        list.add(permission.ACCESS_BACKGROUND_LOCATION)
    }
    return list.toTypedArray()
}


fun Context.infoDialog(title: String? = getString(R.string.error),
                       subTitleText: String,
                       btnText: String? = getString(R.string.ok),
                       isCancelable: Boolean = false,
                       setOnDismissListener: (() -> Unit)? = null,
                       setOnButtonClickListener: (() -> Unit)? = null,
                       showImageError: Boolean = true) {
    try {
        val binding = DataBindingUtil.inflate<DialogInfoBinding>(LayoutInflater.from(this), R.layout.dialog_info, null, false)
        val dialog = AlertDialog.Builder(this)
                .setView(binding.root).show()

        binding.item = DialogText(title, subTitleText, btnText, "")
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCanceledOnTouchOutside(isCancelable)

        if (showImageError) {
            binding.imageError.show()
        } else {
            binding.imageError.hide()
        }
        dialog?.setOnDismissListener {
            setOnDismissListener?.invoke()
        }

        binding?.btnYes?.onClick {
            setOnButtonClickListener?.invoke()
            dialog.dismiss()
        }
    } catch (e: java.lang.Exception) {
        Log.e(ContentValues.TAG, "Dialog:shows error : ", e)


    }

}

fun Context.copyTextDialog(
        isBilling: Boolean = false,

        list: ArrayList<DynamicFormResp>, response: DynamicFormResp, updateView: (() -> Unit)
) {
    Log.e("lisstin",list.toString())

    val binding = DataBindingUtil.inflate<DialogCopyTextBinding>(LayoutInflater.from(this),
            R.layout.dialog_copy_text, null, false)
    val dialog = AlertDialog.Builder(this)
            .setView(binding.root).show()

    LiveAdapter(list, BR.item)
            .map<DynamicFormResp, ItemCopyTextBinding>(R.layout.item_copy_text) {
                onBind { holder ->
                    when (holder.binding.item?.type) {
                        DynamicField.FULLNAME.type -> {
                            setCombineFullName(holder.binding.textValue,
                                    holder.binding.item?.values?.get(AppConstant.FIRSTNAME)?.toString(),
                                    holder.binding.item?.values?.get(AppConstant.MIDDLENAME)?.toString(),
                                    holder.binding.item?.values?.get(AppConstant.LASTNAME)?.toString())
                        }

                        DynamicField.TEXTBOX.type -> {
                            holder.binding.textValue.text = holder.binding.item?.values?.get(AppConstant.VALUE).toString()
                        }
                        DynamicField.PHONENUMBER.type -> {
                            holder.binding.textValue.text = holder.binding.item?.values?.get(AppConstant.VALUE).toString()
                        }
                        DynamicField.ADDRESS.type -> {
                            Log.e("address","address")
                            holder.binding.textValue.tag = getString(R.string.address)
                            addressCombineValues(
                                    holder.binding.textValue,
                                    holder.binding.item?.values?.get(AppConstant.UNIT)?.toString(),
                                    holder.binding.item?.values?.get(AppConstant.COUNTY)?.toString(),
                                    holder.binding.item?.values?.get(AppConstant.ADDRESS1)?.toString(),
                                    holder.binding.item?.values?.get(AppConstant.ADDRESS2)?.toString(),
                                    holder.binding.item?.values?.get(AppConstant.CITY)?.toString(),
                                    holder.binding.item?.values?.get(AppConstant.STATE)?.toString(),
                                    holder.binding.item?.values?.get(AppConstant.ZIPCODE)?.toString(),
                                    holder.binding.item?.values?.get(AppConstant.COUNTRY)?.toString()
                            )
                            holder.binding.layoutService.onClick {
                                val data = holder.binding.item?.values?.clone() as LinkedHashMap<String, Any>
                                Log.e("addressclone","addressclone")

                                if (response.type == DynamicField.BOTHADDRESS.type) {
                                    Log.e("bothaddress","bothaddress")

                                    if (isBilling) {
                                        response.values?.set(AppConstant.BILLINGUNIT, data[AppConstant.UNIT].toString())
                                        response.values?.set(AppConstant.BILLINGCOUNTY, data[AppConstant.COUNTY].toString())
                                        response.values?.set(AppConstant.BILLINGADDRESS1, data[AppConstant.ADDRESS1].toString())
                                        response.values?.set(AppConstant.BILLINGADDRESS2, data[AppConstant.ADDRESS2].toString())
                                        response.values?.set(AppConstant.BILLINGCITY, data[AppConstant.CITY].toString())
                                        response.values?.set(AppConstant.BILLINGSTATE, data[AppConstant.STATE].toString())
                                        response.values?.set(AppConstant.BILLINGZIPCODE, data[AppConstant.ZIPCODE].toString())
                                        response.values?.set(AppConstant.BILLINGCOUNTRY, data[AppConstant.COUNTRY].toString())
                                        response.values?.set(AppConstant.BILLINGLAT, data[AppConstant.LAT].toString())
                                        response.values?.set(AppConstant.BILLINGLNG, data[AppConstant.LNG].toString())

                                    } else {
                                        Log.e("serviceunit","serviceunit")

                                        response.values?.set(AppConstant.SERVICEUNIT, data[AppConstant.UNIT].toString())
                                        response.values?.set(AppConstant.SERVICECOUNTY, data[AppConstant.COUNTY].toString())
                                        response.values?.set(AppConstant.SERVICEADDRESS1, data[AppConstant.ADDRESS1].toString())
                                        response.values?.set(AppConstant.SERVICEADDRESS2, data[AppConstant.ADDRESS2].toString())
                                        response.values?.set(AppConstant.SERVICECITY, data[AppConstant.CITY].toString())
                                        response.values?.set(AppConstant.SERVICESTATE, data[AppConstant.STATE].toString())
                                        response.values?.set(AppConstant.SERVICEZIPCODE, data[AppConstant.ZIPCODE].toString())
                                        response.values?.set(AppConstant.SERVICECOUNTRY, data[AppConstant.COUNTRY].toString())
                                        response.values?.set(AppConstant.SERVICELAT, data[AppConstant.LAT].toString())
                                        response.values?.set(AppConstant.SERVICELNG, data[AppConstant.LNG].toString())

                                    }
                                } else {
                                    response.values = data
                                }
                                updateView.invoke()
                                dialog.dismiss()
                            }
                        }
                        DynamicField.BOTHADDRESS.type -> {
                            holder.binding.textLabel.text = getString(R.string.service_address) + " (${holder.binding.item?.label})"
                            holder.binding.textLabel.tag = getString(R.string.service_address)
                            holder.binding.layoutBilling.tag = getString(R.string.billing_address)
                            holder.binding.textBillingLabel.text = getString(R.string.billing_address) + " (${holder.binding.item?.label})"
                            holder.binding.layoutBilling.show()
                            addressCombineValues(
                                    holder.binding.textValue,
                                    holder.binding.item?.values?.get(AppConstant.SERVICEUNIT)?.toString(),
                                    holder.binding.item?.values?.get(AppConstant.SERVICECOUNTY)?.toString(),
                                    holder.binding.item?.values?.get(AppConstant.SERVICEADDRESS1)?.toString(),
                                    holder.binding.item?.values?.get(AppConstant.SERVICEADDRESS2)?.toString(),
                                    holder.binding.item?.values?.get(AppConstant.SERVICECITY)?.toString(),
                                    holder.binding.item?.values?.get(AppConstant.SERVICESTATE)?.toString(),
                                    holder.binding.item?.values?.get(AppConstant.SERVICEZIPCODE)?.toString(),
                                    holder.binding.item?.values?.get(AppConstant.SERVICECOUNTRY)?.toString()
                            )
                            addressCombineValues(
                                    holder.binding.textBillingValue,
                                    holder.binding.item?.values?.get(AppConstant.BILLINGUNIT)?.toString(),
                                    holder.binding.item?.values?.get(AppConstant.BILLINGCOUNTY)?.toString(),
                                    holder.binding.item?.values?.get(AppConstant.BILLINGADDRESS1)?.toString(),
                                    holder.binding.item?.values?.get(AppConstant.BILLINGADDRESS2)?.toString(),
                                    holder.binding.item?.values?.get(AppConstant.BILLINGCITY)?.toString(),
                                    holder.binding.item?.values?.get(AppConstant.BILLINGSTATE)?.toString(),
                                    holder.binding.item?.values?.get(AppConstant.BILLINGZIPCODE)?.toString(),
                                    holder.binding.item?.values?.get(AppConstant.BILLINGCOUNTRY)?.toString()
                            )

                            holder.binding.layoutBilling.onClick {
                                val data = holder.binding.item?.values?.clone() as LinkedHashMap<String, Any>
                                if (response.type == DynamicField.ADDRESS.type) {
                                    response.values?.set(AppConstant.UNIT, data[AppConstant.BILLINGUNIT].toString())
                                    response.values?.set(AppConstant.COUNTY, data[AppConstant.BILLINGCOUNTY].toString())
                                    response.values?.set(AppConstant.ADDRESS1, data[AppConstant.BILLINGADDRESS1].toString())
                                    response.values?.set(AppConstant.ADDRESS2, data[AppConstant.BILLINGADDRESS2].toString())
                                    response.values?.set(AppConstant.CITY, data[AppConstant.BILLINGCITY].toString())
                                    response.values?.set(AppConstant.STATE, data[AppConstant.BILLINGSTATE].toString())
                                    response.values?.set(AppConstant.ZIPCODE, data[AppConstant.BILLINGZIPCODE].toString())
                                    response.values?.set(AppConstant.COUNTRY, data[AppConstant.BILLINGCOUNTRY].toString())
                                    response.values?.set(AppConstant.LAT, data[AppConstant.BILLINGLAT].toString())
                                    response.values?.set(AppConstant.LNG, data[AppConstant.BILLINGLNG].toString())
                                } else {
                                    if (isBilling) {
                                        response.values?.set(AppConstant.BILLINGUNIT, data[AppConstant.BILLINGUNIT].toString())
                                        response.values?.set(AppConstant.BILLINGCOUNTY, data[AppConstant.BILLINGCOUNTY].toString())
                                        response.values?.set(AppConstant.BILLINGADDRESS1, data[AppConstant.BILLINGADDRESS1].toString())
                                        response.values?.set(AppConstant.BILLINGADDRESS2, data[AppConstant.BILLINGADDRESS2].toString())
                                        response.values?.set(AppConstant.BILLINGCITY, data[AppConstant.BILLINGCITY].toString())
                                        response.values?.set(AppConstant.BILLINGSTATE, data[AppConstant.BILLINGSTATE].toString())
                                        response.values?.set(AppConstant.BILLINGZIPCODE, data[AppConstant.BILLINGZIPCODE].toString())
                                        response.values?.set(AppConstant.BILLINGCOUNTRY, data[AppConstant.BILLINGCOUNTRY].toString())
                                        response.values?.set(AppConstant.BILLINGLAT, data[AppConstant.BILLINGLAT].toString())
                                        response.values?.set(AppConstant.BILLINGLNG, data[AppConstant.BILLINGLNG].toString())


                                    } else {
                                        response.values?.set(AppConstant.SERVICEUNIT, data[AppConstant.BILLINGUNIT].toString())
                                        response.values?.set(AppConstant.SERVICECOUNTY, data[AppConstant.BILLINGCOUNTY].toString())
                                        response.values?.set(AppConstant.SERVICEADDRESS1, data[AppConstant.BILLINGADDRESS1].toString())
                                        response.values?.set(AppConstant.SERVICEADDRESS2, data[AppConstant.BILLINGADDRESS2].toString())
                                        response.values?.set(AppConstant.SERVICECITY, data[AppConstant.BILLINGCITY].toString())
                                        response.values?.set(AppConstant.SERVICESTATE, data[AppConstant.BILLINGSTATE].toString())
                                        response.values?.set(AppConstant.SERVICEZIPCODE, data[AppConstant.BILLINGZIPCODE].toString())
                                        response.values?.set(AppConstant.SERVICECOUNTRY, data[AppConstant.BILLINGCOUNTRY].toString())
                                        response.values?.set(AppConstant.SERVICELAT, data[AppConstant.BILLINGLAT].toString())
                                        response.values?.set(AppConstant.SERVICELNG, data[AppConstant.BILLINGLNG].toString())

                                    }
                                }
                                updateView.invoke()
                                dialog.dismiss()
                            }

                            holder.binding.layoutService.onClick {
                                val data = holder.binding.item?.values?.clone() as LinkedHashMap<String, Any>
                                if (response.type == DynamicField.ADDRESS.type) {
                                    response.values?.set(AppConstant.UNIT, data[AppConstant.SERVICEUNIT].toString())
                                    response.values?.set(AppConstant.COUNTY, data[AppConstant.SERVICECOUNTY].toString())
                                    response.values?.set(AppConstant.ADDRESS1, data[AppConstant.SERVICEADDRESS1].toString())
                                    response.values?.set(AppConstant.ADDRESS2, data[AppConstant.SERVICEADDRESS2].toString())
                                    response.values?.set(AppConstant.CITY, data[AppConstant.SERVICECITY].toString())
                                    response.values?.set(AppConstant.STATE, data[AppConstant.SERVICESTATE].toString())
                                    response.values?.set(AppConstant.ZIPCODE, data[AppConstant.SERVICEZIPCODE].toString())
                                    response.values?.set(AppConstant.COUNTRY, data[AppConstant.SERVICECOUNTRY].toString())
                                    response.values?.set(AppConstant.LAT, data[AppConstant.SERVICELAT].toString())
                                    response.values?.set(AppConstant.LNG, data[AppConstant.SERVICELNG].toString())
                                } else {
                                    if (isBilling) {
                                        response.values?.set(AppConstant.BILLINGUNIT, data[AppConstant.SERVICEUNIT].toString())
                                        response.values?.set(AppConstant.BILLINGCOUNTY, data[AppConstant.SERVICECOUNTY].toString())
                                        response.values?.set(AppConstant.BILLINGADDRESS1, data[AppConstant.SERVICEADDRESS1].toString())
                                        response.values?.set(AppConstant.BILLINGADDRESS2, data[AppConstant.SERVICEADDRESS2].toString())
                                        response.values?.set(AppConstant.BILLINGCITY, data[AppConstant.SERVICECITY].toString())
                                        response.values?.set(AppConstant.BILLINGSTATE, data[AppConstant.SERVICESTATE].toString())
                                        response.values?.set(AppConstant.BILLINGZIPCODE, data[AppConstant.SERVICEZIPCODE].toString())
                                        response.values?.set(AppConstant.BILLINGCOUNTRY, data[AppConstant.SERVICECOUNTRY].toString())
                                        response.values?.set(AppConstant.BILLINGLAT, data[AppConstant.SERVICELAT].toString())
                                        response.values?.set(AppConstant.BILLINGLNG, data[AppConstant.SERVICELNG].toString())

                                    } else {
                                        response.values?.set(AppConstant.SERVICEUNIT, data[AppConstant.SERVICEUNIT].toString())
                                        response.values?.set(AppConstant.SERVICECOUNTY, data[AppConstant.SERVICECOUNTY].toString())
                                        response.values?.set(AppConstant.SERVICEADDRESS1, data[AppConstant.SERVICEADDRESS1].toString())
                                        response.values?.set(AppConstant.SERVICEADDRESS2, data[AppConstant.SERVICEADDRESS2].toString())
                                        response.values?.set(AppConstant.SERVICECITY, data[AppConstant.SERVICECITY].toString())
                                        response.values?.set(AppConstant.SERVICESTATE, data[AppConstant.SERVICESTATE].toString())
                                        response.values?.set(AppConstant.SERVICEZIPCODE, data[AppConstant.SERVICEZIPCODE].toString())
                                        response.values?.set(AppConstant.SERVICECOUNTRY, data[AppConstant.SERVICECOUNTRY].toString())
                                        response.values?.set(AppConstant.SERVICELAT, data[AppConstant.SERVICELAT].toString())
                                        response.values?.set(AppConstant.SERVICELNG, data[AppConstant.SERVICELNG].toString())
                                    }
                                }
                                updateView.invoke()
                                dialog.dismiss()
                            }
                        }
                        DynamicField.TEXTAREA.type -> {
                            holder.binding.textValue.text = holder.binding.item?.values?.get(AppConstant.VALUE)?.toString()
                        }
                        DynamicField.EMAIL.type -> {
                            holder.binding.textValue.text = holder.binding.item?.values?.get(AppConstant.VALUE)?.toString()

                        }
                    }
                }
                onClick {
                    if (response.type == it.binding.item?.type) {
                        response.values = it.binding.item?.values?.clone() as LinkedHashMap<String, Any>
                    }
                    Log.e("responsedialog",response.type)

                    updateView.invoke()
                    dialog.dismiss()
                }
            }.into(binding.rvCopyText)

    dialog?.btnCancel?.onClick {
        dialog.dismiss()
    }
    dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog?.setCanceledOnTouchOutside(false)
}


fun Context.actionDialog(
        texts: DialogText,
        isCancelable: Boolean = false,
        setOnDismissListener: (() -> Unit)? = null,
        setOnPositiveBanClickListener: (() -> Unit)? = null,
        setOnNegativeBanClickListener: (() -> Unit)? = null

) {
    val binding = DataBindingUtil.inflate<DialogActionBinding>(LayoutInflater.from(this), R.layout.dialog_action, null, false)
    val dialog = AlertDialog.Builder(this)
            .setView(binding.root).show()

    binding.item = texts
    dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog?.setCanceledOnTouchOutside(isCancelable)

    dialog?.setOnDismissListener {
        setOnDismissListener?.invoke()
    }

    binding?.btnCancel?.onClick {
        setOnNegativeBanClickListener?.invoke()
        dialog.dismiss()
    }

    binding?.btnYes?.onClick {
        setOnPositiveBanClickListener?.invoke()
        dialog.dismiss()
    }

}

/**
 * set slideMenuItem Selection
 */
fun Fragment.setItemSelection(item: String) {
    when (activity) {
        is HomeActivity -> {
            (activity as HomeActivity).menuItemSelection(item)
        }
        is ClientHomeActivity -> {
            (activity as ClientHomeActivity).menuItemSelection(item)

        }
    }
}

/**
 * update userProfile data in slideMenu
 */
fun Fragment.updateProfileInMenu() {
    when (activity) {
        is HomeActivity -> {
            (activity as HomeActivity).setProfileData()
        }
        is ClientHomeActivity -> {
            (activity as ClientHomeActivity).setProfileData()
        }
    }
}

/**
 * check requested action in current destination of navigation controller
 */
fun NavController.navigateSafe(
        @IdRes resId: Int,
        args: Bundle? = null,
        navOptions: NavOptions? = null,
        navExtras: Navigator.Extras? = null
) {
    try {
        navigate(resId, args, navOptions, navExtras)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * check requested action in current destination of navigation controller
 */
fun NavController.navigateSafe(
        navDirections: NavDirections
) {
    try {
        navigate(navDirections.actionId, navDirections.arguments, null, null)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}


/**
 * Convert file into RequestBody
 */
fun String?.toRequestBody() = RequestBody.create(MultipartBody.FORM, this ?: "")

/**
 * Convert file into MultipartBody
 */
fun File?.toMultipartBody(name: String, type: String): MultipartBody.Part? {
    this ?: return null
    return MultipartBody.Part.createFormData(
            name,
            this.name,
            RequestBody.create(MediaType.parse(type), this)
    )
}


/**
 * Convert bitmap into file
 */
fun Context.bitmapToFile(imageBitmap: Bitmap?): File {
    val file = File(this.cacheDir, Calendar.getInstance().timeInMillis.toString() + ".png")
    file.createNewFile()

    //Convert bitmap to byte array
    val bitmap = imageBitmap
    val bos = ByteArrayOutputStream()
    bitmap?.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos)

    //write the bytes in file
    var fos: FileOutputStream? = null
    try {
        fos = FileOutputStream(file)
        fos.write(bos.toByteArray())
        fos.flush()
        fos.close()
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    }
    return file
}


fun <V : Any> LinkedHashMap<Int, List<V>>.copy(t: Type): LinkedHashMap<Int, List<V>> {
    val copy = LinkedHashMap<Int, List<V>>()
    for (entry in this.entries) {
        copy[entry.key] = ArrayList<V>()
        for (value in entry.value) {
            val json = Gson().toJson(value)
            (copy[entry.key] as ArrayList).add(Gson().fromJson(json, t))
        }
    }
    return copy
}