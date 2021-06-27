package com.sanjay.weatherapp.data.api

import com.sanjay.weatherapp.data.model.WeatherModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("forecast?")
    suspend fun getWeatherData(
        @Query("lat") lat: Int,
        @Query("lon") lon: Int,
        @Query("APPID") appId: String,
    ): Response<WeatherModel>
}