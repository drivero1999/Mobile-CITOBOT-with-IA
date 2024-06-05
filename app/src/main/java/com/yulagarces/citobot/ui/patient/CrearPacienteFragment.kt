package com.yulagarces.citobot.ui.patient

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
import com.jiangdg.demo.databinding.FragmentCrearPacienteBinding
import com.yulagarces.citobot.data.models.*
import com.yulagarces.citobot.data.responses.PatientDtoResponse
import com.yulagarces.citobot.data.responses.PersonResponse
import com.yulagarces.citobot.data.services.ApiClient
import com.yulagarces.citobot.dialog.DatePickerFragment
import com.yulagarces.citobot.utils.SessionManager
import kotlinx.android.synthetic.main.fragment_crear_paciente.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


class CrearPacienteFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private var _binding: FragmentCrearPacienteBinding? = null
    private val binding get() = _binding!!
    private lateinit var tipoId :String
    private lateinit var fechaNac :String
    private lateinit var primerNombre :String
    private lateinit var otrosNombres :String
    private lateinit var primerApellido :String
    private lateinit var segundoApellido :String
    private lateinit var pacPerId :String
    private var epsId :Int = 0
    private lateinit var infecciones :String
    private var userId :String = ""

    private lateinit var apiClient: ApiClient
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCrearPacienteBinding.inflate(inflater, container, false)
        val root: View = binding.root
        apiClient = ApiClient()
        loadViews()
        binding.root.crp_tipo_id_txt.onItemSelectedListener = this
        val sdf = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault())
        val currentDateandTime: String = sdf.format(Date())
        sessionManager = SessionManager(requireContext())
        sessionManager.fetchUsuIdentification()?.let {
            userId = it
        }
        binding.root.crp_crear.setOnClickListener{
            pacPerId = binding.root.crp_id_txt.text.toString()
            primerNombre = binding.root.crp_primer_nombre_txt.text.toString()
            otrosNombres = binding.root.crp_segundo_nombre_txt.text.toString()
            primerApellido = binding.root.crp_primer_apellido_txt.text.toString()
            segundoApellido = binding.root.crp_segundo_apellido_txt.text.toString()


           if (checkFieldsRequired(binding.root.crp_nuevo_paciente)) {
               val personas = Person(per_identificacion = pacPerId,
                   per_tip_id= binding.root.crp_tipo_id_txt.selectedItem.toString(),
                   per_primer_nombre= primerNombre,
                   per_otros_nombres= otrosNombres,
                   per_primer_apellido= primerApellido,
                   per_segundo_apellido= segundoApellido,
                   per_gen_id= "1",
                   per_fecha_insercion = currentDateandTime
               )
               apiClient.getApiService(requireContext()).createPerson(personas)
                   .enqueue(object : Callback<PersonResponse> {
                       override fun onFailure(call: Call<PersonResponse>, t: Throwable) {
                       //    Toast.makeText(requireContext(),t.toString(), Toast.LENGTH_SHORT).show()
                       }
                       override fun onResponse(call: Call<PersonResponse>, response: Response<PersonResponse>) {
                           if (response.body()?.codigoRespuesta == 0){
                               val patient = Patient(
                                   per_identificacion = pacPerId,
                                   per_tip_id = binding.root.crp_tipo_id_txt.selectedItem.toString(),
                                   per_primer_nombre = primerNombre,
                                   per_otros_nombres = otrosNombres,
                                   per_primer_apellido = primerApellido,
                                   per_segundo_apellido = segundoApellido,
                                   pac_per_identificacion = pacPerId,
                                   pac_fecha_nacimiento = fechaNac,
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
                               apiClient.getApiService(requireContext()).createPatient(patient)
                                   .enqueue(object : Callback<PatientDtoResponse> {
                                       override fun onFailure(call: Call<PatientDtoResponse>, t: Throwable) {
                                           Toast.makeText(requireContext(),t.toString(), Toast.LENGTH_SHORT).show()
                                       }
                                       override fun onResponse(call: Call<PatientDtoResponse>, response: Response<PatientDtoResponse>) {
                                           try {
                                               Toast.makeText(requireContext(),"Paciente creado", Toast.LENGTH_SHORT).show()
                                               val id = response.body()?.objetoRespuesta as PatienteRow

                                           } catch (exception : Exception){
                                           //    Toast.makeText(requireContext(),exception.toString(), Toast.LENGTH_SHORT).show()
                                           }
                                           val navController = Navigation.findNavController(
                                               requireActivity(), R.id.nav_host_fragment_content_main
                                           )
                                          // navController.navigate(R.id.nav_menu_principal)
                                           navController.navigate(R.id.action_nav_crear_paciente_to_nav_menu_principal)
                                       }

                                   })
                           }else{
                               Toast.makeText(requireContext(),"Ha ocurrido un error, por favor intenta nuevamente", Toast.LENGTH_SHORT).show()
                           }
                       }
                   })
           } else {
                val toast = Toast.makeText(
                    context,
                    "Valide que el formulario este completo",
                    Toast.LENGTH_LONG
                )
                toast.show()
            }
        }
        return root
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
        binding.root.crp_tipo_id_txt.onItemSelectedListener = this

        //Items para spinner tipo identificacion
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.crp_tipo_id_list,
            R.layout.item_spinner
        )
        adapter.setDropDownViewResource(R.layout.item_spinner)
        binding.root.crp_tipo_id_txt.adapter = adapter
        tipoId = binding.root.crp_tipo_id_txt.selectedItem.toString()


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