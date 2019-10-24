package com.tpv.android.ui.auth.login


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.livinglifetechway.k4kotlin.core.onClick
import com.livinglifetechway.k4kotlin.core.startActivity
import com.tpv.android.R
import com.tpv.android.databinding.FragmentLoginBinding
import com.tpv.android.ui.home.HomeActivity

/**
 * A simple [Fragment] subclass.
 */
class LoginFragment : Fragment() {
    lateinit var mBinding: FragmentLoginBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.btnStart.onClick {
            context.startActivity<HomeActivity>()
            activity?.finish()
        }
    }

}
