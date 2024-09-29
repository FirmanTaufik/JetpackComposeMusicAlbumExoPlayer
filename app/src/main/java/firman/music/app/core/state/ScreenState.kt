package firman.music.app.core.state

import firman.music.app.model.album.Feed

sealed  class ScreenState   {
    data class OnSuccess(val items: Feed?) : ScreenState()
      object OnLoading : ScreenState()
    data class OnError(val message : String?= "Terjadi Kesalahan Coba Lagi"): ScreenState()
}