package com.tpv.android.ui.home.dashboard


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.livinglifetechway.k4kotlin.core.onClick
import com.tpv.android.R
import com.tpv.android.databinding.FragmentDashBoardBinding
import com.tpv.android.ui.home.HomeActivity
import com.tpv.android.ui.home.HomeViewModel
import com.tpv.android.utils.LeadStatus
import com.tpv.android.utils.setItemSelection
import com.tpv.android.utils.setupToolbar

/**
 * A simple [Fragment] subclass.
 *
 */
class DashBoardFragment : Fragment() {

    private lateinit var mBinding: FragmentDashBoardBinding
    private lateinit var mHomeViewModel: HomeViewModel
    private lateinit var mNavController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mHomeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_dash_board, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mNavController = Navigation.findNavController(mBinding.root)
        initialize()
    }

    private fun initialize() {

        setupToolbar(mBinding.toolbar, getString(R.string.dashboard), showMenuIcon = true)

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

    override fun onResume() {
        super.onResume()
        setItemSelection(HomeActivity.DASHBOARD)
    }
}
