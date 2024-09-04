package com.app.joyjittask.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.joyjittask.model.ResponseModel
import com.app.joyjittask.network.NetworkResult
import com.app.joyjittask.repository.Repository
import kotlinx.coroutines.launch

class MainViewModel(private val repository: Repository) : ViewModel() {
    private val TAG = "response"

    private val liveDataResponse = MutableLiveData<NetworkResult<ResponseModel>>()
    val apiResponse = liveDataResponse

    fun fetchData() = viewModelScope.launch {
        liveDataResponse.value = NetworkResult.Loading()
        try {
            val response = repository.fetchData()
            if (response.isSuccessful)
                liveDataResponse.value = NetworkResult.Success(response.body()!!)
            else
                liveDataResponse.value = NetworkResult.Error(response.message())
        } catch (e: Exception) {
            Log.d(TAG, "fetchData: $e")
            liveDataResponse.value = NetworkResult.Error(e.message.toString())
        }
    }

}