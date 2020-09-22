package com.filepicker.camera

import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.filepicker.BuildConfig
import com.filepicker.FilePickerFragment
import com.filepicker.utils.createFile
import java.io.File
import java.io.IOException


/**
 *  Manage the camera intent for image capture and return captured image
 */
class CaptureImage(private val fragment: Fragment) {

    var photoFile: File? = null
    var photoUri: Uri? = null


    /**
     * dispatch image capture intent
     */
    fun dispatchCaptureImageIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            fragment.context?.let { ctx ->
                ctx.packageManager?.let { pckgMngr ->
                    takePictureIntent.resolveActivity(pckgMngr)?.also {
                        // Create the File where the photo should go
                        photoFile = try {
                            ctx.createFile("media/images", "IMG", "jpg")
                        } catch (ex: IOException) {
                            null
                        }
                        // Continue only if the File was successfully created
                        photoFile?.also { file ->
                            photoUri = FileProvider.getUriForFile(
                                ctx,
                                if (BuildConfig.DEBUG) {
                                    "app.monimates.debug.provider"
                                } else {
                                    "app.monimates.provider"
                                }
                                ,
                                file
                            )
                            /**
                             * create file and generate uri with that file and pass it to the intent
                             * so captured image will write into that uri path
                             */
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                            fragment.startActivityForResult(
                                takePictureIntent,
                                FilePickerFragment.REQ_CAPTURE_IMAGE
                            )
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
        photoFile?.let { file ->
            if (file.exists()) {
                function.invoke(file)
            }
        }
    }
}
