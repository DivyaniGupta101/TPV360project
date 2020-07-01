package com.tpv.android.ui.salesagent.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.livinglifetechway.k4kotlin.core.hide
import com.livinglifetechway.k4kotlin.core.onClick
import com.livinglifetechway.k4kotlin.core.show
import com.livinglifetechway.k4kotlin.core.value
import com.tpv.android.R
import com.tpv.android.databinding.ActivitySettingsBinding
import com.tpv.android.utils.AppConstant

class SettingsActivity : AppCompatActivity() {
    companion object {
        var REQUEST_SETTINGS = 0
    }

    lateinit var mBinding: ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_settings)

        mBinding.switchLocation.isChecked = AppConstant.CURRENT_GEO_LOCATION

        mBinding.toolbar.textToolbarTitle.setText(getText(R.string.setting))
        mBinding.toolbar.imageToolbarBack.show()
        mBinding.toolbar.imageToolbarMenu.hide()
        mBinding.toolbar.textSkip.hide()

        mBinding.toolbar.imageToolbarBack.onClick {
            finish()
        }

        mBinding.btnSubmit.onClick {
            if (mBinding.editLocationRadius.value.isNotEmpty()) {
                AppConstant.GEO_LOCATION_RADIOUS = mBinding.editLocationRadius.value
            }
            AppConstant.CURRENT_GEO_LOCATION = mBinding.switchLocation.isChecked
            finish()
        }

        mBinding.btnCancel.onClick {
            finish()
        }
    }

}
