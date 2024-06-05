package com.yulagarces.citobot.ui.screening

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.jiangdg.demo.R
import com.jiangdg.demo.databinding.FragmentListaTamizajeBinding
import com.yulagarces.citobot.data.services.ApiClient
import com.yulagarces.citobot.data.models.Screening
import com.yulagarces.citobot.data.responses.ScreeningResponse
import com.yulagarces.citobot.ui.adapters.ListaTamizajesAdapter
import com.yulagarces.citobot.utils.Preferences
import kotlinx.android.synthetic.main.fragment_consultar_paciente.view.*
import kotlinx.android.synthetic.main.fragment_lista_tamizaje.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListaTamizajeFragment : Fragment() {

    private var _binding: FragmentListaTamizajeBinding? = null
    private val binding get() = _binding!!

    lateinit var listaTamizajes: ArrayList<Screening>
    private lateinit var apiClient: ApiClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListaTamizajeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        listaTamizajes = ArrayList()
        val data = ArrayList<Screening>()
        apiClient = ApiClient()
            val prefs: SharedPreferences = requireActivity().getSharedPreferences(requireContext().getString(
                R.string.app_name), Context.MODE_PRIVATE)
            val identificacions= prefs.getString(Preferences().IDENTIFICACION, "")
            apiClient.getApiService(requireContext()).getScreeningById(identificacions.toString())
                .enqueue(object : Callback<ScreeningResponse> {
                    override fun onFailure(call: Call<ScreeningResponse>, t: Throwable) {
                        // Error fetching posts
                        Toast.makeText(requireContext(), t.toString(), Toast.LENGTH_SHORT).show()
                    }
                    override fun onResponse(call: Call<ScreeningResponse>, response: Response<ScreeningResponse>) {
                        if (response.body()!!.objetoRespuesta.isEmpty()){
                            binding.root.tvTamizajes.visibility = View.VISIBLE
                            binding.root.tvTamizajes.text = "Sin tamizajes"
                        }
                        response.body()?.objetoRespuesta?.forEach { screening ->
                           binding.root.lt_recycler.layoutManager = LinearLayoutManager(context)
                           data.add(
                               Screening(screening.per_tip_id,screening.per_identificacion,screening.per_primer_nombre,screening.tam_id, screening.tam_fecha,
                               screening.tam_contraste, screening.tam_vph, screening.tam_vph_no_info, screening.tam_niv_id, screening.niv_mensaje)
                           )
                            data.sortedByDescending { it.tam_fecha }
                        }
                        val adapter = ListaTamizajesAdapter(data)
                        binding.root.lt_recycler.adapter = adapter
                    }
                })

        return root
    }

}