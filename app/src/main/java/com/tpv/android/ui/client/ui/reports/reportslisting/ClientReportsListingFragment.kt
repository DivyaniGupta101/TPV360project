package com.tpv.android.ui.client.ui.reports.reportslisting

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import androidx.core.text.bold
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableArrayList
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.livinglifetechway.k4kotlin.core.*
import com.ravikoradiya.liveadapter.LiveAdapter
import com.tpv.android.BR
import com.tpv.android.R
import com.tpv.android.databinding.*
import com.tpv.android.helper.setPagination
import com.tpv.android.model.internal.BottomSheetItem
import com.tpv.android.model.network.ClientReportReq
import com.tpv.android.model.network.ClientReportResp
import com.tpv.android.model.network.ClientsResp
import com.tpv.android.network.error.AlertErrorHandler
import com.tpv.android.network.resources.Resource
import com.tpv.android.network.resources.apierror.APIError
import com.tpv.android.network.resources.extensions.ifSuccess
import com.tpv.android.ui.client.ui.SearchActivity
import com.tpv.android.utils.AppConstant
import com.tpv.android.utils.enums.ClientMenuItem
import com.tpv.android.utils.enums.SortByItem
import com.tpv.android.utils.navigateSafe
import com.tpv.android.utils.setItemSelection
import com.tpv.android.utils.setupToolbar
import com.tpv.android.utils.validation.EmptyValidator
import com.tpv.android.utils.validation.TextInputValidationErrorHandler
import com.tpv.android.utils.validation.Validator
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ClientReportsListingFragment : Fragment() {
    lateinit var mBinding: FragmentClientReportsListingBinding
    lateinit var mViewModel: ClientReportsListingViewModel

    var mClientList: ArrayList<ClientsResp> = ArrayList()
    var mSalesCenterList: ArrayList<ClientsResp> = ArrayList()
    var mListSortBy: ObservableArrayList<BottomSheetItem> = ObservableArrayList()
    var mLastSelectedSortByItem = SortByItem.LEADID.value
    var isSortByAsc: Boolean = false
    var lastSelectedClientPosition = 0
    var searchText: String? = null

    companion object {
        const val REQUEST_CODE = 11
        const val RESULT_CODE = 12
        const val EXTRA_KEY_SEARCH = "search"
        const val EXTRA_KEY_SEARCH_TEXT = "searchText"

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_client_reports_listing, container, false)
        mBinding.lifecycleOwner = this
        mViewModel = ViewModelProviders.of(this).get(ClientReportsListingViewModel::class.java)
        return mBinding.root
    }

    private fun initialize() {

        setupToolbar(mBinding.toolbar, getString(R.string.critical_alert_report), showMenuIcon = true,
                showBackIcon = true
        )

        mBinding.paginatedLayout.textEmpty.text = getString(R.string.no_reports_available)

        setBottomSheetSortOption()
        getClientList()

        mBinding.sortByContainer.onClick {
            handleSortByBottomSheet()
        }

        mBinding.filterContainer.onClick {
            handleFilterBottomSheet()
        }

        mBinding.fabSearch.onClick {
            val intent = Intent(context, SearchActivity::class.java)
            intent.putExtra(EXTRA_KEY_SEARCH_TEXT, searchText)
            startActivityForResult(intent, REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == RESULT_CODE) {
            searchText = data?.getStringExtra(EXTRA_KEY_SEARCH)
            if (!searchText.isNullOrBlank()) {
                mViewModel.clientReportReq?.searchText = searchText
                mViewModel.clearList()
                mViewModel.getReportsList()
                mBinding.containerSearchResultText.show()
                val s = SpannableStringBuilder()
                        .append(getString(R.string.showing_results_for) + " ").bold { append("\"${searchText}\"") }
                mBinding.textSearchResult.text = s
            }
            mBinding.imageClose.onClick {
                searchText = null
                mBinding.containerSearchResultText.hide()
                mViewModel.clientReportReq?.searchText = ""
                mViewModel.clearList()
                mViewModel.getReportsList()
            }
        }
    }

    private fun handleFilterBottomSheet() {
        val binding = DataBindingUtil.inflate<ClientBottomSheetFilterBinding>(layoutInflater, R.layout.client_bottom_sheet_filter, null, false)

        context?.let {

            val dialog = BottomSheetDialog(it)
            dialog.setContentView(binding.root)
            binding.item = getString(R.string.filter)

            binding.includeDateOfSubmissionStartDateLayout.item = getString(R.string.start_date)
            binding.includeDateOfVerificationStartDateLayout.item = getString(R.string.start_date)
            binding.includeDateOfSubmissionEndDateLayout.item = getString(R.string.end_date)
            binding.includeDateOfVerificationEndDateLayout.item = getString(R.string.end_date)
            binding.includeClientLayout.item = getString(R.string.clients)
            binding.includeSalesCenterLayout.item = getString(R.string.sales_centers)


            if (mClientList.isNotEmpty()) {
                val spinnerValueList = arrayListOf("All")
                spinnerValueList.addAll(mClientList.map { it.name.orEmpty() })
                binding.includeClientLayout.spinner.setItems(spinnerValueList as ArrayList<String>?)
                if (mViewModel.clientReportReq?.clientId != "") {
                    val name = mClientList.find { it.id == mViewModel.clientReportReq?.clientId }?.name
                    binding.includeClientLayout.spinner.setSelection(spinnerValueList.indexOf(name))
                }

            }
            if (mSalesCenterList.isNotEmpty()) {
                val spinnerValueList = arrayListOf("All")
                spinnerValueList.addAll(mSalesCenterList.map { it.name.orEmpty() })

                val spinnerAdapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, spinnerValueList)
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.includeSalesCenterLayout.spinner.adapter = spinnerAdapter

                if (mViewModel.clientReportReq?.salescenterId != "") {
                    val name = mSalesCenterList.find { it.id == mViewModel.clientReportReq?.salescenterId }?.name
                    binding.includeSalesCenterLayout.spinner.setSelection(spinnerValueList.indexOf(name))
                }
            }


            val input = SimpleDateFormat(AppConstant.DATEFORMATE1)
            val output = SimpleDateFormat(AppConstant.DATEFORMATE2)
            try {
                binding.includeDateOfSubmissionStartDateLayout.editDatePicker.setText(output.format(input.parse(mViewModel.clientReportReq?.fromDate)))
                binding.includeDateOfSubmissionEndDateLayout.editDatePicker.setText(output.format(input.parse(mViewModel.clientReportReq?.toDate)))
                if (mViewModel.clientReportReq?.verificationFromDate?.isNotEmpty().orFalse() && mViewModel.clientReportReq?.verificationToDate?.isNotEmpty().orFalse()) {
                    binding.includeDateOfVerificationStartDateLayout.editDatePicker.setText(output.format(input.parse(mViewModel.clientReportReq?.verificationFromDate)))
                    binding.includeDateOfVerificationEndDateLayout.editDatePicker.setText(output.format(input.parse(mViewModel.clientReportReq?.verificationToDate)))
                }

            } catch (e: ParseException) {
                e.printStackTrace()
            }
            binding.includeClientLayout.spinner.onItemSelectedListener =
                    object : OnItemSelectedListener {
                        override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View, position: Int, id: Long) {
                            if (lastSelectedClientPosition != position) {
                                lastSelectedClientPosition = position
                                updateSalesCenterInBottomSheet(binding, position)
                            }
                        }

                        override fun onNothingSelected(parentView: AdapterView<*>?) {
                            // your code here
                        }
                    }


            binding?.includeDateOfSubmissionEndDateLayout?.editDatePicker?.onClick {
                openDatePicker(binding.includeDateOfSubmissionEndDateLayout.editDatePicker)
            }
            binding?.includeDateOfSubmissionEndDateLayout?.imageArrowDown?.onClick {
                openDatePicker(binding.includeDateOfSubmissionEndDateLayout.editDatePicker)
            }
            binding?.includeDateOfSubmissionStartDateLayout?.editDatePicker?.onClick {
                openDatePicker(binding.includeDateOfSubmissionStartDateLayout.editDatePicker, true)
            }
            binding?.includeDateOfSubmissionStartDateLayout?.imageArrowDown?.onClick {
                openDatePicker(binding.includeDateOfSubmissionStartDateLayout.editDatePicker, true)
            }
            binding.includeDateOfVerificationStartDateLayout.editDatePicker.onClick {
                openDatePicker(binding.includeDateOfVerificationStartDateLayout.editDatePicker, true)
            }
            binding.includeDateOfVerificationStartDateLayout.imageArrowDown.onClick {
                openDatePicker(binding.includeDateOfVerificationStartDateLayout.editDatePicker, true)
            }
            binding.includeDateOfVerificationEndDateLayout.editDatePicker.onClick {
                openDatePicker(binding.includeDateOfVerificationEndDateLayout.editDatePicker)
            }
            binding.includeDateOfVerificationEndDateLayout.imageArrowDown.onClick {
                openDatePicker(binding.includeDateOfVerificationEndDateLayout.editDatePicker)
            }

            binding.btnApply.onClick()
            {
                if (isValid(binding)) {
                    val output = SimpleDateFormat(AppConstant.DATEFORMATE1)
                    val input = SimpleDateFormat(AppConstant.DATEFORMATE2)
                    var clientId: String? = null
                    var salesCenterId: String? = null

                    if (binding.includeClientLayout.spinner.selectedItemPosition == 0) {
                        clientId = ""
                    } else {
                        clientId = mClientList[binding.includeClientLayout.spinner.selectedItemPosition.minus(1)].id
                    }

                    if (binding.includeSalesCenterLayout.spinner.selectedItemPosition == 0) {
                        salesCenterId = ""
                    } else {
                        salesCenterId = mSalesCenterList[binding.includeSalesCenterLayout.spinner.selectedItemPosition.minus(1)].id
                    }

                    mViewModel.clientReportReq?.also {
                        it.clientId = clientId
                        it.salescenterId = salesCenterId
                        it.fromDate = output.format(input.parse(binding.includeDateOfSubmissionStartDateLayout.editDatePicker.value))
                        it.toDate = output.format(input.parse(binding.includeDateOfSubmissionEndDateLayout.editDatePicker.value))
                        if (binding.includeDateOfVerificationStartDateLayout.editDatePicker.value.isNotEmpty()
                                &&
                                binding.includeDateOfVerificationEndDateLayout.editDatePicker.value.isNotEmpty()) {
                            it.verificationFromDate = output.format(input.parse(binding.includeDateOfVerificationStartDateLayout.editDatePicker.value))
                            it.verificationToDate = output.format(input.parse(binding.includeDateOfVerificationEndDateLayout.editDatePicker.value))
                        }

                    }

                    setRecyclerView()
                    dialog.hide()
                }
            }
            dialog.show()
        }
    }

    private fun updateSalesCenterInBottomSheet(binding: ClientBottomSheetFilterBinding?, position: Int) {

        mSalesCenterList.clear()
        binding?.progressBarContainer?.show()

        val liveData: LiveData<Resource<List<ClientsResp>, APIError>>
        if (position == 0) {
            liveData = mViewModel.getSalesCenter("")
        } else {
            liveData = mViewModel.getSalesCenter(mClientList[position - 1].id)
        }
        liveData.observe(this@ClientReportsListingFragment, Observer {
            it?.ifSuccess { list ->
                mSalesCenterList.addAll(list.orEmpty())

                val spinnerValueList = arrayListOf("All")
                spinnerValueList.addAll(mSalesCenterList.map { it.name.orEmpty() })

                val spinnerAdapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, spinnerValueList)
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding?.includeSalesCenterLayout?.spinner?.adapter = spinnerAdapter
                binding?.progressBarContainer?.hide()
            }
        })


    }


    /**
     * Validate input
     */
    private fun isValid(binding: ClientBottomSheetFilterBinding): Boolean {
        if (binding.includeDateOfVerificationEndDateLayout.editDatePicker.value.isNotEmpty() || binding.includeDateOfVerificationStartDateLayout.editDatePicker.value.isNotEmpty()) {
            if (binding.includeDateOfVerificationEndDateLayout.editDatePicker.value.isEmpty()) {
                return Validator(TextInputValidationErrorHandler()) {

                    addValidate(
                            binding.includeDateOfVerificationEndDateLayout.editDatePicker,
                            EmptyValidator(),
                            getString(R.string.please_select_end_date)
                    )
                }.validate()
            }
            if (binding.includeDateOfVerificationStartDateLayout.editDatePicker.value.isEmpty()) {
                return Validator(TextInputValidationErrorHandler()) {
                    addValidate(
                            binding.includeDateOfVerificationStartDateLayout.editDatePicker,
                            EmptyValidator(),
                            getString(R.string.please_select_start_date)
                    )
                }.validate()
            }
        }
        return true

    }

    private fun openDatePicker(editText: TextInputEditText, isFirstDateOfMonth: Boolean = false) {
        val c = Calendar.getInstance()

        if (isFirstDateOfMonth) {
            c.set(Calendar.DAY_OF_MONTH, 1)

        }
        val mYear = c[Calendar.YEAR] // current year
        val mMonth = c[Calendar.MONTH] // current month
        val mDay = c[Calendar.DAY_OF_MONTH] // current day

        // date picker dialog
        // date picker dialog
        val datePickerDialog = DatePickerDialog(context,
                OnDateSetListener { view, year, monthOfYear, dayOfMonth -> // set day of month , month and year value in the edit text
                    editText.setText(dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year)
                }, mYear, mMonth, mDay)
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun setBottomSheetSortOption() {
        mListSortBy.clear()
//        mListSortBy.add(BottomSheetItem(getString(R.string.lead_id), SortByItem.LEADID.value, false))
        mListSortBy.add(BottomSheetItem(getString(R.string.reference_id), SortByItem.REFERENCEID.value, false))
        mListSortBy.add(BottomSheetItem(getString(R.string.alert_status), SortByItem.ALERTSTATUS.value, false))
        mListSortBy.add(BottomSheetItem(getString(R.string.lead_status), SortByItem.LEADSTATUS.value, false))
        mListSortBy.add(BottomSheetItem(getString(R.string.client_name), SortByItem.CLIENTNAME.value, false))
        mListSortBy.add(BottomSheetItem(getString(R.string.salescenter_name), SortByItem.SALESCENTERNAME.value, false))
        mListSortBy.add(BottomSheetItem(getString(R.string.salescenter_location), SortByItem.SALESCENTERLOCATIONADDRESS.value, false))
        mListSortBy.add(BottomSheetItem(getString(R.string.salesagent_name), SortByItem.SALESAGENTNAME.value, false))
        mListSortBy.add(BottomSheetItem(getString(R.string.date_of_submission), SortByItem.DATEOFSUBMISSION.value, false))
        mListSortBy.add(BottomSheetItem(getString(R.string.date_of_tpv), SortByItem.DATEOFTPV.value, false))
        mListSortBy.add(BottomSheetItem(getString(R.string.salescenter_location_name), SortByItem.SALESCENTERLOCATIONNAME.value, false))

        mListSortBy.forEach {
            if (mLastSelectedSortByItem == it.tag) {
                it.isSelected = true
            }
        }
    }

    private fun handleSortByBottomSheet() {
        val binding = DataBindingUtil.inflate<BottomSheetBinding>(layoutInflater, R.layout.bottom_sheet, null, false)

        context?.let {

            binding.switchSortBy.isChecked = isSortByAsc

            binding.switchSortBy.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
                override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                    // do something, the isChecked will be
                    // true if the switch is in the On position
                    isSortByAsc = isChecked
                }
            })

            val dialog = BottomSheetDialog(it)
            dialog.setContentView(binding.root)
            binding.item = getString(R.string.sort_by)

            mListSortBy.forEach {

                val bindingBottomSheet = DataBindingUtil.inflate<ItemBottomSheetBinding>(layoutInflater,
                        R.layout.item_bottom_sheet,
                        binding.bottomSheetItemContainer,
                        true)

                bindingBottomSheet.item = it
                if (it.tag == mLastSelectedSortByItem) {
                    bindingBottomSheet.radioContainer.isChecked = true
                }

                bindingBottomSheet.radioContainer.onClick {
                    mLastSelectedSortByItem = bindingBottomSheet.item?.tag.toString()
                    mListSortBy.forEach {
                        it?.isSelected = it.tag == bindingBottomSheet.item?.tag
                    }

                    val tempList = bindingBottomSheet.item?.tag?.split("_")

                    mViewModel.clientReportReq?.also {
                        it.sortBy = bindingBottomSheet.item?.tag
                        //  it.sortOrder = tempList?.get(tempList.lastIndex)
                        it.sortOrder = if (binding.switchSortBy.isChecked) "desc" else "asc"
                    }
                    setRecyclerView()
                    dialog.dismiss()
                }

            }

            dialog.show()
        }
    }

    private fun getClientList() {
        mClientList.clear()
        val liveData = mViewModel.getClients()
        liveData.observe(this, Observer {
            it?.ifSuccess { list ->
                mClientList.addAll(list.orEmpty())
                getSalesCenters()
            }
        })
        mBinding.resource = liveData as LiveData<Resource<Any, APIError>>
    }

    private fun getSalesCenters(id: String? = "") {
        mSalesCenterList.clear()
        val liveData = mViewModel.getSalesCenter(id)
        liveData.observe(this, Observer {
            it?.ifSuccess { list ->
                mSalesCenterList.addAll(list.orEmpty())

                if (mViewModel.clientReportReq == null) {
                    val c = Calendar.getInstance()  // this takes current date
                    c.set(Calendar.DAY_OF_MONTH, 1)
                    mViewModel.clientReportReq = ClientReportReq(
                            fromDate = SimpleDateFormat(AppConstant.DATEFORMATE1).format(c.time)
                            , toDate = SimpleDateFormat(AppConstant.DATEFORMATE1).format(Calendar.getInstance().time)
                            , sortBy = mLastSelectedSortByItem
                            , sortOrder = if (isSortByAsc) AppConstant.DESC else AppConstant.ASC
                    )
                }

                setRecyclerView()
            }
        })
        mBinding.resource = liveData as LiveData<Resource<Any, APIError>>
    }


    private fun setRecyclerView() {

        mViewModel.criticalAlertReportsLiveData.observe(this, Observer {
            if (it?.isNotEmpty().orFalse()) {
                mBinding.listReports.scrollToPosition(mViewModel.mLastPosition)
            }
        })

        mViewModel.clearList()
        mBinding.listReports.adapter = null
        mBinding.listReports.clearOnScrollListeners()

        mBinding.paginatedLayout.errorHandler = AlertErrorHandler(mBinding.root)
        mBinding.paginatedLayout.resource = mViewModel.criticalAlertReportsPaginatedResourceLiveData as LiveData<Resource<Any, APIError>>
        mBinding.paginatedLayout.showEmptyView = mViewModel.showEmptyView

        LiveAdapter(mViewModel.criticalAlertReportsLiveData, BR.item)
                .map<ClientReportResp, ItemClientReportsBinding>(R.layout.item_client_reports) {
                    onClick {
                        mViewModel.mLastPosition = it.adapterPosition
                        Navigation.findNavController(mBinding.root).navigateSafe(
                                ClientReportsListingFragmentDirections.actionClientReportsListingFragmentToClientReportsDetailsFragment(it.binding.item?.referenceId.toString())
                        )
                    }
                }
                .into(mBinding.listReports)

        mBinding.listReports.setPagination(mViewModel.criticalAlertReportsPaginatedResourceLiveData) { page ->
            mViewModel.getReportsList(page)
        }
    }

    override fun onResume() {
        super.onResume()
        setItemSelection(ClientMenuItem.REPORTS.value)
    }
}