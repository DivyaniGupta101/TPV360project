package com.tpv.android.ui.home.enrollment.planszipcode


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
import com.livinglifetechway.k4kotlin.core.show
import com.tpv.android.R
import com.tpv.android.databinding.FragmentPlansZipcodeBinding
import com.tpv.android.model.ZipCodeResp
import com.tpv.android.network.error.AlertErrorHandler
import com.tpv.android.ui.home.enrollment.SetEnrollViewModel
import com.tpv.android.utils.Plan
import com.tpv.android.utils.navigateSafe
import com.tpv.android.utils.setupToolbar


/**
 * A simple [Fragment] subclass.
 */
class PlansZipcodeFragment : Fragment() {
    private lateinit var mBinding: FragmentPlansZipcodeBinding
    private var mZipcodeList = ArrayList<ZipCodeResp>()
    private var mGasList = arrayListOf("Banana", "Apple", "Cherry", "Kiwi", "Mango")
    private var mElectricList = arrayListOf("Banana", "Apple", "Cherry", "Kiwi", "Mango")
    private lateinit var mSetEnrollViewModel: SetEnrollViewModel
    private var toolbarTitle = ""
    private lateinit var mViewModel: PlansZipcodeViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_plans_zipcode, container, false)
        activity?.let { mSetEnrollViewModel = ViewModelProviders.of(it).get(SetEnrollViewModel::class.java) }
        mBinding.lifecycleOwner = this
        mViewModel = ViewModelProviders.of(this).get(PlansZipcodeViewModel::class.java)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.errorHandler = AlertErrorHandler(mBinding.root)

        if (mSetEnrollViewModel.selectedUtility.equals(Plan.GASFUEL.value)) {
            toolbarTitle = getString(R.string.natural_gas)
            setGasUtility()
        } else if (mSetEnrollViewModel.selectedUtility.equals(Plan.ELECTRICFUEL.value)) {
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
//            mSetEnrollViewModel.selectedUtilities = arrayListOf(SelectedUtility(""))
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
//        mBinding.textZipcode.threshold = 1
//
//        mBinding.textZipcode.addTextWatcher { s, start, before, count ->
//            Log.d("TAG", s.toString())
//
//            mViewModel.getZipCode(ZipCodeReq(s.toString())).observe(this, Observer {
//                it.ifSuccess { list ->
//                    mZipcodeList.clear()
//                    mZipcodeList.addAll(list.orEmpty())
//                    val autoCompleteAdapter = ArrayAdapter<String>(context, android.R.layout.simple_selectable_list_item,list?.map { it.label })
//                    mBinding.textZipcode.setAdapter(autoCompleteAdapter)
//
//                    mBinding.textZipcode.setOnFocusChangeListener { v, hasFocus ->
//                        if (hasFocus) {
//                            mBinding.textZipcode.showDropDown()
//                        }
//                    }
//
//
//                    mBinding.textZipcode.setOnItemClickListener { parent, view, position, id ->
//                        mBinding.textZipcode.showDropDown()
//                    }
//
//                }
//            })
//        }


    }


}
