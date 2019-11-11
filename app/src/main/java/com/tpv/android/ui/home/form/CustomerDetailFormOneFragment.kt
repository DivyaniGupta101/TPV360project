package com.tpv.android.ui.home.form


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.livinglifetechway.k4kotlin.core.setItems
import com.tpv.android.R
import com.tpv.android.databinding.FragmentCustomerDetailFormOneBinding
import com.tpv.android.utils.setupToolbar

/**
 * A simple [Fragment] subclass.
 */
class CustomerDetailFormOneFragment : Fragment() {
    private lateinit var mBinding: FragmentCustomerDetailFormOneBinding
    private var mList = arrayListOf("Banana", "Apple", "Cherry", "Kiwi", "Mango")


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_customer_detail_form_one, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.spinnerRelationShip.setItems(mList)
        setupToolbar(mBinding.toolbar, getString(R.string.customer_data), showBackIcon = true)
    }


}
