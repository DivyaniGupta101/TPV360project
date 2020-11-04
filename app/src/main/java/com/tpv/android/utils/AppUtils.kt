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

fun Context.copyTextDialog(list: ArrayList<DynamicFormResp>, response: DynamicFormResp, updateView: (() -> Unit)
) {

    val binding = DataBindingUtil.inflate<DialogCopyTextBinding>(LayoutInflater.from(this),
            R.layout.dialog_copy_text, null, false)
    val dialog = AlertDialog.Builder(this)
            .setView(binding.root).show()

    LiveAdapter(list, BR.item)
            .map<DynamicFormResp, ItemCopyTextBinding>(R.layout.item_copy_text) {
                onBind { holder ->
                    when (response.type) {
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
                        DynamicField.ADDRESS.type, DynamicField.BOTHADDRESS.type -> {
                            if (DynamicField.ADDRESS.type == holder.binding.item?.type) {
                                addressCombineValues(
                                        holder.binding.textValue,
                                        holder.binding.item?.values?.get(AppConstant.UNIT)?.toString(),
                                        holder.binding.item?.values?.get(AppConstant.ADDRESS1)?.toString(),
                                        holder.binding.item?.values?.get(AppConstant.ADDRESS2)?.toString(),
                                        holder.binding.item?.values?.get(AppConstant.CITY)?.toString(),
                                        holder.binding.item?.values?.get(AppConstant.STATE)?.toString(),
                                        holder.binding.item?.values?.get(AppConstant.ZIPCODE)?.toString(),
                                        holder.binding.item?.values?.get(AppConstant.COUNTRY)?.toString()
                                )
                            } else {

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
                    response.values = it.binding.item?.values?.clone() as LinkedHashMap<String, Any>
                    updateView.invoke()
                    dialog.hide()
                }
            }.into(binding.rvCopyText)

    dialog?.btnCancel?.onClick {
        dialog.hide()
    }
    dialog?.imageClose?.onClick {
        dialog.hide()
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