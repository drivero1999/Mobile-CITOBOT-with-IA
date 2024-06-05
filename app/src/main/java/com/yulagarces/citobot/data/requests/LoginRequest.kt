package com.yulagarces.citobot.data.requests

import com.google.gson.annotations.SerializedName

data class LoginRequest (
    @SerializedName("email") var email: String
)