package com.tpv.android.ui.home.enrollment.form.gasdetailform


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.livinglifetechway.k4kotlin.core.onClick
import com.tpv.android.R
import com.tpv.android.databinding.FragmentGasDetailFormBinding
import com.tpv.android.ui.home.enrollment.SetEnrollViewModel
import com.tpv.android.utils.Plan
import com.tpv.android.utils.navigateSafe
import com.tpv.android.utils.setupToolbar

/**
 * A simple [Fragment] subclass.
 */
class GasDetailFormFragment : Fragment() {
    private lateinit var mBinding: FragmentGasDetailFormBinding
    private lateinit var mViewModel: SetEnrollViewModel
    private var mCountryCodeList = arrayListOf("+1")


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_gas_detail_form, container, false)
        activity?.let { mViewModel = ViewModelProviders.of(it).get(SetEnrollViewModel::class.java) }
        mBinding.viewModel = mViewModel
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar(mBinding.toolbar, getString(R.string.customer_data), showBackIcon = true)

        mBinding.btnNext.onClick {
            when (mViewModel.planType) {
                Plan.GASFUEL.value -> {
                    Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_gasDetailFormFragment_to_clientInfoFragment)
                }
                Plan.DUALFUEL.value -> {
                    Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_gasDetailFormFragment_to_electricDetailFormFragment)
                }
            }
        }
    }


}
