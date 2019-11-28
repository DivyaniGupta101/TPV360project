package com.tpv.android.ui.home.newpassword


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.tpv.android.R
import com.tpv.android.databinding.FragmentNewPasswordBinding

class NewPasswordFragment : Fragment() {

    lateinit var mBinding: FragmentNewPasswordBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_password, container, false)
        return mBinding.root
    }


}
