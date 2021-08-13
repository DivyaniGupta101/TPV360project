package com.tpv.android.ui.salesagent.home.enrollment.customerinfo


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.livinglifetechway.k4kotlin.core.hide
import com.livinglifetechway.k4kotlin.core.onClick
import com.livinglifetechway.k4kotlin.core.orFalse
import com.livinglifetechway.k4kotlin.core.show
import com.livinglifetechway.k4kotlin.toast
import com.tpv.android.R
import com.tpv.android.databinding.*
import com.tpv.android.helper.OnBackPressCallBack
import com.tpv.android.model.internal.DialogText
import com.tpv.android.model.network.*
import com.tpv.android.network.error.AlertErrorHandler
import com.tpv.android.network.resources.Resource
import com.tpv.android.network.resources.apierror.APIError
import com.tpv.android.network.resources.extensions.ifSuccess
import com.tpv.android.ui.salesagent.home.enrollment.SetEnrollViewModel
import com.tpv.android.ui.salesagent.home.enrollment.dynamicform.DynamicFormFragment
import com.tpv.android.utils.AppConstant
import com.tpv.android.utils.BindingAdapter.addressCombineValues
import com.tpv.android.utils.actionDialog
import com.tpv.android.utils.enums.DynamicField
import com.tpv.android.utils.navigateSafe
import com.tpv.android.utils.setupToolbar
import kotlinx.android.synthetic.main.customer_info_adapter.*
import kotlinx.android.synthetic.main.fragment_customer_info_new.*
import kotlinx.android.synthetic.main.fragment_success.*
import kotlinx.android.synthetic.main.toolbar.*

class CustomerInfoFragmentNew : Fragment() , OnBackPressCallBack,CustomerInformationAdapter.Onitemclicklistener {
    private lateinit var mBinding: FragmentCustomerInfoNewBinding
    private lateinit var mViewModel: SetEnrollViewModel
    private var mViewGroup: ViewGroup? = null
    var temp_leadid:String=""
    var adapter:CustomerInformationAdapter?=null
    var tmpDataItem:TmpDataItem?=null





    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_customer_info_new, container, false)
        activity?.let { mViewModel = ViewModelProviders.of(it).get(SetEnrollViewModel::class.java) }
        mBinding.lifecycleOwner = this
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
    }



    private fun initialize() {
        mBinding.errorHandler = AlertErrorHandler(mBinding.root)
        setupToolbar(mBinding.toolbar, getString(R.string.verify_customer_information), showBackIcon = true,backIconClickListener = {
            if(mViewModel.addenrollement==true){
                mViewModel.custome_toolbar_clicked=true

            }
            mViewModel.customerback=true
            mViewModel.add_enrollement_value=mViewModel.secondclick

        })

        getcustomerinformation(mViewModel.mList)
        mBinding.btnNext.onClick {
               if(mViewModel.dynamicSettings?.le_client_enrollment_type.orFalse()){
                   if (mViewModel.dynamicSettings?.isEnableRecording.orFalse()) {
                       Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_clientInfoFragment_to_recordingFragment)
                   } else  if (DynamicFormFragment.image_upload==1) {
                       Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_clientInfoFragment_to_uploadbillimageFragment)
                   }  else {
                       Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_clientInfoFragment_to_signatureVerificationFragment)
                   }
               }else{
                   if (mViewModel.dynamicSettings?.isEnableRecording.orFalse()) {
                       Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_clientInfoFragment_to_recordingFragment)
                   } else  if (mViewModel.is_image_upload.orFalse()) {
                       Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_clientInfoFragment_to_uploadbillimageFragment)
                   } else {
                       Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_clientInfoFragment_to_signatureVerificationFragment)
                   }
               }


            }


    }

    private fun getcustomerinformation(templeadid:List<String>){
        val liveData=mViewModel.customerverificationinformation(RequestCustomer(tmpLeadIds = templeadid) )
        liveData.observe(this, Observer {
            it.ifSuccess {
                mViewModel.templeaddetails= it?.tmpData as ArrayList<TmpDataItem>

                    adapter=CustomerInformationAdapter(mViewModel.templeaddetails,this)
                    customer_information.adapter=adapter


            }
        })
        mBinding.resource = liveData as LiveData<Resource<Any, APIError>>

    }

    override fun handleOnBackPressed(): Boolean {
        mViewModel.customerback=true
        Log.e("backpressed",DynamicFormFragment.back_pressed.toString())
        mViewModel.add_enrollement_value=mViewModel.secondclick
        Log.e("addenrollement",mViewModel.add_enrollement_value.toString())
        if(mViewModel.addenrollement==true){
            mViewModel.custome_toolbar_clicked=true

        }
        initialize()
        return true
    }

    fun cancelenrollement(temp_leadid:String){
        val liveData=mViewModel.cancelenrollementform(templeadid = temp_leadid)
        liveData.observe(this, Observer {
            it.ifSuccess {
                tmpDataItem?.let { it1 -> adapter?.removeItem(it1) }
                toast("Enrollement is deleted")
                for(i in 0 until mViewModel.mList.size){
                    if(temp_leadid.equals(mViewModel.mList.get(i))){
                        mViewModel.mList.remove(temp_leadid)
                    }
                }
            }
        })


    }

    override fun onclick(position: Int) {
        context?.actionDialog(
                DialogText(getString(R.string.delete_enrollment),
                        getString(R.string.enroll_delete),
                        getString(R.string.yes),
                        getString(R.string.no)),
                setOnPositiveBanClickListener = {
                    tmpDataItem=mViewModel.templeaddetails.get(position)
                    temp_leadid=mViewModel.templeaddetails.get(position).tempLeadId.toString()
                    cancelenrollement(temp_leadid)
                    adapter?.notifyDataSetChanged()

                }
        )

    }

}
