package com.yulagarces.citobot.ui.patient
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.jiangdg.demo.databinding.FragmentConsultarPacienteBinding
import com.yulagarces.citobot.data.services.ApiClient
import com.yulagarces.citobot.data.models.Patient
import com.yulagarces.citobot.data.responses.PatientResponse
import com.yulagarces.citobot.ui.adapters.ListaPacientesAdapter
import com.yulagarces.citobot.utils.SessionManager
import kotlinx.android.synthetic.main.fragment_consultar_paciente.view.cop_buscar
import kotlinx.android.synthetic.main.fragment_consultar_paciente.view.cop_filtro
import kotlinx.android.synthetic.main.fragment_consultar_paciente.view.cop_recycler
import kotlinx.android.synthetic.main.fragment_consultar_paciente.view.tvUser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConsultarMainFragment : Fragment() {

    private var _binding: FragmentConsultarPacienteBinding? = null
    private val binding get() = _binding!!
    private lateinit var apiClient: ApiClient
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConsultarPacienteBinding.inflate(inflater, container, false)
        val root: View = binding.root

        sessionManager = SessionManager(requireContext())
        apiClient = ApiClient()
        binding.root.cop_buscar.setOnClickListener {
            val data = ArrayList<Patient>()
            binding.root.tvUser.visibility = View.GONE
            apiClient.getApiService(requireContext()).getPatientById(binding.root.cop_filtro.text.toString())
                .enqueue(object : Callback<PatientResponse> {
                    override fun onFailure(call: Call<PatientResponse>, t: Throwable) {
                        // Error fetching posts
                    }
                    override fun onResponse(call: Call<PatientResponse>, response: Response<PatientResponse>) {
                        if (response.body()!!.objetoRespuesta.isEmpty()){
                            binding.root.tvUser.visibility = View.VISIBLE
                            binding.root.tvUser.text = "Paciente no registrado"
                        }
                        val recyclerview = binding.root.cop_recycler
                        recyclerview.layoutManager = LinearLayoutManager(requireContext())
                        response.body()?.objetoRespuesta?.forEach { patient ->
                            data.add(
                                Patient(patient.per_identificacion,patient.per_tip_id,patient.per_primer_nombre,patient.per_otros_nombres,
                                patient.per_primer_apellido,patient.per_segundo_apellido,patient.pac_per_identificacion,
                                patient.pac_fecha_nacimiento,patient.pac_direccion,patient.pac_telefono,patient.pac_celular,patient.pac_correo,
                                    patient.pac_contacto_alternativo,patient.pac_telefono_contacto_alternativo,patient.pac_nivel_educacion,
                                    patient.pac_estado_civil, patient.pac_situacion_laboral,patient.pac_eps_id,patient.pac_regimen_salud,patient.pac_estrato,
                                    patient.pac_diabetes,patient.pac_fuma,patient.pac_peso,patient.pac_talla,patient.pac_primera_mestruacion,patient.pac_partos,
                                    patient.pac_dispositivo_intrauterino,patient.pac_tiempo_insercion_DIU,patient.pac_anticonceptivos_orales,
                                    patient.pac_parejas_sexuales,patient.pac_relacion_condon,patient.pac_vacuna_vph,patient.pac_ultima_citologia,
                                    patient.pac_prueba_ADN_VPH,patient.pac_menopausia,patient.pac_infecciones_ts)
                            )
                        }
                        val adapter =
                            ListaPacientesAdapter(
                                data
                            )
                        recyclerview.adapter = adapter
                    }
                })
        }

        return root
    }


}