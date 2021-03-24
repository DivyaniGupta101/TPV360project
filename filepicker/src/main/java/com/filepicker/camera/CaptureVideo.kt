package com.filepicker.camera

import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.filepicker.FilePickerFragment
import com.filepicker.utils.createFile
import java.io.File
import java.io.IOException


/**
 *  Manage the camera intent for image capture and return captured image
 */
class CaptureVideo(private val fragment: Fragment) {

    var videoFile: File? = null


    /**
     * dispatch video capture intent
     */
    fun dispatchCaptureVideoIntent() {
        Intent(MediaStore.ACTION_VIDEO_CAPTURE).also { takeVideoIntent ->
            fragment.context?.let { ctx ->
                ctx.packageManager?.let { pckgMngr ->
                    takeVideoIntent.resolveActivity(pckgMngr)?.also {
                        // Create the File where the photo should go
                        videoFile = try {
                            ctx.createFile("media/videos", "VID", "mp4")
                        } catch (ex: IOException) {
                            null
                        }
                        // Continue only if the File was successfully created
                        videoFile?.also { file ->
                            val videoURI: Uri = FileProvider.getUriForFile(
                                ctx,
                                "com.filepicker.provider",
                                file
                            )
                            /**
                             * create file and generate uri with that file and pass it to the intent
                             * so captured image will write into that uri path
                             */
                            takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoURI)
                            fragment.startActivityForResult(takeVideoIntent, FilePickerFragment.REQ_CAPTURE_VIDEO)
                        }
                    }
                }
            }

        }
    }

    /**
     * pass result file to the function
     */
    fun onActivityResult(function: (file: File) -> Unit) {
        videoFile?.let { file ->
            if (file.exists()) {
                function.invoke(file)
            }
        }
    }
}
