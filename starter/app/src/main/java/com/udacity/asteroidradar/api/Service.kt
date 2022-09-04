package com.udacity.asteroidradar.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET



private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .baseUrl(Constants.BASE_URL)
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .build()

interface AsteroidService {

    @GET("neo/rest/v1/feed?api_key=${Constants.API_KEY}")
    suspend fun getAsteroid(): String

    @GET("planetary/apod?api_key=${Constants.API_KEY}")
    suspend fun getPictureOfTheDay():PictureOfDay

}

object AsteroidApi {
    val retrofitService: AsteroidService by lazy {
        retrofit.create(AsteroidService::class.java)
    }
}