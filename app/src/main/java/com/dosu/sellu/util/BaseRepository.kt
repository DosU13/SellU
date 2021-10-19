package com.dosu.sellu.util

import android.util.Log
//import com.squareup.moshi.Moshi
//import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.dosu.sellu.data.network.NetworkResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.util.Objects.toString

//import retrofit2.HttpException
//import java.net.ConnectException
//import java.net.SocketTimeoutException

abstract class BaseRepository {
    suspend fun <T> safeApiCall(
        apiCall: suspend () -> T
    ): NetworkResponse<T> {
        return withContext(Dispatchers.IO) {
            try {
                NetworkResponse.Success(apiCall.invoke())
            }catch (e: Exception){
                e.printStackTrace()
                Log.e(Companion.TAG, "safeApiCall: NetworkResponse Failure${this.javaClass.name}", e)
                NetworkResponse.Failure(false, null, null)
            }catch (throwable: Throwable) {
                throwable.printStackTrace()
                when (throwable) {
//                    is HttpException -> {
//                        NetworkResponse.Failure(
//                            false, throwable.code(), convertErrorBody(throwable)
//                        )
//                    }
                    is SocketTimeoutException -> {
                        NetworkResponse.Failure(
                            false, 1, null
                        )
                    }
                    is ConnectException -> {
                        NetworkResponse.Failure(
                            false, 2, null
                        )
                    }
                    else -> {
                        NetworkResponse.Failure(true, null, null)
                    }
                }
            }
        }
    }

//    private fun convertErrorBody(throwable: HttpException): ErrorResponse? {
//        return try {
//            throwable.response()?.errorBody()?.source()?.let {
//                Log.d("BaseRepository", "convertErrorBody source: $it")
//                val moshiAdapter = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
//                    .adapter(ErrorResponse::class.java)
//                moshiAdapter.fromJson(it)
//            }
//        } catch (exception: Exception) {
//            Log.d("BaseRepository", "convertErrorBody $exception")
//            null
//        }
//    }
    companion object {
        private const val TAG = "BaseRepository"
    }
}