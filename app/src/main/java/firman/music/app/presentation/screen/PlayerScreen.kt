package firman.music.app.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import firman.music.app.core.state.PlayerUIState
import firman.music.app.model.TrackItem
import firman.music.app.presentation.component.AudioPlayerView
import firman.music.app.presentation.component.PlayerControlsView
import firman.music.app.presentation.component.PlaylistItemView
import firman.music.app.viewmodel.PlayerViewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import firman.music.app.ui.theme.BasicColor
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun PlayerScreen(viewModel: PlayerViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentTrackState by viewModel.currentPlayingIndex.collectAsStateWithLifecycle()
    val isPlayingState by viewModel.isPlaying.collectAsStateWithLifecycle()
    val isShuffleState by viewModel.isShuffle.collectAsStateWithLifecycle()
    val isRepeatMode by viewModel.isRepeatMode.collectAsStateWithLifecycle()
    val totalDurationState by viewModel.totalDurationInMS.collectAsStateWithLifecycle()
    val isBuffering by viewModel.isBuffering.collectAsStateWithLifecycle()
    var currentPositionState by remember { mutableLongStateOf(viewModel.getPlayer()?.currentPosition!!) }

    LaunchedEffect(isPlayingState) {
        while (isPlayingState) {
            currentPositionState = viewModel.getPlayer().currentPosition!!
            delay(1.seconds)
        }
    }

    when (uiState) {
        PlayerUIState.Loading -> {

        }

        is PlayerUIState.Tracks -> {
            val data = (uiState as PlayerUIState.Tracks).items
            if (data?.size == 0) return
            Box(contentAlignment = Alignment.Center) {

                Column(modifier = Modifier.fillMaxSize()) {
                    AudioPlayerView(viewModel)
                    data?.get(currentTrackState)?.let {
                        PlayerControlsView(
                            currentTrackImage = it.teaserUrl,
                            totalDuration = totalDurationState,
                            currentPosition = currentPositionState,
                            isPlaying = isPlayingState,
                            isShuffle = isShuffleState,
                            isRepeatMode = isRepeatMode,
                            navigateTrack = { action -> viewModel.updatePlaylist(action) },
                            seekPosition = { position -> viewModel.updatePlayerPosition((position * 1000).toLong()) }
                        )
                    }
                    //PLAYLIST
                    LazyColumn {
                        items((uiState as PlayerUIState.Tracks).items?.size ?: 0) {
                            PlaylistItemView(
                                data?.get(it) ?: TrackItem("", "", "", "", ""),
                                currentTrackState == it
                            ) {
                                viewModel.skipToPosition(it)
                            }
                        }
                    }
                }
                if (isBuffering) {
                    Popup(
                        alignment = Alignment.Center,
                        properties = PopupProperties(
                            focusable = true,
                            dismissOnBackPress = false, dismissOnClickOutside = false
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                modifier = Modifier
                                    .wrapContentSize()
                                    .padding(20.dp)
                                    .background(color = BasicColor, RoundedCornerShape(10.dp)),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "Loading...", color = Color.White,
                                    modifier = Modifier.padding(top = 15.dp, start = 15.dp, end = 15.dp))
                                Spacer(modifier = Modifier.height(15.dp))
                                CircularProgressIndicator(color = Color.White)
                                Spacer(modifier = Modifier.height(15.dp))
                            }
                        }
                    }
                }

            }
        }
    }
}