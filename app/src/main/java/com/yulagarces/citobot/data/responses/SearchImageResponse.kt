package com.yulagarces.citobot.data.responses

import com.google.gson.annotations.SerializedName
import com.yulagarces.citobot.data.models.*

data class SearchImageResponse (
    @SerializedName("codigoRespuesta")
    var codigoRespuesta: Int,

    @SerializedName("descripcionRespuesta")
    var descripcionRespuesta: String,

    @SerializedName("objetoRespuesta")
    var objetoRespuesta: List<SearchImagesModel>
)