package com.tpv.android.ui.home.programs


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.livinglifetechway.k4kotlin.core.*
import com.ravikoradiya.liveadapter.LiveAdapter
import com.tpv.android.BR
import com.tpv.android.R
import com.tpv.android.databinding.FragmentProgramsListingBinding
import com.tpv.android.databinding.ItemProgramsBinding
import com.tpv.android.model.Programs
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

    private var mList: ArrayList<Any> = arrayListOf("Electric Programs",
            Programs("1", null, null, null, null, null, null, null, null, null, null, null, null, false, Plan.ELECTRICFUEL.value),
            Programs("2", null, null, null, null, null, null, null, null, null, null, null, null, false, Plan.ELECTRICFUEL.value),
            Programs("3", null, null, null, null, null, null, null, null, null, null, null, null, false, Plan.ELECTRICFUEL.value)
            , "Gas Programs",
            Programs("4", null, null, null, null, null, null, null, null, null, null, null, null, false, Plan.GASFUEL.value),
            Programs("5", null, null, null, null, null, null, null, null, null, null, null, null, false, Plan.GASFUEL.value),
            Programs("6", null, null, null, null, null, null, null, null, null, null, null, null, false, Plan.GASFUEL.value))

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

        mBinding.btnNext?.onClick {
            Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_programsListingFragment_to_customerDetailFormOneFragment)
        }
    }

    private fun setRecyclerView() {
        LiveAdapter(mList, BR.item)
                .map<Programs, ItemProgramsBinding>(R.layout.item_programs) {
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
                        val programs = it.binding.item
                        selectItem(type = programs?.utilityType.orEmpty(), selectedPosition = it.adapterPosition)
                    }
                }
                .map<String, ItemProgramsBinding>(R.layout.item_title_programs)
                .into(mBinding.listPrograms)

    }

    private fun selectItem(type: String, selectedPosition: Int) {
        when (type) {
            Plan.GASFUEL.value -> {
                if (mLastSelectedGasPosition != null) {
                    (mList[mLastSelectedGasPosition.orZero()] as Programs).isSelcected = false
                    (mList[selectedPosition] as Programs).isSelcected = true
                }
                mLastSelectedGasPosition = selectedPosition
            }
            Plan.ELECTRICFUEL.value -> {
                if (mLastSelectedElectricPosition != null) {
                    (mList[mLastSelectedElectricPosition.orZero()] as Programs).isSelcected = false
                    (mList[selectedPosition] as Programs).isSelcected = true
                }
                mLastSelectedElectricPosition = selectedPosition
            }
        }
        mBinding.listPrograms.adapter?.notifyDataSetChanged()

    }

}
