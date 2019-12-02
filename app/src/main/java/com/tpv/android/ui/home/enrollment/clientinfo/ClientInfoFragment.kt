package com.tpv.android.ui.home.enrollment.clientinfo


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.livinglifetechway.k4kotlin.core.hide
import com.livinglifetechway.k4kotlin.core.onClick
import com.livinglifetechway.k4kotlin.core.show
import com.tpv.android.R
import com.tpv.android.databinding.FragmentClientInfoBinding
import com.tpv.android.databinding.ItemProgramsBinding
import com.tpv.android.ui.home.enrollment.SetEnrollViewModel
import com.tpv.android.utils.enums.Plan
import com.tpv.android.utils.navigateSafe
import com.tpv.android.utils.setupToolbar

class ClientInfoFragment : Fragment() {
    private lateinit var mBinding: FragmentClientInfoBinding
    private lateinit var mViewModel: SetEnrollViewModel
    private var mViewGroup: ViewGroup? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_client_info, container, false)
        mBinding.lifecycleOwner = this
        activity?.let { mViewModel = ViewModelProviders.of(it).get(SetEnrollViewModel::class.java) }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.textElectric.hide()
        mBinding.textGas.hide()

        mBinding.item = mViewModel.customerData

        setupToolbar(mBinding.toolbar, getString(R.string.client_info), showBackIcon = true)

        setProgramInformation()

        mBinding.btnNext.onClick {
            Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_clientInfoFragment_to_recordingFragment)
        }

        mBinding.imageEdit.onClick {
            Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_clientInfoFragment_to_personalDetailFormFragment)
        }

    }

    private fun setProgramInformation() {
        mViewModel.programList.forEach { programsResp ->

            when (programsResp.utilityType) {
                Plan.GASFUEL.value -> {
                    mBinding.textGas.show()
                    mViewGroup = mBinding.gasInforamtionContainer
                }
                Plan.ELECTRICFUEL.value -> {
                    mBinding.textElectric.show()
                    mViewGroup = mBinding.electricInforamtionContainer
                }
            }

            val binding = DataBindingUtil.inflate<ItemProgramsBinding>(layoutInflater, R.layout.item_programs, mViewGroup, true)
            binding.mainContainer.background = context?.getDrawable(R.drawable.bg_rectangle_border)
            binding.item = programsResp

        }
    }
}
