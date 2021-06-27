package com.sanjay.weatherapp.data.api

import com.sanjay.weatherapp.data.model.WeatherModel
import retrofit2.Response
import javax.inject.Inject

class ApiHelperImpl @Inject constructor(private val apiService: ApiService) : ApiHelper {
    override suspend fun getWeatherData(
        lat: Int, lon: Int, appId: String
    ): Response<WeatherModel> =
        apiService.getWeatherData(lat, lon, appId)
}