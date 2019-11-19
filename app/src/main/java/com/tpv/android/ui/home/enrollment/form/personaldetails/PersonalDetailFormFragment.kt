package com.tpv.android.ui.home.enrollment.form.personaldetails


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.livinglifetechway.k4kotlin.core.androidx.hideKeyboard
import com.livinglifetechway.k4kotlin.core.onClick
import com.livinglifetechway.k4kotlin.core.setItems
import com.livinglifetechway.k4kotlin.core.value
import com.tpv.android.R
import com.tpv.android.databinding.FragmentPersonalDetailFormBinding
import com.tpv.android.ui.home.enrollment.SetEnrollViewModel
import com.tpv.android.utils.Plan
import com.tpv.android.utils.navigateSafe
import com.tpv.android.utils.setupToolbar

/**
 * A simple [Fragment] subclass.
 */
class PersonalDetailFormFragment : Fragment() {

    private lateinit var mBinding: FragmentPersonalDetailFormBinding
    private lateinit var mViewModel: SetEnrollViewModel
    private var relationShipList = arrayListOf("Banana", "Apple", "Cherry", "Kiwi", "Mango")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_personal_detail_form, container, false)
        activity?.let { mViewModel = ViewModelProviders.of(it).get(SetEnrollViewModel::class.java) }
        mBinding.viewModel = mViewModel
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar(mBinding.toolbar, getString(R.string.personal_data), showBackIcon = true)

        mBinding.item = mViewModel.serviceDetail

        mBinding.spinnerRelationShip.setItems(relationShipList)
        mBinding.spinnerCountryCode.setItems(arrayListOf("+1"))

        mBinding.spinnerRelationShip.setSelection(relationShipList.indexOf(mViewModel.serviceDetail.relationShip))


        mBinding.btnNext.onClick {
            hideKeyboard()
            setValueInViewModel()

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

    fun setValueInViewModel() {
        mViewModel.serviceDetail.apply {
            if (mViewModel.planType == Plan.DUALFUEL.value) {
                gasAuthRelationship = mBinding.spinnerRelationShip.selectedItem.toString()
                relationShip = mBinding.spinnerRelationShip.selectedItem.toString()
            } else {
                relationShip = mBinding.spinnerRelationShip.selectedItem.toString()
            }
            authorizedFirstName = mBinding.editAuthorisedFirstName.value
            authorizedMiddleInitial = mBinding.editAuthorisedMiddleName.value
            authorizedLastName = mBinding.editAuthorisedLastName.value
            phoneNumber = mBinding.editPhoneNumber.value
            email = mBinding.editAuthorisedEmail.value
            countryCode = "+1"
        }
    }
}
