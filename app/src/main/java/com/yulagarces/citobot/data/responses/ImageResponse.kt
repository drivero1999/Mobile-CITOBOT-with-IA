package com.yulagarces.citobot.data.responses

import com.google.gson.annotations.SerializedName
import com.yulagarces.citobot.data.models.ImagesModel
import com.yulagarces.citobot.data.models.Screening
import com.yulagarces.citobot.data.models.TamizajesRow
import com.yulagarces.citobot.data.models.User

data class ImageResponse (
    @SerializedName("codigoRespuesta")
    var codigoRespuesta: Int,

    @SerializedName("descripcionRespuesta")
    var descripcionRespuesta: String,

    @SerializedName("objetoRespuesta")
    var objetoRespuesta: TamizajesRow
)