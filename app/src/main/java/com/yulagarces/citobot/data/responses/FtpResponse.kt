package com.yulagarces.citobot.data.responses

import com.google.gson.annotations.SerializedName
import com.yulagarces.citobot.data.models.ImagesModel
import com.yulagarces.citobot.data.models.Screening
import com.yulagarces.citobot.data.models.User

data class FtpResponse (
    @SerializedName("url")
    var url: String,
    @SerializedName("code")
    var code: String,
    @SerializedName("message")
    var message: String
)