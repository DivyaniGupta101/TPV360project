package com.tpv.android.ui.home.form


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.tpv.android.R

/**
 * A simple [Fragment] subclass.
 */
class CustomerDetailFormOneFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_customer_detail_form_one, container, false)
    }


}
