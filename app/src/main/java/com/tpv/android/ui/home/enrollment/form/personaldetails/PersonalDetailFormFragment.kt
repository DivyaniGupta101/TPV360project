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
import com.livinglifetechway.k4kotlin.core.androidx.hideKeyboard
import com.tpv.android.R
import com.tpv.android.databinding.DialogOtpBinding
import com.tpv.android.databinding.DialogRelationshipBinding
import com.tpv.android.databinding.FragmentPersonalDetailFormBinding
import com.tpv.android.model.DialogText
import com.tpv.android.model.OTPReq
import com.tpv.android.model.VerifyOTPReq
import com.tpv.android.network.error.AlertErrorHandler
import com.tpv.android.network.resources.Resource
import com.tpv.android.network.resources.apierror.APIError
import com.tpv.android.network.resources.extensions.ifSuccess
import com.tpv.android.ui.home.enrollment.SetEnrollViewModel
import com.tpv.android.utils.Plan
import com.tpv.android.utils.navigateSafe
import com.tpv.android.utils.setupToolbar
import com.tpv.android.utils.validation.*

/**
 * A simple [Fragment] subclass.
 */
class PersonalDetailFormFragment : Fragment() {

    private lateinit var mBinding: FragmentPersonalDetailFormBinding
    private lateinit var mSetEnrollViewModel: SetEnrollViewModel
    private lateinit var mViewModel: SetEnrollViewModel
    private var relationShipList: ObservableArrayList<String> = ObservableArrayList()
    private var mLastSelectedItem: Int = 0


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_personal_detail_form, container, false)
        mBinding.lifecycleOwner = this
        activity?.let { mSetEnrollViewModel = ViewModelProviders.of(it).get(SetEnrollViewModel::class.java) }
        mViewModel = ViewModelProviders.of(this).get(SetEnrollViewModel::class.java)
        mBinding.viewModel = mSetEnrollViewModel
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (mViewModel?.relationShipList?.isNotEmpty()) {
            relationShipList.clear()
            relationShipList.addAll(mViewModel.relationShipList)
        } else {
            relationShipList.addAll(arrayListOf(getString(R.string.account_holder), getString(R.string.spouse), getString(R.string.power_of_attorney), getString(R.string.family_member), getString(R.string.other)))
        }


        mBinding.errorHandler = AlertErrorHandler(mBinding.root)
        setupToolbar(mBinding.toolbar, getString(R.string.customer_data), showBackIcon = true)

        mBinding.item = mSetEnrollViewModel.customerData

        mBinding.spinnerRelationShip.adapter = ArrayAdapter<String>(context, android.R.layout.simple_selectable_list_item, relationShipList)
        mBinding.spinnerCountryCode.setItems(arrayListOf("+1"))

        mBinding.spinnerRelationShip.setSelection(relationShipList.indexOf(mSetEnrollViewModel.customerData.relationShip))

        mBinding.textVerify.onClick {
            hideKeyboard()
            if (mBinding.editPhoneNumber.value.isNotEmpty()) {
                generateOTPCall()
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
                mBinding.textVerify.isEnabled = true
                mBinding.textVerify.setText(R.string.verify)
            }
        })
        mBinding.spinnerRelationShip.onItemSelected { parent, view, position, id ->

            if (getString(R.string.other) == relationShipList.get(position.orZero())) {
                showRelationShipDialog()
            } else {
                mLastSelectedItem = position.orZero()
            }
        }


        mBinding.btnNext.onClick {
            hideKeyboard()
            if (isValid()) {
                setValueInViewModel()
                when (mSetEnrollViewModel.planType) {
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

    private fun isValid(): Boolean {
        return Validator(TextInputValidationErrorHandler()) {
            addValidate(
                    mBinding.editAuthorisedFirstName,
                    EmptyValidator(),
                    getString(R.string.enter_authorised_first_name)
            )
            addValidate(
                    mBinding.editAuthorisedMiddleName,
                    EmptyValidator(),
                    getString(R.string.enter_authorised_middle_name)
            )
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

    private fun showRelationShipDialog() {
        val binding = DataBindingUtil.inflate<DialogRelationshipBinding>(layoutInflater, R.layout.dialog_relationship, null, false)
        context?.let { context ->

            val dialog = AlertDialog.Builder(context)
                    .setView(binding.root).show()

            binding.item = DialogText("", "", getString(R.string.submit), getString(R.string.cancel))
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            binding.btnCancel?.onClick {
                mBinding.spinnerRelationShip.setSelection(mLastSelectedItem.orZero())
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
                relationShipList.add(relationShipList.size - 1, binding.editNewRelationship.value)
                mBinding.spinnerRelationShip.adapter = ArrayAdapter<String>(context, android.R.layout.simple_selectable_list_item, relationShipList)
                mBinding.spinnerRelationShip.setSelection(relationShipList.size - 2)
                dialog?.dismiss()

            }

        }

    }

    private fun verifyOTPCall(otp: String, dialog: AlertDialog, binding: DialogOtpBinding) {
        val liveData = mViewModel.verifyOTP(VerifyOTPReq(otp = otp, phonenumber = mBinding.editPhoneNumber.value))

        liveData.observe(this, Observer {
            it.ifSuccess {
                dialog.dismiss()
                mBinding.textVerify.setText(R.string.verified)
                mBinding.textVerify.isEnabled = false
                activity?.hideKeyboard()
            }
        })

        binding.resource = liveData as LiveData<Resource<Any, APIError>>

    }

    private fun generateOTPCall() {
        val liveData = mViewModel.generateOTP(OTPReq(mBinding.editPhoneNumber.value))

        liveData.observe(this, Observer {
            it?.ifSuccess {
                showOTPDialog()
            }
        })

        mBinding.resource = liveData as LiveData<Resource<Any, APIError>>
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
                verifyOTPCall(binding.pinView.value, dialog, binding)
            }

            binding.btnCancel?.onClick {
                dialog.dismiss()
            }

            binding.textResendOTP?.onClick {
                dialog.dismiss()
                generateOTPCall()
            }

        }
    }

    fun setValueInViewModel() {
        mSetEnrollViewModel.customerData.apply {
            if (mSetEnrollViewModel.planType == Plan.DUALFUEL.value) {
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
