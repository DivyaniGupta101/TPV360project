package com.tpv.android.ui.home.profile


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tpv.android.R
import com.tpv.android.ui.home.HomeActivity
import com.tpv.android.utils.setItemSelection

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }


    override fun onResume() {
        super.onResume()
        setItemSelection(HomeActivity.PROFILE)
    }
}
