package com.app.joyjittask.repository

import com.app.joyjittask.model.ResponseModel
import com.app.joyjittask.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class Repository(private val apiService: ApiService) {

    suspend fun fetchData(): Response<ResponseModel> {
        return withContext(Dispatchers.IO) { apiService.fetchData()}
    }

}