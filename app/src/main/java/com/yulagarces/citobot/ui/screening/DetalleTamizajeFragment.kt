package com.yulagarces.citobot.ui.screening

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.jiangdg.demo.R
import com.jiangdg.demo.databinding.FragmentDetalleTamizajeBinding
import com.yulagarces.citobot.data.models.ImageDto
import com.yulagarces.citobot.data.models.ImagesModel
import com.yulagarces.citobot.data.models.Tamizajes
import com.yulagarces.citobot.data.responses.*
import com.yulagarces.citobot.data.services.ApiClient
import com.yulagarces.citobot.ui.adapters.ListaImagenesAdapter
import com.yulagarces.citobot.ui.home.MainMenuActivity
import com.yulagarces.citobot.utils.Preferences
import com.yulagarces.citobot.utils.SessionManager
import kotlinx.android.synthetic.main.fragment_detalle_tamizaje.view.*
import kotlinx.android.synthetic.main.fragment_detalle_tamizaje.view.lt_recycler
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DetalleTamizajeFragment : Fragment() {

    private var _binding: FragmentDetalleTamizajeBinding? = null
    private val binding get() = _binding!!
    var preferences: SharedPreferences? = null
    var util = Preferences()
    private lateinit var apiClient: ApiClient
    private lateinit var sessionManager: SessionManager
    private lateinit var id: String
    private lateinit var perTipId: String
    private lateinit var perIdentification: String
    private lateinit var perPrimerNombre: String
    private lateinit var nivMensaje: String
    private lateinit var idUser: String
    private lateinit var vphUser: String
    private lateinit var tamVphNoInfo: String
    private lateinit var tamId: String
    private lateinit var newTamId: String
    private lateinit var contraste: String
    private var sizeImages: Int = 0

    var pathImages = ArrayList<String>()
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetalleTamizajeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        apiClient = ApiClient()
        vphUser = "Sin VPH"
        tamVphNoInfo = "1"

        val prefs: SharedPreferences = requireActivity().getSharedPreferences(requireContext().getString(R.string.app_name), Context.MODE_PRIVATE)
        tamId = prefs.getString(util.TAM_ID, "").toString()
        perTipId = prefs.getString(util.TAM_PER_TIPO_ID, "").toString()
        perIdentification = prefs.getString(util.TAM_PER_IDENTIFICATION, "").toString()
        perPrimerNombre = prefs.getString(util.TAM_PER_NOMBRE, "").toString()
        val tamFecha = prefs.getString(util.TAM_FECHA, "").toString()
        val tamContraste = prefs.getString(util.TAM_CONTRASTE, "").toString()
        val tamVph = prefs.getString(util.TAM_VPH, "").toString()
        nivMensaje = prefs.getString(util.TAM_NIV_MENSAJE, "").toString()

        val data = ArrayList<ImageDto>()
        pathImages = ArrayList()

        sessionManager = SessionManager(requireContext())

        binding.root.dt_tipo_id.text = perTipId
        binding.root.dt_num_doc.text = " $perIdentification"
        binding.root.dt_nombre.text = perPrimerNombre
        binding.root.dt_fecha.text = tamFecha
        binding.root.dp_contraste.text = tamContraste
        binding.root.dp_vph.text = tamVph
        binding.root.dt_tipo_riesgo.text = nivMensaje
        binding.root.dt_contenedor.setOnClickListener{
            val goHome = Intent(context, MainMenuActivity::class.java)
            startActivity(goHome)
            requireActivity().finish()
        }
        binding.root.lt_recycler.layoutManager = LinearLayoutManager(requireContext())

        binding.root.t2_vph_switch_new.text="+"
        binding.root.t2_vph_switch_new.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                vphUser =  "Positivo"
                binding.root.t2_vph_switch_new.text="+"
                tamVphNoInfo = "0"
            }else {
                vphUser = "Negativo"
                binding.root.t2_vph_switch_new.text="-"
                tamVphNoInfo = "0"
            }
        }
        apiClient.getApiService(requireContext()).getImageByTamId(tamId)
            .enqueue(object : Callback<SearchImageResponse> {
                override fun onFailure(call: Call<SearchImageResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), t.toString(), Toast.LENGTH_SHORT).show()
                }
                override fun onResponse(call: Call<SearchImageResponse>, response: Response<SearchImageResponse>) {

                    response.body()?.objetoRespuesta?.forEach { image ->
                        newTamId = image.ima_ruta
                        pathImages.add(image.ima_ruta)
                        apiClient.getApiService(requireContext()).getImageByName(image.ima_ruta)
                            .enqueue(object : Callback<ResponseBody> {
                                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                    // Log.INFO
                                }
                                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                                    val responseCode = response.code()
                                    val gson = Gson()
                                    val json = gson.fromJson(response.body()?.string(), FtpResponse::class.java)
                                    if (responseCode == 200) {
                                        sizeImages++
                                        data.add(ImageDto(url = json.url))

                                        val adapter = ListaImagenesAdapter(data)
                                        binding.root.lt_recycler.adapter = adapter
                                    } else {
                                        Toast.makeText(requireContext(),"Error uploading image",Toast.LENGTH_SHORT).show()
                                    }

                                }
                            })
                    }
                }
            })

        binding.root.newScreen.setOnClickListener {
            binding.root.dp_contenedor_vph.visibility = View.GONE
            binding.root.dp_contenedor_contraste.visibility = View.GONE
            binding.root.newScreen.visibility = View.GONE
            binding.root.lt_recycler.visibility = View.GONE
            binding.root.t2_contenedor_vph_new.visibility = View.VISIBLE
            binding.root.t2_contenedor_contraste_new.visibility = View.VISIBLE
            binding.root.contenedorButtons.visibility = View.VISIBLE
        }

        binding.root.cancelar.setOnClickListener {
            binding.root.dp_contenedor_vph.visibility = View.VISIBLE
            binding.root.dp_contenedor_contraste.visibility = View.VISIBLE
            binding.root.newScreen.visibility = View.VISIBLE
            binding.root.lt_recycler.visibility = View.VISIBLE

            binding.root.t2_contenedor_vph_new.visibility = View.GONE
            binding.root.t2_contenedor_contraste_new.visibility = View.GONE
            binding.root.contenedorButtons.visibility = View.GONE
        }

        binding.root.btn_guardar.setOnClickListener {
            createNewScreening()
        }
        return root
    }

    private fun createNewScreening() {
        sessionManager.fetchUsuIdentification()?.let {
            idUser = it
        }

        if (validarOpcion(binding.root.t2_contraste_new)) {
            val sdf = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault())
            val currentDateandTime: String = sdf.format(Date())

            val screening = Tamizajes(
                tam_id = 1,
                tam_pac_per_identificacion = perIdentification,
                tam_usu_per_identificacion = idUser,
                tam_fecha = currentDateandTime,
                tam_contraste = contraste,
                tam_vph = vphUser,
                tam_vph_no_info = tamVphNoInfo,
                tam_niv_id = getRiskLevelId(nivMensaje)
            )

            apiClient.getApiService(requireContext()).createScreening(screening)
                .enqueue(object : Callback<InsertScreeningResponse> {
                    override fun onFailure(call: Call<InsertScreeningResponse>, t: Throwable) {
                        Toast.makeText(requireContext(), t.toString(), Toast.LENGTH_SHORT).show()
                    }
                    override fun onResponse(
                        call: Call<InsertScreeningResponse>,
                        response: Response<InsertScreeningResponse>
                    ) {
                        id = response.body()?.objetoRespuesta!!.insertId
                        createNewImage(id)
                    }
                })
        }
    }

    private fun getRiskLevelId(nivMensaje: String): String {
        return when (nivMensaje) {
            "SIN RIESGO" -> "1"
            "CON RIESGO" -> "2"
            else -> "0" // Valor por defecto, se toma como "SIN DIAGNOSTICO"
        }
    }

    fun createNewImage(id: String) {
        for (num in 0 until sizeImages) {
            if(sizeImages==1){
                pathImages[num]
                uploadImages(id, ".jpg",pathImages[num])
            }else {
                uploadImages(id, "_"+(num + 1).toString()+".jpg",pathImages[num])
            }
            if (num==4 || sizeImages==1){
                val toast =
                    Toast.makeText(context, "Nuevo tamizaje creado", Toast.LENGTH_LONG)
                toast.show()
                val navController = Navigation.findNavController(
                    requireActivity(), R.id.nav_host_fragment_content_main
                )
                navController.navigate(R.id.action_nav_detalle_tamizaje_to_nav_lista_tamizaje)
            }
        }
    }

    private fun uploadImages(idTam: String, idPicture: String, pathImage: String) {
        val image = ImagesModel(
            ima_tam_id = idTam,
            ima_tipo = pathImage,
            ima_ruta = pathImage
        )
        apiClient.getApiService(requireContext()).createImage(image)
            .enqueue(object : Callback<ImageResponse> {
                override fun onFailure(call: Call<ImageResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "create image $t", Toast.LENGTH_LONG).show()
                }
                override fun onResponse(
                    call: Call<ImageResponse>,
                    response: Response<ImageResponse>
                ) {
                    response.body()?.objetoRespuesta!!.insertId
                }
            })
    }

    private fun validarOpcion(viewGroup: ViewGroup?): Boolean {
        var valido = false

        if (binding.root.radio_sin_contraste_new.isChecked){
            contraste = "Sin Contraste"
            valido = true
        }
        if (binding.root.radio_acido_new.isChecked){
            contraste = "Ácido Acético"
            valido = true
        }
        if (binding.root.radio_lugol_new.isChecked){
            contraste = "Lugol"
            valido = true
        }
        if (binding.root.radio_acido_lugol_new.isChecked){
            contraste = "Ácido acético + Lugol"
            valido = true
        }
        return valido
    }
}