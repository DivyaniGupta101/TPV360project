package com.tpv.android.ui.home.enrollment.programs


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.livinglifetechway.k4kotlin.core.*
import com.ravikoradiya.liveadapter.LiveAdapter
import com.tpv.android.BR
import com.tpv.android.R
import com.tpv.android.databinding.FragmentProgramsListingBinding
import com.tpv.android.databinding.ItemProgramsBinding
import com.tpv.android.model.ProgramsReq
import com.tpv.android.model.ProgramsResp
import com.tpv.android.network.resources.extensions.ifSuccess
import com.tpv.android.ui.home.enrollment.SetEnrollViewModel
import com.tpv.android.utils.Plan
import com.tpv.android.utils.navigateSafe
import com.tpv.android.utils.setupToolbar

/**
 * A simple [Fragment] subclass.
 */
class ProgramsListingFragment : Fragment() {
    private lateinit var mBinding: FragmentProgramsListingBinding
    private var mLastSelectedGasPosition: Int? = null
    private var mLastSelectedElectricPosition: Int? = null
    private lateinit var mViewModel: ProgramsListingViewModel
    private lateinit var mSetEnrollViewModel: SetEnrollViewModel

    private var mList: ArrayList<Any> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_programs_listing, container, false)
        mBinding.lifecycleOwner = this
        mViewModel = ViewModelProviders.of(this).get(ProgramsListingViewModel::class.java)
        mSetEnrollViewModel = ViewModelProviders.of(this).get(SetEnrollViewModel::class.java)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar(mBinding.toolbar, getString(R.string.select_plan), showBackIcon = true)
        setRecyclerView()

        mSetEnrollViewModel.utilitiesList.forEach { utilityResp ->

            mViewModel.getPrograms(ProgramsReq(utilityResp?.utid.toString())).observe(this, Observer {
                it.ifSuccess {
                    mList.add(utilityResp?.commodity + getString(R.string.programs))
                    mList.add(it.orEmpty())
                }
            })


        }

        mBinding.btnNext?.onClick {
            Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_programsListingFragment_to_personalDetailFormFragment)
        }
    }

    private fun setRecyclerView() {
        LiveAdapter(mList, BR.item)
                .map<ProgramsResp, ItemProgramsBinding>(R.layout.item_programs) {
                    onBind {
                        if (it.binding.item?.isSelcected.orFalse()) {
                            it.binding.mainContainer.background = context?.getDrawable(R.drawable.bg_rectangle_program_border)
                            it.binding.imageEnroll.show()
                        } else {
                            it.binding.mainContainer.background = null
                            it.binding.imageEnroll.hide()
                        }
                    }
                    onClick {
                        selectItem(type = it.binding.item?.utilityType.orEmpty(), selectedPosition = it.adapterPosition)
                    }
                }
                .map<String, ItemProgramsBinding>(R.layout.item_title_programs)
                .into(mBinding.listPrograms)

    }

    private fun selectItem(type: String, selectedPosition: Int) {
        when (type) {
            Plan.GASFUEL.value -> {
                if (mLastSelectedGasPosition != null) {
                    (mList[mLastSelectedGasPosition.orZero()] as ProgramsResp).isSelcected = false
                }
                (mList[selectedPosition] as ProgramsResp).isSelcected = true
                mLastSelectedGasPosition = selectedPosition

            }
            Plan.ELECTRICFUEL.value -> {
                if (mLastSelectedElectricPosition != null) {
                    (mList[mLastSelectedElectricPosition.orZero()] as ProgramsResp).isSelcected = false
                }
                (mList[selectedPosition] as ProgramsResp).isSelcected = true
                mLastSelectedElectricPosition = selectedPosition
            }
        }
        mBinding.listPrograms.adapter?.notifyDataSetChanged()

    }

}
