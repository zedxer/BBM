package com.example.naqi.bebettermuslim.data

import com.example.naqi.bebettermuslim.Utils.Constants
import com.example.naqi.bebettermuslim.Utils.Constants.Companion.BASE_API_URL
import com.example.naqi.bebettermuslim.Utils.Constants.Companion.GOOGLE_MAP_BASE_URL
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import com.google.gson.GsonBuilder
import java.util.concurrent.TimeUnit


object ApiClient {

    var retrofit: Retrofit? = null
    private val gson = GsonBuilder().create()

    private val httpLoggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)!!
    private val okHttpClientBuilder = OkHttpClient.Builder()
        .addInterceptor(httpLoggingInterceptor)

    private val okHttpClient = okHttpClientBuilder.build()

    fun <T> createService(serviceClass: Class<T>): T {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(BASE_API_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }
        return retrofit!!.create(serviceClass)
    }

    val client: Retrofit?
        get() {
            val httpClient = OkHttpClient.Builder()
            httpClient.retryOnConnectionFailure(true);
            httpClient.readTimeout(60, TimeUnit.SECONDS)

            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(Constants.BASE_API_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(httpClient.build())
                    .build()
            }
            return retrofit
        }

  /*  var retrofit2 = Retrofit.Builder()
        .baseUrl(GOOGLE_MAP_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    var service = retrofit2.create(ApiInterface::class.java)*/
    fun <T> createGoogleService(serviceClass: Class<T>): T {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(GOOGLE_MAP_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }
        return retrofit!!.create(serviceClass)
    }
}