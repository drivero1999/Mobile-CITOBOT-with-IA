package com.yulagarces.citobot.data.models

import com.google.gson.annotations.SerializedName

data class Tamizajes (
    @SerializedName("tam_id") val tam_id: Int,
    @SerializedName("tam_pac_per_identificacion") val tam_pac_per_identificacion: String,
    @SerializedName("tam_usu_per_identificacion") val tam_usu_per_identificacion: String,
    @SerializedName("tam_fecha") val tam_fecha: String,
    @SerializedName("tam_contraste") val tam_contraste: String,
    @SerializedName("tam_vph") val tam_vph: String,
    @SerializedName("tam_vph_no_info") val tam_vph_no_info: String,
    @SerializedName("tam_niv_id") val tam_niv_id: String
)