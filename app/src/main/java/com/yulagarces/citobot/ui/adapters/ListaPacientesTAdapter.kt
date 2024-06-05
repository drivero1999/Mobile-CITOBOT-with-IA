package com.yulagarces.citobot.ui.adapters

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.jiangdg.demo.R
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.Navigation
import com.yulagarces.citobot.data.models.Patient
import com.yulagarces.citobot.utils.Preferences
import com.yulagarces.citobot.utils.SessionManager
import kotlinx.android.synthetic.main.fragment_configuracion.view.*

class ListaPacientesTAdapter(var listaPacientes: ArrayList<Patient>) :
    RecyclerView.Adapter<ListaPacientesTAdapter.PacienteViewHolder>() {
    private lateinit var sessionManager: SessionManager
    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PacienteViewHolder {
        val vista =
            LayoutInflater.from(parent.context).inflate(R.layout.list_pacientes, parent, false)
        context = parent.context
        sessionManager = SessionManager(context)
        return PacienteViewHolder(vista)
    }

    override fun onBindViewHolder(holder: PacienteViewHolder, position: Int) {
        holder.nombre.text = listaPacientes[position].per_primer_nombre+ " "+ listaPacientes[position].per_primer_apellido
        holder.tipoDoc.text = listaPacientes[position].per_tip_id
        holder.noDoc.text = listaPacientes[position].per_identificacion
        if (position % 2 == 0) {
            holder.rootView.setBackgroundResource(R.drawable.fondo_gris_azulado)
        } else {
            holder.rootView.setBackgroundResource(R.drawable.fondo_gris)
        }

        holder.itemView.setOnClickListener {
            val navController = Navigation.findNavController(
                context as Activity, R.id.nav_host_fragment_content_main
            )
            navController.navigate(R.id.action_nav_consultar_paciente_to_nav_detalle_paciente)

            val prefs: SharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

            val editor = prefs.edit()
            editor.putString(Preferences().NOMBRE, listaPacientes[position].per_primer_nombre)
            editor.putString(Preferences().APELLIDO, listaPacientes[position].per_primer_apellido)
            editor.putString(Preferences().IDENTIFICACION, listaPacientes[position].per_identificacion)
            editor.putString(Preferences().TIPO_IDENTIFICACION, listaPacientes[position].per_tip_id)
            editor.apply()
        }
    }

    override fun getItemCount(): Int {
        return listaPacientes.size
    }

    inner class PacienteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var rootView: LinearLayout
        var nombre: TextView
        var tipoDoc: TextView
        var noDoc: TextView

        init {
            nombre = itemView.findViewById(R.id.lp_nombre)
            tipoDoc = itemView.findViewById(R.id.lp_tipo_documento)
            noDoc = itemView.findViewById(R.id.lp_numero_documento)
            rootView = itemView.findViewById(R.id.lp_root_view)
        }
    }
}