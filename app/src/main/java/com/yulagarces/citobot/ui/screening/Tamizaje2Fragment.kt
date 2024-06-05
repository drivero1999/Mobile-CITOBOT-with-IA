package com.yulagarces.citobot.ui.screening

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.jiangdg.demo.R
import com.jiangdg.demo.databinding.FragmentTamizaje2Binding
import com.yulagarces.citobot.data.models.Tamizajes
import com.yulagarces.citobot.data.responses.InsertScreeningResponse
import com.yulagarces.citobot.data.services.ApiClient
import com.yulagarces.citobot.utils.Preferences
import com.yulagarces.citobot.utils.SessionManager
import kotlinx.android.synthetic.main.fragment_tamizaje1.view.*
import kotlinx.android.synthetic.main.fragment_tamizaje2.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class Tamizaje2Fragment : Fragment() {

    private var _binding: FragmentTamizaje2Binding? = null
    private val binding get() = _binding!!
    var preferences: SharedPreferences? = null
    var util = Preferences()
    private lateinit var apiClient: ApiClient
    private lateinit var id: String
    private lateinit var id_user: String
    private lateinit var vph_user: String
    private lateinit var tam_vph_no_info: String
    private lateinit var contraste: String
    private lateinit var sessionManager: SessionManager

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTamizaje2Binding.inflate(inflater, container, false)
        val root: View = binding.root
        apiClient = ApiClient()

        preferences = requireActivity().getSharedPreferences(requireContext().getString(R.string.app_name), Context.MODE_PRIVATE)
        val prefs: SharedPreferences = requireActivity().getSharedPreferences(requireContext().getString(R.string.app_name), Context.MODE_PRIVATE)
        val vph = prefs.getBoolean(util.PREFERENCIAS_VPH, true)
        val nombre = prefs.getString(util.NOMBRE, "")
        val apellido = prefs.getString(util.APELLIDO, "")
        vph_user = "Sin VPH"
        tam_vph_no_info = "1"
        sessionManager = SessionManager(requireContext())

        sessionManager.fetchPatientName()?.let {
            binding.root.t2_nombre.text = it
        }
        val sdf = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault())
        val currentDateandTime: String = sdf.format(Date())
        if (!vph) {
            binding.root.t2_contenedor_vph.visibility = View.GONE
        }
        binding.root.t2_vph_switch.text="+"
        binding.root.t2_vph_switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                vph_user =  "Positivo"
                binding.root.t2_vph_switch.text="+"
                tam_vph_no_info = "0"
            }else {
                vph_user = "Negativo"
                binding.root.t2_vph_switch.text="-"
                tam_vph_no_info = "0"
            }
        }
        binding.root.t2_continuar.setOnClickListener {
            sessionManager.fetchUsuIdentification()?.let {
                id_user = it
            }
            validarOpcion(binding.root.t2_contraste)
            val navController = Navigation.findNavController(
                requireActivity(), R.id.nav_host_fragment_content_main
            )
            // navController.navigate(R.id.nav_menu_principal)
            navController.navigate(R.id.nav_tamizaje_foto)

        }
        return root
    }

    private fun validarOpcion(viewGroup: ViewGroup?): Boolean {
        var valido = false
        val prefs: SharedPreferences = requireContext().getSharedPreferences(requireContext().getString(R.string.app_name), Context.MODE_PRIVATE)
        val editor = prefs.edit()
        if (binding.root.radio_sin_contraste.isChecked){
            contraste = "Sin Contraste"
            editor.putString(Preferences().CONTRASTE, contraste)
            valido = true
        }
        if (binding.root.radio_acido.isChecked){
            contraste = "Ácido Acético"
            editor.putString(Preferences().CONTRASTE, contraste)
            valido = true
        }
        if (binding.root.radio_lugol.isChecked){
            contraste = "Lugol"
            editor.putString(Preferences().CONTRASTE, contraste)
            valido = true
        }
        if (binding.root.radio_acido_lugol.isChecked){
            contraste = "Ácido acético + Lugol"
            editor.putString(Preferences().CONTRASTE, contraste)
            valido = true
        }
        editor.apply()
        return valido
    }

}