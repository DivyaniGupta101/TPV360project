    package com.tpv.android.ui.salesagent.home.enrollment.programs


    import android.os.Bundle
    import android.util.Log
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import androidx.core.text.TextUtilsCompat
    import androidx.databinding.DataBindingUtil
    import androidx.fragment.app.Fragment
    import androidx.lifecycle.LiveData
    import androidx.lifecycle.Observer
    import androidx.lifecycle.ViewModelProviders
    import androidx.navigation.Navigation
    import androidx.recyclerview.widget.LinearLayoutManager
    import com.livinglifetechway.k4kotlin.core.onClick
    import com.livinglifetechway.k4kotlin.core.orFalse
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
    import com.tpv.android.ui.salesagent.home.enrollment.dynamicform.DynamicFormFragment
    import com.tpv.android.ui.salesagent.home.enrollment.planszipcode.PlansZipcodeFragment
    import com.tpv.android.utils.navigateSafe
    import com.tpv.android.utils.setupToolbar
    import kotlinx.android.synthetic.main.fragment_programs_listing.*
    import android.text.TextUtils.join as join1


    class GasListingFragment : Fragment(),GasUtilityAdapter.Onitemclicklistener,OnBackPressCallBack {
        private lateinit var mBinding: FragmentGasElectricListingBinding
        private lateinit var mViewModel: SetEnrollViewModel
        private lateinit var mProgramListingViewModel: ProgramListingViewModel
        var gasadapter: GasUtilityAdapter?=null
        var gas_listing:ArrayList<GasdataItem> =ArrayList()


        companion object {
            var reward_name: String = ""
            var button_preesed:Boolean?=null
            var positon:Int=-1
            var selectedid:ArrayList<String> =ArrayList()
            var gasid:String=""
            var selectedvalue:Boolean=false





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
            mBinding.errorHandler = AlertErrorHandler(mBinding.root)
            listPrograms.layoutManager=LinearLayoutManager(context)
            mBinding.utilityName.text="GAS UTILITY"
            if(mViewModel.dynamicSettings?.is_enable_duel_fuel_mandatory.orFalse()){
                mBinding.btnNext.isEnabled=false
            }
            if(ElectricListingFragment.onback==true || DynamicFormFragment.back_pressed==true){
                if(mViewModel.dynamicSettings?.is_enable_duel_fuel_mandatory.orFalse()){
                    if(mViewModel.programid.isNotEmpty()){
                        mBinding.btnNext.isEnabled=true

                    }
                }
            }

            setupToolbar(mBinding.toolbar, getString(R.string.select_plan), showBackIcon = true,backIconClickListener = {
                mViewModel.programid=""
                mViewModel.utility_list.clear()
                mViewModel.selectedUtilityList.clear()
                ElectricListingFragment.onback=false
            })
            handleNextButtonState()
            getgasprogram(PlansZipcodeFragment.gasutility_id)
            if(mViewModel.zipcode.isEmpty()){
                getimage(mViewModel.utility_list,mViewModel.state_id,"")

            }else{
                getimage(mViewModel.utility_list,"",mViewModel.zipcode)

            }


        }


        private fun  getimage(list:ArrayList<String>,stateid:String,zipcode:String){
            val liveData=mViewModel.getimageupload(Requentutilityid(list,state_id = stateid,zipcode = zipcode))
            liveData.observe(this, Observer {
                it.ifSuccess {
                    mViewModel.is_image_upload=it?.imageUpload?.isEnableImageUpload
                    mViewModel.is_image_upload_mandatory=it?.imageUpload?.isEnableImageUploadMandatory

                }
            })
        }


        private fun handleNextButtonState() {

            mBinding.btnNext.onClick {
                if(ElectricListingFragment.onback==true && DynamicFormFragment.back_pressed==false){
                    ElectricListingFragment.electricid=""
//                    if(selectedvalue==true){
//                        button_preesed=false
//                    }
//                    button_preesed=true
                    mViewModel.gaslist=mViewModel.gaslist
                }else if( DynamicFormFragment.back_pressed==true && ElectricListingFragment.onback==true){
                    if(selectedvalue==true){
                        button_preesed=false
                    }
                    button_preesed=true
                    mViewModel.gaslist=mViewModel.gaslist
                } else{
                    mViewModel.gaslist=gas_listing

                }
                Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_programsListingFragment_to_electriclistingfragment)

           }

        }




       private fun getgasprogram(utilityid:String){

           val liveData=mViewModel.gasutility(utilityid)
           liveData.observe(this, Observer {
               it.ifSuccess {
                  gas_listing= it?.gasdata as ArrayList<GasdataItem>
                   if(ElectricListingFragment.onback==true || DynamicFormFragment.back_pressed==true  ){
                       gasadapter=GasUtilityAdapter(mViewModel.gaslist,this)
                       listPrograms.adapter=gasadapter
                   }else{
                       gasadapter=GasUtilityAdapter(gas_listing,this)
                       listPrograms.adapter=gasadapter

                   }

               }
           })
           mBinding.resource = liveData as LiveData<Resource<Any, APIError>>

       }



        override fun onclick(position: Int) {
            mBinding.btnNext.isEnabled=true
            gasid=""
            mViewModel.gas_commodityd_id=""
            selectedid.clear()
            if( DynamicFormFragment.back_pressed==true ){
                DynamicFormFragment.back_pressed=false
                selectedvalue=true
                gasid=gas_listing.get(position).id.toString()
//                mViewModel.gas_commodityd_id=gas_listing.get(position).commodity_id
//                mViewModel.commodity_id.add(mViewModel.gas_commodityd_id)
//                Log.e("id", mViewModel.gas_commodityd_id)
                selectedid.add(gasid)
                reward_name=gas_listing.get(position).rewardName

            }else{
                reward_name=gas_listing.get(position).rewardName
                gasid=gas_listing.get(position).id.toString()
//                mViewModel.gas_commodityd_id=gas_listing.get(position).commodity_id
//                mViewModel.commodity_id.add(mViewModel.gas_commodityd_id)
//                Log.e("id", mViewModel.gas_commodityd_id)
                selectedid.add(gasid)

            }
        }

        override fun handleOnBackPressed(): Boolean {
            mViewModel.programid=""
            mViewModel.utility_list.clear()
            mViewModel.selectedUtilityList.clear()
            ElectricListingFragment.onback=false
            return true
        }


    }



