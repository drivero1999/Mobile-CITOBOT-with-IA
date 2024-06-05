package com.yulagarces.citobot.data.services
import com.yulagarces.citobot.data.responses.ImageResponse

data class ResultUpload(
    val message: String,
    val data: List<ImageResponse>
)
