package com.tpv.android.ui.client.ui.profile

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.filepicker.captureImage
import com.filepicker.pickFile
import com.livinglifetechway.k4kotlin.core.onClick
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import com.tpv.android.R
import com.tpv.android.databinding.FragmentClientProfileBinding
import com.tpv.android.helper.Pref
import com.tpv.android.network.resources.Resource
import com.tpv.android.network.resources.apierror.APIError
import com.tpv.android.network.resources.extensions.ifSuccess
import com.tpv.android.utils.enums.ClientMenuItem
import com.tpv.android.utils.glide.GlideApp
import com.tpv.android.utils.setItemSelection
import com.tpv.android.utils.setupToolbar
import com.tpv.android.utils.toMultipartBody
import com.tpv.android.utils.updateProfileInMenu
import java.io.File

class ClientProfileFragment : Fragment() {

    lateinit var mBinding: FragmentClientProfileBinding
    private lateinit var mViewModel: ClientProfileViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_client_profile, container, false)
        mBinding.lifecycleOwner = this
        mViewModel = ViewModelProviders.of(this).get(ClientProfileViewModel::class.java)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
    }

    private fun initialize() {
        setupToolbar(mBinding.toolbar, getString(R.string.profile), true, true)

        getProfileApiCall()

        mBinding.item = Pref.user

        mBinding.imgCamera.onClick {
            imagePicker()
        }

    }

    private fun imagePicker() {
        android.app.AlertDialog.Builder(context).setItems(arrayOf("Gallery", "Camera")) { _, which ->
            when (which) {
                0 -> {
                    runWithPermissions(Manifest.permission.READ_EXTERNAL_STORAGE) {
                        pickFile {
                            mimeType = arrayOf("image/*")
                            onSuccess {
                                updateProfileImage(it)
                            }
                        }
                    }
                }
                1 -> {
                    runWithPermissions(Manifest.permission.CAMERA) {
                        captureImage {
                            onSuccess {
                                updateProfileImage(it)
                            }
                        }
                    }
                }
            }

        }.create().show()


    }

    private fun updateProfileImage(file: File) {
        val liveData =
                file.toMultipartBody("file", "image/*")?.let {
                    mViewModel.updateProfilePhoto(
                            mediaFile = it)
                }
        liveData?.observe(viewLifecycleOwner, Observer {
            it?.ifSuccess {
                Pref.user = it
                updateProfileInMenu()
                GlideApp.with(this@ClientProfileFragment).load(file)
                        .into(mBinding.imageProfile)
            }
        })

        mBinding.resource = liveData as LiveData<Resource<Any, APIError>>
    }

    private fun getProfileApiCall() {

        val liveData = mViewModel.getProfile()
        liveData.observe(viewLifecycleOwner, Observer {
            it.ifSuccess {
                mBinding.item = Pref.user
                updateProfileInMenu()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        setItemSelection(ClientMenuItem.PROFILE.value)
    }
}