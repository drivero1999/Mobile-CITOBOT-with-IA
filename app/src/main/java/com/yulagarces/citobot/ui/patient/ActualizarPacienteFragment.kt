package com.yulagarces.citobot.ui.patient

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatSpinner
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.jiangdg.demo.R
import com.jiangdg.demo.databinding.FragmentActualizarPacienteBinding
import com.yulagarces.citobot.data.models.*
import com.yulagarces.citobot.data.responses.*
import com.yulagarces.citobot.data.services.ApiClient
import com.yulagarces.citobot.dialog.DatePickerFragment
import com.yulagarces.citobot.ui.adapters.CustomDropDownAdapter
import com.yulagarces.citobot.utils.Preferences
import com.yulagarces.citobot.utils.SessionManager
import kotlinx.android.synthetic.main.fragment_actualizar_paciente.view.*
import kotlinx.android.synthetic.main.fragment_crear_paciente.view.crp_crear
import kotlinx.android.synthetic.main.fragment_crear_paciente.view.crp_fecha_nacimiento_txt
import kotlinx.android.synthetic.main.fragment_crear_paciente.view.crp_id_txt
import kotlinx.android.synthetic.main.fragment_crear_paciente.view.crp_nuevo_paciente
import kotlinx.android.synthetic.main.fragment_crear_paciente.view.crp_primer_apellido_txt
import kotlinx.android.synthetic.main.fragment_crear_paciente.view.crp_primer_nombre_txt
import kotlinx.android.synthetic.main.fragment_crear_paciente.view.crp_segundo_apellido_txt
import kotlinx.android.synthetic.main.fragment_crear_paciente.view.crp_segundo_nombre_txt
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class ActualizarPacienteFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private var _binding: FragmentActualizarPacienteBinding? = null
    private val binding get() = _binding!!
    private var fechaNac :String = ""
    private lateinit var primerNombre :String
    private lateinit var otrosNombres :String
    private lateinit var primerApellido :String
    private lateinit var segundoApellido :String
    private lateinit var pacPerId :String
    private lateinit var tipo_id :String
    private lateinit var telefono :String
    private lateinit var celular :String
    private lateinit var correo :String
    private lateinit var contactoAlter :String
    private lateinit var telContactoAlter :String
    private var peso :Int = 0
    private var talla :Int = 0
    private var primerMens :Int = 0
    private var parejasSex :Int = 0
    private var epsId :Int = 0
    private lateinit var newDate :String
    private lateinit var userId :String

    private lateinit var apiClient: ApiClient
    private lateinit var sessionManager: SessionManager
    private lateinit var patientId : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentActualizarPacienteBinding.inflate(inflater, container, false)
        val root: View = binding.root
        apiClient = ApiClient()
        val prefs: SharedPreferences = requireActivity().getSharedPreferences(requireContext().getString(R.string.app_name), Context.MODE_PRIVATE)
        prefs.getString(Preferences().NOMBRE, "")
        prefs.getString(Preferences().APELLIDO, "")
        patientId=  prefs.getString(Preferences().IDENTIFICACION, "").toString()
        loadViews()
        loadEps()
        val sdf = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault())
        val currentDateandTime: String = sdf.format(Date())

        loadUser()
        binding.root.crp_crear.setOnClickListener{
            updateData()
        }
        return root
    }

    private fun loadUser() {
        apiClient.getApiService(requireContext()).getPatientById(patientId)
            .enqueue(object : Callback<PatientResponse> {
                override fun onFailure(call: Call<PatientResponse>, t: Throwable) {
                    // Error fetching posts
                }
                override fun onResponse(call: Call<PatientResponse>, response: Response<PatientResponse>) {
                    if (response.body()!!.objetoRespuesta.isEmpty()){
                    }
                    response.body()?.objetoRespuesta?.forEach { patient ->
                        tipo_id = patient.per_tip_id

                        binding.root.crp_id_txt.setText( patient.per_identificacion)
                        binding.root.crp_tipo_id_txt_.text = patient.per_tip_id
                        binding.root.crp_primer_nombre_txt.setText( patient.per_primer_nombre)
                        binding.root.crp_segundo_nombre_txt.setText( patient.per_otros_nombres)
                        binding.root.crp_primer_apellido_txt.setText( patient.per_primer_apellido)
                        binding.root.crp_segundo_apellido_txt.setText( patient.per_segundo_apellido)

                        binding.root.crp_fecha_nacimiento_txt.setText(stringDate(patient.pac_fecha_nacimiento))

                            Patient(patient.per_identificacion,patient.per_tip_id,patient.per_primer_nombre,patient.per_otros_nombres,
                                patient.per_primer_apellido,patient.per_segundo_apellido,patient.pac_per_identificacion,
                                patient.pac_fecha_nacimiento,patient.pac_direccion,patient.pac_telefono,patient.pac_celular,patient.pac_correo,
                                patient.pac_contacto_alternativo,patient.pac_telefono_contacto_alternativo,patient.pac_nivel_educacion,
                                patient.pac_estado_civil, patient.pac_situacion_laboral,patient.pac_eps_id,patient.pac_regimen_salud,patient.pac_estrato,
                                patient.pac_diabetes,patient.pac_fuma,patient.pac_peso,patient.pac_talla,patient.pac_primera_mestruacion,patient.pac_partos,
                                patient.pac_dispositivo_intrauterino,patient.pac_tiempo_insercion_DIU,patient.pac_anticonceptivos_orales,
                                patient.pac_parejas_sexuales,patient.pac_relacion_condon,patient.pac_vacuna_vph,patient.pac_ultima_citologia,
                                patient.pac_prueba_ADN_VPH,patient.pac_menopausia,patient.pac_infecciones_ts)
                    }
                }
            })
    }

    private fun stringDate(pacFechaNacimiento: String): String {
        val delim = "T"
        val list = pacFechaNacimiento.split(delim)
        newDate = list[0]
        return list[0]
    }
    private fun updateData() {
        pacPerId = binding.root.crp_id_txt.text.toString()
        primerNombre = binding.root.crp_primer_nombre_txt.text.toString()
        otrosNombres = binding.root.crp_segundo_nombre_txt.text.toString()
        primerApellido = binding.root.crp_primer_apellido_txt.text.toString()
        segundoApellido = binding.root.crp_segundo_apellido_txt.text.toString()

        sessionManager = SessionManager(requireContext())
        sessionManager.fetchUsuIdentification()?.let {
            userId = it
        }
         if (checkFieldsRequired(binding.root.crp_nuevo_paciente)) {
        val personas = PersonUpdate(
            per_tip_id= tipo_id,
            per_primer_nombre= primerNombre,
            per_otros_nombres= otrosNombres,
            per_primer_apellido= primerApellido,
            per_segundo_apellido= segundoApellido,
            per_gen_id= "1"
        )
        apiClient.getApiService(requireContext()).updatePersonById(pacPerId, personas)
            .enqueue(object : Callback<PersonUpdateResponse> {
                override fun onFailure(call: Call<PersonUpdateResponse>, t: Throwable) {
                    Toast.makeText(requireContext(),t.toString(), Toast.LENGTH_SHORT).show()
                }
                override fun onResponse(call: Call<PersonUpdateResponse>, response: Response<PersonUpdateResponse>) {
                    try {
                      //  Toast.makeText(requireContext(),"Paciente actualizado", Toast.LENGTH_SHORT).show()
                   //    val id = response.body()?.objetoRespuesta as PatienteRow

                        if (response.body()?.objetoRespuesta!!.affectedRows == "1"){
                            if (fechaNac.equals("0000-00-00")||fechaNac.isEmpty()){
                                fechaNac=newDate
                            }
                            val patient = Patient(per_identificacion = userId,
                                per_tip_id= tipo_id,
                                per_primer_nombre= primerNombre,
                                per_otros_nombres= otrosNombres,
                                per_primer_apellido= primerApellido,
                                per_segundo_apellido= segundoApellido,
                                pac_per_identificacion= pacPerId,
                                pac_fecha_nacimiento= fechaNac,
                                pac_direccion = "",
                                pac_telefono = "",
                                pac_celular = "",
                                pac_correo = "",
                                pac_contacto_alternativo = "",
                                pac_telefono_contacto_alternativo = "",
                                pac_nivel_educacion = "",
                                pac_estado_civil = "",
                                pac_situacion_laboral = "",
                                pac_eps_id = 1,
                                pac_regimen_salud = "",
                                pac_estrato = 0,
                                pac_diabetes = "",
                                pac_fuma = "",
                                pac_peso = 0,
                                pac_talla = 0,
                                pac_primera_mestruacion = 0,
                                pac_partos = "",
                                pac_dispositivo_intrauterino = "",
                                pac_tiempo_insercion_DIU = "",
                                pac_anticonceptivos_orales = "",
                                pac_parejas_sexuales = 0,
                                pac_relacion_condon = "",
                                pac_vacuna_vph = "",
                                pac_ultima_citologia = "",
                                pac_prueba_ADN_VPH = "",
                                pac_menopausia = "",
                                pac_infecciones_ts = ""
                            )
                            apiClient.getApiService(requireContext()).updatePatientById(patientId, patient)
                                .enqueue(object : Callback<PatientDtoResponse> {
                                    override fun onFailure(call: Call<PatientDtoResponse>, t: Throwable) {
                                        Toast.makeText(requireContext(),t.toString(), Toast.LENGTH_SHORT).show()
                                    }
                                    override fun onResponse(call: Call<PatientDtoResponse>, response: Response<PatientDtoResponse>) {
                                        try {
                                            Toast.makeText(requireContext(),"Paciente actualizado", Toast.LENGTH_SHORT).show()
                                            val id = response.body()?.objetoRespuesta as PatienteRow

                                        } catch (exception : Exception){
                                            //    Toast.makeText(requireContext(),exception.toString(), Toast.LENGTH_SHORT).show()
                                        }
                                        val navController = Navigation.findNavController(
                                            requireActivity(), R.id.nav_host_fragment_content_main
                                        )
                                        navController.navigate(R.id.nav_menu_principal)
                                        // navController.navigate(R.id.action_nav_crear_paciente_to_nav_menu_principal)
                                    }

                                })
                        }

                    } catch (exception : Exception){
                           Toast.makeText(requireContext(),exception.toString(), Toast.LENGTH_SHORT).show()
                    }

                }
            })} else {
             val toast = Toast.makeText(
                 context,
                 "Valide que el formulario este completo",
                 Toast.LENGTH_LONG
             )
             toast.show()
         }

    }

    private fun loadEps() {
        val data = ArrayList<Eps>()
        apiClient.getApiService(requireContext()).getEps()
            .enqueue(object : Callback<EpsResponse> {
                override fun onFailure(call: Call<EpsResponse>, t: Throwable) {
                }
                override fun onResponse(call: Call<EpsResponse>, response: Response<EpsResponse>) {
                    response.body()?.objetoRespuesta?.forEach { eps ->
                        data.add(
                            Eps(eps.eps_id, eps.eps_nombre)
                        )
                    }

                    val customDropDownAdapter = CustomDropDownAdapter(requireContext(), data)

            }
            })
    }

    fun checkFieldsRequired(viewGroup: ViewGroup?): Boolean {
        var valido = true
        for (i in 0 until viewGroup!!.childCount) {
            val view = viewGroup.getChildAt(i)
            if (view is RelativeLayout) {
                val vg = view as ViewGroup
                valido = checkFieldsRequired(vg)
                if (!valido) {
                    break
                }
            } else {
                if (view is EditText) {
                    if (!(view.tag != null && view.tag.toString() == "opcional") && view.text.toString().trim {
                            it <= ' ' } == "") {
                        view.error = "Este campo es requerido"
                        valido = false
                        break
                    }
                    if (view.tag != null && view.tag.toString() == "email" &&!validarEmail(view.text.toString().trim { it <= ' ' })) {
                        view.error = "Valide el formato del correo electrónico"
                        valido = false
                        break
                    }

                } else {
                    if (view is AppCompatSpinner) {
                        val spinner = view as Spinner
                        if (spinner.selectedItem.toString()
                                .compareTo("Seleccione una opción") == 0
                        ) {
                            (spinner.selectedView as TextView).error = "Seleccione una opción"
                            valido = false
                            break
                        }
                    }
                }
            }
        }
        return valido
    }

    private fun loadViews(){


        binding.root.crp_fecha_nacimiento_txt.setOnClickListener{
            val newFragment = DatePickerFragment.newInstance { _, year, month, day -> // +1 because January is zero
                    fechaNac = year.toString() + "-" + (month + 1) + "-" + day
                    binding.root.crp_fecha_nacimiento_txt.setText(fechaNac)
                   // fechaNac = selectedDate
                }
            newFragment.show(requireActivity().supportFragmentManager, "datePicker")
        }
    }

    fun validarEmail(email: String): Boolean {
        val pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }
}