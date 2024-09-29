package firman.music.app.viewmodel

import firman.music.app.helper.NOW_PLAYING_CHANNEL_ID
import firman.music.app.core.App
import firman.music.app.core.state.PlayerUIState
import firman.music.app.helper.ControlButtons
import firman.music.app.helper.ExtentionHelper.getListSongs
import firman.music.app.helper.ExtentionHelper.isServiceForegrounded
import firman.music.app.model.TrackItem
import firman.music.app.service.MusicService
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val player: ExoPlayer,
    private val dataSource: CacheDataSource.Factory,
    private val application: App
) : ViewModel() {

    companion object{
        val SESSION_INTENT_REQUEST_CODE = 0
    }

    val TAG = "Media3AppTag"

    private var playlist = arrayListOf<TrackItem>()
    private val _currentPlayingIndex = MutableStateFlow(0)
    val currentPlayingIndex = _currentPlayingIndex.asStateFlow()

    private val _totalDurationInMS = MutableStateFlow(0L)
    val totalDurationInMS = _totalDurationInMS.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _isShuffle = MutableStateFlow(false)
    val isShuffle = _isShuffle.asStateFlow()

    private val _isRepeatMode = MutableStateFlow(false)
    val isRepeatMode = _isRepeatMode.asStateFlow()

    private val _isBuffering = MutableStateFlow(true)
    val isBuffering = _isBuffering.asStateFlow()


    private val _currentPosition = MutableStateFlow(0L)

    var uiState: StateFlow<PlayerUIState> =
        MutableStateFlow(PlayerUIState.Loading).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            initialValue = PlayerUIState.Loading
        )


    //protected lateinit var mediaSession: MediaSession
    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)


    private var isStarted = false

    fun getPlayer() = player

    fun setPlayList(artistName: String, content: String) {
        uiState = MutableStateFlow(PlayerUIState.Loading)
        if (player?.isPlaying == true) {
            player?.stop()
        }
        playlist.clear()
        playlist = content.getListSongs(artistName)
        if (playlist.isNotEmpty()) {
            uiState =
                MutableStateFlow(PlayerUIState.Tracks(playlist)).stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(5_000),
                    initialValue = PlayerUIState.Loading
                )
        }

        val intent = Intent(application, MusicService::class.java)
        if (!application.isServiceForegrounded(MusicService::class.java)) {
            intent.putExtra(MusicService.COMMANDSERVICE, MusicService.STARTSERVICE)
            /* ContextCompat.startForegroundService(
                 application,
                 intent
             )*/
            application.startService(intent)
        }
    }

    fun preparePlayer(context: Context) {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .build()
        player?.apply {
            setAudioAttributes(audioAttributes, true)
            repeatMode = Player.REPEAT_MODE_ALL
            addListener(playerListener)
        }


        setupPlaylist(context)
    }

    private fun setupPlaylist(context: Context) {

        val videoItems: ArrayList<MediaSource> = arrayListOf()
        playlist.forEach {

            val mediaMetaData = MediaMetadata.Builder()
                .setArtworkUri(Uri.parse(it.teaserUrl))
                .setTitle(it.title)
                .setAlbumArtist(it.artistName)
                .build()

            val trackUri = Uri.parse(it.audioUrl)
            val mediaItem = MediaItem.Builder()
                .setUri(trackUri)
                .setMediaId(it.id)
                .setMediaMetadata(mediaMetaData)
                .build()

            val mediaSource =
                ProgressiveMediaSource.Factory(dataSource).createMediaSource(mediaItem)

            videoItems.add(
                mediaSource
            )
        }

        onStart(context)
        player?.apply {
            playWhenReady = false
            setMediaSources(videoItems)
            prepare()
            play()
        }
    }

    fun updatePlaylist(action: ControlButtons) {
        Log.d(TAG, "updatePlaylist: $action ${player?.isPlaying}")
        //  _isPlaying.value = player?.isPlaying == true
        when (action) {
            ControlButtons.Play -> {
                if (player.isPlaying) {
                    player?.pause()
                } else {
                    player?.play()
                }
            }

            ControlButtons.Next -> player?.seekToNextMediaItem()
            ControlButtons.Rewind -> player?.seekToPreviousMediaItem()
            ControlButtons.Previous -> player?.seekToPreviousMediaItem()
            ControlButtons.Repeat -> {
                val state = player?.repeatMode
                player?.repeatMode =
                    if (state == Player.REPEAT_MODE_OFF) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
                _isRepeatMode.value = player?.repeatMode != Player.REPEAT_MODE_OFF
            }

            ControlButtons.Shuffle -> {
                player?.shuffleModeEnabled = !player?.shuffleModeEnabled!!
                _isShuffle.value = player?.shuffleModeEnabled == true
            }
        }
    }

    fun updatePlayerPosition(position: Long) {
        player?.seekTo(position)
    }

    fun onStart(context: Context) {
        if (isStarted) return

        isStarted = true
    }

    /**
     * Destroy audio notification
     */
    fun onDestroy() {
        onClose()
        player?.release()
    }

    fun skipToPosition(position: Int) {
        player?.seekTo(position, 0)
    }

    /**
     * Close audio notification
     */
    fun onClose() {
        stopNotification()
        if (!isStarted) return

        isStarted = false
        /*  mediaSession.run {
              release()
          }*/
        player?.playWhenReady = false
        // Hide notification
        //notificationManager.hideNotification()

        // Free ExoPlayer resources.
        player?.removeListener(playerListener)
    }
    //    }

    /**
     * Listen to events from ExoPlayer.
     */

    private val playerListener = object : Player.Listener {

        override fun onPlaybackStateChanged(playbackState: Int) {
            Log.d(TAG, "onPlaybackStateChanged: ${playbackState} ${player?.duration}")
            super.onPlaybackStateChanged(playbackState)
            when (playbackState) {
                Player.STATE_BUFFERING -> _isBuffering.value = true
                Player.STATE_READY -> {
                    _isBuffering.value = false
                    syncPlayerFlows()
                    //   notificationManager.showNotificationForPlayer(player)
                }

                else -> {
                    // notificationManager.hideNotification()
                }
            }
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            Log.d(TAG, "onMediaItemTransition: ${mediaItem?.mediaMetadata?.title}")
            super.onMediaItemTransition(mediaItem, reason)
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _isPlaying.value = isPlaying
            super.onIsPlayingChanged(isPlaying)
        }

        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
            Log.e(TAG, "Error: ${error.message}")
        }
    }


    private fun syncPlayerFlows() {
        _currentPlayingIndex.value = player?.currentMediaItemIndex!!
        _totalDurationInMS.value = player?.duration?.coerceAtLeast(0L)!!
    }


    fun stopNotification() {
        val mNotificationManager =
            application.applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager?

        mNotificationManager!!.deleteNotificationChannel(NOW_PLAYING_CHANNEL_ID)
        mNotificationManager.cancelAll()
    }


}


