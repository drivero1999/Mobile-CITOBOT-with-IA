package com.yulagarces.citobot.ui.screening

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.jiangdg.demo.R
import com.jiangdg.demo.databinding.FragmentTamizaje1Binding
import com.yulagarces.citobot.utils.SessionManager
import kotlinx.android.synthetic.main.fragment_tamizaje1.view.*

class Tamizaje1Fragment : Fragment() {

    private var _binding: FragmentTamizaje1Binding? = null
    private val binding get() = _binding!!

    private lateinit var sessionManager: SessionManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTamizaje1Binding.inflate(inflater, container, false)
        val root: View = binding.root

        sessionManager = SessionManager(requireContext())
        sessionManager.fetchPatientName()?.let {
          binding.root.t1_nombre.text = it
        }
        binding.root.t1_iniciar.setOnClickListener{
            val navController = Navigation.findNavController(
                requireActivity(), R.id.nav_host_fragment_content_main
            )
            navController.navigate(R.id.nav_menu_principal)
            navController.navigate(R.id.nav_tamizaje_paso2)

        }
        return root
    }
}