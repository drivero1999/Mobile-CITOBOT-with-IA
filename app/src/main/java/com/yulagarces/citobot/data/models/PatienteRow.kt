package com.yulagarces.citobot.data.models

import com.google.gson.annotations.SerializedName

data class PatienteRow (
    @SerializedName("fieldCount") val fieldCount: String,
    @SerializedName("affectedRows") val affectedRows: String,
    @SerializedName("insertId") val insertId: String,
    @SerializedName("serverStatus") val serverStatus: String,
    @SerializedName("warningCount") val warningCount: String,
    @SerializedName("message") val message: String,
    @SerializedName("protocol41") val protocol41: String,
    @SerializedName("changedRows") val changedRows: String
    )
