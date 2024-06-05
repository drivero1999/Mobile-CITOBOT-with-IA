package com.yulagarces.citobot.data.models

import com.google.gson.annotations.SerializedName

data class Eps (
    @SerializedName("eps_id") var eps_id: Int,
    @SerializedName("eps_nombre") var eps_nombre: String
)