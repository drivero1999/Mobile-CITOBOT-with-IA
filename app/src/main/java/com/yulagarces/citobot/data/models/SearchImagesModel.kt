package com.yulagarces.citobot.data.models

import com.google.gson.annotations.SerializedName

data class SearchImagesModel (
    @SerializedName("ima_id") var ima_id: String,
    @SerializedName("ima_tam_id") var ima_tam_id: String,
    @SerializedName("ima_tipo") var ima_tipo: String,
    @SerializedName("ima_ruta") var ima_ruta: String
    )
