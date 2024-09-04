package com.app.joyjittask

import android.graphics.Typeface
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.app.joyjittask.databinding.ActivityMainBinding
import com.app.joyjittask.model.MyData
import com.app.joyjittask.network.NetworkResult
import com.app.joyjittask.network.RetrofitClient
import com.app.joyjittask.repository.Repository
import com.app.joyjittask.viewModel.MainViewModel
import com.app.joyjittask.viewModel.factory.ViewModelFactory
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, ViewModelFactory(Repository(RetrofitClient.apiService)))[MainViewModel::class.java]
        val connectionManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectionManager.activeNetworkInfo
        if (networkInfo != null) {
            if (networkInfo.isConnected)
                viewModel.fetchData()
            else
                Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show()
        }

        viewModel.apiResponse.observe(this){
            when(it){
                is NetworkResult.Loading ->{
                    binding.progressBar.visibility = View.VISIBLE
                }
                is NetworkResult.Success ->{
                    binding.progressBar.visibility = View.GONE
                    Log.d("DATA", "onCreate: ${it.data}")
                    val message = it.data?.choices?.get(0)?.message
                    val completionToken = it.data?.usage?.completion_tokens ?: 0
                    val promptToken = it.data?.usage?.prompt_tokens ?: 0
                    val totalToken = it.data?.usage?.total_tokens ?: 1

                    binding.tvTitle.text = application.packageName

                    val gson = Gson()
                    val content = gson.fromJson(message?.content, MyData::class.java)
                    binding.tvTitleGenerated.text = content.titles[0]
                    binding.tvDesc.text = content.description

                    val percentage = ((completionToken.toDouble() / totalToken) * 100).toInt()

                    binding.circularProgressIndicator.progress = percentage

                    //spannable string
                    val spannableCompletionString = SpannableStringBuilder(completionToken.toString()).apply {
                        setSpan(StyleSpan(Typeface.BOLD), 0,length,SPAN_EXCLUSIVE_EXCLUSIVE)
                        setSpan(ForegroundColorSpan(getColor(R.color.yellow)),0,length,SPAN_EXCLUSIVE_EXCLUSIVE)
                        setSpan(RelativeSizeSpan(1.5f),0, length, SPAN_EXCLUSIVE_EXCLUSIVE)
                    }

                    binding.tvScorePercentage.text = spannableCompletionString .append("\n/$totalToken")

                    val spannableAverage = SpannableStringBuilder("Average").apply {
                        setSpan(StyleSpan(Typeface.BOLD), 0,length,SPAN_EXCLUSIVE_EXCLUSIVE)
                        setSpan(ForegroundColorSpan(getColor(R.color.yellow)),0,length,SPAN_EXCLUSIVE_EXCLUSIVE)
                        setSpan(RelativeSizeSpan(0.8f),0, length, SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                    binding.tvScore.text = SpannableStringBuilder("Overall Score").append("\n").append(spannableAverage)

                    val spannableHighGreen = SpannableStringBuilder("High").apply {
                        setSpan(StyleSpan(Typeface.BOLD), 0,length,SPAN_EXCLUSIVE_EXCLUSIVE)
                        setSpan(ForegroundColorSpan(getColor(R.color.green)),0,length,SPAN_EXCLUSIVE_EXCLUSIVE)
                        setSpan(RelativeSizeSpan(1.2f),0, length, SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                    binding.tvSearchVolume.text = SpannableStringBuilder("Search Volume").append("\n").append(spannableHighGreen)

                    val spannableHighRed = SpannableStringBuilder("High").apply {
                        setSpan(StyleSpan(Typeface.BOLD), 0,length,SPAN_EXCLUSIVE_EXCLUSIVE)
                        setSpan(ForegroundColorSpan(getColor(R.color.red)),0,length,SPAN_EXCLUSIVE_EXCLUSIVE)
                        setSpan(RelativeSizeSpan(1.2f),0, length, SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                    binding.tvCompetition.text = SpannableStringBuilder("Competition").append("\n").append(spannableHighRed)
                }
                is NetworkResult.Error ->{
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, it.msg, Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
}