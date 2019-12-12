package com.tpv.android.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import androidx.annotation.IdRes
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.*
import com.livinglifetechway.k4kotlin.core.androidx.hideKeyboard
import com.livinglifetechway.k4kotlin.core.hide
import com.livinglifetechway.k4kotlin.core.invisible
import com.livinglifetechway.k4kotlin.core.onClick
import com.livinglifetechway.k4kotlin.core.show
import com.tpv.android.R
import com.tpv.android.databinding.DialogActionBinding
import com.tpv.android.databinding.DialogInfoBinding
import com.tpv.android.databinding.ToolbarBinding
import com.tpv.android.model.internal.DialogText
import com.tpv.android.ui.home.HomeActivity
import kotlinx.android.synthetic.main.toolbar.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.util.*

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
            if (backIconClickListener != null) {
                backIconClickListener.invoke()
            }
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
    }
}

fun Context.infoDialog(title: String? = getString(R.string.error),
                       subTitleText: String,
                       btnText: String? = getString(R.string.ok),
                       isCancelable: Boolean = false,
                       setOnDismissListener: (() -> Unit)? = null,
                       setOnBtnClickLisener: (() -> Unit)? = null) {
    val binding = DataBindingUtil.inflate<DialogInfoBinding>(LayoutInflater.from(this), R.layout.dialog_info, null, false)
    val dialog = AlertDialog.Builder(this)
            .setView(binding.root).show()

    binding.item = DialogText(title, subTitleText, btnText, "")
    dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog?.setCanceledOnTouchOutside(isCancelable)

    dialog?.setOnDismissListener {
        setOnDismissListener?.invoke()
    }

    binding?.btnYes?.onClick {
        setOnBtnClickLisener?.invoke()
        dialog.dismiss()
    }
}

fun Context.actionDialog(
        texts: DialogText,
        isCancelable: Boolean = false,
        setOnDismissListener: (() -> Unit)? = null,
        setOnPositiveBtnClickLisener: (() -> Unit)? = null,
        setOnNegativeBtnClickLisener: (() -> Unit)? = null

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
        setOnNegativeBtnClickLisener?.invoke()
        dialog.dismiss()
    }

    binding?.btnYes?.onClick {
        setOnPositiveBtnClickLisener?.invoke()
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
    val file = File(this.cacheDir, Calendar.getInstance().timeInMillis.toString() + ".jpg")
    file.createNewFile()

    //Convert bitmap to byte array
    val bitmap = imageBitmap
    val bos = ByteArrayOutputStream()
    bitmap?.compress(Bitmap.CompressFormat.JPEG, 0 /*ignored for PNG*/, bos)

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


