package firman.music.app.presentation.component

import firman.music.app.viewmodel.PlayerViewModel
import firman.music.app.helper.ControlButtons
import firman.music.app.helper.ExtentionHelper.toMinute
import firman.music.app.model.TrackItem
import firman.music.app.ui.theme.BasicColor
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage

/**
 * Composable function to display the audio player view.
 * This composable sets up and displays the ExoPlayer view for audio playback.
 * @param viewModel The view model for managing media playback.
 */
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun AudioPlayerView(viewModel: PlayerViewModel) {
    // Fetching the Local Context
    val mContext = LocalContext.current

    // Declaring ExoPlayer
    val mExoPlayer = remember(viewModel.getPlayer()) {
        ExoPlayer.Builder(mContext).build().apply {
            viewModel.preparePlayer(context = mContext)
        }
    }

    // Implementing ExoPlayer
    DisposableEffect(
        AndroidView(modifier = Modifier.size(0.dp), factory = { context ->
            PlayerView(context).apply {
                this.player = mExoPlayer
                hideController()
                useController = false
                controllerHideOnTouch = false
            }
        })
    ) {
        // Disposes the player when the composable is removed from the composition
        onDispose { /*viewModel.onDestroy()*/ }
    }
}


/**
 * Composable function to display the player controls.
 * This composable displays controls for managing audio playback, including a slider for track progress,
 * buttons for rewinding, playing/pausing, and skipping tracks, and current track information.
 * @param currentTrackImage The image URL of the current track.
 * @param totalDuration The total duration of the current track.
 * @param currentPosition The current position within the track.
 * @param isPlaying Whether the track is currently playing or paused.
 * @param navigateTrack Function to navigate to the next or previous track.
 * @param seekPosition Function to seek to a specific position within the track.
 */
@Composable
fun PlayerControlsView(
    currentTrackImage: String,
    totalDuration: Long,
    currentPosition: Long,
    isPlaying: Boolean,
    isShuffle: Boolean,
    isRepeatMode: Boolean,
    navigateTrack: (ControlButtons) -> Unit,
    seekPosition: (Float) -> Unit
) {
    Log.d("FirmanTAG", "PlayerControlsView: $isPlaying")
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Spacer for layout
        Spacer(modifier = Modifier.height(40.dp))

        // Display current track image
        AsyncImage(
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape),
            model = currentTrackImage,
            contentDescription = "Player Image"
        )

        // Slider for track progress
        Slider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, start = 30.dp, end = 30.dp),
            value = (currentPosition / 1000).toFloat(),
            valueRange = 0f..(totalDuration / 1000).toFloat(),
            onValueChange = { seekPosition(it) },
            colors = SliderDefaults.colors(
                thumbColor = BasicColor,
                activeTickColor = MaterialTheme.colorScheme.onBackground,
                activeTrackColor =BasicColor
            )
        )

        // Display current position and total duration
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = currentPosition.toMinute())
            Text(text = totalDuration.toMinute())
        }

        // Row for control buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp, bottom = 30.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rewind button
            IconButton(
                modifier = Modifier.size(45.dp),
                onClick = { navigateTrack(ControlButtons.Repeat) }
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = if (isRepeatMode) androidx.media3.ui.R.drawable.exo_icon_repeat_one else androidx.media3.ui.R.drawable.exo_icon_repeat_off),
                    contentDescription = "Repeat",
                    tint = BasicColor
                )
            }

            IconButton(
                modifier = Modifier.size(45.dp),
                onClick = { navigateTrack(ControlButtons.Previous) }
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = androidx.media3.ui.R.drawable.exo_icon_previous),
                    contentDescription = "Next",
                    tint = BasicColor
                )
            }

            // Play/pause button
            IconButton(
                modifier = Modifier.size(70.dp),
                onClick = { navigateTrack(ControlButtons.Play) }
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(
                        id = if (isPlaying) androidx.media3.ui.R.drawable.exo_icon_pause else androidx.media3.ui.R.drawable.exo_icon_play
                    ),
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = BasicColor,
                    modifier = Modifier.size(70.dp)
                )
            }

            // Next button
            IconButton(
                modifier = Modifier.size(45.dp),
                onClick = { navigateTrack(ControlButtons.Next) }
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = androidx.media3.ui.R.drawable.exo_ic_skip_next),
                    contentDescription = "Next",
                    tint = BasicColor
                )
            }

            IconButton(
                modifier = Modifier.size(45.dp),
                onClick = { navigateTrack(ControlButtons.Shuffle) }
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = if (isShuffle)  androidx.media3.ui.R.drawable.exo_icon_shuffle_on
                            else androidx.media3.ui.R.drawable.exo_legacy_controls_shuffle_off),
                    contentDescription = "Shuffle",
                    tint = BasicColor
                )
            }
        }
    }
}

/**
 * Composable function to display the playlist.
 * This composable displays a list of track items in a lazy column, where each track item is represented
 * by the `PlaylistItemView` composable. It also highlights the currently playing track based on the
 * `currentTrack` parameter.
 * @param tracks The list of track items in the playlist.
 * @param currentTrack The index of the current track in the playlist.
 */
/*@Composable
fun PlaylistView(tracks: List<TrackItem>, currentTrack: Int) {
    LazyColumn {
        items(tracks.size) {
            PlaylistItemView(tracks[it], currentTrack == it)
        }
    }
}*/

/**
 * Composable function to display a single track item in the playlist.
 * This composable displays a single track item within a row layout. Each track item includes an image,
 * title, artist name, and duration. The background color of the row is changed to blue if the track
 * is currently playing, otherwise it remains transparent.
 * @param trackItem The track item to display.
 * @param isPlaying Whether the track is currently playing.
 */
@Composable
fun PlaylistItemView(trackItem: TrackItem, isPlaying: Boolean, onItemClick : ()->Unit) {
    Row(
        modifier = Modifier
            .clickable {
                onItemClick()
            }
            .fillMaxWidth()
            .background(if (isPlaying) BasicColor else Color.Transparent),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Display the track image
        TrackImageView(imageUrl = trackItem.teaserUrl)

        // Display the track title and artist name in a column
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Text(text = trackItem.title)
            Text(text = trackItem.artistName)
        }

        // Display the track duration
      //  Text(text = trackItem.duration)
    }
}

/**
 * Composable function to display the image of a track.
 * This composable displays the image of a track using the `AsyncImage` composable. It allows specifying
 * the size of the image and the URL of the image to load.
 * @param size The size of the image.
 * @param imageUrl The URL of the track image.
 */
@Composable
fun TrackImageView(size: Dp = 75.dp, imageUrl: String) {
    AsyncImage(
        modifier = Modifier
            .size(size)
            .padding(horizontal = 10.dp),
        model = imageUrl,
        contentDescription = null
    )
}
