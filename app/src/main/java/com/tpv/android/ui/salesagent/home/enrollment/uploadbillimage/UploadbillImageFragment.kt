package com.tpv.android.ui.salesagent.home.enrollment.uploadbillimage


import android.content.Context
import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isNotEmpty
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.filepicker.captureImage
import com.filepicker.pickFile
import com.livinglifetechway.k4kotlin.core.onClick
import com.livinglifetechway.k4kotlin.core.orFalse
import com.livinglifetechway.k4kotlin.toast
import com.livinglifetechway.k4kotlin.toastNow
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import com.tpv.android.R
import com.tpv.android.databinding.FragmentImageUploadBinding
import com.tpv.android.model.internal.DialogText
import com.tpv.android.network.error.AlertErrorHandler
import com.tpv.android.network.resources.Resource
import com.tpv.android.network.resources.apierror.APIError
import com.tpv.android.network.resources.extensions.ifSuccess
import com.tpv.android.ui.salesagent.home.enrollment.SetEnrollViewModel
import com.tpv.android.utils.*
import com.tpv.android.utils.enums.MenuItem
import com.tpv.android.utils.glide.GlideApp
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.fragment_image_upload.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.util.jar.Manifest


class UploadbillImageFragment : Fragment() {

     lateinit var mBinding: FragmentImageUploadBinding
     lateinit var mSetEnrollViewModel: SetEnrollViewModel
      private var save_image: String=""
      private var savefile_upload:File?=null
      var onclick:Boolean=false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_image_upload, container, false)
        mBinding.lifecycleOwner = this
        activity?.let { mSetEnrollViewModel = ViewModelProviders.of(it).get(SetEnrollViewModel::class.java) }
        return mBinding.root
    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        initialize()
        super.onActivityCreated(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        initialize()
    }

    override fun onPause() {
        initialize()
        super.onPause()
    }



    private fun  initialize() {
        setupToolbar(mBinding.toolbar, getString(R.string.billupload), false, true)
        if (mSetEnrollViewModel.dynamicSettings?.isEnableImageUploadMandatory.orFalse()) {
            mBinding.textSkip.isEnabled=false
        }else{
            mBinding.textSkip.isEnabled=true
        }
        if (mSetEnrollViewModel.upload_imagefile.isNotEmpty()) {
            mBinding.uploadImageConstraint.visibility = View.VISIBLE
            GlideApp.with(this).load(mSetEnrollViewModel.upload_imagefile).into(mBinding.billImage)
            mBinding.overlayImage.visibility = View.VISIBLE
            mBinding.overlayText.visibility = View.VISIBLE
            mBinding.uploadImage.visibility = View.GONE
        } else {
            mBinding.uploadImageConstraint.visibility = View.GONE
            mBinding.uploadImage.visibility = View.VISIBLE
            mBinding.overlayImage.visibility = View.GONE
            mBinding.overlayText.visibility = View.GONE
            mBinding.uploadImage.visibility = View.VISIBLE
        }
        mBinding.checkbox.onClick {
            handleNextButton()
        }

        mBinding.uploadImage.onClick {
            runWithPermissions(android.Manifest.permission.CAMERA) {
                captureImage {
                    onSuccess {
                        Log.e("file",it.path)
                        GlideApp.with(context).load(it.path).into(mBinding.billImage)
                        mBinding.uploadImageConstraint.visibility = View.VISIBLE
                        mBinding.uploadImage.visibility = View.GONE
                        mBinding.overlayImage.visibility = View.VISIBLE
                        mBinding.overlayText.visibility = View.VISIBLE
                        save_image = it.path
                        savefile_upload = it
                        mSetEnrollViewModel.file_uploaded = savefile_upload
                        mSetEnrollViewModel.upload_imagefile = save_image
                        handleNextButton()

                    }
                }
            }

        }



        mBinding.reshotImage.onClick {
            runWithPermissions(android.Manifest.permission.CAMERA) {
                captureImage {
                    onSuccess {
                        GlideApp.with(this@UploadbillImageFragment).load(it.path).into(mBinding.billImage)
                        save_image = it.path
                        savefile_upload = it
                        mSetEnrollViewModel.file_uploaded = savefile_upload
                        mSetEnrollViewModel.upload_imagefile = save_image

                    }
                }


            }
        }


        mBinding.textSkip.onClick {
            if(mSetEnrollViewModel.upload_imagefile.isNotEmpty()){
                context?.actionDialog(DialogText(getString(R.string.are_you_sure),
                        getString(R.string.msg_skip_image),
                        getString(R.string.skip_btn),
                        getString(R.string.cancel)),
                        setOnPositiveBanClickListener = {
                            mSetEnrollViewModel.upload_imagefile=""
                            Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_uploadbilling_fragment_to_signatureVerificationFragment)

                        }
                )
            }else{
                Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_uploadbilling_fragment_to_signatureVerificationFragment)

            }

        }


        mBinding.btnNext.onClick {
            if(mSetEnrollViewModel.upload_imagefile.isNotEmpty()){
                    Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_uploadbilling_fragment_to_signatureVerificationFragment)
                }
            }

        }


    private fun handleNextButton() {
        mBinding.btnNext.isEnabled = (mSetEnrollViewModel.upload_imagefile?.isNotEmpty() && mBinding.checkbox.isChecked)

    }
}
