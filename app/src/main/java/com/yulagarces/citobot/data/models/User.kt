package com.yulagarces.citobot.data.models

import com.google.gson.annotations.SerializedName

data class User (
    @SerializedName("per_identificacion") var per_identificacion: String,
    @SerializedName("per_tip_id") var per_tip_id: String,
    @SerializedName("per_primer_nombre") var per_primer_nombre: String,
    @SerializedName("per_otros_nombres") var per_otros_nombres: String,
    @SerializedName("per_primer_apellido") var per_primer_apellido: String,
    @SerializedName("per_segundo_apellido") var per_segundo_apellido: String,
    @SerializedName("gen_nombre") var gen_nombre: String,
    @SerializedName("usu_usuario") var usu_usuario: String,
    @SerializedName("usu_email") var usu_email: String,
    @SerializedName("pro_nombre") var pro_nombre: String,
    @SerializedName("usu_rol") var usu_rol: String,
    @SerializedName("usu_estado") var usu_estado: String
)