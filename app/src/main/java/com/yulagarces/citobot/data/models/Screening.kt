package com.yulagarces.citobot.data.models

import com.google.gson.annotations.SerializedName

data class Screening (
    @SerializedName("per_tip_id") val per_tip_id: String,
    @SerializedName("per_identificacion") val per_identificacion: String,
    @SerializedName("per_primer_nombre") val per_primer_nombre: String,
    @SerializedName("tam_id") val tam_id: String,
    @SerializedName("tam_fecha") val tam_fecha: String,
    @SerializedName("tam_contraste") val tam_contraste: String,
    @SerializedName("tam_vph") val tam_vph: String,
    @SerializedName("tam_vph_no_info") val tam_vph_no_info: Int,
    @SerializedName("tam_niv_id") val tam_niv_id: Int,
    @SerializedName("niv_mensaje") val niv_mensaje: String
)
