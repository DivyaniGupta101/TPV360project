package com.tpv.android.ui.salesagent.home.enrollment.commodity


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.ravikoradiya.liveadapter.LiveAdapter
import com.tpv.android.BR
import com.tpv.android.R
import com.tpv.android.databinding.FragmentCommodityBinding
import com.tpv.android.databinding.ItemCommodityBinding
import com.tpv.android.model.network.CommodityResp
import com.tpv.android.model.network.DynamicFormReq
import com.tpv.android.network.error.AlertErrorHandler
import com.tpv.android.network.resources.Resource
import com.tpv.android.network.resources.apierror.APIError
import com.tpv.android.network.resources.extensions.ifSuccess
import com.tpv.android.ui.salesagent.home.enrollment.SetEnrollViewModel
import com.tpv.android.utils.enums.MenuItem
import com.tpv.android.utils.navigateSafe
import com.tpv.android.utils.setItemSelection
import com.tpv.android.utils.setupToolbar

class CommodityFragment : Fragment() {
    private lateinit var mBinding: FragmentCommodityBinding
    private lateinit var mViewModel: SetEnrollViewModel
    private lateinit var mViewModelCommodity: CommodityViewModel
    private var mList: ArrayList<CommodityResp> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_commodity, container, false)
        activity?.let { mViewModel = ViewModelProviders.of(it).get(SetEnrollViewModel::class.java) }
        mViewModelCommodity = ViewModelProviders.of(this).get(CommodityViewModel::class.java)
        mBinding.lifecycleOwner = this
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
    }

    private fun initialize() {
        setupToolbar(mBinding.toolbar, getString(R.string.lead_enroll), showBackIcon = true, showMenuIcon = true)
        mBinding.errorHandler = AlertErrorHandler(mBinding.root)
        getCommodityApiCall()
        setRecyclerView()
    }

    /**
     * Get list of commodity
     */
    private fun getCommodityApiCall() {
        val liveData = mViewModelCommodity.getCommodity()
        liveData.observe(this, Observer {
            it?.ifSuccess {
                mList.clear()
                mList.addAll(it.orEmpty())
                setRecyclerView()
            }

        })
        mBinding.resource = liveData as LiveData<Resource<Any, APIError>>
    }

    private fun setRecyclerView() {
        LiveAdapter(mList, BR.item)
                .map<CommodityResp, ItemCommodityBinding>(R.layout.item_commodity) {
                    onClick { holder ->
                        getDynamicFormApiCall(holder.binding.item?.id.toString(), holder.binding.item?.formname)
                    }
                }
                .into(mBinding.listPlans)
    }

    /**
     * Get dynamic form
     */
    private fun getDynamicFormApiCall(id: String, title: String?) {
        val liveData = mViewModel.getDynamicForm(DynamicFormReq(formId = id))
        liveData.observe(this, Observer {
            it.ifSuccess {
                mViewModel.utilityList.addAll(mList.find { it.id.toString() == id }?.commodities.orEmpty())
                mViewModel.planId = id
                Navigation.findNavController(mBinding.root).navigateSafe(CommodityFragmentDirections.actionCommodityFragmentToPlansZipcodeFragment(title.orEmpty()))
            }
        })
        mBinding.resource = liveData as LiveData<Resource<Any, APIError>>
    }

    override fun onResume() {
        super.onResume()
        setItemSelection(MenuItem.ENROLL.value)
    }
}
