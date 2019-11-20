package com.tpv.android.ui.home.enrollment.success


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
import com.tpv.android.databinding.FragmentSuccessBinding
import com.tpv.android.ui.home.enrollment.SetEnrollViewModel
import com.tpv.android.utils.navigateSafe
import com.tpv.android.utils.setupToolbar

/**
 * A simple [Fragment] subclass.
 */
class SuccessFragment : Fragment() {

    lateinit var mBinding: FragmentSuccessBinding
    lateinit var mSetEnrollViewModel: SetEnrollViewModel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_success, container, false)
        mBinding.lifecycleOwner = this
        activity?.let { mSetEnrollViewModel = ViewModelProviders.of(it).get(SetEnrollViewModel::class.java) }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar(mBinding.toolbar, getString(R.string.success))
        mBinding.item = mSetEnrollViewModel.savedLeadDetail

        mBinding.btnVerify.onClick {

        }

        mBinding.textBackToDashBoard.onClick {
            Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_successFragment_to_dashBoardFragment)
        }
    }

}
