package com.sanjay.weatherapp.data.repository

import com.sanjay.weatherapp.data.api.ApiHelper
import com.sanjay.weatherapp.data.model.WeatherModel
import com.sanjay.weatherapp.utils.Constants
import com.sanjay.weatherapp.utils.NetworkHelper
import com.sanjay.weatherapp.utils.Resource
import com.sanjay.weatherapp.utils.ResponseHandler
import retrofit2.Response
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val apiHelper: ApiHelper,
    private val responseHandler: ResponseHandler,
    private val networkHelper: NetworkHelper
) {
    suspend fun getWeatherData(
        lat: Int, lon: Int
    ): Resource<Response<WeatherModel>> {
        return try {
            if (networkHelper.isNetworkConnected()) {

                val response = apiHelper.getWeatherData(lat, lon, Constants.appId)
                if (response.isSuccessful) {
                    return responseHandler.handleSuccess(response)
                } else {
                    val message = if (response.code() == 500) {
                        "Something went wrong"
                    } else {
                        response.errorBody()?.string()?.let { errorBody ->
                            NetworkHelper.getErrorResponseMessage(
                                errorBody
                            )
                        }.toString()
                    }
                    return responseHandler.handleException(java.lang.Exception(message))
                }
            } else {
                return responseHandler.handleNoNetworkException(java.lang.Exception("No Network Connection"))
            }
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }

}