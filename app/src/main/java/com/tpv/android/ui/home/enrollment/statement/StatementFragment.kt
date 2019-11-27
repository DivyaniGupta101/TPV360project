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
import com.tpv.android.helper.Pref
import com.tpv.android.model.ContractReq
import com.tpv.android.model.DialogText
import com.tpv.android.model.SaveLeadsDetailReq
import com.tpv.android.model.SaveLeadsDetailResp
import com.tpv.android.network.error.AlertErrorHandler
import com.tpv.android.network.resources.Resource
import com.tpv.android.network.resources.apierror.APIError
import com.tpv.android.network.resources.extensions.ifSuccess
import com.tpv.android.ui.home.enrollment.SetEnrollViewModel
import com.tpv.android.utils.*
import com.tpv.android.utils.glide.GlideApp
import java.io.File

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


        mBinding.item = mViewModel.customerData

        mBinding.checkContract.setOnCheckedChangeListener { buttonView, isChecked ->
            setButtonEnable()
        }

        mBinding.btnNext.onClick {
            saveCustomerData()
        }

        mBinding.imageSign.onClick {
            showSignatureDialog()
        }
    }

    private fun saveCustomerData() {
        var liveData: LiveData<Resource<SaveLeadsDetailResp?, APIError>>? = null
        when (mViewModel.planType) {
            Plan.DUALFUEL.value -> {
                liveData = mViewModel.saveLeadDetail(SaveLeadsDetailReq(
                        clientid = Pref.user?.clientId.toString(),
                        commodity = mViewModel.planType,
                        gasutilityId = mViewModel.utilitiesList.find { it?.commodity == Plan.GASFUEL.value }?.utid.toString(),
                        gasprogramid = mViewModel.programList.find { it.utilityType == Plan.GASFUEL.value }?.id,
                        electricutilityId = mViewModel.utilitiesList.find { it?.commodity == Plan.ELECTRICFUEL.value }?.utid.toString(),
                        electricprogramid = mViewModel.programList.find { it.utilityType == Plan.ELECTRICFUEL.value }?.id,
                        fields = arrayListOf(mViewModel.customerData),
                        zipcode = mViewModel.zipcode?.zipcode)
                )
            }
            else -> {
                liveData = mViewModel.saveLeadDetail(SaveLeadsDetailReq(
                        clientid = Pref.user?.clientId.toString(),
                        commodity = mViewModel.planType,
                        programId = mViewModel.programList.get(0).id,
                        utilityId = mViewModel.utilitiesList.get(0)?.utid.toString(),
                        zipcode = mViewModel.zipcode?.zipcode,
                        fields = arrayListOf(mViewModel.customerData)))
            }
        }

        liveData.observe(this, Observer {
            it?.ifSuccess {
                mViewModel.savedLeadDetail = it
                mViewModel.saveContract(contractReq = ContractReq(mViewModel.savedLeadDetail?.id))
                if (mViewModel.recordingFile.isNotEmpty()) {
                    saveRecording()
                } else {
                    saveSignatureCall()
                }
            }
        })

        mBinding.resource = liveData as LiveData<Resource<Any, APIError>>
    }

    private fun saveRecording() {
        val liveData =
                File(mViewModel.recordingFile).toMultipartBody("media", "audio/*")?.let {
                    mViewModel.saveMedia(mViewModel.savedLeadDetail?.id.toRequestBody(),
                            it)
                }


        liveData?.observe(this, Observer {
            it?.ifSuccess {
                saveSignatureCall()
            }
        })

        mBinding.resource = liveData as LiveData<Resource<Any, APIError>>
    }

    private fun saveSignatureCall() {

        val liveData = context?.BitmapToFile(mSignImage).toMultipartBody("media", "image/jpeg")?.let {
            mViewModel.saveMedia(mViewModel.savedLeadDetail?.id.toRequestBody(),
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


//        if (mSignImage != null) {
//            binding.btnSave.isEnabled = true
//            binding.signatureView.signatureBitmap = mSignImage
//        }
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


