package com.tpv.android.ui.home.enrollment.planszipcode


import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
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
import com.tpv.android.databinding.LayoutPlanZipcodeSpinnerBinding
import com.tpv.android.helper.OnBackPressCallBack
import com.tpv.android.model.network.UtilityReq
import com.tpv.android.model.network.UtilityResp
import com.tpv.android.model.network.ZipCodeReq
import com.tpv.android.model.network.ZipCodeResp
import com.tpv.android.network.error.AlertErrorHandler
import com.tpv.android.network.resources.Resource
import com.tpv.android.network.resources.apierror.APIError
import com.tpv.android.network.resources.extensions.ifFailure
import com.tpv.android.network.resources.extensions.ifSuccess
import com.tpv.android.ui.home.enrollment.SetEnrollViewModel
import com.tpv.android.utils.infoDialog
import com.tpv.android.utils.navigateSafe
import com.tpv.android.utils.setupToolbar

class PlansZipcodeFragment : Fragment(), OnBackPressCallBack {

    private lateinit var mBinding: FragmentPlansZipcodeBinding
    private var mZipcodeList = ObservableArrayList<ZipCodeResp>()
    private var mUtilityList = ArrayList<UtilityResp>()
    private lateinit var mSetEnrollViewModel: SetEnrollViewModel
    private lateinit var mViewModel: PlansZipcodeViewModel
    private var lastSearchZipCode = ""
    val bindingList: ArrayList<LayoutPlanZipcodeSpinnerBinding> = ArrayList()

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
        initialize()
    }

    override fun handleOnBackPressed(): Boolean {
        mSetEnrollViewModel.clearSavedData()
        return true
    }

    private fun initialize() {
        mBinding.errorHandler = AlertErrorHandler(mBinding.root)
        val toolbarTitle = arguments?.let { PlansZipcodeFragmentArgs.fromBundle(it).item }

        //Set toolbar title as per utility
        setupToolbar(mBinding.toolbar, toolbarTitle.orEmpty(), showBackIcon = true) {
            mSetEnrollViewModel.clearSavedData()
        }

        setAutoCompleterTextView()

        //Check if selectedUtilityList list available then show respective value in dropdown
        if (mSetEnrollViewModel.selectedUtilityList.isNotEmpty()) {
            setUtilitySpinners()
        }

        mBinding.btnNext?.onClick {
            setData()
            hideKeyboard()
            mSetEnrollViewModel.zipcode = mBinding.textZipcode.value
            Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_plansZipcodeFragment_to_programsListingFragment)
        }
    }

    /**
     * Set value in viewModel for further use.
     */
    private fun setData() {

        mSetEnrollViewModel.selectedUtilityList.clear()

        bindingList.forEach { binding ->
            val utilities = mUtilityList.find { it.fullname == binding.spinner.selectedItem && it.commodityId == binding.item?.id }
            utilities?.let { mSetEnrollViewModel.selectedUtilityList.add(it) }
        }
        mViewModel.clearZipCodeListData()
    }

    private fun setAutoCompleterTextView() {
        mBinding.textZipcode.threshold = 1

        /*  val autoCompleteAdapter = ArrayAdapter<String>(context, android.R.layout.simple_selectable_list_item, mZipcodeList.map { it.label })
          mBinding.textZipcode.setAdapter(autoCompleteAdapter)*/

        val adaptor = object : ArrayAdapter<ZipCodeResp>(context, android.R.layout.simple_selectable_list_item, ArrayList<ZipCodeResp>()) {

            override fun getFilter(): Filter {
                return object : Filter() {


                    override fun performFiltering(constraint: CharSequence?): FilterResults {
                        val filterResult = FilterResults()
                        if (constraint.isNullOrBlank()) {
                            filterResult.values = emptyList<String>()
                            filterResult.count = 0
                            return filterResult
                        }

                        val resource = mViewModel.getZipCodeSynchronously(ZipCodeReq(constraint.toString()))
                        resource.ifSuccess {

                            mZipcodeList.clear()
                            mZipcodeList.addAll(it.orEmpty())

                            filterResult.values = it.orEmpty()
                            filterResult.count = it?.size.orZero()
                        }
                        resource.ifFailure { _, _ ->
                            filterResult.values = emptyList<String>()
                            filterResult.count = 0
                        }
                        return filterResult
                    }

                    override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                        clear()
                        results?.values?.let {
                            addAll(it as List<ZipCodeResp>)
                        }
                    }

                }
            }
        }

        mBinding.textZipcode.setAdapter(adaptor)

        //On Click of dropdown, set selected value in editText
        //Also set that value in "lastSearchZipcode"
        mBinding.textZipcode.setOnItemClickListener { parent, view, position, id ->
            lastSearchZipCode = mZipcodeList[position].zipcode.orEmpty()
            mBinding.textZipcode.value = lastSearchZipCode
            mBinding.textZipcode.setSelection(lastSearchZipCode.length)
        }

        //On click of outside of dropdown, get zipcode from editText
        // Also check if that zipcode available in list then set spinner according.
        //Else hide all spinners
        mBinding.textZipcode.setOnDismissListener {
            if (mZipcodeList.any { it.zipcode == mBinding.textZipcode.value }) {
                hideKeyboard()
                getUtilityListApiCall(mBinding.textZipcode.value)
            } else {
                mBinding.spinnerContainer.removeAllViews()
                hideViews()
                mBinding.btnNext.isEnabled = false
            }
        }
    }

    /**
     * Get Utilities details as per zipcode and selected planId
     */
    private fun getUtilityListApiCall(zipcode: String) {
        val liveData = mViewModel.getUtility(UtilityReq(zipcode = zipcode, commodity =
        android.text.TextUtils.join(",", mSetEnrollViewModel.utilityList.map { it.id })))
        liveData.observe(this, Observer {
            it.ifSuccess {
                mUtilityList.clear()
                mUtilityList.addAll(it.orEmpty())
                setUtilitySpinners()
            }
            it.ifFailure { _, _ ->
                hideViews()
            }
        })

        mBinding.resource = liveData as LiveData<Resource<Any, APIError>>
    }

    /**
     * If utility list is empty then show
     */
    private fun showNoUtilityDialog() {
        context?.infoDialog(subTitleText = getString(R.string.msg_no_utilitiy_available))
    }

    /** Show utility spinner according to selection of fuel in previous page.
     * For instance, If user select "Dual Fuel" then gas and electric both spinner will show.
     */
    private fun setUtilitySpinners() {
        bindingList.clear()
        var noUtility = false
        mSetEnrollViewModel.utilityList.forEach { commodity ->

            val binding = DataBindingUtil.inflate<LayoutPlanZipcodeSpinnerBinding>(layoutInflater,
                    R.layout.layout_plan_zipcode_spinner,
                    mBinding.spinnerContainer,
                    true)
            binding.item = commodity

            bindingList.add(binding)

            val spinnerList = mUtilityList.filter { it.commodityId == commodity.id }.map { it.fullname.orEmpty() }
            if (spinnerList.isNotEmpty()) {
                binding.spinner.setItems(ArrayList(spinnerList))
                mBinding.btnNext.isEnabled = true
            } else {

                mBinding.btnNext.isEnabled = false
                bindingList.forEach {
                    it.textTitle.hide()
                    it.spinner.hide()
                    it.divider.hide()
                }
                noUtility = true
            }

        }
        if (noUtility) {
            showNoUtilityDialog()

        }
    }

    /**
     * hide all the spinners and all the label texts and dividers
     */
    private fun hideViews() {
        mBinding.spinnerContainer.removeAllViews()
        mBinding.btnNext.isEnabled = false
    }
}
