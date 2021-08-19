package com.tpv.android.ui.salesagent.home.enrollment.success




import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.livinglifetechway.k4kotlin.core.onClick
import com.tpv.android.R
import com.tpv.android.databinding.DialogSelectVerificationBinding
import com.tpv.android.databinding.FragmentSuccessBinding
import com.tpv.android.helper.OnBackPressCallBack
import com.tpv.android.model.internal.DialogText
import com.tpv.android.model.network.ScheduleTPVCallRequest
import com.tpv.android.model.network.SuccessReq
import com.tpv.android.network.error.AlertErrorHandler
import com.tpv.android.network.resources.Resource
import com.tpv.android.network.resources.apierror.APIError
import com.tpv.android.network.resources.extensions.ifSuccess
import com.tpv.android.ui.salesagent.home.enrollment.SetEnrollViewModel
import com.tpv.android.ui.salesagent.home.enrollment.dynamicform.DynamicFormFragment
import com.tpv.android.ui.salesagent.home.enrollment.planszipcode.PlansZipcodeFragment
import com.tpv.android.ui.salesagent.home.enrollment.programs.ElectricListingFragment
import com.tpv.android.ui.salesagent.home.enrollment.programs.GasListingFragment
import com.tpv.android.utils.AppConstant
import com.tpv.android.utils.infoDialog
import com.tpv.android.utils.navigateSafe
import com.tpv.android.utils.setupToolbar
import kotlinx.android.synthetic.main.fragment_success.*

class SuccessFragment : Fragment(), OnBackPressCallBack {

    lateinit var mBinding: FragmentSuccessBinding
    lateinit var mViewModel: SetEnrollViewModel
    private var mVerificationType: ArrayList<String> = ArrayList()

