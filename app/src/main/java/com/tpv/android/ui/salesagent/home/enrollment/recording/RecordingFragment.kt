package com.tpv.android.ui.salesagent.home.enrollment.recording


import android.Manifest
import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.livinglifetechway.k4kotlin.core.androidx.alert
import com.livinglifetechway.k4kotlin.core.hide
import com.livinglifetechway.k4kotlin.core.onClick
import com.livinglifetechway.k4kotlin.core.orFalse
import com.livinglifetechway.k4kotlin.core.show
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import com.tpv.android.R
import com.tpv.android.databinding.FragmentRecordingBinding
import com.tpv.android.model.internal.DialogText
import com.tpv.android.ui.salesagent.home.enrollment.SetEnrollViewModel
import com.tpv.android.utils.actionDialog
import com.tpv.android.utils.audio.AudioDataReceivedListener
import com.tpv.android.utils.audio.RecordingThread
import com.tpv.android.utils.enums.DynamicField
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
        initialize()
    }

    override fun onStop() {
        super.onStop()
        mRecordingThread?.stop()
    }

    private fun initialize() {
        mBinding.item = mViewModel.dynamicFormData.find { it.type == DynamicField.FULLNAME.type && it.meta?.isPrimary == true }

        setupToolbar(mBinding.toolbar, getString(R.string.recording), showBackIcon = true, backIconClickListener = {
            if (recordedFile?.isNotEmpty().orFalse()) {

                mediaPlayer.stop()
            }
        })

        mBinding.checkRecording.onClick {
            handleNextButton()
        }


        mBinding.textSkip.onClick {
            if (recordedFile.isNullOrEmpty()) {
                if (mViewModel.dynamicSettings?.isEnableImageUpload.orFalse()) {
                    Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_recordingFragment_to_uploadbillimageFragment)
                } else {
                    Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_recordingFragment_to_signatureVerificationFragment)
                }
            } else {
                //Show dialog when recording is recorded and user want to skip
                //On Click of "Skip" button remove recording and send to next page
                context?.actionDialog(DialogText(getString(R.string.are_you_sure),
                        getString(R.string.msg_skip),
                        getString(R.string.skip_btn),
                        getString(R.string.cancel)),
                        setOnPositiveBanClickListener = {
                            recordedFile = ""
                            if (mViewModel.dynamicSettings?.isEnableImageUpload.orFalse()) {
                                Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_recordingFragment_to_uploadbillimageFragment)
                            } else {
                                Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_recordingFragment_to_signatureVerificationFragment)
                            }
                        }
                )
            }
        }

        mBinding.btnNext.onClick {
            if (recordedFile?.isNotEmpty().orFalse()) {
                mediaPlayer.stop()
                mViewModel.recordingFile = recordedFile.orEmpty()
                Log.e("value","value")
            }

            if (mViewModel.dynamicSettings?.isEnableImageUpload.orFalse()) {
                Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_recordingFragment_to_uploadbillimageFragment)
            } else {
                Navigation.findNavController(mBinding.root).navigateSafe(R.id.action_recordingFragment_to_signatureVerificationFragment)
            }
        }

        //While audio playing or in Pause state then set seekbar value according to it.
        mBinding.chronometer.setOnChronometerTickListener {
            if (mediaPlayer.isPlaying || isAudioPause) {
                mBinding.seekbarAudio.progress = mediaPlayer.currentPosition
            }
        }


        //Set timer value as per progress bar progress.
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

        //Initial checking, that if any recording saved then play it else start new recording.
        if (mViewModel.recordingFile.isNotEmpty()) {
            handleNextButton()
            recordedFile = mViewModel.recordingFile
            handleImages(AUDIO_START)
        } else {
            handleImages(RECORD_START)
        }


        //After complete mediaPlayer, set time to "00:00" and set audio player position and seekbar progress value.
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

        //Recording start
        mBinding.recordStartImage?.onClick {
            handleRecordStart()
        }


        //Recording stop
        mBinding.recordStopImage?.onClick {
            handleRecordStop()
        }

        //Audion start (Saved Recording play)
        mBinding.audioStartImage?.onClick {
            handleAudioStart()
        }

        //Audio stop (Saved Recording finish/pause)
        mBinding.audioStopImage?.onClick {
            handleAudioPause()
        }

        //Record again
        mBinding.recordAgainContainer.onClick {

            // Show dialog for recordAgain
            // On click of "yes" stop timer and set value of progrss to 0 and handle image according to state.

            context?.actionDialog(DialogText(getString(R.string.are_you_sure),
                    getString(R.string.msg_confirmation_again_record),
                    getString(R.string.yes),
                    getString(R.string.cancel)),
                    setOnPositiveBanClickListener = {
                        if (mediaPlayer.isPlaying) {
                            mBinding.chronometer.stop()
                            mBinding.seekbarAudio.progress = 0
                            mBinding.seekbarAudio.max = 0
                            mediaPlayer.stop()
                        }

                        handleRecordStart()
                    }
            )
        }
    }

    private fun handleNextButton() {
        mBinding.btnNext.isEnabled = (recordedFile?.isNotEmpty().orFalse() && mBinding.checkRecording.isChecked && mViewModel.dynamicSettings?.isEnableImageUpload.orFalse())
    }

    /**
     * Start recording and handle image according to state and also make Directory for save recoded file
     */
    private fun handleRecordStart() {
        val audioManager = context?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (audioManager.mode == 0) {
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
        } else {
            alert {
                this.setMessage(getString(R.string.msg_unable_record))
                this.setPositiveButton(getString(R.string.ok), { dialog, which ->
                    dialog?.dismiss()
                }).show()
            }

        }
    }


    /**
     * Stop recording and save in file and handle image according
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

        handleNextButton()
    }


    /**
     * Start Play the recorded file (Audio file)
     * Variable "isAudioPlayer" check if audio was paused by user
     * Then maintain the state and when play it again then that audio should be start from resume state
     * Also set timer time and seekbar value according to audio player position
     */
    private fun handleAudioStart() {

        try {
            handleImages(AUDIO_STOP)

            if (isAudioPause) {
                isAudioPause = false
            } else {
                mediaPlayer.seekTo(mBinding.seekbarAudio.progress)
            }

            mediaPlayer.start()

            mBinding.chronometer.base = SystemClock.elapsedRealtime() - mediaPlayer.currentPosition
            mBinding.chronometer.start()
        } catch (e: Exception) {
        }
    }

    /**
     * Stop playing audio(Audio Pause)
     * Change variable value in "isAudioPause"
     * Stop Timer and handle image according to state
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

    /**
     * Image hide/show according to state
     */
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
}
