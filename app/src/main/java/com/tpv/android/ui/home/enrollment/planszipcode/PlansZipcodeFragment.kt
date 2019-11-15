package com.tpv.android.ui.home.enrollment.planszipcode


import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.livinglifetechway.k4kotlin.core.addTextWatcher
import com.livinglifetechway.k4kotlin.core.androidx.hideKeyboard
import com.livinglifetechway.k4kotlin.core.androidx.toastNow
import com.livinglifetechway.k4kotlin.core.onClick
import com.livinglifetechway.k4kotlin.core.setItems
import com.livinglifetechway.k4kotlin.core.show
import com.tpv.android.R
import com.tpv.android.databinding.FragmentPlansZipcodeBinding
import com.tpv.android.model.UtilityReq
import com.tpv.android.model.UtilityResp
import com.tpv.android.model.ZipCodeReq
import com.tpv.android.model.ZipCodeResp
import com.tpv.android.network.error.AlertErrorHandler
import com.tpv.android.network.resources.Resource
import com.tpv.android.network.resources.apierror.APIError
import com.tpv.android.network.resources.extensions.ifSuccess
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
    private var mUtilityList = ArrayList<UtilityResp>()
    private lateinit var mSetEnrollViewModel: SetEnrollViewModel
    private var toolbarTitle = ""
    private lateinit var mViewModel: PlansZipcodeViewModel

    private val mHandler = Handler()

    companion object {
        /**
         * Wait for at least these seconds to get the text change results
         * This will result in the low api calls when rate limit 1 api call every millis
         * as per the value of this variable
         */
        private const val TEXT_CHANGE_DELAY = 200L
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_plans_zipcode, container, false)
        activity?.let { mSetEnrollViewModel = ViewModelProviders.of(it).get(SetEnrollViewModel::class.java) }
        mBinding.lifecycleOwner = this
        mViewModel = ViewModelProviders.of(this).get(PlansZipcodeViewModel::class.java)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.errorHandler = AlertErrorHandler(mBinding.root)


        //set toolbar title as per utility
        setToolbar()
        setAutoCompleterTextView()

        //check if utilies list already not empty then show respective dropdown
        if (mSetEnrollViewModel.utilities.isNotEmpty()) {
            if (mSetEnrollViewModel.planType.equals(Plan.GASFUEL.value)) {
                setGasUtility()
            }
            if (mSetEnrollViewModel.planType.equals(Plan.ELECTRICFUEL.value)) {
                setElectricUtility()
            }
            if (mSetEnrollViewModel.planType.equals(Plan.DUALFUEL.value)) {
                setGasUtility()
                setElectricUtility()
            }
        }


        mBinding.btnNext?.onClick {
            mSetEnrollViewModel.utilities.clear()

            if (mBinding.spinnerElectricity.isShown) {
                mSetEnrollViewModel.utilities.add(mUtilityList.find { it.fullname == mBinding.spinnerElectricity.selectedItem })
            }
            if (mBinding.spinnerGas.isShown) {
                mSetEnrollViewModel.utilities.add(mUtilityList.find { it.fullname == mBinding.spinnerGas.selectedItem })
            }

            if (isValid()) {
                hideKeyboard()
                Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_plansZipcodeFragment_to_programsListingFragment)
            }
        }

    }

    private fun isValid(): Boolean {

        if (!mBinding.spinnerGas.isShown && !mBinding.spinnerElectricity.isShown) {
            toastNow("Please Select Zipcode")
            return false
        }
        return true
    }

    /* set toolbar title according to selection of fuel in previous screen.
    For example, if user select gas fuel then title should be "Natural Gas"
     */

    private fun setToolbar() {
        if (mSetEnrollViewModel.planType.equals(Plan.GASFUEL.value)) {
            toolbarTitle = getString(R.string.natural_gas)
        } else if (mSetEnrollViewModel.planType.equals(Plan.ELECTRICFUEL.value)) {
            toolbarTitle = getString(R.string.electricity)
        } else {
            toolbarTitle = getString(R.string.dual_fuel)
        }

        setupToolbar(mBinding.toolbar, toolbarTitle, showBackIcon = true) {
            hideKeyboard()
            mSetEnrollViewModel.utilities.clear()
        }
    }


    private fun setAutoCompleterTextView() {
        mBinding.textZipcode.threshold = 1

        mViewModel.zipCodeLiveData.observe(this, Observer { list ->
            mZipcodeList.clear()
            mZipcodeList.addAll(list.orEmpty())
            val autoCompleteAdapter = ArrayAdapter<String>(context, android.R.layout.simple_selectable_list_item, list.map { it.label })
            mBinding.textZipcode.setAdapter(autoCompleteAdapter)
            mBinding.textZipcode.showDropDown()
        })

        mBinding.textZipcode.addTextWatcher { s, start, before, count ->
            mHandler.removeCallbacksAndMessages(null)
            mHandler.postDelayed({
                mBinding.btnNext.isEnabled = false
                mViewModel.getZipCode(ZipCodeReq(s.toString()))
            }, TEXT_CHANGE_DELAY)
        }

        mBinding.textZipcode.setOnItemClickListener { parent, view, position, id ->
            hideKeyboard()
            getUtilityListApiCall()
            mBinding.textZipcode.showDropDown()
        }

    }

    private fun getUtilityListApiCall() {
        val liveData = mViewModel.getUtility(UtilityReq(zipcode = "01007", commodity = mSetEnrollViewModel.planType))
        liveData.observe(this, Observer {
            it.ifSuccess {
                mUtilityList.clear()
                mUtilityList.addAll(it.orEmpty())
                setUtilitySpinners()
            }

        })


        mBinding.resource = liveData as LiveData<Resource<Any, APIError>>
    }

    /*show utility spinner according to selection of fuel in previous page.
    for instance, if user select dual then gas and electric both spinner will show.
     */

    private fun setUtilitySpinners() {
        if (mSetEnrollViewModel.planType.equals(Plan.GASFUEL.value)) {
            setGasUtility()
        }
        if (mSetEnrollViewModel.planType.equals(Plan.ELECTRICFUEL.value)) {
            setElectricUtility()
        }
        if (mSetEnrollViewModel.planType.equals(Plan.DUALFUEL.value)) {
            setGasUtility()
            setElectricUtility()
        }

        mBinding.btnNext.isEnabled = true
    }

    /*set electric utility list in spinner and show spinner*/

    private fun setElectricUtility() {
        val listOfElectricUtility = mUtilityList.filter { it.commodity.equals(Plan.ELECTRICFUEL.value) }.map { it.fullname.orEmpty() }
        mBinding.spinnerElectricity.setItems(ArrayList(listOfElectricUtility))
        mBinding.textElectric.show()
        mBinding.dividerElectric.show()
        mBinding.spinnerElectricity.show()
    }

    //set gas utility list in spinner and show spinner
    private fun setGasUtility() {
        val listOfGasUtility = mUtilityList.filter { it.commodity.equals(Plan.GASFUEL.value) }.map { it.fullname.orEmpty() }
        mBinding.spinnerGas.setItems(ArrayList(listOfGasUtility))
        mBinding.textGas.show()
        mBinding.dividerGas.show()
        mBinding.spinnerGas.show()
    }

}
