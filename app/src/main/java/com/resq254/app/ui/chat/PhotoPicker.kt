package com.resq254.app.ui.chat

import android.content.Context
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File

class PhotoPickerLaunchers internal constructor(
    private val cameraLauncher: ManagedActivityResultLauncher<Uri, Boolean>,
    private val galleryLauncher: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>,
    private val pendingCameraUri: () -> Uri
) {
    fun launchCamera() = cameraLauncher.launch(pendingCameraUri())
    fun launchGallery() = galleryLauncher.launch(
        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
    )
}

@Composable
fun rememberPhotoPickerLaunchers(onPhotoReady: (Uri) -> Unit): PhotoPickerLaunchers {
    val context = LocalContext.current
    var lastCameraUri by remember { mutableStateOf(createImageUri(context)) }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) onPhotoReady(lastCameraUri)
        lastCameraUri = createImageUri(context)
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) onPhotoReady(uri)
    }

    return remember {
        PhotoPickerLaunchers(cameraLauncher, galleryLauncher) { lastCameraUri }
    }
}

private fun createImageUri(context: Context): Uri {
    val file = File.createTempFile("scene_${System.currentTimeMillis()}", ".jpg", context.cacheDir)
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}