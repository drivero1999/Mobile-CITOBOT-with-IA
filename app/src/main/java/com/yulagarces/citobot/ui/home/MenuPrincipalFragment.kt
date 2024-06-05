package com.yulagarces.citobot.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jiangdg.demo.R
import androidx.navigation.Navigation
import com.jiangdg.demo.databinding.FragmentMenuPrincipalBinding
import kotlinx.android.synthetic.main.fragment_menu_principal.view.*

class MenuPrincipalFragment : Fragment() {

    private var _binding: FragmentMenuPrincipalBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuPrincipalBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.root.mp_nuevo_paciente.setOnClickListener{
            val navController = Navigation.findNavController(
                requireActivity(), R.id.nav_host_fragment_content_main
            )
            navController.navigate(R.id.nav_crear_paciente)
        }
        binding.root.mp_nuevo_tamizaje.setOnClickListener{
            val navController = Navigation.findNavController(
                requireActivity(), R.id.nav_host_fragment_content_main
            )
            navController.navigate(R.id.action_nav_menu_principal_to_nav_consultar_paciente)
        }
        return root
    }
}