package com.tpv.android.ui.home.enrollment.statement


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
import com.tpv.android.databinding.FragmentStatementBinding
import com.tpv.android.model.ContractReq
import com.tpv.android.ui.home.enrollment.SetEnrollViewModel
import com.tpv.android.utils.navigateSafe
import com.tpv.android.utils.setupToolbar

/**
 * A simple [Fragment] subclass.
 */
class StatementFragment : Fragment() {
    private lateinit var mBinding: FragmentStatementBinding
    private lateinit var mSetEnrollViewModel: SetEnrollViewModel
    private lateinit var mViewModel: StatementViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_statement, container, false)
        mBinding.lifecycleOwner = this
        activity?.let { mSetEnrollViewModel = ViewModelProviders.of(it).get(SetEnrollViewModel::class.java) }
        mViewModel = ViewModelProviders.of(this).get(StatementViewModel::class.java)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar(mBinding.toolbar, getString(R.string.statement), showBackIcon = true)

        mViewModel.saveContract(contractReq = ContractReq(mSetEnrollViewModel.savedLeadDetail?.id.toString()))

        mBinding.item = mSetEnrollViewModel.serviceDetail

        mBinding.btnNext.onClick {
            Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_statementFragment_to_successFragment)
        }
    }


}
