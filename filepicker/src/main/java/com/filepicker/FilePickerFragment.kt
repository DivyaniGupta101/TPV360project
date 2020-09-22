package com.filepicker


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.filepicker.camera.CaptureImage
import com.filepicker.camera.CaptureVideo
import com.filepicker.file.PickFile
import com.filepicker.file.PickMultipleFile
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.io.File
import java.net.URI
import kotlin.coroutines.CoroutineContext

/**
 * A simple [Fragment] subclass.
 *
 */
class FilePickerFragment : Fragment(), CoroutineScope {
    private lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private var mPickFileStatus: FilePicker.PickFileStatuses? = null
    private var mPickMultipleFileStatus: FilePicker.PickFileMultipleStatuses? = null
    private var mImageCaptureStatus: FilePicker.ImageCaptureStatuses? = null
    private var mVideoCaptureStatus: FilePicker.VideoCaptureStatuses? = null
    private var lastPickerRequest: Int = -11

    companion object {

        fun newInstance() = FilePickerFragment()

        const val REQ_FILE = 113
        const val REQ_MULTIPLE_FILE = 114
        const val REQ_CAPTURE_IMAGE = 115
        const val REQ_CAPTURE_VIDEO = 116
    }

    private var pickFileObj: PickFile? = null
    private var pickMultipleFileObj: PickMultipleFile? = null
    private var captureImageObj: CaptureImage? = null
    private var captureVideoObj: CaptureVideo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()
    }

    fun updateFileStatuses(fileStatuses: FilePicker.FileStatuses) {
        when (fileStatuses) {
            is FilePicker.PickFileStatuses -> mPickFileStatus = fileStatuses
            is FilePicker.PickFileMultipleStatuses -> mPickMultipleFileStatus = fileStatuses
            is FilePicker.ImageCaptureStatuses -> mImageCaptureStatus = fileStatuses
            is FilePicker.VideoCaptureStatuses -> mVideoCaptureStatus = fileStatuses
        }
    }

    fun pickFile() {
        mPickFileStatus?.onLoading?.invoke()
        pickFileObj = PickFile(fragment = this)
        pickFileObj?.dispatchPickFileIntent(mPickFileStatus)
    }

    fun pickMultipleFile() {
        mPickMultipleFileStatus?.onLoading?.invoke()
        pickMultipleFileObj = PickMultipleFile(fragment = this)
        pickMultipleFileObj?.dispatchPickMultipleFileIntent(mPickMultipleFileStatus)
    }

    fun captureImage() {
        mImageCaptureStatus?.onLoading?.invoke()
        captureImageObj = CaptureImage(fragment = this)
        captureImageObj?.dispatchCaptureImageIntent()
    }

    fun captureVideo() {
        mVideoCaptureStatus?.onLoading?.invoke()
        captureVideoObj = CaptureVideo(fragment = this)
        captureVideoObj?.dispatchCaptureVideoIntent()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQ_FILE -> {
                    lastPickerRequest = requestCode
                    startCroppingActivity(data?.data)
                }
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val result = CropImage.getActivityResult(data)
                    if (lastPickerRequest == REQ_FILE) {
                        if (resultCode == Activity.RESULT_OK) {
                            mPickFileStatus?.onSuccess?.invoke(File(URI.create(result.uri.toString())))
                        } else {
                            pickFileObj?.onActivityResult(result.originalUri) { file ->
                                mPickFileStatus?.onSuccess?.invoke(file)
                            }
                        }
                    } else if (lastPickerRequest == REQ_CAPTURE_IMAGE) {
                        if (resultCode == Activity.RESULT_OK) {
                            mImageCaptureStatus?.onSuccess?.invoke(File(URI.create(result.uri.toString())))
                        } else {
                            captureImageObj?.onActivityResult { file ->
                                mImageCaptureStatus?.onSuccess?.invoke(file)
                            }
                        }
                    }
                }
                REQ_CAPTURE_IMAGE -> {
                    lastPickerRequest = requestCode
                    startCroppingActivity(captureImageObj?.photoUri)
                }

                REQ_CAPTURE_VIDEO -> {
                    captureVideoObj?.onActivityResult { file ->
                        mVideoCaptureStatus?.onSuccess?.invoke(file)
                    }
                }

                REQ_MULTIPLE_FILE -> {
                    pickMultipleFileObj?.onActivityResult(data) { fileList ->
                        mPickMultipleFileStatus?.onSuccess?.invoke(fileList)
                    }
                }
            }

        } else {
            mPickFileStatus?.onError?.invoke()
            mImageCaptureStatus?.onError?.invoke()
            mVideoCaptureStatus?.onError?.invoke()
            mPickMultipleFileStatus?.onError?.invoke()
        }
    }

    private fun startCroppingActivity(uri: Uri?) {
        context?.let {
            CropImage.activity(uri)
                .setAspectRatio(1, 1)
                .setFixAspectRatio(true)
                .setAllowFlipping(false)
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .start(it, this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
