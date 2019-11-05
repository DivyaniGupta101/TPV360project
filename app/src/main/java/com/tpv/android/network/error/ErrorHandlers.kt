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

        binding.item = resource.errorData?.message

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        binding?.btnYes?.onClick {
            dialog.dismiss()
//            TODO  Remove function invoke
            func?.invoke()
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