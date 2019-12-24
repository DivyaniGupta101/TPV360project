package com.tpv.android.ui.home.leaddetail


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.tpv.android.R
import com.tpv.android.databinding.FragmentLeadDetailBinding
import com.tpv.android.utils.setupToolbar

class LeadDetailFragment : Fragment() {

    private lateinit var mBinding: FragmentLeadDetailBinding
    private lateinit var mViewModel: LeadDetailViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_lead_detail, container, false)
        mBinding.lifecycleOwner = this
        mViewModel = ViewModelProviders.of(this).get(LeadDetailViewModel::class.java)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
    }

    private fun initialize() {

        setupToolbar(mBinding.toolbar, getString(R.string.lead_details), showBackIcon = true)
//        getLeadDetailApiCall()
    }

//    private fun getLeadDetailApiCall() {
//        val liveData = mViewModel.getLeadDetail(arguments?.let { LeadDetailFragmentArgs.fromBundle(it) }?.item)
//        liveData.observe(this, Observer {
//            it?.data?.forEach {
//                val binding = DataBindingUtil.inflate<ItemLeadDetailBinding>(layoutInflater, R.layout.item_lead_detail, mBinding.leadDetailContainer, true)
//                binding.textTitle.setText(it.key)
//                binding.editTitle.value = it.value.orEmpty()
//            }
//
//        })
//
//        mBinding.resource = liveData as LiveData<Resource<Any, APIError>>
//    }

}
