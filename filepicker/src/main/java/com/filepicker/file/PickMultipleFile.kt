package com.filepicker.file

import android.content.Intent
import android.net.Uri
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


class PickMultipleFile(
    private val fragment: Fragment
) {
    fun dispatchPickMultipleFileIntent(mFileStatus: FilePicker.PickFileMultipleStatuses?) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            if (mFileStatus?.mimeType?.isNotEmpty().orFalse()) {
                val mimeTypes = mFileStatus?.mimeType
                putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            }
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }

        fragment.context?.packageManager?.let { packageManager ->
            if (intent.resolveActivity(packageManager) != null) {
                fragment.startActivityForResult(intent, FilePickerFragment.REQ_MULTIPLE_FILE)
            }
        }
    }

    fun onActivityResult(data: Intent?, function: (fileList: List<File>) -> Unit) {
        (fragment as FilePickerFragment).launch {
            val fileList = arrayListOf<File>()

            if (data?.clipData == null) {
                data?.data?.let { uri ->
                    processUriTofile(fragment, uri, fileList)
                }
            } else {
                data.clipData?.let { clipData ->
                    fileList.clear()
                    for (i in 0 until clipData.itemCount) {
                        val uri = clipData.getItemAt(i).uri
                        processUriTofile(fragment, uri, fileList)
                    }
                }
            }

            function.invoke(fileList)
        }
    }

    private suspend fun processUriTofile(
        fragment: FilePickerFragment,
        uri: Uri,
        fileList: ArrayList<File>
    ) {
        val context = fragment.context

        var openInputStream: InputStream? = null

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
                fileList.add(file)
            }
        }
    }
}
