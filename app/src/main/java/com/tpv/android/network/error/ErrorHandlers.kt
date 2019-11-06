package com.tpv.android.network.error

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.google.android.material.snackbar.Snackbar
import com.livinglifetechway.k4kotlin.core.isNetworkAvailable
import com.livinglifetechway.k4kotlin.core.onClick
import com.tpv.android.R
import com.tpv.android.databinding.DialogErrorBinding
import com.tpv.android.network.resources.APIError
import com.tpv.android.network.resources.Resource


interface ErrorHandler {
    fun onError(resource: Resource<*, APIError>)
}

//interface PaginatedErrorHandler {
//    fun onInitialError(paginatedResource: PaginatedResource<*>)
//    fun onPageLoadingError(paginatedResource: PaginatedResource<*>)
//}


//class SnackbarPaginatedErrorHandler(private val parentView: View) : PaginatedErrorHandler {
//    override fun onInitialError(paginatedResource: PaginatedResource<*>) {
//        showError(paginatedResource)
//    }
//
//    override fun onPageLoadingError(paginatedResource: PaginatedResource<*>) {
//        showError(paginatedResource)
//    }

//    private fun showError(resource: PaginatedResource<*>) {
//        val snackbar = Snackbar.make(parentView, resource.message ?: "Unknown Error", Snackbar.LENGTH_INDEFINITE)
//        snackbar.setAction("OK") {
//            snackbar.dismiss()
//        }
//        snackbar.show()
//    }
//}

class SnackbarErrorHandler(private val parentView: View) : ErrorHandler {
    override fun onError(resource: Resource<*, APIError>) {
        val snackbar =
                Snackbar.make(
                        parentView,
                        resource.errorData?.message ?: "Unknown Error",
                        Snackbar.LENGTH_INDEFINITE
                )
        val textView =
                snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.maxLines = 5
        snackbar.setAction("OK") {
            snackbar.dismiss()
        }
        snackbar.show()
    }
}

class AlertErrorHandler(
        private val view: View,
        private val isCancelable: Boolean = false,
        private val func: (() -> Unit)? = null
) : ErrorHandler {
    override fun onError(resource: Resource<*, APIError>) {

        val binding = DataBindingUtil.inflate<DialogErrorBinding>(LayoutInflater.from(view.context), R.layout.dialog_error, null, false)
        val dialog = AlertDialog.Builder(view.context)
                .setView(binding.root).show()


        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        binding?.btnYes?.onClick {
            if (func == null) {
                dialog.dismiss()
            } else {
                dialog.dismiss()
                func.invoke()
            }
        }

        //handle error which is come from api or no internet connection or poor connection

        val errorData = resource.errorData
        if (errorData is APIError) {
            binding.item = errorData.message ?: view.context?.getString(R.string.unknown_error)
        } else {
            if (!view.context.isNetworkAvailable()) {
                binding.item = view.context.getString(R.string.no_internet_connection_message)
            } else {
                binding.item = view.context.getString(R.string.something_went_wrong)
            }
        }
    }
}

class ToastErrorHandler(private val view: View) : ErrorHandler {
    override fun onError(resource: Resource<*, APIError>) {
        Toast.makeText(
                view.context,
                resource.errorData?.message ?: "Unknown Error",
                Toast.LENGTH_SHORT
        ).show()
    }
}