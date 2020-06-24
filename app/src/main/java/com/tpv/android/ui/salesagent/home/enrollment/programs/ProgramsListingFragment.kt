package com.tpv.android.ui.salesagent.home.enrollment.programs


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
import com.google.gson.reflect.TypeToken
import com.livinglifetechway.k4kotlin.core.hide
import com.livinglifetechway.k4kotlin.core.onClick
import com.livinglifetechway.k4kotlin.core.orFalse
import com.livinglifetechway.k4kotlin.core.show
import com.ravikoradiya.liveadapter.LiveAdapter
import com.tpv.android.BR
import com.tpv.android.R
import com.tpv.android.databinding.FragmentProgramsListingBinding
import com.tpv.android.databinding.ItemProgramsBinding
import com.tpv.android.model.internal.itemSelection
import com.tpv.android.model.network.DynamicFormResp
import com.tpv.android.model.network.ProgramsResp
import com.tpv.android.network.error.AlertErrorHandler
import com.tpv.android.network.resources.Resource
import com.tpv.android.network.resources.apierror.APIError
import com.tpv.android.network.resources.extensions.ifSuccess
import com.tpv.android.ui.salesagent.home.enrollment.SetEnrollViewModel
import com.tpv.android.utils.copy
import com.tpv.android.utils.navigateSafe
import com.tpv.android.utils.setupToolbar


class ProgramsListingFragment : Fragment() {
    private lateinit var mBinding: FragmentProgramsListingBinding
    private var mLastSelectedGasPosition: Int? = null
    private var mLastSelectedElectricPosition: Int? = null
    private lateinit var mViewModel: SetEnrollViewModel
    private var mLastSelected: ArrayList<itemSelection> = ArrayList()

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
        initialize()
    }

    private fun initialize() {
        mBinding.errorHandler = AlertErrorHandler(mBinding.root)

        setupToolbar(mBinding.toolbar, getString(R.string.select_plan), showBackIcon = true)

        //If mList is empty then getPrograms from api and then set in recyclerView else only set in recyclerView
        if (mList.isEmpty()) {
            getProgramsApiCall()
        } else {
            setRecyclerView()
        }

        handleNextButtonState()


        //Save ProgramDetail in viewModel
        mBinding.btnNext.onClick {
            mViewModel.programList.clear()
            mList.forEach {
                if (it is ProgramsResp) {
                    if (mLastSelected.contains(itemSelection(it.utilityId, it.id))) {
                        mViewModel.programList.add(it)
                    }
                }
            }

            mViewModel.formPageMap = mViewModel.duplicatePageMap?.copy(object : TypeToken<DynamicFormResp>() {}.type)
            Navigation.findNavController(mBinding.root).navigateSafe(ProgramsListingFragmentDirections.actionProgramsListingFragmentToDynamicFormFragment(1))
        }
    }

    /**
     * Get programs
     */
    private fun getProgramsApiCall() {

        val liveData = mViewModel.getPrograms(mViewModel.selectedUtilityList)

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
                        itemSelection(utilityId = it.binding.item?.utilityId.orEmpty(),
                                selectedItemId = it.binding.item?.id.orEmpty())
                        handleNextButtonState()
                    }
                }
                .map<String, ItemProgramsBinding>(R.layout.item_title_programs)
                .into(mBinding.listPrograms)
    }

    /**
     * Check there should be one item selected from all the categories of programs then only button should be enable
     */

    private fun handleNextButtonState() {
        mBinding.btnNext.isEnabled = mLastSelected.size == mViewModel.selectedUtilityList.size
    }


    /**
     * Handle itemSelection, only one item from each categories should be selected
     * Other will be unSelected automatically
     */
    private fun itemSelection(utilityId: String, selectedItemId: String) {
        //Get list of data which contain same utilityId
        val lastSelectedList = mLastSelected.filter { it.utilityId == utilityId }

        //"lastSelectedList" is empty then add in list
        // Else  set all other value as false also remove all other values and add latest selected value
        if (lastSelectedList.isNotEmpty()) {

            lastSelectedList.forEach { lastSelected ->
                mList.forEach {
                    if (it is ProgramsResp) {
                        if (it.id == lastSelected.lastSelected) {
                            it.isSelcected = false
                        }
                    }
                }
            }
            mLastSelected.removeAll { it.utilityId == utilityId }
            itemSelection(utilityId, selectedItemId)
        } else {
            mList.forEach {
                if (it is ProgramsResp) {
                    if (it.id == selectedItemId) {
                        it.isSelcected = true
                        mLastSelected.add(itemSelection(it.utilityId, it.id))
                    }
                }
            }
        }
        mBinding.listPrograms.adapter?.notifyDataSetChanged()
    }

}