    companion object onback{
        var Onback:Boolean=false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_success, container, false)
        mBinding.lifecycleOwner = this
        activity?.let { mViewModel = ViewModelProviders.of(it).get(SetEnrollViewModel::class.java) }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
    }

    override fun onResume() {
        super.onResume()
    }

    /**

     * On click of backButton remove stored Data
     */


    override fun handleOnBackPressed(): Boolean {
        mViewModel.clearSavedData()

        return true
    }

    private fun initialize() {
        setupToolbar(mBinding.toolbar, getString(R.string.success))
        mBinding.errorHandler = AlertErrorHandler(mBinding.root)
        mBinding.item = mViewModel.savedLeadResp

        if(mViewModel.savedLeadResp?.isOnOutBoundTPV ==true){
            Onback=true
            textBackToDashBoard.isEnabled=false
            textBackToDashBoard.setTextColor(resources.getColor(R.color.colorDarkGray))

        }else{
            Onback=false
            textBackToDashBoard.isEnabled=true
            textBackToDashBoard.setTextColor(resources.getColor(R.color.colorTertiaryText))


        }

//        mVerificationType.add(getString(R.string.email))
//        mVerificationType.add(getString(R.string.phone))


        mBinding.btnVerify.onClick {
            DynamicFormFragment.back_pressed=false
            ElectricListingFragment.onback=false
            DynamicFormFragment.image_upload=null
            PlansZipcodeFragment.gasutility_id=""
            PlansZipcodeFragment.electric_utitlityid=""
            PlansZipcodeFragment.add_enrollementbutton_clicked=false
            GasListingFragment.reward_name=""
            GasListingFragment.gasid=""
            PlansZipcodeFragment.leclient=false
            ElectricListingFragment.electricid=""
            GasListingFragment.selectedid.clear()
            GasListingFragment.selectedvalue=false
            if (mBinding.checkBoxEmail.isChecked || mBinding.checkBoxPhone.isChecked) {
                selfVerificationApiCall()
            } else {
                context.infoDialog(subTitleText = context?.getString(R.string.please_select_at_least_one_method).orEmpty(),
                        showImageError = true)
            }
        }

        mBinding.checkBoxEmail.setOnCheckedChangeListener { buttonView, isChecked ->
            getSelectedCheckBoxValue(isChecked, buttonView)
        }

        mBinding.checkBoxPhone.setOnCheckedChangeListener { buttonView, isChecked ->
            getSelectedCheckBoxValue(isChecked, buttonView)
        }

        mBinding.btnTPVNOW.onClick {
            DynamicFormFragment.back_pressed=false
            ElectricListingFragment.onback=false
            PlansZipcodeFragment.gasutility_id=""
            PlansZipcodeFragment.electric_utitlityid=""
            GasListingFragment.reward_name=""
            GasListingFragment.gasid=""
            ElectricListingFragment.electricid=""
            DynamicFormFragment.image_upload=null

            PlansZipcodeFragment.add_enrollementbutton_clicked=false
            GasListingFragment.selectedid.clear()
            PlansZipcodeFragment.leclient=false
            GasListingFragment.selectedvalue=false
            selectLanguageDialog()
        }

        mBinding.textBackToDashBoard.onClick {
            mViewModel.clearSavedData()
            DynamicFormFragment.back_pressed=false
            DynamicFormFragment.image_upload=null

            ElectricListingFragment.onback=false
            PlansZipcodeFragment.gasutility_id=""
            PlansZipcodeFragment.electric_utitlityid=""
            GasListingFragment.reward_name=""
            GasListingFragment.gasid=""
            ElectricListingFragment.electricid=""
            GasListingFragment.selectedid.clear()
            PlansZipcodeFragment.leclient=false
            GasListingFragment.selectedvalue=false
            PlansZipcodeFragment.add_enrollementbutton_clicked=false
            Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_successFragment_to_dashBoardFragment)
        }
    }

    /**
     * Check if @param isChecked is true then add @param buttonView's text in mVerificationType list
     * Else remove from mVerificationType list
     * Also check if list is empty then verify button should not be enabled
     */
    private fun getSelectedCheckBoxValue(isChecked: Boolean, buttonView: CompoundButton?) {
        if (isChecked) {
            mVerificationType.add(buttonView?.text.toString())


        } else {
            if (mVerificationType.isNotEmpty()) {
                mVerificationType.remove(mVerificationType.find { it == buttonView?.text.toString() })
            }

        }
        if (mVerificationType.isNotEmpty()) {
            mBinding.btnVerify.background = context?.getDrawable(R.drawable.bg_gradient_button)
        } else {
            mBinding.btnVerify.background = context?.getDrawable(R.drawable.bg_button_disable)
        }
    }

    private fun setTPVCallApi(language: String) {
        val liveData = mViewModel.setTPVCallData(
                ScheduleTPVCallRequest(callNow = true,
                        callLang = language,
                        telesaleId = mViewModel.savedLeadResp?.id)
        )
        liveData.observe(this, Observer {
            it?.ifSuccess {
                context?.infoDialog(title = getString(R.string.success),
                        subTitleText = it?.message.orEmpty(),
                        showImageError = false, setOnButtonClickListener =
                {
                    mViewModel.clearSavedData()
                    Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_successFragment_to_dashBoardFragment)

                })

            }
        })

        mBinding.resource = liveData as LiveData<Resource<Any, APIError>>
    }

    private fun selfVerificationApiCall() {

        val liveData = mViewModel.selfVerification(SuccessReq(verificationType = mVerificationType.joinToString(separator = ","), leadId = mViewModel.savedLeadResp?.id))
        liveData.observe(this, Observer {
            it?.ifSuccess {
                mViewModel.clearSavedData()
//                DynamicFormFragment.back_pressed=false
                Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_successFragment_to_dashBoardFragment)
            }
        })

        mBinding.resource = liveData as LiveData<Resource<Any, APIError>>
    }


    private fun selectLanguageDialog() {
        var selectedMethod = AppConstant.EN

        val binding = DataBindingUtil.inflate<DialogSelectVerificationBinding>(LayoutInflater.from(context),
                R.layout.dialog_select_verification, null, false)

        val dialog = context?.let {
            AlertDialog.Builder(it)
                    .setView(binding.root).show()
        }
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.item = DialogText(
                title = getString(R.string.select_language),
                description = "",
                positiveButtonText = getString(R.string.submit),
                negativeButtonText = getString(R.string.cancel))

        binding.btnSms.setText(getString(R.string.english))
        binding.btnVoice.setText(getString(R.string.spanish))

        binding?.btnSms?.onClick {
            selectedMethod = AppConstant.EN



        }

        binding?.btnVoice?.onClick {
            selectedMethod = AppConstant.ES


        }

        binding?.btnCancel?.onClick {
            dialog?.dismiss()

        }
        binding?.btnSubmit?.onClick {
            dialog?.dismiss()
            setTPVCallApi(selectedMethod)
        }

    }
}
