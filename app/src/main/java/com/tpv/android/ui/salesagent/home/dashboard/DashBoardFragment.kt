package com.tpv.android.ui.salesagent.home.dashboard


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.livinglifetechway.k4kotlin.core.hide
import com.livinglifetechway.k4kotlin.core.onClick
import com.livinglifetechway.k4kotlin.core.orFalse
import com.tpv.android.R
import com.tpv.android.databinding.FragmentDashBoardBinding
import com.tpv.android.helper.Pref
import com.tpv.android.model.network.ResponseAppMessaging
import com.tpv.android.network.error.AlertErrorHandler
import com.tpv.android.network.resources.extensions.ifSuccess
import com.tpv.android.utils.enums.MenuItem
import com.tpv.android.utils.navigateSafe
import com.tpv.android.utils.setItemSelection
import com.tpv.android.utils.setupToolbar
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.*

class DashBoardFragment : Fragment() {

    private lateinit var mBinding: FragmentDashBoardBinding
    private lateinit var mNavController: NavController
    private lateinit var mViewModel: DashBoardViewModel


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_dash_board, container, false)
        mBinding.lifecycleOwner = this
        activity?.let { mViewModel = ViewModelProviders.of(it).get(DashBoardViewModel::class.java) }

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mNavController = Navigation.findNavController(mBinding.root)
        getIPAddress(true)
        getmessagevalue(Pref.user?.clientId.toString())
        initialize()
    }
    companion object{
        var sAddr :String=""
    }


    fun getIPAddress(useIPv4: Boolean): String? {
        try {
            val interfaces: List<NetworkInterface> = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (intf in interfaces) {
                val addrs: List<InetAddress> = Collections.list(intf.inetAddresses)
                for (addr in addrs) {
                    if (!addr.isLoopbackAddress()) {
                        sAddr = addr.hostAddress
                        Log.e("ipaddress", sAddr)
                        val isIPv4 = sAddr.indexOf(':') < 0
                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr
                        }
                        if (isIPv4 && !useIPv4) {
                            continue
                        }
                        if (useIPv4 && !isIPv4) {
                            val delim: Int = sAddr.indexOf('%') // drop ip6 zone suffix
                            sAddr = if (delim < 0)sAddr.toUpperCase() else sAddr.substring(0, delim).toUpperCase()
                        }
                    }

                }


            }


        } catch (ignored: Exception) {

        }
        return null
    }
    private fun initialize() {

        setupToolbar(mBinding.toolbar, getString(R.string.dashboard), showMenuIcon = true)

        getDashBoardDetailApiCall()

        mBinding.errorHandler = AlertErrorHandler(mBinding.root)
        mBinding.marqueeMsg.isSelected=true

        mBinding.item = mViewModel.dashBoardCount


        mBinding.includeItemDashboardPending.mainContainer.onClick {
            mBinding.includeItemDashboardPending.item?.let {
                mNavController.navigateSafe(DashBoardFragmentDirections
                        .actionDashBoardFragmentToLeadListingFragment(
                                it))
            }

        }

        mBinding.includeItemDashboardVerified.mainContainer.onClick {
            mBinding.includeItemDashboardVerified.item?.let {
                mNavController.navigateSafe(DashBoardFragmentDirections
                        .actionDashBoardFragmentToLeadListingFragment(
                                it))
            }
        }

        mBinding.includeItemDashboardDeclined.mainContainer.onClick {
            mBinding.includeItemDashboardDeclined.item?.let {
                mNavController.navigateSafe(DashBoardFragmentDirections
                        .actionDashBoardFragmentToLeadListingFragment(
                                it))
            }
        }

        mBinding.includeItemDashboardDisconnected.mainContainer.onClick {
            mBinding.includeItemDashboardDisconnected.item?.let {
                mNavController.navigateSafe(DashBoardFragmentDirections
                        .actionDashBoardFragmentToLeadListingFragment(
                                it))
            }
        }

        mBinding.includeItemDashboardCancelled.mainContainer.onClick {
            mBinding.includeItemDashboardCancelled.item?.let {
                mNavController.navigateSafe(DashBoardFragmentDirections
                        .actionDashBoardFragmentToLeadListingFragment(
                                it))
            }
        }

        mBinding.includeItemDashboardExpired.mainContainer.onClick {
            mBinding.includeItemDashboardExpired.item?.let {
                mNavController.navigateSafe(DashBoardFragmentDirections
                        .actionDashBoardFragmentToLeadListingFragment(
                                it))
            }
        }

        mBinding.includeItemDashboardSelfVerified.mainContainer.onClick {
            mBinding.includeItemDashboardSelfVerified.item?.let {
                mNavController.navigateSafe(DashBoardFragmentDirections
                        .actionDashBoardFragmentToLeadListingFragment(
                                it))
            }
        }

        mBinding.imageEnroll.onClick {
            mNavController.navigateSafe(R.id.action_global_commodityFragment)
        }

    }

    private fun getDashBoardDetailApiCall() {
        context?.let { mViewModel.getDashBoardDetail(it) }
    }


    private fun getmessagevalue(client_id:String){
        val liveData=mViewModel.setmessagevalue(client_id)
        liveData.observe(this, androidx.lifecycle.Observer {
            it.ifSuccess {ResponseAppMessaging ->
                ResponseAppMessaging?.data?.forEach {
                    if(it.message.isEmpty()){
                        mBinding.marqueeMsg.visibility=View.GONE
                    }else{
                        mBinding.marqueeMsg.visibility=View.VISIBLE
                        mBinding.marqueeMsg.setText(it?.message)

                    }

                }
            }

        })
    }


    override fun onResume() {
        super.onResume()
        setItemSelection(MenuItem.DASHBOARD.value)
        getDashBoardDetailApiCall()
    }


}

