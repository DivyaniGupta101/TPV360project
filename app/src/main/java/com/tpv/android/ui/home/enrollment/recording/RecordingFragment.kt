package com.tpv.android.ui.home.enrollment.recording


import android.Manifest
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.livinglifetechway.k4kotlin.core.hide
import com.livinglifetechway.k4kotlin.core.onClick
import com.livinglifetechway.k4kotlin.core.show
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import com.tpv.android.R
import com.tpv.android.databinding.DialogLogoutBinding
import com.tpv.android.databinding.FragmentRecordingBinding
import com.tpv.android.model.DialogText
import com.tpv.android.network.error.AlertErrorHandler
import com.tpv.android.network.resources.Resource
import com.tpv.android.network.resources.apierror.APIError
import com.tpv.android.network.resources.extensions.ifSuccess
import com.tpv.android.ui.home.enrollment.SetEnrollViewModel
import com.tpv.android.utils.navigateSafe
import com.tpv.android.utils.setupToolbar
import com.tpv.android.utils.toMultipartBody
import com.tpv.android.utils.toRequestBody
import java.io.File


/**
 * A simple [Fragment] subclass.
 */
class RecordingFragment : Fragment() {

    private lateinit var mBinding: FragmentRecordingBinding
    private lateinit var mViewModel: RecordingViewModel
    private lateinit var mSetEnrollViewModel: SetEnrollViewModel
    private var myAudioRecorder: MediaRecorder = MediaRecorder()
    private var recordedFile: String? = null
    private var mediaPlayer: MediaPlayer = MediaPlayer()
    var lastPaused = 0L
    var audioTime = 0L
    var isAudioPause: Boolean = false
    var AUDIO_START = 0
    var AUDIO_STOP = 1
    var RECORD_START = 2
    var RECORD_STOP = 3


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_recording, container, false)
        mBinding.lifecycleOwner = this
        mViewModel = ViewModelProviders.of(this).get(RecordingViewModel::class.java)
        activity?.let { mSetEnrollViewModel = ViewModelProviders.of(it).get(SetEnrollViewModel::class.java) }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.errorHandler = AlertErrorHandler(mBinding.root)

        setupToolbar(mBinding.toolbar, getString(R.string.recording), showBackIcon = true, showSkipText = true, skipTextClickListener = {
            if (recordedFile.isNullOrEmpty()) {
                Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_recordingFragment_to_statementFragment)
            } else {
                confirmationDialogForSkip()
            }

        })

        mBinding.btnNext.onClick {
            if (recordedFile.isNullOrEmpty()) {
                Navigation.findNavController(mBinding.root).navigateSafe(com.tpv.android.R.id.action_recordingFragment_to_statementFragment)
            } else {
                saveRecordingCall()
            }

        }


        mBinding.chronometer.setOnChronometerTickListener {
            if (mediaPlayer.isPlaying || isAudioPause) {
                val mCurrentPosition = mediaPlayer.currentPosition / 1000
                mBinding.seekbarAudio.progress = mCurrentPosition + 1
            }
        }


        mBinding.seekbarAudio.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    seekBar?.progress?.let { mediaPlayer.seekTo(it) }
                    mBinding.chronometer.stop()
                    mBinding.chronometer.base -= progress / 1000
                    mBinding.chronometer.start()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        handleImages(RECORD_START)

        mBinding.recordAgainContainer.hide()

        mediaPlayer.setOnCompletionListener {
            mediaPlayer.stop()
            isAudioPause = false
//            mBinding.seekbarAudio.progress = mBinding.seekbarAudio.max
            mBinding.chronometer.stop()
            handleImages(AUDIO_START)
        }

        mBinding.recordStartImage?.onClick {
            handleRecordStart()
        }


        mBinding.recordStopImage?.onClick {
            handleRecordStop()
        }


        mBinding.audioStartImage?.onClick {
            handleAudioStart()
        }

        mBinding.audioStopImage?.onClick {
            handleAudioPause()
        }

        mBinding.recordAgainContainer.onClick {
            confirmationDialogForReRecord()
        }
    }

    private fun saveRecordingCall() {

        val liveData =
                File(recordedFile).toMultipartBody("media", "audio/*")?.let {
                    mViewModel.saveRecording(mSetEnrollViewModel.savedLeadDetail?.id.toRequestBody(),
                            it)
                }


        liveData?.observe(this, Observer {
            it?.ifSuccess {
                Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_recordingFragment_to_statementFragment)
            }
        })

        mBinding.resource = liveData as LiveData<Resource<Any, APIError>>
    }


    /*
   this method will start recording the audio
   */
    private fun handleRecordStart() {
        runWithPermissions(Manifest.permission.RECORD_AUDIO) {


            val folder = File(context?.filesDir?.absolutePath + "/recordings")
            folder.mkdirs()
            recordedFile = folder.absolutePath + "/recording.mp3"
            myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB)
            myAudioRecorder.setOutputFile(recordedFile)


            myAudioRecorder.prepare()
            myAudioRecorder.start()

            mBinding.chronometer.base = SystemClock.elapsedRealtime()
            audioTime = mBinding.chronometer.base
            mBinding.chronometer.start()
            handleImages(RECORD_STOP)

        }
    }


    /*
    this method will stop recoding the audio
     */
    private fun handleRecordStop() {
        myAudioRecorder.stop()
        myAudioRecorder.release()

        mBinding.chronometer.stop()
        audioTime = audioTime - SystemClock.elapsedRealtime()
        mBinding.seekbarAudio.max = Math.abs(audioTime / 1000).toInt()

        handleImages(AUDIO_START)
    }


    /*
    this method start the recorded audio
    isAudioPlayer check if audio was paused by user
    then on click of play it will start from resume else restart the audio
     */
    private fun handleAudioStart() {
        try {

            handleImages(AUDIO_STOP)

            if (isAudioPause) {
                mediaPlayer.start()
                mBinding.chronometer.base = SystemClock.elapsedRealtime() + (mBinding.chronometer.base - lastPaused)
                mBinding.chronometer.start()
            } else {
                mBinding.seekbarAudio.progress = -1
                mediaPlayer.reset()
                mediaPlayer.setDataSource(recordedFile)
                mediaPlayer.prepare()
                mediaPlayer.start()
                mBinding.chronometer.base = SystemClock.elapsedRealtime()
                mBinding.chronometer.start()
            }


        } catch (e: Exception) {
            // make something
        }
    }

    /*
    this method will stop audio which is currently playing
    if audio will resume then set isAudioPause = true
     */
    private fun handleAudioPause() {
        try {
            isAudioPause = true
            mediaPlayer.pause()
            mBinding.chronometer.stop()
            lastPaused = SystemClock.elapsedRealtime()
            handleImages(AUDIO_START)

        } catch (e: Exception) {

        }
    }

    private fun handleImages(state: Int) {
        mBinding.audioStartImage.hide()
        mBinding.audioStopImage.hide()
        mBinding.recordStartImage.hide()
        mBinding.recordStopImage.hide()
        mBinding.recordAgainContainer.hide()
        mBinding.graySpeakerImage.hide()
        mBinding.redSpeakerImage.hide()
        mBinding.seekbarAudio.hide()

        when (state) {
            AUDIO_START -> {
                mBinding.audioStartImage.show()
                mBinding.recordAgainContainer.show()
                mBinding.redSpeakerImage.show()
                mBinding.seekbarAudio.show()
            }
            AUDIO_STOP -> {
                mBinding.audioStopImage.show()
                mBinding.recordAgainContainer.show()
                mBinding.redSpeakerImage.show()
                mBinding.seekbarAudio.show()
            }
            RECORD_START -> {
                mBinding.recordStartImage.show()
                mBinding.recordAgainContainer.show()
                mBinding.graySpeakerImage.show()
            }
            RECORD_STOP -> {
                mBinding.recordStopImage.show()
                mBinding.graySpeakerImage.show()
            }
        }
    }


    private fun confirmationDialogForReRecord() {
        val binding = DataBindingUtil.inflate<DialogLogoutBinding>(layoutInflater, R.layout.dialog_logout, null, false)
        val dialog = context?.let { AlertDialog.Builder(it) }
                ?.setView(binding.root)?.show()

        binding.item = DialogText(getString(R.string.are_you_sure),
                getString(R.string.msg_confirmation_again_record),
                getString(R.string.yes),
                getString(R.string.cancel))

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding?.btnCancel?.onClick {
            dialog?.dismiss()
        }
        binding?.btnYes?.onClick {
            myAudioRecorder = MediaRecorder()
            handleRecordStart()
            dialog?.dismiss()
        }

    }


    private fun confirmationDialogForSkip() {
        val binding = DataBindingUtil.inflate<DialogLogoutBinding>(layoutInflater, R.layout.dialog_logout, null, false)
        val dialog = context?.let { AlertDialog.Builder(it) }
                ?.setView(binding.root)?.show()

        binding.item = DialogText(getString(R.string.are_you_sure),
                getString(R.string.msg_skip),
                getString(R.string.skip_btn),
                getString(R.string.cancel))

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding?.btnCancel?.onClick {
            dialog?.dismiss()
        }
        binding?.btnYes?.onClick {
            Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_recordingFragment_to_statementFragment)
        }

    }
}
