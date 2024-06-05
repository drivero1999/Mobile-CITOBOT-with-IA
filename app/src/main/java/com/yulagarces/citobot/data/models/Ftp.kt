package com.yulagarces.citobot.data.models

import com.google.gson.annotations.SerializedName

data class Ftp (
    @SerializedName("nombre") val nombre: String,
    @SerializedName("base64") val base64: String
)