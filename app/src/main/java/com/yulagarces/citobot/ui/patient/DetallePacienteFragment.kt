package com.yulagarces.citobot.ui.patient

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.jiangdg.demo.R
import com.jiangdg.demo.databinding.FragmentDetallePacienteBinding
import com.yulagarces.citobot.utils.Preferences
import kotlinx.android.synthetic.main.fragment_detalle_paciente.view.*

class DetallePacienteFragment : Fragment() {

    private var _binding: FragmentDetallePacienteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetallePacienteBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val prefs: SharedPreferences = requireActivity().getSharedPreferences(requireContext().getString(R.string.app_name), Context.MODE_PRIVATE)
        binding.root.dp_tipo_nombre_txt.text = prefs.getString(Preferences().NOMBRE, "")
        binding.root.dp_apellido_txt.text = prefs.getString(Preferences().APELLIDO, "")
        binding.root.dp_tipo_id_txt.text = prefs.getString(Preferences().TIPO_IDENTIFICACION, "")
        binding.root.dp_id_txt.text = prefs.getString(Preferences().IDENTIFICACION, "")

        binding.root.dp_contenedor_actualizar.setOnClickListener {
            val navController = Navigation.findNavController(
                requireActivity(), R.id.nav_host_fragment_content_main
            )
            navController.navigate(R.id.nav_actualizar_paciente)
        }
        binding.root.dp_contenedor_historial.setOnClickListener {
            val navController = Navigation.findNavController(
                requireActivity(), R.id.nav_host_fragment_content_main
            )
            navController.navigate(R.id.nav_lista_tamizaje)
        }
        binding.root.dp_contenedor_tamizaje.setOnClickListener{
            val navController = Navigation.findNavController(
                requireActivity(), R.id.nav_host_fragment_content_main
            )
            navController.navigate(R.id.nav_tamizaje_paso2)
        }
        binding.root.dp_informacion.setOnClickListener{}
        return root
    }


}