package com.tpv.android.ui.client.ui.reports.reportsdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.livinglifetechway.k4kotlin.core.orFalse
import com.tpv.android.R
import com.tpv.android.databinding.*
import com.tpv.android.network.error.AlertErrorHandler
import com.tpv.android.network.resources.Resource
import com.tpv.android.network.resources.apierror.APIError
import com.tpv.android.network.resources.extensions.ifSuccess
import com.tpv.android.utils.setupToolbar

class ClientReportsDetailsFragment : Fragment() {
    lateinit var mBinding: FragmentClientReportsDetailsBinding
    lateinit var mViewModel: ClientReportsDetailsViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_client_reports_details, container, false)
        mBinding.lifecycleOwner = this
        mViewModel = ViewModelProviders.of(this).get(ClientReportsDetailsViewModel::class.java)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.errorHandler = AlertErrorHandler(mBinding.root)
        setupToolbar(mBinding.toolbar, getString(R.string.lead_details), showMenuIcon = false,
                showBackIcon = true)
        getLeadDetail()
    }

    private fun getLeadDetail() {
        val liveData = mViewModel.getClientLeadDetail()
        liveData.observe(this, Observer {
            it?.ifSuccess {
                val programList = it?.programs.orEmpty()
                val formDetailList = it?.leadDetails.orEmpty()
                mBinding.item = it
                if (programList.isNotEmpty().orFalse()) {
                    programList.forEach {
                        setLabelField(it.commodity + " " + getString(R.string.utility))

                        val binding = DataBindingUtil.inflate<ItemProgramsBinding>(layoutInflater,
                                R.layout.item_programs,
                                mBinding.leadDetailContainer,
                                true)
                        binding.item = it

                    }

                }
                setTimeLine()
            }
        })
        mBinding.resource = liveData as LiveData<Resource<Any, APIError>>
    }

    private fun setTimeLine() {

        val liveData = mViewModel.getClientTimeLine()
        liveData.observe(this, Observer {
            it?.ifSuccess { list ->

                setLabelField(getString(R.string.time_line))
                list?.forEachIndexed { index, clientTimeLineRep ->

                    val binding = DataBindingUtil.inflate<ItemClientTimeLineBinding>(layoutInflater,
                            R.layout.item_client_time_line,
                            mBinding.leadDetailContainer,
                            true)
                    binding.item = clientTimeLineRep

                    if (index != list.size.minus(1)) {
                        setSeparateField()
                    }
                }
            }
        })

        mBinding.resource = liveData as LiveData<Resource<Any, APIError>>
    }

    /**
     * Inflate view for label
     */
    private fun setLabelField(title: String) {
        val binding = DataBindingUtil.inflate<LayoutInputLabelBinding>(layoutInflater,
                R.layout.layout_input_label,
                mBinding.leadDetailContainer,
                true)

        binding.item = title

    }

    /**
     * Inflate view for separate or divider
     */
    private fun setSeparateField() {
        DataBindingUtil.inflate<LayoutOutputSeparateBinding>(layoutInflater,
                R.layout.layout_output_separate,
                mBinding.leadDetailContainer,
                true)
    }

}