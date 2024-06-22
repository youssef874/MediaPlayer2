package com.example.mediaplayer.ui

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.example.mediaplayer.domain.model.UiAudio
import com.example.mediaplayer.domain.model.UiPlayList
import com.example.mediaplayer.ui.Components.list.ItemData

typealias function0 = () -> Unit

@Composable
fun RequestSinglePermission(
    permission: String,
    onPermissionGranted: @Composable (Context) -> Unit,
    onPermissionDenied: @Composable function0,
    owWaitingForResponse: @Composable function0
) {
    val context = LocalContext.current
    var isPermissionGranted by rememberSaveable {
        mutableStateOf<Boolean?>(null)
    }
    val launcherState =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted ->
            isPermissionGranted = isGranted
        }
    if (
        ContextCompat.checkSelfPermission(
            context, permission
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        SideEffect {
            try {
                launcherState.launch(permission)
            }catch (_: Exception){

            }
        }
    } else {
        isPermissionGranted = true
    }
    when (isPermissionGranted) {
        true -> {
            onPermissionGranted(context)
        }
        false -> {
            onPermissionDenied()
        }
        else -> {
            owWaitingForResponse()
        }
    }
}

@Composable
fun ObserveLifecycle(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onEvent : (LifecycleOwner, Lifecycle.Event) ->Unit
) {
    DisposableEffect(key1 = lifecycleOwner){
        val observer = LifecycleEventObserver{source, event ->
            onEvent(source,event)
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

fun UiAudio.toItemData(): ItemData {
    val second = duration / 1000
    val minutes = second / 60
    val hours = minutes / 60
    val formattedDuration =
        String.format("%02d:%02d:%02d", hours, minutes % 60, second % 60)
    return ItemData(
        id = id,
        imageUri = thumbnailUri,
        title = songName,
        subtitle = artist,
        endText = formattedDuration

    )
}

fun Int.timeFormatter(): String {
    val second = this / 1000
    val minutes = second / 60
    val hours = minutes / 60
    return String.format("%02d:%02d:%02d", hours, minutes % 60, second % 60)
}

fun UiPlayList.toItemData(): ItemData{
    return ItemData(
        id = playListId,
        title = playListName,
        imageUri = thumbnailUri
    )
}


fun ItemData.toUiPlaylist(): UiPlayList = UiPlayList(playListId = id, playListName = title)

