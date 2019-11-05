package com.tpv.android.ui.home.dashboard


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.livinglifetechway.k4kotlin.core.onClick
import com.livinglifetechway.k4kotlin.core.orFalse
import com.tpv.android.R
import com.tpv.android.databinding.FragmentDashBoardBinding
import com.tpv.android.network.error.AlertErrorHandler
import com.tpv.android.network.resources.APIError
import com.tpv.android.network.resources.Resource
import com.tpv.android.network.resources.ifSuccess
import com.tpv.android.ui.home.HomeActivity
import com.tpv.android.utils.LeadStatus
import com.tpv.android.utils.setItemSelection
import com.tpv.android.utils.setupToolbar

/**
 * A simple [Fragment] subclass.
 *
 */
class DashBoardFragment : Fragment() {

    private lateinit var mBinding: FragmentDashBoardBinding
    private lateinit var mNavController: NavController
    private lateinit var mViewModel: DashBoardViewModel


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_dash_board, container, false)
        mBinding.lifecycleOwner = this
        mViewModel = ViewModelProviders.of(this).get(DashBoardViewModel::class.java)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mNavController = Navigation.findNavController(mBinding.root)
        initialize()
    }

    private fun initialize() {

        setupToolbar(mBinding.toolbar, getString(R.string.dashboard), showMenuIcon = true)

        getDashBoardData()

        mBinding.pendingContainer.onClick {
            mNavController.navigate(DashBoardFragmentDirections.actionHomeFragmentToLeadListingFragment(LeadStatus.PENDING.value))
        }

        mBinding.verifiedContainer.onClick {
            mNavController.navigate(DashBoardFragmentDirections.actionHomeFragmentToLeadListingFragment(LeadStatus.VERIFIED.value))
        }

        mBinding.declinendContainer.onClick {
            mNavController.navigate(DashBoardFragmentDirections.actionHomeFragmentToLeadListingFragment(LeadStatus.DECLINED.value))
        }

        mBinding.hangUpContainer.onClick {
            mNavController.navigate(DashBoardFragmentDirections.actionHomeFragmentToLeadListingFragment(LeadStatus.HANGUP.value))
        }

        mBinding.imageEnroll.onClick {
            mNavController.navigate(R.id.action_global_plansListFragment)
        }

    }

    private fun getDashBoardData() {
        mBinding.errorHandler = AlertErrorHandler(mBinding.root)

        val liveData = mViewModel.getDashBoardDetail()
        liveData.observe(viewLifecycleOwner, Observer {
            it.ifSuccess {
                it?.forEach { dashboard ->

                    if (dashboard.status?.equals(LeadStatus.PENDING.value).orFalse()) {
                        mBinding.textPending.setText(dashboard.value.toString())
                    }
                    if (dashboard.status?.equals(LeadStatus.VERIFIED.value).orFalse()) {
                        mBinding.textVerified.setText(dashboard.value.toString())
                    }
                    if (dashboard.status?.equals(LeadStatus.DECLINED.value).orFalse()) {
                        mBinding.textDeclined.setText(dashboard.value.toString())
                    }
                    if (dashboard.status?.equals(LeadStatus.HANGUP.value).orFalse()) {
                        mBinding.textHangUp.setText(dashboard.value.toString())
                    }
                }
            }
        })
        mBinding.resource = liveData as LiveData<Resource<Any, APIError>>

    }

    override fun onResume() {
        super.onResume()
        setItemSelection(HomeActivity.DASHBOARD)
    }
}
