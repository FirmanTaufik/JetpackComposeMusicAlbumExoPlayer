package firman.music.app.helper

import firman.music.app.model.TrackItem
import android.app.ActivityManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import org.jsoup.Jsoup
import java.util.concurrent.TimeUnit

object ExtentionHelper {

    fun Long.toMinute():String{
      return  String.format("%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(this),
            TimeUnit.MILLISECONDS.toSeconds(this) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(this))
        );
    }

     fun String.fetchImage(): String {
        val document  = Jsoup.parse(this)
        return document.select("img").attr("src")
    }

    fun String.getListSongs(artistName :String):ArrayList<TrackItem>{
        val image = this.fetchImage()
        val document = Jsoup.parse(this)
        val elements = document.select("ol").select("li")
        val list :ArrayList<TrackItem> = arrayListOf()
        elements.forEach {
            val title =it.select("a").text()
            val path =it.select("a").attr("href")
            it.select("a").remove()
            if (it.text() != null && it.text() != "") {

            }
            val item = TrackItem(
                title, path, teaserUrl = image,
                title = title, artistName
            )
            list.add(item)
        }
        return list
    }

    @Suppress("DEPRECATION") // Deprecated for third party Services.
    fun <T> Context.isServiceForegrounded(service: Class<T>) =
        (getSystemService(ACTIVITY_SERVICE) as? ActivityManager)
            ?.getRunningServices(Integer.MAX_VALUE)
            ?.find { it.service.className == service.name }
            ?.foreground == true
}