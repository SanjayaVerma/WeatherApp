package com.sanjay.weatherapp.data.model

import com.google.gson.annotations.SerializedName


data class WeatherModel(
    @SerializedName("cod") var cod: String,
    @SerializedName("message") var message: Int,
    @SerializedName("cnt") var cnt: Int,
    @SerializedName("list") var list: List<DataList>,
    @SerializedName("city") var city: City
)

data class Main(
    @SerializedName("temp") var temp: Double,
    @SerializedName("feels_like") var feelsLike: Double,
    @SerializedName("temp_min") var tempMin: Double,
    @SerializedName("temp_max") var tempMax: Double,
    @SerializedName("pressure") var pressure: Int,
    @SerializedName("sea_level") var seaLevel: Int,
    @SerializedName("grnd_level") var grndLevel: Int,
    @SerializedName("humidity") var humidity: Int,
    @SerializedName("temp_kf") var tempKf: Double
)

data class Weather(
    @SerializedName("id") var id: Int,
    @SerializedName("main") var main: String,
    @SerializedName("description") var description: String,
    @SerializedName("icon") var icon: String
)


data class Clouds(
    @SerializedName("all") var all: Int
)


data class Wind(
    @SerializedName("speed") var speed: Double,
    @SerializedName("deg") var deg: Int,
    @SerializedName("gust") var gust: Double

)


data class Rain(
    @SerializedName("3h") var h: Double
)

data class Sys(
    @SerializedName("pod") var pod: String
)

data class DataList(
    @SerializedName("dt") var dt: Int,
    @SerializedName("main") var main: Main,
    @SerializedName("weather") var weather: List<Weather>,
    @SerializedName("clouds") var clouds: Clouds,
    @SerializedName("wind") var wind: Wind,
    @SerializedName("visibility") var visibility: Int,
    @SerializedName("pop") var pop: Double,
    @SerializedName("rain") var rain: Rain,
    @SerializedName("sys") var sys: Sys,
    @SerializedName("dt_txt") var dtTxt: String
)

data class Coord(
    @SerializedName("lat") var lat: Int,
    @SerializedName("lon") var lon: Int
)

data class City(
    @SerializedName("id") var id: Int,
    @SerializedName("name") var name: String,
    @SerializedName("coord") var coord: Coord,
    @SerializedName("country") var country: String,
    @SerializedName("population") var population: Int,
    @SerializedName("timezone") var timezone: Int,
    @SerializedName("sunrise") var sunrise: Int,
    @SerializedName("sunset") var sunset: Int
)