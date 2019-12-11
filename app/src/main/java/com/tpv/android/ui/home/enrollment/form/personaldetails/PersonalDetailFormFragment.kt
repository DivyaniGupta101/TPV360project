package com.tpv.android.ui.home.enrollment.form.personaldetails


import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableArrayList
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.livinglifetechway.k4kotlin.core.*
import com.livinglifetechway.k4kotlin.core.androidx.color
import com.livinglifetechway.k4kotlin.core.androidx.hideKeyboard
import com.tpv.android.R
import com.tpv.android.databinding.DialogOtpBinding
import com.tpv.android.databinding.DialogRelationshipBinding
import com.tpv.android.databinding.FragmentPersonalDetailFormBinding
import com.tpv.android.model.internal.DialogText
import com.tpv.android.model.network.OTPReq
import com.tpv.android.model.network.VerifyOTPReq
import com.tpv.android.network.error.AlertErrorHandler
import com.tpv.android.network.resources.Resource
import com.tpv.android.network.resources.apierror.APIError
import com.tpv.android.network.resources.extensions.ifSuccess
import com.tpv.android.ui.home.enrollment.SetEnrollViewModel
import com.tpv.android.utils.enums.Plan
import com.tpv.android.utils.navigateSafe
import com.tpv.android.utils.setupToolbar
import com.tpv.android.utils.validation.*

class PersonalDetailFormFragment : Fragment() {

