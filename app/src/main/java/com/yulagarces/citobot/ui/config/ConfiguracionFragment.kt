package com.yulagarces.citobot.ui.config

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jiangdg.demo.R
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.jiangdg.demo.databinding.FragmentConfiguracionBinding
import com.yulagarces.citobot.utils.Preferences
import kotlinx.android.synthetic.main.fragment_configuracion.view.*
import kotlinx.android.synthetic.main.fragment_tamizaje2.view.*

class ConfiguracionFragment : Fragment() {

    var util = Preferences()
    private var _binding: FragmentConfiguracionBinding? = null
    private val binding get() = _binding!!
  
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConfiguracionBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val prefs: SharedPreferences = requireActivity().getSharedPreferences(requireContext().getString(R.string.app_name), Context.MODE_PRIVATE)
        val vph = prefs.getBoolean(util.PREFERENCIAS_VPH, true)
        val modo = prefs.getString(util.PREFERENCIAS_MODO, "").toString()
        val camera = prefs.getString(util.CAMERA, "").toString()
        binding.root.cfg_info_switch.isChecked = vph
        if (modo == "0") {
            binding.root.cfg_radio_entrenamiento.isChecked = true
        } else {
            binding.root.cfg_radio_verificacion.isChecked = true
        }
        if (camera == "0") {
            binding.root.cfg_radio_device_camera.isChecked = true
        } else {
            binding.root.cfg_radio_usb_camera.isChecked = true
        }
        if (vph){
            binding.root.cfg_info_switch.text="Si"
        }else{
            binding.root.cfg_info_switch.text="No"
        }
        binding.root.cfg_info_switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                binding.root.cfg_info_switch.text="Si"
            }else {
                binding.root.cfg_info_switch.text="No"
            }
        }
        binding.root.cfg_guardar.setOnClickListener {
            val editor = prefs.edit()
            editor.putBoolean(util.PREFERENCIAS_VPH, binding.root.cfg_info_switch.isChecked)
            val opcion = if (binding.root.cfg_radio_entrenamiento.isChecked) 0 else 1
            editor.putString(util.PREFERENCIAS_MODO, opcion.toString())
            editor.apply()
            val cameraOption = if (binding.root.cfg_radio_device_camera.isChecked) 0 else 1
            editor.putString(util.CAMERA, cameraOption.toString())
            editor.apply()
            val toast = Toast.makeText(context, "Se guardaron las preferencias", Toast.LENGTH_LONG)
            toast.show()
            val navController = Navigation.findNavController(
               requireActivity(), R.id.nav_host_fragment_content_main
            )
            navController.navigate(R.id.nav_menu_principal)
        }
        return root
    }

}