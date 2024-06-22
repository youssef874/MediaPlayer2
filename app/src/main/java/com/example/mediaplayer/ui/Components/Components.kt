package com.example.mediaplayer.ui.Components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.ShuffleOn
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.mediaplayer.R
import com.example.mediaplayer.data.model.RepeatMode
import com.example.mediaplayer.ui.theme.LightBlue
import com.example.mediaplayer.viewmodel.model.UiResult

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
    }
}


@Composable
fun ErrorScreen(modifier: Modifier = Modifier, content: String? = null) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.music_off_fill0_wght400_grad0_opsz24),
            contentDescription = null,
            modifier = Modifier.padding(16.dp)
        )
        Text(
            text = content ?: stringResource(R.string.default_failed_fetch_string),
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}

@Composable
fun ItemImage(modifier: Modifier = Modifier, imageUri: Uri?) {
    imageUri?.let {
        AsyncImage(
            model = it.toString(),
            contentDescription = null,
            modifier = modifier,
            contentScale = ContentScale.Crop,
        )
    } ?: run {
        Image(
            painter = painterResource(id = R.drawable.baseline_audio_file_24),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun TitleText(modifier: Modifier = Modifier, text: String, isPlaying: Boolean = false) {
    if (isPlaying) {
        Text(
            text = text,
            modifier = modifier,
            maxLines = 1,
            color = LightBlue,
            fontWeight = FontWeight.Bold
        )
    } else {
        Text(
            text = text, modifier = modifier, maxLines = 1, fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SubTitleText(modifier: Modifier = Modifier, text: String, isPlaying: Boolean = false) {
    if (isPlaying) {
        Text(text = text, modifier = modifier, maxLines = 1, color = LightBlue)
    } else {
        Text(text = text, modifier = modifier, maxLines = 1)
    }
}

@Composable
fun RegularText(modifier: Modifier = Modifier, text: String) {
    Text(text = text, modifier = modifier)
}

@Composable
fun PreviousButton(modifier: Modifier = Modifier, onPrevious: () -> Unit) {
    Icon(Icons.Filled.SkipPrevious, contentDescription = null, modifier = modifier.clickable {
        onPrevious()
    }, tint = Color.White)
}

@Composable
fun PlayButton(modifier: Modifier = Modifier, onPlay: () -> Unit) {
    Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = modifier.clickable {
        onPlay()
    }, tint = Color.White)
}

@Composable
fun PauseButton(modifier: Modifier = Modifier, onPause: () -> Unit) {
    Icon(Icons.Filled.Pause, contentDescription = null, modifier = modifier.clickable {
        onPause()
    }, tint = Color.White)
}

@Composable
fun NextButton(modifier: Modifier = Modifier, onNext: () -> Unit) {
    Icon(Icons.Filled.SkipNext, contentDescription = null, modifier = modifier.clickable {
        onNext()
    }, tint = Color.White)
}

@Composable
fun RepeatButton(
    modifier: Modifier = Modifier,
    repeatMode: RepeatMode = RepeatMode.NO_REPEAT,
    autoPlayAction: () -> Unit
) {
    when (repeatMode) {
        RepeatMode.NO_REPEAT -> {
            Icon(
                painter = painterResource(id = R.drawable.repeat_off_icon_138246),
                contentDescription = null,
                modifier = modifier.clickable {
                    autoPlayAction()
                },
                tint = Color.White
            )
        }

        RepeatMode.ONE_REPEAT -> {
            Icon(
                Icons.Filled.RepeatOne,
                contentDescription = null,
                modifier = modifier.clickable {
                    autoPlayAction()
                },
                tint = Color.White
            )
        }

        RepeatMode.REPEAT_ALL -> {
            Icon(
                Icons.Filled.Repeat,
                contentDescription = null,
                modifier = modifier.clickable {
                    autoPlayAction()
                },
                tint = Color.White
            )
        }
    }
}

@Composable
fun PlayOrStoppedButton(
    modifier: Modifier = Modifier,
    isStopped: Boolean = true,
    action: () -> Unit
) {
    if (isStopped) {
        PauseButton(modifier) {
            action()
        }
    } else {
        PlayButton(modifier = modifier) {
            action()
        }
    }
}

@Composable
fun ShuffleButton(
    modifier: Modifier = Modifier,
    isInRandomMode: Boolean = false,
    shuffleAction: () -> Unit
) {
    if (!isInRandomMode) {
        Icon(Icons.Filled.Shuffle, contentDescription = null, modifier = modifier.clickable {
            shuffleAction()
        }, tint = Color.White)
    } else {
        Icon(Icons.Filled.ShuffleOn, contentDescription = null, modifier = modifier.clickable {
            shuffleAction()
        }, tint = Color.White)
    }
}

@Composable
fun AudioListAction(modifier: Modifier = Modifier, onAudioListRequest: () -> Unit) {
    Icon(Icons.AutoMirrored.Filled.QueueMusic, contentDescription = null, modifier = modifier.clickable {
        onAudioListRequest()
    }, tint = Color.White)
}


@Composable
fun FavoriteAction(
    modifier: Modifier = Modifier,
    isFavorite: Boolean = false,
    onFavoriteChange: () -> Unit
) {
    if (isFavorite) {
        Icon(Icons.Filled.Favorite, contentDescription = null, modifier = modifier.clickable {
            onFavoriteChange()
        }, tint = Color.White)
    } else {
        Icon(Icons.Filled.FavoriteBorder, contentDescription = null, modifier = modifier.clickable {
            onFavoriteChange()
        }, tint = Color.White)
    }
}

@Composable
fun AddSongToAction(modifier: Modifier = Modifier, onAddSongToRequested: () -> Unit) {
    Icon(Icons.Filled.Add, contentDescription = null, modifier = modifier.clickable {
        onAddSongToRequested()
    }, tint = Color.White)
}

@Composable
fun AddItemComponent(
    modifier: Modifier = Modifier,
    itemName: String,
    defaultValue: String? = null,
    attachToSongResult: UiResult<Unit>? = null,
    onAddClicked: (defaultValue: String) -> Unit,
    onDismissRequest: () -> Unit
) {
    var text by rememberSaveable {
        mutableStateOf(defaultValue?:"")
    }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = modifier
                .fillMaxWidth()
                .padding(start = 32.dp, end = 32.dp)
        ) {
            Column {
                Text(
                    text = stringResource(R.string.add_item_title_string, itemName),
                    fontWeight = FontWeight.Bold,
                    modifier = modifier.padding(start = 8.dp, bottom = 32.dp, top = 8.dp)
                )
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text(text = stringResource(R.string.name_add_item_label, itemName)) },
                    singleLine = true,
                    modifier = modifier.padding(start = 8.dp, end = 8.dp, bottom = 32.dp)
                )
                Row (
                    modifier = modifier
                        .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                        .fillMaxWidth()
                ){
                    Button(onClick = onDismissRequest) {
                        Text(text = stringResource(R.string.cancel_string))
                    }
                    Spacer(modifier = modifier.weight(1F))
                    Button(onClick = { onAddClicked(text) }) {
                        if (attachToSongResult == null){
                            Text(text = stringResource(R.string.add_string))
                        }else if (attachToSongResult is UiResult.Success){
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}