    private lateinit var mBinding: FragmentPersonalDetailFormBinding
    private lateinit var mViewModel: SetEnrollViewModel
    private var relationShipList: ObservableArrayList<String> = ObservableArrayList()
    private var mLastSelectedRelationShipPosition: Int = 0
    private var verifiedNumber: String = ""


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_personal_detail_form, container, false)
        mBinding.lifecycleOwner = this
        activity?.let { mViewModel = ViewModelProviders.of(it).get(SetEnrollViewModel::class.java) }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
    }

    private fun initialize() {
        setupToolbar(mBinding.toolbar, getString(R.string.customer_data), showBackIcon = true)

        mBinding.errorHandler = AlertErrorHandler(mBinding.root)

        mBinding.item = mViewModel.customerData
        mBinding.viewModel = mViewModel

        //Check if stored relationShip list is not empty then getStored List value and add in fragment's list
        //Else add value in fragment's list
        if (mViewModel.relationShipList.isNotEmpty()) {
            relationShipList.clear()
            relationShipList.addAll(mViewModel.relationShipList)
        } else {
            relationShipList.addAll(arrayListOf(getString(R.string.account_holder), getString(R.string.spouse), getString(R.string.power_of_attorney), getString(R.string.family_member), getString(R.string.other)))
        }

        mBinding.spinnerRelationShip.adapter = ArrayAdapter<String>(context, android.R.layout.simple_selectable_list_item, relationShipList)

        if (mViewModel.customerData.relationShip?.isNotEmpty().orFalse()) {
            mBinding.spinnerRelationShip.setSelection(relationShipList.indexOf(mViewModel.customerData.relationShip))
        }

        mBinding.spinnerCountryCode.setItems(arrayListOf("+1"))


        mBinding.textVerify.onClick {
            hideKeyboard()
            if (mBinding.editPhoneNumber.value.isNotEmpty()) {
                generateOTPApiCall()
            } else {
                Validator(TextInputValidationErrorHandler()) {
                    addValidate(
                            mBinding.editPhoneNumber,
                            EmptyValidator(),
                            context.getString(R.string.enter_phone_number)
                    )
                    addValidate(
                            mBinding.editPhoneNumber,
                            PhoneNumberValidator(),
                            getString(R.string.enter_valid_phone_number)
                    )
                }.validate()
            }

        }

        mBinding.editPhoneNumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().equals(verifiedNumber)) {
                    handleVerifiedText(false)
                } else {
                    handleVerifiedText(true)
                }
            }
        })

        //Check if item is "other" then show Dialog for add NewRelationShip
        mBinding.spinnerRelationShip.onItemSelected { parent, view, position, id ->

            if (getString(R.string.other) == relationShipList.get(position.orZero())) {
                relationShipDialog()
            } else {
                mLastSelectedRelationShipPosition = position.orZero()
            }
        }


        mBinding.btnNext.onClick {
            hideKeyboard()
            if (isValid()) {
                setValueInViewModel()

                //Check if DuelFuel then next page will be Gas Form screen else Electric Form screen
                when (mViewModel.planType) {
                    Plan.DUALFUEL.value, Plan.GASFUEL.value -> {
                        Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_personalDetailFormFragment_to_gasDetailFormFragment)
                    }
                    Plan.ELECTRICFUEL.value -> {
                        Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_personalDetailFormFragment_to_electricDetailFormFragment)
                    }
                }
            }
        }
    }

    private fun handleVerifiedText(isEditable: Boolean) {
        if (isEditable) {
            mBinding.textVerify.isEnabled = true
            mBinding.textVerify.setText(R.string.verify)
            mBinding.textVerify.setTextColor(context?.color(R.color.colorTertiaryText).orZero())
        } else {
            mBinding.textVerify.isEnabled = false
            mBinding.textVerify.setText(R.string.verified)
            mBinding.textVerify.setTextColor(context?.color(R.color.colorVerifiedText).orZero())
        }
    }

    private fun generateOTPApiCall() {
        val liveData = mViewModel.generateOTP(OTPReq(mBinding.editPhoneNumber.value))
        liveData.observe(this, Observer {
            it?.ifSuccess {
                showOTPDialog()
            }
        })

        mBinding.resource = liveData as LiveData<Resource<Any, APIError>>
    }

    private fun verifyOTPApiCall(otp: String, dialog: AlertDialog, binding: DialogOtpBinding) {
        val liveData = mViewModel.verifyOTP(VerifyOTPReq(otp = otp, phonenumber = mBinding.editPhoneNumber.value))
        liveData.observe(this, Observer {
            it.ifSuccess {
                verifiedNumber = mBinding.editPhoneNumber.value
                dialog.dismiss()
                handleVerifiedText(false)
                activity?.hideKeyboard()
            }
        })

        binding.resource = liveData as LiveData<Resource<Any, APIError>>
    }

    private fun isValid(): Boolean {
        return Validator(TextInputValidationErrorHandler()) {
            addValidate(
                    mBinding.editAuthorisedFirstName,
                    EmptyValidator(),
                    getString(R.string.enter_authorised_first_name)
            )
//            addValidate(
//                    mBinding.editAuthorisedMiddleName,
//                    EmptyValidator(),
//                    getString(R.string.enter_authorised_middle_name)
//            )
            addValidate(
                    mBinding.editAuthorisedLastName,
                    EmptyValidator(),
                    getString(R.string.enter_authorised_last_name)
            )
            addValidate(
                    mBinding.editPhoneNumber,
                    EmptyValidator(),
                    getString(R.string.enter_phone_number)
            )
            addValidate(
                    mBinding.editAuthorisedEmail,
                    EmptyValidator(),
                    getString(R.string.enter_email)
            )
            addValidate(
                    mBinding.editPhoneNumber,
                    PhoneNumberValidator(),
                    getString(R.string.enter_valid_phone_number)
            )
            addValidate(
                    mBinding.editAuthorisedEmail,
                    EmailValidator(),
                    getString(R.string.enter_valid_email)
            )
        }.validate()
    }

    private fun relationShipDialog() {
        val binding = DataBindingUtil.inflate<DialogRelationshipBinding>(layoutInflater, R.layout.dialog_relationship, null, false)
        context?.let { context ->

            val dialog = AlertDialog.Builder(context)
                    .setView(binding.root).show()

            binding.item = DialogText("", "", getString(R.string.submit), getString(R.string.cancel))
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            binding.btnCancel?.onClick {
                //show last selected list's item as selected
                mBinding.spinnerRelationShip.setSelection(mLastSelectedRelationShipPosition.orZero())
                dialog.dismiss()
            }

            binding.editNewRelationship.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    binding.btnSubmit.isEnabled = (binding.editNewRelationship.value.isNotEmpty().orFalse())
                }
            })

            binding.btnSubmit?.onClick {
                //Add new Relation ship in list and set it as selected
                relationShipList.add(relationShipList.size - 1, binding.editNewRelationship.value)
                mBinding.spinnerRelationShip.adapter = ArrayAdapter<String>(context, android.R.layout.simple_selectable_list_item, relationShipList)
                mBinding.spinnerRelationShip.setSelection(relationShipList.size - 2)
                dialog?.dismiss()

            }

        }

    }

    private fun showOTPDialog() {
        val binding = DataBindingUtil.inflate<DialogOtpBinding>(layoutInflater, R.layout.dialog_otp, null, false)

        binding.lifecycleOwner = this
        binding.errorHandler = AlertErrorHandler(binding.root)

        context?.let { context ->
            val dialog = AlertDialog.Builder(context)
                    .setView(binding.root).show()

            binding.item = DialogText(getString(R.string.enter_otp),
                    getString(R.string.resend_otp), getString(R.string.submit), getString(R.string.cancel))

            dialog?.setCanceledOnTouchOutside(false)
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            binding.pinView.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    binding.btnSubmit.isEnabled = (start == 5 && count == 1)
                }
            })

            binding?.btnSubmit?.onClick {
                verifyOTPApiCall(binding.pinView.value, dialog, binding)
            }

            binding.btnCancel?.onClick {
                dialog.dismiss()
            }

            binding.textResendOTP?.onClick {
                dialog.dismiss()
                generateOTPApiCall()
            }
        }
    }

    fun setValueInViewModel() {

        mViewModel.customerData.apply {
            if (mViewModel.planType == Plan.DUALFUEL.value) {
                gasAuthRelationship = mBinding.spinnerRelationShip.selectedItem.toString()
            } else {
                relationShip = mBinding.spinnerRelationShip.selectedItem.toString()
            }
            authorizedFirstName = mBinding.editAuthorisedFirstName.value
            authorizedMiddleInitial = mBinding.editAuthorisedMiddleName.value
            authorizedLastName = mBinding.editAuthorisedLastName.value
            phoneNumber = mBinding.editPhoneNumber.value
            email = mBinding.editAuthorisedEmail.value
            countryCode = mBinding.spinnerCountryCode.selectedItem.toString()
        }

        mViewModel.relationShipList.clear()
        mViewModel.relationShipList.addAll(relationShipList)
    }

}
