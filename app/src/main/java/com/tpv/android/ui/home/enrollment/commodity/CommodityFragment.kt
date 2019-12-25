package com.tpv.android.ui.home.enrollment.commodity


import android.os.Bundle
import android.util.Log
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
import com.tpv.android.model.internal.Commodity
import com.tpv.android.model.network.DynamicFormReq
import com.tpv.android.network.error.AlertErrorHandler
import com.tpv.android.network.resources.Resource
import com.tpv.android.network.resources.apierror.APIError
import com.tpv.android.network.resources.extensions.ifSuccess
import com.tpv.android.ui.home.enrollment.SetEnrollViewModel
import com.tpv.android.utils.enums.MenuItem
import com.tpv.android.utils.enums.Plan
import com.tpv.android.utils.navigateSafe
import com.tpv.android.utils.setItemSelection
import com.tpv.android.utils.setupToolbar

class CommodityFragment : Fragment() {
    private lateinit var mBinding: FragmentCommodityBinding
    private lateinit var mViewModel: SetEnrollViewModel
    private lateinit var mViewModelCommodity: CommodityViewModel
    private var mList: ArrayList<Commodity> = ArrayList()

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
        setupToolbar(mBinding.toolbar, getString(R.string.commodity), showBackIcon = true, showMenuIcon = true)
        mBinding.errorHandler = AlertErrorHandler(mBinding.root)
        getCommodityApiCall()
        setRecyclerView()
    }

    private fun getCommodityApiCall() {
        val liveData = mViewModelCommodity.getCommodity()
        liveData.observe(this, Observer {
            it?.ifSuccess {
                Log.d("Commodity Fragment:", "List $it")
            }

        })
        mBinding.resource = liveData as LiveData<Resource<Any, APIError>>
    }

    private fun setRecyclerView() {


        mList.clear()
        mList.add(Commodity(context?.getDrawable(R.drawable.ic_fire_gray), getString(R.string.dual_fuel), Plan.DUALFUEL.value))
        mList.add(Commodity(context?.getDrawable(R.drawable.ic_idea_gray), getString(R.string.electricity), Plan.ELECTRICFUEL.value))
        mList.add(Commodity(context?.getDrawable(R.drawable.ic_natural_gas_gray), getString(R.string.natural_gas), Plan.GASFUEL.value))

        LiveAdapter(mList, BR.item)
                .map<Commodity, ItemCommodityBinding>(R.layout.item_commodity) {
                    onClick { holder ->
                        getDynamicFormApiCall(holder.binding.item?.planType.orEmpty())
                    }
                }
                .into(mBinding.listPlans)
    }

    private fun getDynamicFormApiCall(type: String) {
        val liveData = mViewModel.getDynamicForm(DynamicFormReq(clientid = "102",
                commodity = "Electric", programid = "716"))
        liveData.observe(this, Observer {
            it.ifSuccess {
                mViewModel.planType = type
                Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_commodityFragment_to_plansZipcodeFragment)
            }
        })
        mBinding.resource = liveData as LiveData<Resource<Any, APIError>>
    }

    override fun onResume() {
        super.onResume()
        setItemSelection(MenuItem.ENROLL.value)
    }
}
