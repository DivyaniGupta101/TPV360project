package com.tpv.android.ui.salesagent.home.enrollment.dynamicform.spinner

import android.content.Context
import android.util.Log
import androidx.core.view.get
import androidx.lifecycle.Observer
import com.livinglifetechway.k4kotlin.core.*
import com.tpv.android.App
import com.tpv.android.R
import com.tpv.android.databinding.LayoutInputSpinnerBinding
import com.tpv.android.helper.Pref
import com.tpv.android.model.network.Data
import com.tpv.android.model.network.DynamicFormResp
import com.tpv.android.model.network.EnrollementType
import com.tpv.android.model.network.Option
import com.tpv.android.network.resources.extensions.ifSuccess
import com.tpv.android.ui.salesagent.home.enrollment.SetEnrollViewModel
import com.tpv.android.ui.salesagent.home.enrollment.dynamicform.DynamicFormFragment.Companion.image_upload
import com.tpv.android.ui.salesagent.home.enrollment.dynamicform.serviceandbillingaddress.selectedState
import com.tpv.android.utils.AppConstant

fun LayoutInputSpinnerBinding.setField(response: DynamicFormResp,viewModel: SetEnrollViewModel) {

    val binding = this
    binding.item = response
    var enrollementtype:String?=null
    var enrollementbollean:String?=null



    binding.item?.values = linkedMapOf(AppConstant.OPTIONS to response.meta?.options as Any)
    val listOfOption = response.values?.get(AppConstant.OPTIONS) as ArrayList<Option>
    val spinnerValueList: ArrayList<String> = arrayListOf(this.spinner.context.getString(R.string.select_default))
    spinnerValueList.addAll(listOfOption.map { it.option.orEmpty() })


    binding.spinner?.setItems(spinnerValueList as ArrayList<String>?)

    //Check if listOfOption contain any true value then set that item as a selected
    if (listOfOption.filter { it.selected == true }.isNotEmpty().orFalse()) {
        val selectedItem = listOfOption.find { it.selected == true
        }?.option

        binding.spinner?.setSelection(spinnerValueList.indexOf(selectedItem).orZero())
    }

    binding.spinner?.onItemSelected { parent, view, position, id ->
        enrollementtype=spinnerValueList[binding.spinner.selectedItemPosition]
        when(enrollementtype){
            "Paper Enrollment"->enrollementbollean="1"
            "Electronic Enrollment"->enrollementbollean="0"
        }
        if(enrollementtype.equals("Paper Enrollment") || enrollementtype.equals("Electronic Enrollment")){
             enrollementvalue(viewModel, enrollementbollean.toString(),Pref.user?.clientId.toString())
        }
        listOfOption.forEachIndexed { index, option ->
            option.selected = (option.option == binding.spinner.selectedItem)

        }

    }


}


private fun enrollementvalue(viewModel: SetEnrollViewModel,enrollementbollean:String,clientid:String){
    val livedata=viewModel.generateenrollementtype(enrollementtype= enrollementbollean,clientid = clientid)
    livedata.observeForever {
        it.ifSuccess {
                image_upload= it?.data?.isEnableImageUploadMandatory
                Log.e("image_upload",image_upload.toString())


        }

    }




}


fun LayoutInputSpinnerBinding.isValid(context: Context?): Boolean {
    val binding = this
    return if (binding.item?.validations?.required.orFalse()) {
        if (binding.item?.meta?.options?.filter { it.selected == true }?.isNotEmpty().orFalse()) {
            binding.textError.hide()
            true
        } else {
            val errorMessage = context?.getString(R.string.please_select) + " " + binding.item?.label?.toLowerCase()
            binding.textError.text = errorMessage
            binding.textError.show()
            false
        }
    } else {
        return true
    }

}