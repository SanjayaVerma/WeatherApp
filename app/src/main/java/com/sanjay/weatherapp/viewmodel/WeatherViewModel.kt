package com.sanjay.weatherapp.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sanjay.weatherapp.data.model.WeatherModel
import com.sanjay.weatherapp.data.repository.WeatherRepository
import com.sanjay.weatherapp.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class WeatherViewModel @ViewModelInject constructor(
    private val repository: WeatherRepository
) :
    ViewModel() {
    fun getWeatherData(
        lat: Int, lon: Int
    ): LiveData<Resource<Response<WeatherModel>>> {
        val dataList = MutableLiveData<Resource<Response<WeatherModel>>>()
        viewModelScope.launch {
            val responseList = repository.getWeatherData(lat, lon)
            dataList.value = responseList
        }
        return dataList
    }
}