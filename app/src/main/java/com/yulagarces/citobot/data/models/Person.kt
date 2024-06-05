package com.yulagarces.citobot.data.models

import com.google.gson.annotations.SerializedName

data class Person (
    @SerializedName("per_identificacion") var per_identificacion: String,
    @SerializedName("per_tip_id") var per_tip_id: String,
    @SerializedName("per_primer_nombre") var per_primer_nombre: String,
    @SerializedName("per_otros_nombres") var per_otros_nombres: String,
    @SerializedName("per_primer_apellido") var per_primer_apellido: String,
    @SerializedName("per_segundo_apellido") var per_segundo_apellido: String,
    @SerializedName("per_gen_id") var per_gen_id: String,
    @SerializedName("per_fecha_insercion") var per_fecha_insercion: String
)