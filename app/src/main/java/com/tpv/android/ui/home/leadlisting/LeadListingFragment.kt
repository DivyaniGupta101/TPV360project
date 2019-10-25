package com.tpv.android.ui.leadlisting


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.ravikoradiya.liveadapter.LiveAdapter
import com.tpv.android.BR
import com.tpv.android.R
import com.tpv.android.databinding.FragmentLeadListingBinding
import com.tpv.android.databinding.ItemLeadListBinding
import com.tpv.android.utils.setupToolbar

/**
 * A simple [Fragment] subclass.
 */
class LeadListingFragment : Fragment() {
    lateinit var mBinding: FragmentLeadListingBinding
    var mList = arrayListOf(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_lead_listing, container, false)
        return mBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar(mBinding.toolbar, getString(R.string.pending_leads), showMenuIcon = true, showBackIcon = true)

        setRecyclerView()
    }

    private fun setRecyclerView() {
        LiveAdapter(mList, BR.item)
                .map<Int, ItemLeadListBinding>(R.layout.item_lead_list)
                .into(mBinding.listLead)
    }

}
