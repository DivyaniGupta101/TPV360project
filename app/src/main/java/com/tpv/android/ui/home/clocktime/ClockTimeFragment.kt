package com.tpv.android.ui.home.clocktime

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProviders
import com.livinglifetechway.k4kotlin.core.*
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import com.tpv.android.R
import com.tpv.android.databinding.FragmentClockTimeBinding
import com.tpv.android.model.network.AgentActivityRequest
import com.tpv.android.network.error.AlertErrorHandler
import com.tpv.android.network.resources.Resource
import com.tpv.android.network.resources.apierror.APIError
import com.tpv.android.network.resources.extensions.ifFailure
import com.tpv.android.network.resources.extensions.ifSuccess
import com.tpv.android.ui.NotificationForegroundService
import com.tpv.android.ui.home.HomeViewModel
import com.tpv.android.ui.home.TransparentActivity
import com.tpv.android.utils.AppConstant
import com.tpv.android.utils.LocationHelper.getLastKnownLocation
import com.tpv.android.utils.LocationHelper.isBestLocation
import com.tpv.android.utils.infoDialog
import com.tpv.android.utils.setupToolbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class ClockTimeFragment : Fragment() {
    lateinit var mBinding: FragmentClockTimeBinding
    private lateinit var mViewModel: HomeViewModel
    private lateinit var mClockTimeViewModel: ClockTimeViewModel
    private var seconds = 0

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)
    private var locationManager: LocationManager? = null

    private var running = false
    private var isPause = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_clock_time, container, false)
        mBinding.lifecycleOwner = this
        activity?.let { mViewModel = ViewModelProviders.of(it).get(HomeViewModel::class.java) }
        mClockTimeViewModel = ViewModelProviders.of(this).get(ClockTimeViewModel::class.java)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar(mBinding.toolbar, title = getString(R.string.time_clock), showBackIcon = true)
        mBinding.errorHandler = AlertErrorHandler(mBinding.root)

        if (mViewModel.location == null) {
            getLocation()
        }

        getCurrentActivity()

        mBinding.imageRefresh.onClick {
            getCurrentActivity()
        }

        mBinding.btnBreak.isEnabled = false
        mBinding.btnCustomerVisit.isEnabled = false

        if (mBinding.btnCustomerVisit.tag == null) {
            mBinding.btnCustomerVisit.tag = AppConstant.ARRIVALIN
        }

        if (mBinding.btnClock.tag == null) {
            mBinding.btnClock.tag = AppConstant.CLOCKIN
        }

        if (mBinding.btnBreak.tag == null) {
            mBinding.btnBreak.tag = AppConstant.BREAKIN
        }

        mBinding.btnCustomerVisit.onClick {
            processButtonClick(this)
        }
        mBinding.btnClock.onClick {
            processButtonClick(this)
        }
        mBinding.btnBreak.onClick {
            processButtonClick(this)
        }

        Timer()
    }

    private fun processButtonClick(buttonView: TextView) {
        buttonView.isClickable = false
        err("1")
        uiScope.launch {
            err("2")
            locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            err("3")
            if (!locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER).orFalse()) {
                err("4")
                context?.infoDialog(subTitleText = getString(R.string.msg_gps_location))
                buttonView.isClickable = true
            } else {
                err("5")
                val liveData = isBestLocation(buttonView.context, getLastKnownLocation(buttonView.context))
                err("6")
                mBinding.resource = liveData as LiveData<Resource<Any, APIError>>
                liveData.observe(this@ClockTimeFragment, androidx.lifecycle.Observer {
                    it?.ifSuccess {
                        err("7")
                        if (it == true) {
                            setTextAndButton(buttonView)
                        } else {
                            context?.infoDialog(subTitleText = getString(R.string.msg_unable_detect_location))
                        }
                        buttonView.isClickable = true
                    }
                    it?.ifFailure { _, _ ->
                        buttonView.isClickable = true
                    }
                })
            }
        }
    }

    /**
     * Get current user's activity from api
     */
    private fun getCurrentActivity() {

        val liveData = mClockTimeViewModel.getCurrentActivity()
        liveData.observe(this, androidx.lifecycle.Observer {
            it?.ifSuccess {
                mBinding.item = it
                val time = Calendar.getInstance().time.format("hh:mm a z")
                mBinding.textLastUpdated.setText("Last updated at: $time")
                setButtonSelectionFromData()
            }
        })
        mBinding.resource = liveData as LiveData<Resource<Any, APIError>>
    }

    /**
     * Get user's activity and handle button selection
     */
    private fun setButtonSelectionFromData() {

        if (mBinding.item?.isClockIn.orFalse() && mBinding.item?.isBreakIn.orFalse()) {
            mBinding.btnClock.tag = AppConstant.CLOCKIN
            mBinding.btnBreak.tag = AppConstant.BREAKIN
            setTextAndButton(mBinding.btnClock, false)
            setTextAndButton(mBinding.btnBreak, false)
        } else if (mBinding.item?.isClockIn.orFalse() && mBinding.item?.isArrivalIn.orFalse()) {
            mBinding.btnClock.tag = AppConstant.CLOCKIN
            mBinding.btnCustomerVisit.tag = AppConstant.ARRIVALIN
            setTextAndButton(mBinding.btnClock, false)
            setTextAndButton(mBinding.btnCustomerVisit, false)
        } else if (mBinding.item?.isClockIn.orFalse()) {
            mBinding.btnClock.tag = AppConstant.CLOCKIN
            setTextAndButton(mBinding.btnClock, false)
        } else if (mBinding.item?.isArrivalIn.orFalse()) {
            mBinding.btnCustomerVisit.tag = AppConstant.ARRIVALIN
            setTextAndButton(mBinding.btnCustomerVisit, false)
        } else if (mBinding.item?.isBreakIn.orFalse()) {
            mBinding.btnBreak.tag = AppConstant.BREAKIN
            setTextAndButton(mBinding.btnBreak, false)
        } else {
            mBinding.btnClock.tag = AppConstant.CLOCKOUT
            setTextAndButton(mBinding.btnClock, false)
        }
    }

    /**
     * Handle button text and background and api call
     */
    private fun setTextAndButton(view: TextView, isApiCall: Boolean = true) {
        if (!isApiCall) {
            setButtonBackground()
            seconds = mBinding.item?.currentTime.orZero()
        }

        when (view.tag) {
            AppConstant.ARRIVALIN -> {
                mBinding.btnCustomerVisit.setText(R.string.customer_visit_departure)
                handleButtonState(AppConstant.ARRIVALIN)
                setTimerAndApiCall(AppConstant.ARRIVALIN, isApiCall)
            }
            AppConstant.ARRIVALOUT -> {
                mBinding.btnCustomerVisit.setText(R.string.customer_visit_arrival)
                handleButtonState(AppConstant.ARRIVALOUT)
                setTimerAndApiCall(AppConstant.ARRIVALOUT, isApiCall)
            }
            AppConstant.CLOCKIN -> {
                mBinding.btnClock.setText(R.string.clock_out)
                handleButtonState(AppConstant.CLOCKIN)
                setTimerAndApiCall(AppConstant.CLOCKIN, isApiCall)
            }
            AppConstant.CLOCKOUT -> {
                mBinding.btnClock.setText(R.string.clock_in)
                mBinding.btnCustomerVisit.setText(getString(R.string.customer_visit_arrival))
                mBinding.btnBreak.setText(getString(R.string.go_on_break))
                handleButtonState(AppConstant.CLOCKOUT)
                setTimerAndApiCall(AppConstant.CLOCKOUT, isApiCall)
            }

            AppConstant.BREAKIN -> {
                mBinding.btnBreak.setText(R.string.back_from_break)
                handleButtonState(AppConstant.BREAKIN)
                setTimerAndApiCall(AppConstant.BREAKIN, isApiCall)
            }
            AppConstant.BREAKOUT -> {
                mBinding.btnBreak.setText(R.string.go_on_break)
                handleButtonState(AppConstant.BREAKOUT)
                setTimerAndApiCall(AppConstant.BREAKOUT, isApiCall)
            }
        }
    }


    /**
     * Handle button disable and enable state
     */
    private fun handleButtonState(state: String) {
        when (state) {
            AppConstant.CLOCKIN -> {
                mBinding.btnBreak.isEnabled = true
                mBinding.btnCustomerVisit.isEnabled = true
                mBinding.btnClock.tag = AppConstant.CLOCKOUT
            }
            AppConstant.CLOCKOUT -> {
                mBinding.btnBreak.isEnabled = false
                mBinding.btnCustomerVisit.isEnabled = false
                mBinding.btnClock.tag = AppConstant.CLOCKIN
            }
            AppConstant.ARRIVALIN -> {
                mBinding.btnCustomerVisit.isEnabled = true
                mBinding.btnBreak.isEnabled = true
                mBinding.btnCustomerVisit.tag = AppConstant.ARRIVALOUT
            }
            AppConstant.ARRIVALOUT -> {
                mBinding.btnCustomerVisit.isEnabled = true
                mBinding.btnBreak.isEnabled = true
                mBinding.btnCustomerVisit.tag = AppConstant.ARRIVALIN
            }
            AppConstant.BREAKOUT -> {
                mBinding.btnCustomerVisit.isEnabled = true
                mBinding.btnBreak.isEnabled = true
                mBinding.btnBreak.tag = AppConstant.BREAKIN
            }
            AppConstant.BREAKIN -> {
                mBinding.btnCustomerVisit.isEnabled = false
                mBinding.btnBreak.isEnabled = true
                mBinding.btnBreak.tag = AppConstant.BREAKOUT
            }
        }

    }

    /**
     * Handle button's background
     */
    private fun setButtonBackground() {
        mBinding.isOnBreak = mBinding.item?.isBreakIn.orFalse()
        mBinding.isClockIn = mBinding.item?.isClockIn.orFalse()
        mBinding.isCustomerVisit = mBinding.item?.isArrivalIn.orFalse()
    }

    /**
     * handle timer and set in textView
     */
    private fun Timer() {
        val handler = Handler()

        handler.post(object : Runnable {
            override fun run() {
                val hours = seconds / 3600
                val minutes = seconds % 3600 / 60
                val secs = seconds % 60


                mBinding.textTimer.text = String.format(Locale.getDefault(),
                        "%d:%02d:%02d", hours,
                        minutes, secs)

                if (running) {
                    seconds++;
                }

                handler.postDelayed(this, 1000)
            }
        })
    }

    /**
     * set timer pause and play
     */
    private fun setTimerAndApiCall(activityType: String, isApiCall: Boolean = true) {
        when (activityType) {
            AppConstant.BREAKIN -> {
                isPause = true
                running = false
                if (isApiCall) {
                    setAgentActivityCall(AppConstant.BREAKIN)
                }
            }
            AppConstant.BREAKOUT -> {
                running = true
                isPause = false
                if (isApiCall) {
                    setAgentActivityCall(AppConstant.BREAKOUT)
                }
            }
            AppConstant.CLOCKIN -> {
                running = true
                isPause = false
                startForeGroundService()
                if (isApiCall) {
                    setAgentActivityCall(AppConstant.CLOCKIN)
                }
            }
            AppConstant.CLOCKOUT -> {
                running = false
                isPause = false
                stopForeGroundService()
                if (isApiCall) {
                    setAgentActivityCall(AppConstant.CLOCKOUT)
                }
            }
            AppConstant.ARRIVALOUT -> {
                running = true
                if (isApiCall) {
                    setAgentActivityCall(AppConstant.ARRIVALOUT)
                }
            }
            AppConstant.ARRIVALIN -> {
                running = true
                if (isApiCall) {
                    setAgentActivityCall(AppConstant.ARRIVALIN)
                }
            }
        }
    }

    private fun stopForeGroundService() {
        val intent = Intent(activity, NotificationForegroundService::class.java)
        intent.action = NotificationForegroundService.STOPACTION
        activity?.startService(intent)
    }

    private fun startForeGroundService() {
        val intent = Intent(activity, NotificationForegroundService::class.java)
        intent.putExtra(NotificationForegroundService.LOCATIONKEY, mViewModel.location)
        activity?.startService(intent)
    }

    /**
     * send current status of user's activity in Api
     */
    private fun setAgentActivityCall(activityType: String) {


        val liveData = mClockTimeViewModel.setAgentActivity(
                AgentActivityRequest(activityType,
                        lat = NotificationForegroundService.location?.latitude.toString(),
                        lng = NotificationForegroundService.location?.longitude.toString()))
        liveData.observe(this, androidx.lifecycle.Observer {
            it?.ifSuccess {
                getCurrentActivity()
            }
        })

        mBinding.resource = liveData as LiveData<Resource<Any, APIError>>

    }


    /**
    //     * Get location using location manager
    //     */
    @SuppressLint("MissingPermission")
    private fun getLocation() = runWithPermissions(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    ) {
        uiScope.launch {
            locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            mViewModel.location = context?.let { getLastKnownLocation(it) }


            if (!locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER).orFalse()) {
                context?.infoDialog(subTitleText = getString(R.string.msg_gps_location))
            } else {
                if (NotificationForegroundService.location == null) {
                    startActivityForResult(Intent(context, TransparentActivity::class.java), TransparentActivity.REQUEST_CHECK_SETTINGS)
                } else {
                    var lastTime = Math.abs(mViewModel.location?.time.orZero() - System.currentTimeMillis())
                    if (lastTime >= 1000) {

                        for (i in 1..3) {
                            lastTime = Math.abs(mViewModel.location?.time.orZero() - System.currentTimeMillis())
                            if (lastTime < 1000) {
                                break
                            } else {
                                startActivityForResult(Intent(context, TransparentActivity::class.java), TransparentActivity.REQUEST_CHECK_SETTINGS)
                                mViewModel.location = context?.let { getLastKnownLocation(it) }
                            }
                        }
                        if (mViewModel.location?.time.orZero() >= 1000) {
                            context?.infoDialog(subTitleText = getString(R.string.msg_unable_detect_location))
                        }
                    }
                }
            }

        }
    }

    override fun onResume() {
        super.onResume()
        getCurrentActivity()
    }

}






