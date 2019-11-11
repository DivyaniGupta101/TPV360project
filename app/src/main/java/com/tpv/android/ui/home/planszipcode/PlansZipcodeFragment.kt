package com.tpv.android.ui.home.planszipcode


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.livinglifetechway.k4kotlin.core.androidx.hideKeyboard
import com.livinglifetechway.k4kotlin.core.onClick
import com.livinglifetechway.k4kotlin.core.setItems
import com.livinglifetechway.k4kotlin.core.show
import com.tpv.android.R
import com.tpv.android.databinding.FragmentPlansZipcodeBinding
import com.tpv.android.ui.home.plans.PlanListViewModel
import com.tpv.android.utils.Plan
import com.tpv.android.utils.navigateSafe
import com.tpv.android.utils.setupToolbar


/**
 * A simple [Fragment] subclass.
 */
class PlansZipcodeFragment : Fragment() {
    private lateinit var mBinding: FragmentPlansZipcodeBinding
    private var mZipcodeList = arrayListOf("Banana", "Apple", "Cherry", "Kiwi", "Mango")
    private var mGasList = arrayListOf("Banana", "Apple", "Cherry", "Kiwi", "Mango")
    private var mElectricList = arrayListOf("Banana", "Apple", "Cherry", "Kiwi", "Mango")
    private lateinit var mPlanListViewModel: PlanListViewModel
    private var toolbarTitle = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_plans_zipcode, container, false)
        activity?.let { mPlanListViewModel = ViewModelProviders.of(it).get(PlanListViewModel::class.java) }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (mPlanListViewModel.selectedUtility.equals(Plan.GASFUEL.value)) {
            toolbarTitle = getString(R.string.natural_gas)
            setGasUtility()
        } else if (mPlanListViewModel.selectedUtility.equals(Plan.ELECTRICFUEL.value)) {
            toolbarTitle = getString(R.string.electricity)
            setElectricUtility()

        } else {
            toolbarTitle = getString(R.string.dual_fuel)
            setGasUtility()
            setElectricUtility()
        }

        setupToolbar(mBinding.toolbar, toolbarTitle, showBackIcon = true)
        setAutoCompleterTextView()


        mBinding.btnNext?.onClick {
            hideKeyboard()
            Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_plansZipcodeFragment_to_programsListingFragment)
        }

    }

    private fun setElectricUtility() {
        mBinding.spinnerElectricity.setItems(mGasList)
        mBinding.textElectric.show()
        mBinding.dividerElectric.show()
        mBinding.spinnerElectricity.show()
    }

    private fun setGasUtility() {
        mBinding.spinnerGas.setItems(mElectricList)
        mBinding.textGas.show()
        mBinding.dividerGas.show()
        mBinding.spinnerGas.show()
    }

    private fun setAutoCompleterTextView() {
        mBinding.textZipcode.threshold = 1
        mBinding.textZipcode.setAdapter(context?.let { ArrayAdapter(it, android.R.layout.simple_selectable_list_item, mZipcodeList) })
    }


}
