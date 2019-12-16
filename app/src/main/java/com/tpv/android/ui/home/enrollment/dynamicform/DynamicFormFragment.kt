package com.tpv.android.ui.home.enrollment.dynamicform


import DynamicFormReq
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.tpv.android.R
import com.tpv.android.databinding.FragmentDynamicFormBinding
import com.tpv.android.network.error.AlertErrorHandler
import com.tpv.android.network.resources.Resource
import com.tpv.android.network.resources.apierror.APIError
import com.tpv.android.network.resources.extensions.ifSuccess


class DynamicFormFragment : Fragment() {
    private lateinit var mBinding: FragmentDynamicFormBinding
    private lateinit var mViewModel: DynamicFormViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_dynamic_form, container, false)
        mBinding.lifecycleOwner = this
        mViewModel = ViewModelProviders.of(this).get(DynamicFormViewModel::class.java)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
    }

    fun initialize() {

        mBinding.errorHandler = AlertErrorHandler(mBinding.root)
        getFormApiCall()

    }

    private fun getFormApiCall() {
        val liveData = mViewModel.getDynamicForm(DynamicFormReq(clientid = "102",
                commodity = "Electric", programid = "716"))
        liveData.observe(this, Observer {
            it.ifSuccess {

            }

        })

        mBinding.resource = liveData as LiveData<Resource<Any, APIError>>
    }
}
