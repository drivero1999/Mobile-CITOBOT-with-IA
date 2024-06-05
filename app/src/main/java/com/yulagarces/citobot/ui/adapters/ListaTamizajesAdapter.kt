package com.yulagarces.citobot.ui.adapters

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.jiangdg.demo.R
import com.yulagarces.citobot.data.models.Screening
import com.yulagarces.citobot.utils.Preferences

class ListaTamizajesAdapter(var listaTamizajes: ArrayList<Screening>) :

    RecyclerView.Adapter<ListaTamizajesAdapter.TamizajeViewHolder>() {
    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TamizajeViewHolder {
        val vista =
            LayoutInflater.from(parent.context).inflate(R.layout.list_tamizajes, parent, false)
        context = parent.context
        return TamizajeViewHolder(vista)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: TamizajeViewHolder, position: Int) {
        holder.lt_numero_documento.text = listaTamizajes[position].tam_fecha
        holder.nombrePaciente.text = listaTamizajes[position].niv_mensaje

        if (position % 2 == 0) {
            holder.rootView.setBackgroundResource(R.drawable.fondo_gris_azulado)
        } else {
            holder.rootView.setBackgroundResource(R.drawable.fondo_gris)
        }

        holder.itemView.setOnClickListener {
            val prefs: SharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)
            val editor = prefs.edit()
            editor.putString(Preferences().TAM_ID, listaTamizajes[position].tam_id)
            editor.putString(Preferences().TAM_PER_TIPO_ID, listaTamizajes[position].per_tip_id)
            editor.putString(Preferences().TAM_PER_IDENTIFICATION, listaTamizajes[position].per_identificacion)
            editor.putString(Preferences().TAM_PER_NOMBRE, listaTamizajes[position].per_primer_nombre)
            editor.putString(Preferences().TAM_FECHA, listaTamizajes[position].tam_fecha)
            editor.putString(Preferences().TAM_CONTRASTE, listaTamizajes[position].tam_contraste)
            editor.putString(Preferences().TAM_VPH, listaTamizajes[position].tam_vph)
            editor.putString(Preferences().TAM_NIV_ID, listaTamizajes[position].tam_niv_id.toString())
            editor.putString(Preferences().TAM_NIV_MENSAJE, listaTamizajes[position].niv_mensaje)
            editor.apply()
            val navController = Navigation.findNavController(
                context as Activity, R.id.nav_host_fragment_content_main
            )
            navController.navigate(R.id.action_nav_lista_tamizaje_to_nav_detalle_tamizaje)
        }
    }

    override fun getItemCount(): Int {
        return listaTamizajes.size
    }

    inner class TamizajeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var rootView: LinearLayout
        var lt_numero_documento: TextView
        var nombrePaciente: TextView

        init {
            rootView = itemView.findViewById(R.id.lt_root_view)
            lt_numero_documento = itemView.findViewById(R.id.lt_numero_documento)
            nombrePaciente = itemView.findViewById(R.id.lt_nombre_paciente)
        }
    }
}