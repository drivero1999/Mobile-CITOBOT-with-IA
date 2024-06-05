package com.yulagarces.citobot.utils

import android.content.Context
import android.content.SharedPreferences
import com.jiangdg.demo.R

class SessionManager (context: Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)
    val PREFERENCIAS = "citobot"
    val PREFERENCIAS_VPH = "VPH"
    val PREFERENCIAS_MODO = "MODO"

    var tamizajeNumeroFoto = 0
    companion object {
        //user
        const val USER_TOKEN = "user_token"
        const val PER_IDENTICATION = "per_identificacion"
        const val PER_TIPO_ID = "per_tip_id"
        const val PER_PRIMER_NOMBRE = "per_primer_nombre"
        const val PER_OTROS_NOMBRES = "per_otros_nombres"
        const val PER_PRIMER_APELLIDO = "per_primer_apellido"
        const val PER_SEGUNDO_APELLIDO = "per_segundo_apellido"
        const val GEN_NOMBRE = "gen_nombre"
        const val USU_USUARIO = "usu_usuario"
        const val USU_EMAIL = "usu_email"
        const val PRO_NOMBRE = "pro_nombre"
        const val USU_ROL = "usu_rol"
        const val USU_ESTADO = "usu_estado"
        //paciente
        const val PATIENT_NAME = "patient_name"
        const val PATIENT_DOCUMENT = "patient_document"

    }

    fun saveAuthToken(token: String) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }

    fun saveUser(per_identification: String, per_tipo_id: String,per_primer_nombre: String,per_otros_nombres: String,
                 per_primer_apellido: String,per_segundo_apellido: String,gen_nombre: String,usu_usuario: String,
                 usu_email: String,pro_nombre: String,usu_rol: String,usu_estado: String) {
        val editor = prefs.edit()
        editor.putString(PER_IDENTICATION, per_identification)
        editor.putString(PER_TIPO_ID, per_tipo_id)
        editor.putString(PER_PRIMER_NOMBRE, per_primer_nombre)
        editor.putString(PER_OTROS_NOMBRES, per_otros_nombres)
        editor.putString(PER_PRIMER_APELLIDO, per_primer_apellido)
        editor.putString(PER_SEGUNDO_APELLIDO, per_segundo_apellido)
        editor.putString(GEN_NOMBRE, gen_nombre)
        editor.putString(USU_USUARIO, usu_usuario)
        editor.putString(USU_EMAIL, usu_email)
        editor.putString(PRO_NOMBRE, pro_nombre)
        editor.putString(USU_ROL, usu_rol)
        editor.putString(USU_ESTADO, usu_estado)
        editor.apply()
    }

    fun savePatientName(patientName: String) {
        val editor = prefs.edit()
        editor.putString(PATIENT_NAME, patientName)
        editor.apply()
    }
    fun fetchAuthToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }
    fun fetchUser(): String? {
        return prefs.getString(USU_USUARIO, null)
    }
    fun fetchRol(): String? {
        return prefs.getString(USU_ROL, null)
    }
    fun fetchUsuIdentification(): String? {
        return prefs.getString(PER_IDENTICATION, null)
    }
    fun fetchPatientName(): String? {
        return prefs.getString(PATIENT_NAME, null)
    }
}
