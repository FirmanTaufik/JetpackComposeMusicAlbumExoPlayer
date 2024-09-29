package firman.music.app.core.source

import firman.music.app.model.album.Detail
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {


    @GET("/feeds/posts/default?alt=json&&max-results=1000")
    suspend fun getAllPost(): Response<Detail>


}