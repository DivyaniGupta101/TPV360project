package com.filepicker.file

import android.content.Intent
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.filepicker.FilePicker
import com.filepicker.FilePickerFragment
import com.filepicker.utils.copyFile
import com.filepicker.utils.createFile
import com.filepicker.utils.getFileName
import com.filepicker.utils.orFalse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.InputStream


class PickFile(
        private val fragment: Fragment
) {
    fun dispatchPickFileIntent(mFileStatus: FilePicker.PickFileStatuses?) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            if (mFileStatus?.mimeType?.isNotEmpty().orFalse()) {
                val mimeTypes = mFileStatus?.mimeType
                putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            }
        }

        fragment.context?.packageManager?.let { packageManager ->
            if (intent.resolveActivity(packageManager) != null) {
                fragment.startActivityForResult(intent, FilePickerFragment.REQ_FILE)
            }
        }
    }

    fun onActivityResult(data: Intent?, function: (file: File) -> Unit) {
        data?.data?.let { uri ->

            val context = fragment.context

            var openInputStream: InputStream? = null

            (fragment as FilePickerFragment).launch {

                try {
                    withContext(Dispatchers.IO) {
                        openInputStream = context?.contentResolver?.openInputStream(uri)
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                            context?.applicationContext,
                            "Error in picking file, Please try again",
                            Toast.LENGTH_SHORT
                    ).show()
                }

                val filePath = "media/files"

                val fileName =
                        if (context?.getFileName(uri).orEmpty().isEmpty()) "FILE" else context?.getFileName(uri).orEmpty()

                val destination =
                        context?.createFile(
                                filePath,
                                fileName.substringBeforeLast("."),
                                fileName.substringAfterLast(".")
                        )

                destination?.let { file ->
                    withContext(Dispatchers.IO) {
                        (openInputStream as? FileInputStream)?.copyFile(file)
                    }
                    if (file.exists()) {
                        function.invoke(destination)
                    }
                }
            }
        }
    }
}