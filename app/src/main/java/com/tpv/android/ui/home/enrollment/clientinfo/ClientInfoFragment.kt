package com.tpv.android.ui.home.enrollment.clientinfo


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
import com.livinglifetechway.k4kotlin.core.hide
import com.livinglifetechway.k4kotlin.core.onClick
import com.livinglifetechway.k4kotlin.core.show
import com.tpv.android.R
import com.tpv.android.databinding.FragmentClientInfoBinding
import com.tpv.android.databinding.ItemProgramsBinding
import com.tpv.android.helper.Pref
import com.tpv.android.model.SaveLeadsDetailReq
import com.tpv.android.model.SaveLeadsDetailResp
import com.tpv.android.network.error.AlertErrorHandler
import com.tpv.android.network.resources.Resource
import com.tpv.android.network.resources.apierror.APIError
import com.tpv.android.network.resources.extensions.ifSuccess
import com.tpv.android.ui.home.enrollment.SetEnrollViewModel
import com.tpv.android.utils.Plan
import com.tpv.android.utils.navigateSafe
import com.tpv.android.utils.setupToolbar

/**
 * A simple [Fragment] subclass.
 */
class ClientInfoFragment : Fragment() {
    private lateinit var mBinding: FragmentClientInfoBinding
    private lateinit var mSetEnrollViewModel: SetEnrollViewModel
    private var mViewGroup: ViewGroup? = null
    private lateinit var mViewModel: ClientInfoViewModel
    private var mLiveDataResource: LiveData<Resource<SaveLeadsDetailResp?, APIError>>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_client_info, container, false)
        mBinding.lifecycleOwner = this
        activity?.let { mSetEnrollViewModel = ViewModelProviders.of(it).get(SetEnrollViewModel::class.java) }
        mViewModel = ViewModelProviders.of(this).get(ClientInfoViewModel::class.java)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.errorHandler = AlertErrorHandler(mBinding.root)
        mBinding.textElectric.hide()
        mBinding.textGas.hide()


        setupToolbar(mBinding.toolbar, getString(R.string.client_info), showBackIcon = true)

        mBinding.item = mSetEnrollViewModel.serviceDetail

        setProgramInformation()

        mBinding.btnNext.onClick {
            saveLeadDetailCall()
        }

        mBinding.imageEdit.onClick {
            Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_clientInfoFragment_to_personalDetailFormFragment)
        }

    }

    private fun setProgramInformation() {
        mSetEnrollViewModel.programList.forEach { programsResp ->

            when (programsResp.utilityType) {
                Plan.GASFUEL.value -> {
                    mBinding.textGas.show()
                    mViewGroup = mBinding.gasInforamtionContainer
                }
                Plan.ELECTRICFUEL.value -> {
                    mBinding.textElectric.show()
                    mViewGroup = mBinding.electricInforamtionContainer
                }
            }

            val binding = DataBindingUtil.inflate<ItemProgramsBinding>(layoutInflater, R.layout.item_programs, mViewGroup, true)
            binding.mainContainer.background = context?.getDrawable(R.drawable.bg_rectangle_program_border)
            binding.item = programsResp

        }
    }

    private fun saveLeadDetailCall() {

        when (mSetEnrollViewModel.planType) {
            Plan.DUALFUEL.value -> {
                mLiveDataResource = mViewModel.saveLeadDetail(SaveLeadsDetailReq(
                        clientid = Pref.user?.clientId.toString(),
                        commodity = mSetEnrollViewModel.planType,
                        gasutilityId = mSetEnrollViewModel.utilitiesList.find { it?.commodity == Plan.GASFUEL.value }?.utid.toString(),
                        gasprogramid = mSetEnrollViewModel.programList.find { it.utilityType == Plan.GASFUEL.value }?.id,
                        electricutilityId = mSetEnrollViewModel.utilitiesList.find { it?.commodity == Plan.ELECTRICFUEL.value }?.utid.toString(),
                        electricprogramid = mSetEnrollViewModel.programList.find { it.utilityType == Plan.ELECTRICFUEL.value }?.id,
                        fields = arrayListOf(mSetEnrollViewModel.serviceDetail),
                        zipcode = mSetEnrollViewModel.zipcode?.zipcode)
                )
            }
            else -> {
                mLiveDataResource = mViewModel.saveLeadDetail(SaveLeadsDetailReq(
                        clientid = Pref.user?.clientId.toString(),
                        commodity = mSetEnrollViewModel.planType,
                        programId = mSetEnrollViewModel.programList.get(0).id,
                        utilityId = mSetEnrollViewModel.utilitiesList.get(0)?.utid.toString(),
                        zipcode = mSetEnrollViewModel.zipcode?.zipcode,
                        fields = arrayListOf(mSetEnrollViewModel.serviceDetail)))
            }
        }

        mLiveDataResource?.observe(this, Observer {
            it?.ifSuccess {
                Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_clientInfoFragment_to_recordingFragment)
            }
        })

        mBinding.resource = mLiveDataResource as LiveData<Resource<Any, APIError>>
    }

}
