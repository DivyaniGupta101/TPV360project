package com.tpv.android.ui.home.enrollment.programs


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.livinglifetechway.k4kotlin.core.*
import com.ravikoradiya.liveadapter.LiveAdapter
import com.tpv.android.BR
import com.tpv.android.R
import com.tpv.android.databinding.FragmentProgramsListingBinding
import com.tpv.android.databinding.ItemProgramsBinding
import com.tpv.android.model.ProgramsResp
import com.tpv.android.network.error.AlertErrorHandler
import com.tpv.android.network.resources.Resource
import com.tpv.android.network.resources.apierror.APIError
import com.tpv.android.network.resources.extensions.ifSuccess
import com.tpv.android.ui.home.enrollment.SetEnrollViewModel
import com.tpv.android.utils.enums.Plan
import com.tpv.android.utils.navigateSafe
import com.tpv.android.utils.setupToolbar

class ProgramsListingFragment : Fragment() {
    private lateinit var mBinding: FragmentProgramsListingBinding
    private var mLastSelectedGasPosition: Int? = null
    private var mLastSelectedElectricPosition: Int? = null
    private lateinit var mViewModel: SetEnrollViewModel

    private var mList: ArrayList<Any> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_programs_listing, container, false)
        mBinding.lifecycleOwner = this
        activity?.let { this.mViewModel = ViewModelProviders.of(it).get(SetEnrollViewModel::class.java) }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.errorHandler = AlertErrorHandler(mBinding.root)

        setupToolbar(mBinding.toolbar, getString(R.string.select_plan), showBackIcon = true)

        if (mLastSelectedGasPosition != null) {
            (mList[mLastSelectedGasPosition.orZero()] as ProgramsResp).isSelcected = true
            mBinding.listPrograms.adapter?.notifyDataSetChanged()
        }
        if (mLastSelectedElectricPosition != null) {
            (mList[mLastSelectedElectricPosition.orZero()] as ProgramsResp).isSelcected = true
            mBinding.listPrograms.adapter?.notifyDataSetChanged()
        }

        if (mList.isEmpty()) {
            getProgramsApi()
        } else {
            setRecyclerView()
        }

        handleNextButtonState()


        mBinding.btnNext.onClick {
            this@ProgramsListingFragment.mViewModel.programList.clear()

            when (this@ProgramsListingFragment.mViewModel.planType) {
                Plan.GASFUEL.value -> {
                    this@ProgramsListingFragment.mViewModel.programList.add(mList[mLastSelectedGasPosition.orZero()] as ProgramsResp)
                }
                Plan.ELECTRICFUEL.value -> {
                    this@ProgramsListingFragment.mViewModel.programList.add(mList[mLastSelectedElectricPosition.orZero()] as ProgramsResp)
                }
                Plan.DUALFUEL.value -> {
                    this@ProgramsListingFragment.mViewModel.programList.add(mList[mLastSelectedGasPosition.orZero()] as ProgramsResp)
                    this@ProgramsListingFragment.mViewModel.programList.add(mList[mLastSelectedElectricPosition.orZero()] as ProgramsResp)
                }
            }
            Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_programsListingFragment_to_personalDetailFormFragment)
        }
    }

    private fun getProgramsApi() {

        val liveData = this.mViewModel.getPrograms(this.mViewModel.utilitiesList)

        liveData.observe(this, Observer {
            it.ifSuccess {
                mList.clear()
                mList.addAll(it.orEmpty())
                setRecyclerView()
            }
        })

        mBinding.resource = liveData as LiveData<Resource<Any, APIError>>
    }

    private fun setRecyclerView() {
        LiveAdapter(mList, BR.item)
                .map<ProgramsResp, ItemProgramsBinding>(R.layout.item_programs) {
                    onBind {
                        if (it.binding.item?.isSelcected.orFalse()) {
                            it.binding.mainContainer.background = context?.getDrawable(R.drawable.bg_rectangle_border)
                            it.binding.imageEnroll.show()
                        } else {
                            it.binding.mainContainer.background = null
                            it.binding.imageEnroll.hide()
                        }
                    }
                    onClick {
                        itemSelection(type = it.binding.item?.utilityType.orEmpty(), selectedPosition = it.adapterPosition)
                        handleNextButtonState()
                    }
                }
                .map<String, ItemProgramsBinding>(R.layout.item_title_programs)
                .into(mBinding.listPrograms)
    }

    private fun handleNextButtonState() {
        mBinding.btnNext.isEnabled = when (this.mViewModel.planType) {
            Plan.ELECTRICFUEL.value -> {
                mLastSelectedElectricPosition != null
            }
            Plan.GASFUEL.value -> {
                mLastSelectedGasPosition != null
            }
            Plan.DUALFUEL.value -> {
                (mLastSelectedElectricPosition != null && mLastSelectedGasPosition != null)
            }
            else -> {
                false
            }
        }
    }

    private fun itemSelection(type: String, selectedPosition: Int) {
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
