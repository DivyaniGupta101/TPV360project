package com.tpv.android.ui.home.supportsuccess

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.livinglifetechway.k4kotlin.core.onClick
import com.tpv.android.R
import com.tpv.android.databinding.FragmentSupportSuccessBinding
import com.tpv.android.utils.setupToolbar

/**
 * A simple [Fragment] subclass.
 */
class SupportSuccessFragment : Fragment() {
    lateinit var mBinding: FragmentSupportSuccessBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_support_success, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initalize()
    }

    private fun initalize() {
        setupToolbar(mBinding.toolbar, getString(R.string.success), showBackIcon = true)
        mBinding.textBackToDashBoard.onClick {
            Navigation.findNavController(mBinding.root).navigate(R.id.action_supportSuccessFragment_to_dashBoardFragment)
        }
    }

}
