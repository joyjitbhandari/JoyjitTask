package com.app.joyjittask.network

sealed class NetworkResult<T>(val data: T? = null, val msg: String? = null) {
    class Loading<T>() : NetworkResult<T>()
    class Success<T>(data : T): NetworkResult<T>(data)
    class Error<T>(msg:String): NetworkResult<T>(msg=msg)
}