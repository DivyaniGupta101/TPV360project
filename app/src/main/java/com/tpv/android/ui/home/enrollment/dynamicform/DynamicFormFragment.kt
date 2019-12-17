package com.tpv.android.ui.home.enrollment.dynamicform


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.livinglifetechway.k4kotlin.core.androidx.toastNow
import com.livinglifetechway.k4kotlin.core.onClick
import com.tpv.android.R
import com.tpv.android.databinding.*
import com.tpv.android.model.network.DynamicFormReq
import com.tpv.android.model.network.DynamicFormResp
import com.tpv.android.network.error.AlertErrorHandler
import com.tpv.android.network.resources.Resource
import com.tpv.android.network.resources.apierror.APIError
import com.tpv.android.network.resources.extensions.ifSuccess
import com.tpv.android.ui.home.enrollment.SetEnrollViewModel
import com.tpv.android.ui.home.enrollment.dynamicform.fullname.isValid
import com.tpv.android.ui.home.enrollment.dynamicform.fullname.setField
import com.tpv.android.ui.home.enrollment.dynamicform.heading.setField
import com.tpv.android.ui.home.enrollment.dynamicform.label.setField
import com.tpv.android.ui.home.enrollment.dynamicform.phone.isValid
import com.tpv.android.ui.home.enrollment.dynamicform.phone.setField
import com.tpv.android.ui.home.enrollment.dynamicform.singlelineedittext.isValid
import com.tpv.android.ui.home.enrollment.dynamicform.singlelineedittext.setField
import com.tpv.android.utils.enums.DynamicField
import com.tpv.android.utils.setupToolbar


class DynamicFormFragment : Fragment() {

    private lateinit var mBinding: FragmentDynamicFormBinding
    private lateinit var mViewModel: SetEnrollViewModel
    private var bindingList: ArrayList<Any> = ArrayList()
    private var validList: ArrayList<Boolean> = ArrayList()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_dynamic_form, container, false)

        mBinding.lifecycleOwner = this
        mViewModel = ViewModelProviders.of(this).get(SetEnrollViewModel::class.java)

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
    }

    fun initialize() {

        mBinding.errorHandler = AlertErrorHandler(mBinding.root)

        setupToolbar(mBinding.toolbar, getString(R.string.customer_data), showBackIcon = true)
        getFormApiCall()

        mBinding.btnNext.onClick {

            if (bindingList.isNotEmpty()) {

                bindingList.forEach { view ->
                    checkValid(view)
                }

                if (!validList.contains(false)) {
                    toastNow("good to go")
                }
                validList.clear()
            }


        }

    }

    private fun checkValid(view: Any) {
        context?.let { ctx ->
            when (view) {
                is LayoutInputFullNameBinding -> {
                    validList.add(ctx.isValid(view))
                }
                is LayoutInputSingleLineEditTextBinding -> {
                    validList.add(ctx.isValid(view))
                }
                is LayoutInputPhoneNumberBinding -> {
                    validList.add(ctx.isValid(view))
                }
                else -> {

                }
            }
        }

    }


    private fun getFormApiCall() {
        val liveData = mViewModel.getDynamicForm(DynamicFormReq(clientid = "102",
                commodity = "Electric", programid = "716"))
        liveData.observe(this, Observer {
            it.ifSuccess {
                it?.forEach { resp ->
                    when (resp.id) {
                        DynamicField.FULLNAME.type -> {
                            setFieldsOfFullName(resp)
                        }
                        DynamicField.TEXTBOX.type -> {
                            setFieldsOfSinglLineEditText(resp)
                        }
                        DynamicField.EMAIL.type -> {
                            setFieldsOfSinglLineEditText(resp)
                        }
                        DynamicField.PHONENUMBER.type -> {
                            setFieldsOfPhoneNumber(resp)
                        }
                        DynamicField.HEADING.type -> {
                            setFieldsOfHeading(resp)
                        }
                        DynamicField.LABEL.type -> {
                            setFieldsOfLabel(resp)
                        }
                    }
                }

            }

        })

        mBinding.resource = liveData as LiveData<Resource<Any, APIError>>
    }

    private fun setFieldsOfSinglLineEditText(response: DynamicFormResp) {

        val binding = DataBindingUtil.inflate<LayoutInputSingleLineEditTextBinding>(layoutInflater,
                R.layout.layout_input_single_line_edit_text,
                mBinding.fieldContainer,
                true)

        setField(response, binding)
        bindingList.add(binding)
    }


    private fun setFieldsOfFullName(response: DynamicFormResp) {

        val binding = DataBindingUtil.inflate<LayoutInputFullNameBinding>(layoutInflater,
                R.layout.layout_input_full_name,
                mBinding.fieldContainer,
                true)

        setField(response, binding)
        bindingList.add(binding)
    }

    private fun setFieldsOfHeading(response: DynamicFormResp) {

        val binding = DataBindingUtil.inflate<LayoutInputHeadingBinding>(layoutInflater,
                R.layout.layout_input_heading,
                mBinding.fieldContainer,
                true)

        setField(response, binding)
        bindingList.add(binding)
    }

    private fun setFieldsOfLabel(response: DynamicFormResp) {

        val binding = DataBindingUtil.inflate<LayoutInputLabelBinding>(layoutInflater,
                R.layout.layout_input_label,
                mBinding.fieldContainer,
                true)

        setField(response, binding)
        bindingList.add(binding)
    }

    private fun setFieldsOfPhoneNumber(response: DynamicFormResp) {

        val binding = DataBindingUtil.inflate<LayoutInputPhoneNumberBinding>(layoutInflater,
                R.layout.layout_input_phone_number,
                mBinding.fieldContainer,
                true)

        setField(response, binding, mViewModel, mBinding)
        bindingList.add(binding)
    }

}
