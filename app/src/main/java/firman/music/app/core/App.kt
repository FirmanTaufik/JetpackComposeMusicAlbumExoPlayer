package firman.music.app.core

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class annotated with HiltAndroidApp to enable Hilt for dependency injection.
 */
@HiltAndroidApp
class App : Application()
