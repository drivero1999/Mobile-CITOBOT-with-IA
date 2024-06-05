package com.yulagarces.citobot.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jiangdg.demo.R
import com.yulagarces.citobot.data.models.ImageDto
import com.yulagarces.citobot.ui.adapters.ListaImagenesAdapter.PacienteViewHolder
import com.yulagarces.citobot.utils.SessionManager

class ListaImagenesAdapter(var listaPacientes: ArrayList<ImageDto>) :
    RecyclerView.Adapter<PacienteViewHolder>() {
    private lateinit var sessionManager: SessionManager
    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PacienteViewHolder {
        val vista =
            LayoutInflater.from(parent.context).inflate(R.layout.list_images, parent, false)
        context = parent.context
        sessionManager = SessionManager(context)
        return PacienteViewHolder(vista)
    }

    override fun onBindViewHolder(holder: PacienteViewHolder, position: Int) {
        Glide.with(context)
            .load(listaPacientes[position].url)
            .into(holder.imgTam)
        holder.itemView.setOnClickListener {
        }
    }

    override fun getItemCount(): Int {
        return listaPacientes.size
    }

    inner class PacienteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var rootView: LinearLayout
        var imgTam: ImageView

        init {
            imgTam = itemView.findViewById(R.id.imgTam)
            rootView = itemView.findViewById(R.id.lp_root_view)
        }
    }
}