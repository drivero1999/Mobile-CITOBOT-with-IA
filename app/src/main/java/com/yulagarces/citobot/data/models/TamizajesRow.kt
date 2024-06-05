package com.yulagarces.citobot.data.models

import com.google.gson.annotations.SerializedName

data class TamizajesRow (
    @SerializedName("affectedRows") val affectedRows: String,
    @SerializedName("changeRows") val changeRows: String,
    @SerializedName("fieldCount") val fieldCount: String,
    @SerializedName("insertId") val insertId: String,
    @SerializedName("message") val message: String,
    @SerializedName("protocol41") val protocol41: String,
    @SerializedName("serverStatus") val serverStatus: String,
    @SerializedName("warningCount") val warningCount: String
    )
