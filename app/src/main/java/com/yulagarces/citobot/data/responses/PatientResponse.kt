package com.yulagarces.citobot.data.responses

import com.google.gson.annotations.SerializedName
import com.yulagarces.citobot.data.models.Patient
import com.yulagarces.citobot.data.models.User

data class PatientResponse (
    @SerializedName("codigoRespuesta")
    var codigoRespuesta: Int,

    @SerializedName("descripcionRespuesta")
    var descripcionRespuesta: String,

    @SerializedName("objetoRespuesta")
    var objetoRespuesta: List<Patient>
)