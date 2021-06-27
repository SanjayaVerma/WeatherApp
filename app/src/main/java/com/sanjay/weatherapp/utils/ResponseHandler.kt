package com.sanjay.weatherapp.utils

import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

enum class ErrorCodes(val code: Int) {
    SocketTimeOut(-1),
}

open class ResponseHandler @Inject constructor() {
    fun <T : Any> handleSuccess(data: T): Resource<T> {
        return Resource.success(data)
    }

    fun <T : Any> handleException(e: Exception): Resource<T> {
        return when (e) {
            is HttpException -> Resource.error(getErrorMessage(e.code()), null)
            is SocketTimeoutException -> Resource.error(
                getErrorMessage(ErrorCodes.SocketTimeOut.code),
                null
            )
            is UnknownHostException -> Resource.error("Unable to fetch response", null)
            is ConnectException -> Resource.error("Unable to fetch response", null)
            else -> Resource.error(e.message!!, null)
        }
    }

    fun <T : Any> handleNoNetworkException(e: Exception): Resource<T> {
        return Resource.error("No Network Connection", null)
    }

    private fun getErrorMessage(code: Int): String {
        return when (code) {
            ErrorCodes.SocketTimeOut.code -> "Timeout"
            401 -> "Unauthorised"
            404 -> "Not found"
            else -> "Something went wrong"
        }
    }
}
