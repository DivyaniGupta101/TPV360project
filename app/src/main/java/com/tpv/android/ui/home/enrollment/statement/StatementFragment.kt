package com.tpv.android.ui.home.enrollment.statement


import android.graphics.Bitmap
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
import androidx.navigation.Navigation
import com.github.gcacace.signaturepad.views.SignaturePad
import com.livinglifetechway.k4kotlin.core.onClick
import com.tpv.android.R
import com.tpv.android.databinding.DialogSignatureBinding
import com.tpv.android.databinding.FragmentStatementBinding
import com.tpv.android.model.ContractReq
import com.tpv.android.model.DialogText
import com.tpv.android.network.error.AlertErrorHandler
import com.tpv.android.network.resources.Resource
import com.tpv.android.network.resources.apierror.APIError
import com.tpv.android.network.resources.extensions.ifSuccess
import com.tpv.android.ui.home.enrollment.SetEnrollViewModel
import com.tpv.android.utils.*
import com.tpv.android.utils.glide.GlideApp

/**
 * A simple [Fragment] subclass.
 */
class StatementFragment : Fragment() {
    private lateinit var mBinding: FragmentStatementBinding
    private lateinit var mViewModel: SetEnrollViewModel
    private var mSignImage: Bitmap? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_statement, container, false)
        mBinding.lifecycleOwner = this
        activity?.let { mViewModel = ViewModelProviders.of(it).get(SetEnrollViewModel::class.java) }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.errorHandler = AlertErrorHandler(mBinding.root)
        setupToolbar(mBinding.toolbar, getString(R.string.statement), showBackIcon = true)

        mViewModel.saveContract(contractReq = ContractReq(mViewModel.savedLeadDetail?.id))

        mBinding.item = mViewModel.serviceDetail

        mBinding.checkContract.setOnCheckedChangeListener { buttonView, isChecked ->
            setButtonEnable()
        }

        mBinding.btnNext.onClick {
            saveSignatureCall()
        }

        mBinding.imageSign.onClick {
            showSignatureDialog()
        }
    }

    private fun saveSignatureCall() {

        val liveData = context?.BitmapToFile(mSignImage).toMultipartBody("media", "image/jpeg")?.let {
            mViewModel.saveRecording(mViewModel.savedLeadDetail?.id.toRequestBody(),
                    it)
        }

        liveData?.observe(this, Observer {
            it.ifSuccess {
                Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_statementFragment_to_successFragment)
            }
        })

        mBinding.resource = liveData as LiveData<Resource<Any, APIError>>

    }

    private fun setButtonEnable() {
        mBinding.btnNext.isEnabled = if (mBinding.checkContract.isChecked) {
            if (mSignImage != null) true else false
        } else false
    }


    private fun showSignatureDialog() {
        val binding = DataBindingUtil.inflate<DialogSignatureBinding>(layoutInflater, R.layout.dialog_signature, null, false)
        val dialog = context?.let { AlertDialog.Builder(it) }
                ?.setView(binding.root)?.show()

        binding.item = DialogText("", "",
                getString(R.string.save),
                getString(R.string.cancel))

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.signatureView.setOnSignedListener(object : SignaturePad.OnSignedListener {
            override fun onStartSigning() {
                binding.btnSave.isEnabled = true
            }

            override fun onClear() {
            }

            override fun onSigned() {
            }
        })


        if (mSignImage != null) {
            binding.btnSave.isEnabled = true
            binding.signatureView.signatureBitmap = mSignImage
        }
        binding?.textClear?.onClick {
            binding.btnSave.isEnabled = false
            binding.signatureView.clear()
        }

        binding?.btnCancel?.onClick {
            dialog?.dismiss()
        }


        binding?.btnSave?.onClick {
            mSignImage = binding.signatureView.signatureBitmap
            GlideApp.with(context)
                    .asBitmap()
                    .load(mSignImage)
                    .into(mBinding.imageSign)
            dialog?.dismiss()
            setButtonEnable()
        }

    }

}


