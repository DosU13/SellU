package com.dosu.sellu.data.network

import com.dosu.sellu.util.ErrorResponse

/**
 *  handle api errors
 */
sealed class NetworkResponse<out T> {

    data class Success<out T>(val value: T) : NetworkResponse<T>()

    data class Failure(
        val isNetworkError: Boolean,
        val errorCode: Int?,
        val errorBody: ErrorResponse?
    ) : NetworkResponse<Nothing>()
}