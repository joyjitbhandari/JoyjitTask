package com.app.joyjittask.model

data class ResponseModel(
    val choices: List<Choice>,
    val created: Int,
    val id: String,
    val model: String,
    val `object`: String,
    val system_fingerprint: Any,
    val usage: Usage
)