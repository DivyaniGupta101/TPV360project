    package com.tpv.android.ui.salesagent.home.enrollment.programs


    import android.os.Bundle
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
    import androidx.recyclerview.widget.LinearLayoutManager
    import com.google.gson.reflect.TypeToken
    import com.livinglifetechway.k4kotlin.core.orFalse
    import com.livinglifetechway.k4kotlin.core.orZero
    import com.livinglifetechway.k4kotlin.onClick
    import com.livinglifetechway.k4kotlin.toastNow
    import com.newventuresoftware.waveform.utils.TextUtils
    import com.tpv.android.R
    import com.tpv.android.databinding.FragmentGasElectricListingBinding
    import com.tpv.android.helper.OnBackPressCallBack
    import com.tpv.android.model.internal.itemSelection
    import com.tpv.android.model.network.*
    import com.tpv.android.network.error.AlertErrorHandler
    import com.tpv.android.network.resources.Resource
    import com.tpv.android.network.resources.apierror.APIError
    import com.tpv.android.network.resources.extensions.ifSuccess
    import com.tpv.android.ui.salesagent.home.enrollment.SetEnrollViewModel
    import com.tpv.android.ui.salesagent.home.enrollment.commodity.CommodityFragment
    import com.tpv.android.ui.salesagent.home.enrollment.commodity.CommodityFragmentDirections
    import com.tpv.android.ui.salesagent.home.enrollment.customerinfo.CustomerInfoFragmentNew
    import com.tpv.android.ui.salesagent.home.enrollment.dynamicform.DynamicFormFragment
    import com.tpv.android.ui.salesagent.home.enrollment.planszipcode.PlansZipcodeFragment
    import com.tpv.android.utils.copy
    import com.tpv.android.utils.navigateSafe
    import com.tpv.android.utils.setupToolbar
    import kotlinx.android.synthetic.main.fragment_programs_listing.*
    import kotlinx.coroutines.joinAll
    import java.lang.StringBuilder

    class ElectricListingFragment : Fragment() , ElectricUtilityAdapter.Onitemclicklistener, OnBackPressCallBack {
            private lateinit var mBinding: FragmentGasElectricListingBinding
            private lateinit var mViewModel: SetEnrollViewModel
            private lateinit var mProgramListingViewModel: ProgramListingViewModel
            var electric_listing: ArrayList<ElectricdataItem> = ArrayList()
            var electricadapter: ElectricUtilityAdapter?=null
            var selectedvalue:String=""
            private var mlistcommodity: ArrayList<CommodityResp> = ArrayList()


        companion object{
                var onback:Boolean=false
                var positon:Int=-1
                 var electricid:String=""




        }

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                      savedInstanceState: Bundle?): View? {
                // Inflate the layout for this fragment
                mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_gas_electric_listing, container, false)
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

                if(GasListingFragment.selectedvalue==true ){
                    electricid=""

                }
                if(mViewModel.dynamicSettings?.is_enable_duel_fuel_mandatory.orFalse()){
                    mBinding.btnNext.isEnabled=false
                }
                if( DynamicFormFragment.back_pressed==true){
                    if(mViewModel.dynamicSettings?.is_enable_duel_fuel_mandatory.orFalse()){
                        if(mViewModel.programid.isNotEmpty()){
                            mBinding.btnNext.isEnabled=true

                        }
                    }
                }
                mBinding.errorHandler = AlertErrorHandler(mBinding.root)
                listPrograms.layoutManager=LinearLayoutManager(context)
                mBinding.utilityName.text="Electric UTILITY"
                setupToolbar(mBinding.toolbar, getString(R.string.select_plan), showBackIcon = true,backIconClickListener = {
                    onback=true
                    mViewModel.add_enrollement_value=mViewModel.secondclick
                })
                if(GasListingFragment.reward_name!=null){
                    getelectricprogram(PlansZipcodeFragment.electric_utitlityid,GasListingFragment.reward_name)
                }else{
                    getelectricprogram(PlansZipcodeFragment.electric_utitlityid,"")

                }
                 mBinding.btnNext.onClick {
                    if(DynamicFormFragment.back_pressed==true ){
                        mViewModel.electric_list=mViewModel.electric_list
                    } else{
                        mViewModel.electric_list=electric_listing

                    }

                    handleNextButtonState()
                }

            }

            private fun handleNextButtonState() {
                Log.e("sdfsd",mViewModel.programid)
//                getDynamicFormApiCall(CommodityFragment.selectedid_multienrollement, CommodityFragment.selectedtitle_multienrollement,mViewModel.commodity_id)

                if(electricid?.isNotEmpty()){
                    mViewModel.programid=android.text.TextUtils.join(",",GasListingFragment.selectedid.plus(electricid))

                }else{
                    mViewModel.programid=android.text.TextUtils.join(",",GasListingFragment.selectedid)
                }
                if( mViewModel.programid.isEmpty()){
                    toastNow("Please Select at least One Utility")
                }else{
                    val liveData = mProgramListingViewModel.getAccountNumberRegex(
                            AccountNumberRegexRequest(
                                    formId = mViewModel.planId,
                                    utilityId = mViewModel.selectedUtilityList.map { it.utid }.joinToString(),
                                    programId =  mViewModel.programid
                            )
                    )
                    liveData.observe(this, Observer {
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


                            mViewModel.formPageMap = mViewModel.duplicatePageMap?.copy(object : TypeToken<DynamicFormResp>() {}.type)
                            Navigation.findNavController(mBinding.root).navigateSafe(ElectricListingFragmentDirections.actionProgramsListingFragmentToDynamicFormFragment(1))
                        }
                    })

                    mBinding.resource = liveData as LiveData<Resource<Any, APIError>>

                }



            }


           private fun getelectricprogram(utilityid:String,rewardname:String){
               val liveData=mViewModel.electricutility(utilityid,rewardname)
               liveData.observe(this, Observer {
                   it.ifSuccess {
                       electric_listing= it?.electricdata as ArrayList<ElectricdataItem>
                       if(DynamicFormFragment.back_pressed==true ){
                           Log.e("if","if")
                           electricadapter=ElectricUtilityAdapter(mViewModel.electric_list,this)
                           listPrograms.adapter=electricadapter
                       }else{
                           Log.e("else","else")
                           electricadapter=ElectricUtilityAdapter(electric_listing,this)
                           listPrograms.adapter=electricadapter

                       }

                   }
               })
               mBinding.resource = liveData as LiveData<Resource<Any, APIError>>

           }

            override fun onclick(position: Int) {
                     mBinding.btnNext.isEnabled=true
//                     mViewModel.electric_commodityd_id=electric_listing.get(position).commodity_id
//                     mViewModel.commodity_id.add(mViewModel.electric_commodityd_id)
//                     Log.e("dssfss", mViewModel.commodity_id.toString())
                     electricid=""
                     electricid=electric_listing.get(position).id.toString()
                     Log.e("id",electricid)

            }

            override fun handleOnBackPressed(): Boolean {
                initialize()
                onback=true
                mViewModel.add_enrollement_value=mViewModel.secondclick
                return true
            }


//        private fun getDynamicFormApiCall(id: String, title: String?,list: ArrayList<String>) {
//            val liveData = mViewModel.getDynamicForm(mViewModel.addenrollement,DynamicFormReq(formId = id,commodity_id = list))
//            liveData.observe(this, Observer {
//                it.ifSuccess {
//                    mViewModel.utilityList.addAll(mlistcommodity.find { it.id.toString() == id }?.commodities.orEmpty())
//                    mViewModel.planId = id
//                }
//            })
//            mBinding.resource = liveData as LiveData<Resource<Any, APIError>>
//        }


    }



