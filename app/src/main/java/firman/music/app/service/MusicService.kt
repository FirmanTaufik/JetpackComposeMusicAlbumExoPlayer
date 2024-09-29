package firman.music.app.service

import firman.music.app.helper.MediaNotificationManager
import firman.music.app.core.App
import firman.music.app.viewmodel.PlayerViewModel
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.ui.PlayerNotificationManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MusicService  : Service()  {
    @Inject
    lateinit var player: ExoPlayer
    @Inject
    lateinit var application: App
    companion object {
        val COMMANDSERVICE = "command_service"
        val STOPSERVICE = "stop"
        val STARTSERVICE = "service"
        val CHANGEPLAYLIST  ="change_playlist"
    }


    private lateinit var notificationManager: MediaNotificationManager

    private val binder = LocalBinder()

    private lateinit var mediaSession: MediaSession

    inner class LocalBinder : Binder() {
        fun getService(): MusicService = this@MusicService
        fun getPlayer():ExoPlayer = player
    }

    override fun onBind(intent: Intent?): IBinder = binder


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.hasExtra(COMMANDSERVICE) == true){
            if (intent.getStringExtra(COMMANDSERVICE) == STOPSERVICE ) {
                stopSelf()
            }else startAsForegroundService()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startAsForegroundService() {
        // create the notification channel
        val sessionActivityPendingIntent =
            application.packageManager?.getLaunchIntentForPackage(application.packageName)
                ?.let { sessionIntent ->
                    PendingIntent.getActivity(
                        application,
                        PlayerViewModel.SESSION_INTENT_REQUEST_CODE,
                        sessionIntent,
                        PendingIntent.FLAG_IMMUTABLE
                    )
                }

         mediaSession = MediaSession.Builder(application, player)
             .setId(System.currentTimeMillis().toString())
            .setSessionActivity( sessionActivityPendingIntent!!).build()
        notificationManager =
            MediaNotificationManager(
                application,
                mediaSession.token,
                player,
                PlayerNotificationListener()
            )
        notificationManager.showNotificationForPlayer(player)
    }



    private inner class PlayerNotificationListener :
        PlayerNotificationManager.NotificationListener {
        @UnstableApi
        override fun onNotificationPosted(
            notificationId: Int,
            notification: Notification,
            ongoing: Boolean
        ) {

        }

        @UnstableApi
        override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {

        }
    }
}