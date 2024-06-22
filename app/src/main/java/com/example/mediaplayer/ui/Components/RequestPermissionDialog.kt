package com.example.mediaplayer.ui.Components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.mediaplayer.R


typealias function0 = () -> Unit

@Composable
fun RequestPermissionDialog(
    modifier: Modifier = Modifier,
    permissions: List<String>,
    onDismissRequest: function0? = null,
    onConfirm: function0? = null
) {
    var showDialog by remember { mutableStateOf(true) }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                onDismissRequest?.invoke()
            },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    onConfirm?.invoke()
                }) {
                    Text("OK")
                }
            },
            title = {
                Text(text = stringResource(R.string.permission_needed_string))
            },
            text = {
                Text(
                    text = stringResource(
                        R.string.we_need_to_give_list_of_songs_string,
                        permissions.joinToString(",")
                    )
                )
            },
            modifier = modifier
        )
    }
}
