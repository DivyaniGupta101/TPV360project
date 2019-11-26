package com.tpv.android.ui.home.enrollment.planszipcode


import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
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
import com.tpv.android.databinding.FragmentPlansZipcodeBinding
import com.tpv.android.model.*
import com.tpv.android.network.error.AlertErrorHandler
import com.tpv.android.network.resources.Resource
import com.tpv.android.network.resources.apierror.APIError
import com.tpv.android.network.resources.extensions.ifFailure
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
    private var mZipcodeList = ObservableArrayList<ZipCodeResp>()
    private var mUtilityList = ArrayList<UtilityResp>()
    private lateinit var mSetEnrollViewModel: SetEnrollViewModel
    private var toolbarTitle = ""
    private lateinit var mViewModel: PlansZipcodeViewModel
    private var lastSearchZipCode = ""

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


        //check if utilitiesList list available then show respective in dropdown
        if (mSetEnrollViewModel.utilitiesList.isNotEmpty()) {
            setUtilitySpinners()
        }


        mBinding.btnNext?.onClick {

            mSetEnrollViewModel.utilitiesList.clear()

            //get detail of selected utility and then add in "utilitiesList" for further use.
            if (mBinding.spinnerElectricity.isShown) {
                val electricUtilityResp = mUtilityList.find { it.fullname == mBinding.spinnerElectricity.selectedItem && it.commodity == Plan.ELECTRICFUEL.value }
                electricUtilityResp?.let { mSetEnrollViewModel.utilitiesList.add(it) }
            }
            if (mBinding.spinnerGas.isShown) {
                val gasUtilityResp = mUtilityList.find { it.fullname == mBinding.spinnerGas.selectedItem && it.commodity == Plan.GASFUEL.value }
                gasUtilityResp?.let { mSetEnrollViewModel.utilitiesList.add(it) }
            }

            mSetEnrollViewModel.zipcode = mZipcodeList.find { it.zipcode == mBinding.textZipcode.value }

            hideKeyboard()

            mViewModel.clearZipCodeListData()

            Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_plansZipcodeFragment_to_programsListingFragment)
        }
    }

    /** set toolbar title according to selection of fuel in previous screen.
     * For example, if user select gas fuel then title should be "Natural Gas"
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
            mSetEnrollViewModel.utilitiesList.clear()
            mSetEnrollViewModel.planType = ""
            mSetEnrollViewModel.zipcode = null
            mSetEnrollViewModel.programList.clear()
            mSetEnrollViewModel.customerData = CustomerData()
            mSetEnrollViewModel.savedLeadDetail = null
            mSetEnrollViewModel.recordingFile = ""
            mSetEnrollViewModel.isElectricServiceAddressSame = false
            mSetEnrollViewModel.isGasServiceAddressSame = false
        }
    }


    private fun setAutoCompleterTextView() {
        mBinding.textZipcode.threshold = 1

        mViewModel.zipCodeLiveData.observe(this, Observer { list ->
            mZipcodeList.clear()
            mZipcodeList.addAll(list.orEmpty())
            val autoCompleteAdapter = ArrayAdapter<String>(context, android.R.layout.simple_selectable_list_item, mZipcodeList.map { it.label })
            mBinding.textZipcode.setAdapter(autoCompleteAdapter)
            mBinding.textZipcode.showDropDown()
        })

        mBinding.textZipcode.addTextWatcher { s, start, before, count ->
            mHandler.removeCallbacksAndMessages(null)
            mHandler.postDelayed({
                if (!s.toString().equals(lastSearchZipCode))
                    mViewModel.getZipCode(ZipCodeReq(s.toString()))
            }, TEXT_CHANGE_DELAY)
        }

        mBinding.textZipcode.setOnItemClickListener { parent, view, position, id ->
            lastSearchZipCode = mZipcodeList[position].zipcode.orEmpty()
            mBinding.textZipcode.value = lastSearchZipCode
            mBinding.textZipcode.setSelection(lastSearchZipCode.length)
        }

        mBinding.textZipcode.setOnDismissListener {
            if (mZipcodeList.any { it.zipcode == mBinding.textZipcode.value }) {
                hideKeyboard()
                getUtilityListApiCall(mBinding.textZipcode.value)
            } else {
                hideAllSpinner()
            }
        }
    }

    /**
     * Get Utilities details as per zipcode and selected planType
     */
    private fun getUtilityListApiCall(zipcode: String) {
        val liveData = mViewModel.getUtility(UtilityReq(zipcode = zipcode, commodity = mSetEnrollViewModel.planType))
        liveData.observe(this, Observer {
            it.ifSuccess {
                mUtilityList.clear()
                mUtilityList.addAll(it.orEmpty())
                setUtilitySpinners()
            }

            it.ifFailure { _, _ ->
                hideAllSpinner()
            }
        })

        mBinding.resource = liveData as LiveData<Resource<Any, APIError>>
    }


    /** Show utility spinner according to selection of fuel in previous page.
     * For instance, If user select "Dual Fuel" then gas and electric both spinner will show.
     */

    private fun setUtilitySpinners() {
        when (mSetEnrollViewModel.planType) {
            Plan.DUALFUEL.value -> {
                setGasSpinner()
                setElectricSpinner()
                if (mBinding.spinnerGas.isShown && mBinding.spinnerElectricity.isShown) {
                    mBinding.btnNext.isEnabled = true

                }
            }
            Plan.ELECTRICFUEL.value -> {
                setElectricSpinner()
                if (mBinding.spinnerElectricity.isShown) {
                    mBinding.btnNext.isEnabled = true
                }
            }
            Plan.GASFUEL.value -> {
                setGasSpinner()
                if (mBinding.spinnerGas.isShown) {
                    mBinding.btnNext.isEnabled = true
                }
            }
        }

    }

    /**
     * Find electric utilities From List and then set in spinner
     */

    private fun setElectricSpinner() {
        val listOfElectricUtility = mUtilityList.filter { it.commodity.equals(Plan.ELECTRICFUEL.value) }.map { it.fullname.orEmpty() }
        mBinding.spinnerElectricity.setItems(ArrayList(listOfElectricUtility))
        if (listOfElectricUtility.isNotEmpty()) {
            showElectricSpinner()
        }
    }

    /**
     * Find gas utilities From List and then set in spinner
     */
    private fun setGasSpinner() {
        val listOfGasUtility = mUtilityList.filter { it.commodity.equals(Plan.GASFUEL.value) }.map { it.fullname.orEmpty() }
        mBinding.spinnerGas.setItems(ArrayList(listOfGasUtility))
        if (listOfGasUtility.isNotEmpty()) {
            showGasSpinner()
        }
    }


    /**
     * Show Electric DropDown and Title Text
     */
    private fun showElectricSpinner() {
        mBinding.textElectric.show()
        mBinding.dividerElectric.show()
        mBinding.spinnerElectricity.show()
    }


    /**
     * Show Gas DropDown and Title Text
     */
    private fun showGasSpinner() {
        mBinding.textGas.show()
        mBinding.dividerGas.show()
        mBinding.spinnerGas.show()
    }


    private fun hideAllSpinner() {
        mBinding.textElectric.hide()
        mBinding.textGas.hide()
        mBinding.spinnerElectricity.hide()
        mBinding.spinnerGas.hide()
        mBinding.btnNext.isEnabled = false
    }

}
