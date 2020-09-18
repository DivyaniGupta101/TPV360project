package com.tpv.android.ui.salesagent.home.profile


import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.livinglifetechway.k4kotlin.core.onClick
import com.ravikoradiya.liveadapter.LiveAdapter
import com.tpv.android.BR
import com.tpv.android.R
import com.tpv.android.databinding.DialogTimezoneBinding
import com.tpv.android.databinding.FragmentProfileBinding
import com.tpv.android.databinding.ItemTimezoneBinding
import com.tpv.android.helper.Pref
import com.tpv.android.model.network.TimeZone
import com.tpv.android.model.network.TimeZoneReq
import com.tpv.android.network.error.AlertErrorHandler
import com.tpv.android.network.resources.Resource
import com.tpv.android.network.resources.apierror.APIError
import com.tpv.android.network.resources.extensions.ifSuccess
import com.tpv.android.utils.enums.MenuItem
import com.tpv.android.utils.setItemSelection
import com.tpv.android.utils.setupToolbar
import com.tpv.android.utils.updateProfileInMenu

class ProfileFragment : Fragment() {

    lateinit var mBinding: FragmentProfileBinding
    private var mList: ArrayList<TimeZone> = ArrayList()

    private lateinit var mViewModel: ProfileViewModel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        mBinding.lifecycleOwner = this
        mViewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
    }

    override fun onResume() {
        super.onResume()
        setItemSelection(MenuItem.PROFILE.value)
    }

    private fun initialize() {
        setupToolbar(mBinding.toolbar, getString(R.string.profile), true, true)
        mBinding.item = Pref.user
        mBinding.editTimeZone.onClick {
            getTimeZone()
        }
        getProfileApiCall()
    }

    private fun getTimeZone() {
        val binding = DataBindingUtil.inflate<DialogTimezoneBinding>(LayoutInflater.from(context),
                R.layout.dialog_timezone, null, false)

        val dialog = AlertDialog.Builder(binding.btnCancel.context)
                .setView(binding.root).show()
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        binding.errorHandler = AlertErrorHandler(mBinding.root)
        binding.lifecycleOwner = mBinding.lifecycleOwner

        var selectedTimeZone = ""
        val liveData = mViewModel.getTimeZone()
        liveData.observe(this, Observer {
            it.ifSuccess {
                LiveAdapter(it?.data, BR.item)
                        .map<TimeZone, ItemTimezoneBinding>(R.layout.item_timezone)
                        {
                            onClick { holder ->
                                it?.data?.forEach {
                                    it.selected = it == holder.binding.item
                                }
                                binding.rvTimezone.adapter?.notifyDataSetChanged()
                                selectedTimeZone = holder.binding.item?.timezone.orEmpty()
                            }
                        }.into(binding.rvTimezone)
            }
        })

        binding.resource = liveData as LiveData<Resource<Any, APIError>>

        binding.btnCancel.onClick {
            dialog.hide()
        }
        binding.btnSubmit.onClick {
            updateTimeZone(selectedTimeZone, dialog, binding)
        }
        dialog.show()
    }

    private fun updateTimeZone(selectedTimeZone: String, dialog: AlertDialog, binding: DialogTimezoneBinding) {
        binding.errorHandler = AlertErrorHandler(binding.root)
        binding.lifecycleOwner = viewLifecycleOwner
        val livedata = mViewModel.updateTimeZone(TimeZoneReq(
                timezone = selectedTimeZone
        ))
        livedata.observe(viewLifecycleOwner, Observer {
            it?.ifSuccess {
                dialog.hide()
                getProfileApiCall()
            }
        })

        binding.resource = livedata as LiveData<Resource<Any, APIError>>

    }


    private fun getProfileApiCall() {
        mViewModel.getProfile().observe(viewLifecycleOwner, Observer {
            it.ifSuccess {
                mBinding.item = Pref.user
                updateProfileInMenu()
            }
        })
    }
}
