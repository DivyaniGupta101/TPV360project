package com.tpv.android.ui.home.enrollment.clientinfo


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.tpv.android.R
import com.tpv.android.databinding.FragmentClientInfoBinding
import com.tpv.android.databinding.ItemProgramsBinding
import com.tpv.android.utils.setupToolbar

/**
 * A simple [Fragment] subclass.
 */
class ClientInfoFragment : Fragment() {
    private lateinit var mBinding: FragmentClientInfoBinding
    private var mList = arrayListOf("1", "1")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_client_info, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar(mBinding.toolbar,getString(R.string.client_info), showBackIcon = true)

        mList.forEach {
            var binding = DataBindingUtil.inflate<ItemProgramsBinding>(layoutInflater, R.layout.item_programs, mBinding.planInforamtionContainer, true)

        }

    }

}
