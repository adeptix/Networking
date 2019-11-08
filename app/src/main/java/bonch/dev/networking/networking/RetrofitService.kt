package bonch.dev.networking.networking

import bonch.dev.networking.models.Album
import bonch.dev.networking.models.Photo
import bonch.dev.networking.models.Post
import bonch.dev.networking.models.User
import retrofit2.Response
import okhttp3.ResponseBody
import retrofit2.http.*


interface RetrofitService {

    @GET("/posts")
    suspend fun getPosts() : Response<List<Post>>

    @GET("/users")
    suspend fun getUsers() : Response<List<User>>

    @GET("/albums")
    suspend fun getAlbums(@Query("userId") userId : Int = 1) : Response<MutableList<Album>>

    @GET("/photos")
    suspend fun getPhotos(@Query("albumId") albumId : Int = 1) : Response<List<Photo>>

    @DELETE("/albums/{id}")
    suspend fun deleteAlbum(@Path("id") id: Int): Response<ResponseBody>

    @FormUrlEncoded
    @POST("/posts")
    suspend fun postPost(@Field("title") title: String, @Field("body") body: String): Response<ResponseBody>
}