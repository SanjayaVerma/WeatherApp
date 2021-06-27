package com.sanjay.weatherapp.data.api

import com.sanjay.weatherapp.data.model.WeatherModel
import retrofit2.Response

interface ApiHelper {
    suspend fun getWeatherData(
        lat: Int, lon: Int, appId: String
    ): Response<WeatherModel>

}