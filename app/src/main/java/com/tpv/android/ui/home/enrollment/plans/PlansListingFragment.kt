package com.tpv.android.ui.home.enrollment.plans


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.ravikoradiya.liveadapter.LiveAdapter
import com.tpv.android.BR
import com.tpv.android.R
import com.tpv.android.databinding.FragmentPlansListingBinding
import com.tpv.android.databinding.ItemPlansBinding
import com.tpv.android.model.Plans
import com.tpv.android.ui.home.enrollment.SetEnrollViewModel
import com.tpv.android.utils.Plan
import com.tpv.android.utils.navigateSafe
import com.tpv.android.utils.setupToolbar

/**
 * A simple [Fragment] subclass.
 */
class PlansListingFragment : Fragment() {
    private lateinit var mBinding: FragmentPlansListingBinding
    private lateinit var mViewModel: SetEnrollViewModel
    private var mList: ArrayList<Plans> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_plans_listing, container, false)
        activity?.let { mViewModel = ViewModelProviders.of(it).get(SetEnrollViewModel::class.java) }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar(mBinding.toolbar, getString(R.string.plans), showBackIcon = true, showMenuIcon = true)
        setRecyclerView()
    }

    private fun setRecyclerView() {

        mList.clear()
        mList.add(Plans(context?.getDrawable(R.drawable.ic_fire_gray), getString(R.string.dual_fuel), Plan.DUALFUEL.value))
        mList.add(Plans(context?.getDrawable(R.drawable.ic_idea_gray), getString(R.string.electricity), Plan.ELECTRICFUEL.value))
        mList.add(Plans(context?.getDrawable(R.drawable.ic_natural_gas_gray), getString(R.string.natural_gas), Plan.GASFUEL.value))

        LiveAdapter(mList, BR.item)
                .map<Plans, ItemPlansBinding>(R.layout.item_plans) {
                    onClick { holder ->
                        mViewModel.planType = holder.binding.item?.planType.toString()
                        Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_plansListingFragment_to_plansZipcodeFragment)
                    }
                }
                .into(mBinding.listPlans)
    }

}
