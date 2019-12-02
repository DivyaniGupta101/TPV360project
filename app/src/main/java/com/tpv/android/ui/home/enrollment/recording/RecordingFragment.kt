package com.tpv.android.ui.home.enrollment.recording


import android.Manifest
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.livinglifetechway.k4kotlin.core.hide
import com.livinglifetechway.k4kotlin.core.onClick
import com.livinglifetechway.k4kotlin.core.orFalse
import com.livinglifetechway.k4kotlin.core.show
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import com.tpv.android.R
import com.tpv.android.databinding.DialogLogoutBinding
import com.tpv.android.databinding.FragmentRecordingBinding
import com.tpv.android.model.DialogText
import com.tpv.android.ui.home.enrollment.SetEnrollViewModel
import com.tpv.android.utils.audio.AudioDataReceivedListener
import com.tpv.android.utils.audio.RecordingThread
import com.tpv.android.utils.navigateSafe
import com.tpv.android.utils.setupToolbar
import java.io.File

class RecordingFragment : Fragment() {

    private lateinit var mBinding: FragmentRecordingBinding
    private lateinit var mViewModel: SetEnrollViewModel
    private var mRecordingThread: RecordingThread? = null
    private var recordedFile: String? = null
    private var mediaPlayer: MediaPlayer = MediaPlayer()
    var isAudioPause: Boolean = false
    var AUDIO_START = 0
    var AUDIO_STOP = 1
    var RECORD_START = 2
    var RECORD_STOP = 3


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_recording, container, false)
        activity?.let { mViewModel = ViewModelProviders.of(it).get(SetEnrollViewModel::class.java) }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.item = mViewModel.customerData

        setupToolbar(mBinding.toolbar, getString(R.string.recording), showBackIcon = true, backIconClickListener = {
            if (recordedFile?.isNotEmpty().orFalse()) {
                mediaPlayer.stop()
            }
        })


        mBinding.textSkip.onClick {
            if (recordedFile.isNullOrEmpty()) {
                Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_recordingFragment_to_statementFragment)
            } else {
                confirmationDialogForSkip()
            }
        }

        mBinding.btnNext.onClick {
            if (recordedFile?.isNotEmpty().orFalse()) {
                mediaPlayer.stop()
                mViewModel.recordingFile = recordedFile.orEmpty()
            }
            Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_recordingFragment_to_statementFragment)
        }

        mBinding.chronometer.setOnChronometerTickListener {
            if (mediaPlayer.isPlaying || isAudioPause) {
                mBinding.seekbarAudio.progress = mediaPlayer.currentPosition
            }
        }


        mBinding.seekbarAudio.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                mBinding.chronometer.stop()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.progress?.let {
                    mediaPlayer.seekTo(it)
                    mBinding.chronometer.base = SystemClock.elapsedRealtime() - it
                    if (mediaPlayer.isPlaying)
                        mBinding.chronometer.start()
                }
            }
        })

        if (mViewModel.recordingFile.isNotEmpty()) {
            recordedFile = mViewModel.recordingFile
            handleImages(AUDIO_START)
        } else {
            handleImages(RECORD_START)
        }



        mediaPlayer.setOnCompletionListener {
            mediaPlayer.stop()
            mediaPlayer.reset()
            mediaPlayer.setDataSource(recordedFile)
            mediaPlayer.prepare()
            mBinding.seekbarAudio.progress = 0
            mBinding.seekbarAudio.max = mediaPlayer.duration - (mediaPlayer.duration % 1000)

            isAudioPause = false
            mBinding.chronometer.stop()
            mBinding.seekbarAudio.progress = 0
            mBinding.chronometer.text = "00:00"
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


    /*
   this method will start recording the audio
   */
    private fun handleRecordStart() {
        runWithPermissions(Manifest.permission.RECORD_AUDIO) {

            val folder = File(context?.filesDir?.absolutePath + "/recordings")
            folder.mkdirs()
            recordedFile = folder.absolutePath + "/recording.mp3"

            mBinding.waveformView.reset()

            mRecordingThread = RecordingThread(File(recordedFile),
                    AudioDataReceivedListener { data ->
                        mBinding.waveformView.samples = data
                    }
            )
            mRecordingThread?.start()

            mBinding.chronometer.stop()

            mBinding.chronometer.base = SystemClock.elapsedRealtime()
            mBinding.chronometer.start()
            handleImages(RECORD_STOP)

        }
    }


    /*
    this method will stop recoding the audio
     */
    private fun handleRecordStop() {
        mRecordingThread?.stop()

        mBinding.chronometer.stop()
        mBinding.chronometer.text = "00:00"

        handleImages(AUDIO_START)

        mediaPlayer.reset()
        mediaPlayer.setDataSource(recordedFile)
        mediaPlayer.prepare()
        mBinding.seekbarAudio.progress = 0
        mBinding.seekbarAudio.max = mediaPlayer.duration - (mediaPlayer.duration % 1000)
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
                isAudioPause = false
            } else {

//                mediaPlayer.reset()
//                mediaPlayer.setDataSource(recordedFile)
//                mediaPlayer.prepare()
                mediaPlayer.start()
                mediaPlayer.seekTo(mBinding.seekbarAudio.progress)
//                mBinding.seekbarAudio.max = mediaPlayer.duration - (mediaPlayer.duration % 1000)

//                mBinding.chronometer.base = SystemClock.elapsedRealtime()
//                mBinding.chronometer.start()
            }
            mBinding.chronometer.base = SystemClock.elapsedRealtime() - mediaPlayer.currentPosition
            mBinding.chronometer.start()


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
        mBinding.waveformView.hide()

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
                mBinding.waveformView.show()
                mBinding.recordStartImage.show()
                mBinding.recordAgainContainer.hide()
                mBinding.graySpeakerImage.show()
            }
            RECORD_STOP -> {
                mBinding.waveformView.show()
                mBinding.recordStopImage.show()
                mBinding.graySpeakerImage.hide()
                mBinding.redSpeakerImage.show()
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

            if (mediaPlayer.isPlaying) {
                mBinding.chronometer.stop()
                mBinding.seekbarAudio.progress = 0
                mBinding.seekbarAudio.max = 0
                mediaPlayer.stop()
            }

            handleRecordStart()
            dialog?.dismiss()
        }

    }

    override fun onStop() {
        super.onStop()
        mRecordingThread?.stop()
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
            recordedFile = ""
            dialog?.dismiss()
            Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_recordingFragment_to_statementFragment)
        }

    }
}
