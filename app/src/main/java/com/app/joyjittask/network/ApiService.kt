package com.app.joyjittask.network

import com.app.joyjittask.model.ResponseModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("b/6HBE")
    suspend fun fetchData() : Response<ResponseModel>
}