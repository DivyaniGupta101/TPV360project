    package com.tpv.android.ui.salesagent.home.enrollment.programs


    import android.os.Bundle
    import android.text.TextUtils
    import android.util.Log
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
    import com.livinglifetechway.k4kotlin.core.*
    import com.ravikoradiya.liveadapter.LiveAdapter
    import com.tpv.android.BR
    import com.tpv.android.R
    import com.tpv.android.databinding.FragmentProgramsListingBinding
    import com.tpv.android.databinding.ItemProgramsBinding
    import com.tpv.android.databinding.LayoutProgramCustomFieldBinding
    import com.tpv.android.helper.OnBackPressCallBack
    import com.tpv.android.model.internal.itemSelection
    import com.tpv.android.model.network.AccountNumberRegexRequest
    import com.tpv.android.model.network.DynamicFormResp
    import com.tpv.android.model.network.ProgramsResp
    import com.tpv.android.model.network.Requentutilityid
    import com.tpv.android.network.error.AlertErrorHandler
    import com.tpv.android.network.resources.Resource
    import com.tpv.android.network.resources.apierror.APIError
    import com.tpv.android.network.resources.extensions.ifSuccess
    import com.tpv.android.ui.salesagent.home.enrollment.SetEnrollViewModel
    import com.tpv.android.ui.salesagent.home.enrollment.dynamicform.DynamicFormFragment
    import com.tpv.android.utils.copy
    import com.tpv.android.utils.navigateSafe
    import com.tpv.android.utils.setupToolbar


    class ProgramsListingFragment : Fragment(), OnBackPressCallBack {
        private lateinit var mBinding: FragmentProgramsListingBinding
        private var mLastSelectedGasPosition: Int? = null
        private var mLastSelectedElectricPosition: Int? = null
        private lateinit var mViewModel: SetEnrollViewModel
        private lateinit var mProgramListingViewModel: ProgramListingViewModel
        private var mLastSelected: ArrayList<itemSelection> = ArrayList()

        private var mList: ArrayList<Any> = ArrayList()

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {
            // Inflate the layout for this fragment
            mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_programs_listing, container, false)
            mBinding.lifecycleOwner = this
            activity?.let { this.mViewModel = ViewModelProviders.of(it).get(SetEnrollViewModel::class.java) }
            mProgramListingViewModel = ViewModelProviders.of(this).get(ProgramListingViewModel::class.java)
            return mBinding.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            initialize()
        }

        private fun initialize() {
            mBinding.errorHandler = AlertErrorHandler(mBinding.root)

            setupToolbar(mBinding.toolbar, getString(R.string.select_plan), showBackIcon = true)
            getimage(mViewModel.utility_list)
            //If mList is empty then getPrograms from api and then set in recyclerView else only set in recyclerView
            if (mList.isEmpty()) {
                getProgramsApiCall()
            } else {

                setRecyclerView()
            }



            handleNextButtonState()


            //Save ProgramDetail in viewModel
            mBinding.btnNext.onClick {
                val liveData = mProgramListingViewModel.getAccountNumberRegex(
                        AccountNumberRegexRequest(
                                formId = mViewModel.planId,
                                utilityId = mViewModel.selectedUtilityList.map { it.utid }.joinToString(),
                                programId = TextUtils.join(",",mLastSelected.map { it.lastSelected }
                                )
                        ))
                liveData.observe(this@ProgramsListingFragment, Observer {
                    it?.ifSuccess {
                        it?.data?.forEach { response ->
                            for (pageNumber in 1..mViewModel.duplicatePageMap?.size.orZero().orZero()) {
                                mViewModel.duplicatePageMap?.get(pageNumber)?.forEach { dynamicResp ->
                                    if (response.field_id == dynamicResp.id) {
                                        dynamicResp.validations?.regexMessage = response?.regex_message
                                        dynamicResp.validations?.regex = response?.regex
                                        if (response.placeHolder?.isNotBlank().orFalse()) {
                                            dynamicResp.meta?.placeHolder = response?.placeHolder
                                        }
                                        if (response.label?.isNotBlank().orFalse()) {
                                            dynamicResp.label = response?.label
                                        }

                                        if (response.option?.isNotEmpty().orFalse()) {
                                            dynamicResp.meta?.options = response?.option
                                        }

                                    }
                                }
                            }
                        }

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
                })

                mBinding.resource = liveData as LiveData<Resource<Any, APIError>>

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

        private fun  getimage(list:ArrayList<String>){
            val liveData=mViewModel.getimageupload(Requentutilityid(list))
            liveData.observe(this, Observer {
                it.ifSuccess {
                    mViewModel.is_image_upload=it?.imageUpload?.isEnableImageUpload
                    mViewModel.is_image_upload_mandatory=it?.imageUpload?.isEnableImageUploadMandatory

                }
            })
        }

        private fun setRecyclerView() {

            LiveAdapter(mList, BR.item)
                    .map<ProgramsResp, ItemProgramsBinding>(R.layout.item_programs) {
                        onBind {
                            it.binding.customFieldsContainer.removeAllViews()
                            it.binding.item?.costomFields?.forEachIndexed { index, programCustomField ->

                                val binding = DataBindingUtil.inflate<LayoutProgramCustomFieldBinding>(layoutInflater,
                                        R.layout.layout_program_custom_field,
                                        it.binding.customFieldsContainer,
                                        true)
                                binding.item = programCustomField
                                if (index == 0) {
                                    binding.dividerView.show()
                                } else {
                                    binding.dividerView.hide()
                                }
                            }
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

        override fun handleOnBackPressed(): Boolean {
            mViewModel.selectedUtilityList.clear()
            return true
        }

    }