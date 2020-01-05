package com.example.naqi.bebettermuslim.data

import com.example.naqi.bebettermuslim.models.PersonalModel
import com.example.naqi.bebettermuslim.models.TimezoneModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInterface {

    @GET("naats")
    fun getNaats(): Call<List<PersonalModel>>

    //albumModel api
    @GET("allalbums")
    fun getAlbumsOfReciter(@Query("reciter_id") reciter_id: String): Call<AlbumResponse>

    @GET("allalbums")
    fun getAllAlbums(): Call<AlbumResponse>

    //recitersModel api
    @GET("allreciters")
    fun getAllReciters(): Call<RecitersResponse>

    //languageModel api
    @GET("langs")
    fun getAllLangs(): Call<LanguageResponse>


    //Naat api
    @GET("allnaat/{reciter_id}/{album_id}")
    fun getNaatsOfAlbum(@Path("album_id") album_id: String, @Path("reciter_id") reciter_id: String): Call<NaatsResponse>


    @GET("allnaat/{reciter_id}")
    fun getNaatsOfReciter(@Path("reciter_id") reciter_id: String): Call<NaatsResponse>


    //places api
    @GET("/maps/api/timezone/json")
    fun getCityResults(
        @Query("location") location: String, @Query("timestamp") timestamp: String,
        @Query("key") key: String
    ): Call<TimezoneModel>

}
