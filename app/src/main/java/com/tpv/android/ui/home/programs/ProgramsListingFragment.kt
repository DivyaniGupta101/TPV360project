package com.tpv.android.ui.home.programs


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.ravikoradiya.liveadapter.LiveAdapter
import com.tpv.android.BR
import com.tpv.android.R
import com.tpv.android.databinding.FragmentProgramsListingBinding
import com.tpv.android.databinding.ItemProgramsBinding
import com.tpv.android.utils.setupToolbar

/**
 * A simple [Fragment] subclass.
 */
class ProgramsListingFragment : Fragment() {
    private lateinit var mBinding: FragmentProgramsListingBinding
    private var mList: ArrayList<Any> = arrayListOf("Electric Programs", 1, 1, 1, 1, 1, 1, "Gas Programs", 1, 1, 1, 1, 1)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_programs_listing, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar(mBinding.toolbar, getString(R.string.select_plan), showBackIcon = true)
        setRecyclerView()

    }

    private fun setRecyclerView() {
        LiveAdapter(mList, BR.item)
                .map<Int, ItemProgramsBinding>(R.layout.item_programs)
                .map<String, ItemProgramsBinding>(R.layout.item_title_programs)
                .into(mBinding.listPrograms)

    }

}
