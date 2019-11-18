package com.tpv.android.ui.home.enrollment.form.personaldetails


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.livinglifetechway.k4kotlin.core.onClick
import com.livinglifetechway.k4kotlin.core.setItems
import com.tpv.android.R
import com.tpv.android.databinding.FragmentPersonalDetailFormBinding
import com.tpv.android.ui.home.enrollment.SetEnrollViewModel
import com.tpv.android.utils.Plan
import com.tpv.android.utils.navigateSafe
import com.tpv.android.utils.setupToolbar

/**
 * A simple [Fragment] subclass.
 */
class PersonalDetailFormFragment : Fragment() {

    private lateinit var mBinding: FragmentPersonalDetailFormBinding
    private lateinit var mViewModel: SetEnrollViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_personal_detail_form, container, false)
        activity?.let { mViewModel = ViewModelProviders.of(it).get(SetEnrollViewModel::class.java) }
        mBinding.viewModel = mViewModel
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar(mBinding.toolbar, getString(R.string.personal_data), showBackIcon = true)

        mBinding.spinnerRelationShip.setItems(arrayListOf("Banana", "Apple", "Cherry", "Kiwi", "Mango"))

        mBinding.btnNext.onClick {
            when (mViewModel.planType) {
                Plan.DUALFUEL.value, Plan.GASFUEL.value -> {
                    Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_personalDetailFormFragment_to_gasDetailFormFragment)
                }
                Plan.ELECTRICFUEL.value -> {
                    Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_personalDetailFormFragment_to_electricDetailFormFragment)
                }
            }
        }
    }
}
