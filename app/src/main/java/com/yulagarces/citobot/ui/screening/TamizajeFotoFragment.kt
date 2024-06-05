package com.yulagarces.citobot.ui.screening

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.jiangdg.demo.R
import com.jiangdg.demo.databinding.FragmentTamizajeFotoBinding
import com.jiangdg.demo.ml.Model
import com.yulagarces.citobot.data.models.ImagesModel
import com.yulagarces.citobot.data.models.Tamizajes
import com.yulagarces.citobot.data.responses.ImageResponse
import com.yulagarces.citobot.data.responses.InsertScreeningResponse
import com.yulagarces.citobot.data.services.ApiClient
import com.yulagarces.citobot.model.Tamizaje
import com.yulagarces.citobot.ui.home.MainActivity
import com.yulagarces.citobot.utils.Preferences
import com.yulagarces.citobot.utils.SessionManager
import kotlinx.android.synthetic.main.fragment_tamizaje_foto.view.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.text.SimpleDateFormat
import java.util.*


class TamizajeFotoFragment : Fragment() {

    private var _binding: FragmentTamizajeFotoBinding? = null
    private val binding get() = _binding!!
    private var preferences: SharedPreferences? = null
    private var util = Preferences()

    // private var modo: Int? = null
    private var tamizaje = Tamizaje()
    private lateinit var apiClient: ApiClient
    private lateinit var path_one: String
    private lateinit var path_two: String
    private lateinit var path_three: String
    private lateinit var multipah_one: String
    private lateinit var multipah_two: String
    private lateinit var multipah_three: String
    private lateinit var multipah_four: String
    private lateinit var multipah_five: String
    private lateinit var modo: String
    private lateinit var id_user: String
    private lateinit var vph_user: String
    private lateinit var vphDetalle: String
    private lateinit var contraste: String
    private lateinit var idTam: String
    private lateinit var pathTotal: String
    private var sizeUploadImages: Int = 0
    private var sizeUploadImages2: Int = 0
    private var vph: Boolean = false

    private lateinit var tam_vph_no_info: String
    lateinit var listaFotos: ArrayList<String>
    private lateinit var sessionManager: SessionManager
    private lateinit var currentDateandTime: String
    private var resultIA: String = ""
    private var confidenceIA: String = ""
    private var imagenFinal: Bitmap? = null

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTamizajeFotoBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //Se traen las preferencias para ver el modo
        val prefs: SharedPreferences = requireActivity().getSharedPreferences(
            requireContext().getString(
                R.string.app_name
            ), Context.MODE_PRIVATE
        )
        modo = prefs.getString(Preferences().PREFERENCIAS_MODO, "").toString()
        contraste = prefs.getString(Preferences().CONTRASTE, "").toString()
        vph = prefs.getBoolean(Preferences().PREFERENCIAS_VPH, true)
        apiClient = ApiClient()
        ocultarImagenes()
        visualizarContenido()
        listaFotos = ArrayList()
        path_one = ""
        path_two = ""
        path_three = ""
        multipah_one = ""
        multipah_two = ""
        multipah_three = ""
        multipah_four = ""
        multipah_five = ""
        idTam = ""

        tam_vph_no_info = "1"
        sessionManager = SessionManager(requireContext())
        val activityLauncher = registerForActivityResult(
            StartActivityForResult()
        ) { result ->
            if (result.resultCode == 2) {
                val i1 = result.data
                if (i1 != null) {
                    val path = i1.getStringExtra("PATH")
                    val tipo = i1.getStringExtra("TYPE")
                    if (path != null) {
                        recibirFoto(path, tipo.toString())
                    }
                }
            }
        }

        binding.root.tf_img1.setOnCheckedChangeListener { _, checked ->
            if (checked){
                loadVph()
                binding.root.tf_img1.isChecked=true
                binding.root.tf_img2.isChecked=false
                binding.root.tf_img3.isChecked=false
            }
        }
        binding.root.tf_img2.setOnCheckedChangeListener { _, checked ->
            if (checked){
                binding.root.tf_img1.isChecked=false
                binding.root.tf_img2.isChecked=true
                binding.root.tf_img3.isChecked=false
                loadVph()
            }
        }
        binding.root.tf_img3.setOnCheckedChangeListener { _, checked ->
            if (checked){
                binding.root.tf_img1.isChecked=false
                binding.root.tf_img2.isChecked=false
                binding.root.tf_img3.isChecked=true
                loadVph()
            }
        }
        binding.root.tf_iniciar.setOnClickListener {
            val intent = Intent(activity, MainActivity::class.java)
            activityLauncher.launch(intent)
        }
        binding.root.tf_foto.setOnClickListener(View.OnClickListener {
            val intent = Intent(activity, MainActivity::class.java)
            activityLauncher.launch(intent)
        })
        binding.root.tf_enviar.setOnClickListener {
            if (Preferences.tamizajeNumeroFoto == 5) {
                uploadPictures()
            } else {
                sizeUploadImages2++
                if (vph) {
                    if (validarOpcion(binding.root.tf_verificacion) && validarOpcion2(binding.root.t2_vph)) {
                        imagenFinal?.let { image ->
                            classifyImage(image)
                        }
                        //    uploadPictures2(path_three)
                        loadNavigation("0")
                    } else {
                        Toast.makeText(context, "Selecciona la imagen y el VPH", Toast.LENGTH_LONG)
                            .show()
                    }
                } else {
                    if (validarOpcion(binding.root.tf_verificacion)) {
                        imagenFinal?.let { image ->
                            classifyImage(image)
                        }
                        //    uploadPictures2(path_three)
                        Toast.makeText(context, "Tamizaje creado", Toast.LENGTH_LONG).show()
                        loadNavigation("0")
                    } else {
                        Toast.makeText(context, "Selecciona la imagen", Toast.LENGTH_LONG).show()
                    }
                }

            }
        }

        binding.root.tf_siguiente.setOnClickListener {
            val numPhoto = Preferences.tamizajeNumeroFoto
            if (numPhoto == 5) {
                uploadPictures()
            } else {
                val intent = Intent(activity, MainActivity::class.java)
                activityLauncher.launch(intent)
            }
        }
        return root
    }

    private fun classifyImage(image: Bitmap) {
        try {
            // Redimensionar la imagen a 224x224
            val resizedImage = Bitmap.createScaledBitmap(image, 224, 224, true)

            // Convertir la imagen a escala de grises
            val grayImage = Bitmap.createBitmap(224, 224, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(grayImage)
            val paint = Paint()
            val colorMatrix = ColorMatrix()
            colorMatrix.setSaturation(0f)
            val filter = ColorMatrixColorFilter(colorMatrix)
            paint.colorFilter = filter
            canvas.drawBitmap(resizedImage, 0f, 0f, paint)

            // Crear un tensor de entrada para la imagen
            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 1), DataType.FLOAT32)
            val byteBuffer = ByteBuffer.allocateDirect(4 * 224 * 224)
            byteBuffer.order(ByteOrder.nativeOrder())

            // Preprocesar la imagen y cargarla en el buffer
            val intValues = IntArray(224 * 224)
            grayImage.getPixels(intValues, 0, grayImage.width, 0, 0, grayImage.width, grayImage.height)
            var pixel = 0
            for (i in 0 until 224) {
                for (j in 0 until 224) {
                    val value = intValues[pixel++]
                    // Extraer el valor del canal rojo (que será igual a los demás en una imagen en escala de grises)
                    byteBuffer.putFloat((value shr 16 and 0xFF) * (1.0f / 255.0f))
                }
            }

            // Cargar el buffer en el tensor de entrada
            inputFeature0.loadBuffer(byteBuffer)

            // Realizar la inferencia
            val model = Model.newInstance(requireContext())
            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer

            // Procesar los resultados de la inferencia
            val confidences = outputFeature0.floatArray

            // Encontrar la clase con la mayor confianza
            var maxPos = 0
            var maxConfidence = 0.0f
            for (i in confidences.indices) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i]
                    maxPos = i
                }
            }

            // Mostrar el resultado en la interfaz de usuario
            val classes = arrayOf("SIN RIESGO", "CON RIESGO")
            resultIA = classes[maxPos]

            var s = ""
            for (i in classes.indices) {
                s += String.format("%s: %.1f%%\n", classes[i], confidences[i] * 100)
            }
            confidenceIA = s.toString()

            // Liberar los recursos del modelo
            model.close()

        } catch (e: Exception) {
            // Manejar cualquier excepción
            e.printStackTrace()
            // Mostrar un mensaje de error al usuario si es necesario
            Toast.makeText(requireContext(), "Error inesperado al evaluar imagen", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getRiskLevelIdFromIA(resultIA: String): String {
        return when (resultIA) {
            "SIN RIESGO" -> "1"
            "CON RIESGO" -> "2"
            else -> "0" //Valor por defecto, se toma como "SIN DIAGNOSTICO"
        }
    }

    private fun loadVph() {
        if (vph) {
            binding.root.t2_contenedor_contraste_new!!.visibility = View.VISIBLE
        }
    }

    private fun uploadPictures() {
        val prefs: SharedPreferences = requireContext().getSharedPreferences(
            requireContext().getString(R.string.app_name),
            Context.MODE_PRIVATE
        )
        sessionManager.fetchUsuIdentification()?.let {
            id_user = it
        }

        val sdf = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault())
        currentDateandTime = sdf.format(Date())
        if (vph) {
            if (validarOpcion2(binding.root.t2_vph)) {
                val screening = Tamizajes(
                    tam_id = 1,
                    tam_pac_per_identificacion = prefs.getString(util.IDENTIFICACION, "")
                        .toString(),
                    tam_usu_per_identificacion = id_user,
                    tam_fecha = currentDateandTime,
                    tam_contraste = contraste,
                    tam_vph = vph_user,
                    tam_vph_no_info = tam_vph_no_info,
                    tam_niv_id = getRiskLevelIdFromIA(resultIA)
                )
                vphDetalle = vph_user

                apiClient.getApiService(requireContext()).createScreening(screening)
                    .enqueue(object : Callback<InsertScreeningResponse> {
                        override fun onFailure(call: Call<InsertScreeningResponse>, t: Throwable) {
                            Toast.makeText(requireContext(), t.toString(), Toast.LENGTH_SHORT)
                                .show()
                        }

                        override fun onResponse(
                            call: Call<InsertScreeningResponse>,
                            response: Response<InsertScreeningResponse>
                        ) {
                            idTam = response.body()?.objetoRespuesta!!.insertId
                            if (response.body()?.objetoRespuesta!!.affectedRows == "1") {
                                for (num in 0 until listaFotos.size) {

                                    uploadImages(
                                        listaFotos[num],
                                        "_" + (num + 1).toString() + ".jpg"
                                    )
                                }
                            }
                        }
                    })
            } else {
                val toast = Toast.makeText(context, "Diligencie el vph", Toast.LENGTH_LONG)
                toast.show()
            }
        } else {
            val screening = Tamizajes(
                tam_id = 1,
                tam_pac_per_identificacion = prefs.getString(util.IDENTIFICACION, "").toString(),
                tam_usu_per_identificacion = id_user,
                tam_fecha = currentDateandTime,
                tam_contraste = contraste,
                tam_vph = "Sin VPH",
                tam_vph_no_info = tam_vph_no_info,
                tam_niv_id = getRiskLevelIdFromIA(resultIA)
            )
            vphDetalle = "Sin VPH"
            apiClient.getApiService(requireContext()).createScreening(screening)
                .enqueue(object : Callback<InsertScreeningResponse> {
                    override fun onFailure(call: Call<InsertScreeningResponse>, t: Throwable) {
                        Toast.makeText(requireContext(), t.toString(), Toast.LENGTH_SHORT).show()
                    }

                    override fun onResponse(
                        call: Call<InsertScreeningResponse>,
                        response: Response<InsertScreeningResponse>
                    ) {
                        idTam = response.body()?.objetoRespuesta!!.insertId
                        if (response.body()?.objetoRespuesta!!.affectedRows == "1") {
                            for (num in 0 until listaFotos.size) {
                                uploadImages(listaFotos[num], "_" + (num + 1).toString() + ".jpg")
                                if (num == 4) {
                                    val toast =
                                        Toast.makeText(
                                            context,
                                            "Tamizaje creado",
                                            Toast.LENGTH_LONG
                                        )
                                    toast.show()
                                    loadNavigation("2")
                                }
                            }
                        }
                    }
                })
        }

    }

    fun recibirFoto(path: String, tipo: String) {
        Preferences.tamizajeNumeroFoto++

        val imgFile = File(path)
        pathTotal = path
        if (tipo == "gallery") {
            val string = imgFile.absolutePath
            val newPath: List<String> = string.split("/raw")
            pathTotal = if (newPath.size == 2) {
                newPath[1]
            } else {
                path
            }
        }
        if (!pathTotal.isNullOrEmpty()) {
            val myBitmap = BitmapFactory.decodeFile(pathTotal)
            imagenFinal = myBitmap
            if (modo == "0") {
                binding.root.tf_contenedor_entrenamiento!!.visibility = View.VISIBLE
                binding.root.tf_botones_entrena!!.visibility = View.VISIBLE
                binding.root.tf_contenedor_verificacion!!.visibility = View.GONE
                binding.root.tf_botones_verifica!!.visibility = View.GONE
                if (Preferences.tamizajeNumeroFoto == 5) {
                    binding.root.tf_siguiente!!.text = "Enviar"
                    binding.root.t2_vph!!.visibility = View.VISIBLE
                }
                var imagen = binding.root.tf_entrenamiento_img1
                when (Preferences.tamizajeNumeroFoto) {
                    1 -> {
                        tamizaje.path1 = pathTotal
                        multipah_one = pathTotal
                        listaFotos.add(pathTotal)
                    }

                    2 -> {
                        tamizaje.path2 = pathTotal
                        imagen = binding.root.tf_entrenamiento_img2
                        multipah_two = pathTotal
                        listaFotos.add(pathTotal)
                    }

                    3 -> {
                        tamizaje.path3 = pathTotal
                        imagen = binding.root.tf_entrenamiento_img3
                        multipah_three = pathTotal
                        listaFotos.add(pathTotal)
                    }

                    4 -> {
                        tamizaje.path4 = pathTotal
                        imagen = binding.root.tf_entrenamiento_img4
                        multipah_four = pathTotal
                        listaFotos.add(pathTotal)
                    }

                    5 -> {
                        tamizaje.path5 = pathTotal
                        imagen = binding.root.tf_entrenamiento_img5
                        multipah_five = pathTotal
                        listaFotos.add(pathTotal)
                        loadVph()
                    }

                    else -> {}
                }
                imagen!!.setImageBitmap(myBitmap)
                imagen.visibility = View.VISIBLE
            } else {
                var radio = binding.root.tf_img1
                val drawable: Drawable = BitmapDrawable(resources, myBitmap)
                binding.root.tf_contenedor_entrenamiento!!.visibility = View.GONE
                binding.root.tf_contenedor_verificacion!!.visibility = View.VISIBLE
                binding.root.tf_botones_entrena!!.visibility = View.GONE
                binding.root.tf_botones_verifica!!.visibility = View.VISIBLE
                if (Preferences.tamizajeNumeroFoto == 3) {
                    binding.root.tf_foto!!.visibility = View.INVISIBLE
                }
                when (Preferences.tamizajeNumeroFoto) {
                    1 -> {
                        tamizaje.path1 = pathTotal
                        path_one = pathTotal
                        binding.root.tf_iv_img1.setImageBitmap(myBitmap)
                    }

                    2 -> {
                        tamizaje.path2 = pathTotal
                        radio = binding.root.tf_img2
                        path_two = pathTotal
                        binding.root.tf_iv_img2.setImageBitmap(myBitmap)
                    }

                    3 -> {
                        tamizaje.path3 = pathTotal
                        radio = binding.root.tf_img3
                        binding.root.tf_iv_img3.setImageBitmap(myBitmap)
                        path_three = pathTotal
                    }

                    else -> {}
                }
                //   radio!!.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
                radio.visibility = View.VISIBLE
            }
            visualizarContenido()
        }
    }

    private fun ocultarImagenes() {
        Preferences.tamizajeNumeroFoto = 0
        binding.root.tf_entrenamiento_img1!!.visibility = View.GONE
        binding.root.tf_entrenamiento_img2!!.visibility = View.GONE
        binding.root.tf_entrenamiento_img3!!.visibility = View.GONE
        binding.root.tf_entrenamiento_img4!!.visibility = View.GONE
        binding.root.tf_entrenamiento_img5!!.visibility = View.GONE
        binding.root.tf_img1!!.visibility = View.GONE
        binding.root.tf_img2!!.visibility = View.GONE
        binding.root.tf_img3!!.visibility = View.GONE
    }

    private fun visualizarContenido() {
        if (Preferences.tamizajeNumeroFoto == 0) { //Si no se ha tomado ninguna foto se muestra el boton de iniciar
            binding.root.tf_iniciar!!.visibility = View.VISIBLE
            binding.root.tf_contenedor_entrenamiento!!.visibility = View.GONE
            binding.root.tf_contenedor_verificacion!!.visibility = View.GONE
            binding.root.tf_botones_entrena!!.visibility = View.GONE
            binding.root.tf_botones_verifica!!.visibility = View.GONE
        } else { //Si ya hay fotos, se muestra segun el modo configurado
            binding.root.tf_iniciar!!.visibility = View.GONE
        }

    }

    fun imageBase64(filepath: String): String {
        FileInputStream(filepath).use {
            val bytes = it.readBytes()
            return Base64.encodeToString(bytes, Base64.DEFAULT)
        }
    }

    private fun validarOpcion(viewGroup: ViewGroup?): Boolean {
        var valido = false
        if (binding.root.tf_img1.isChecked) {
            valido = true
            uploadPictures2(path_one)
        }
        if (binding.root.tf_img2.isChecked) {
            valido = true
            uploadPictures2(path_two)
        }
        if (binding.root.tf_img3.isChecked) {
            valido = true
            uploadPictures2(path_three)
        }

        for (i in 0 until viewGroup!!.childCount) {
            val view = viewGroup.getChildAt(i)
            if (view is RadioButton) {
                if (view.isChecked) {
                    valido = true
                    break
                }
            }
        }
        return valido
    }

    private fun uploadImages(path: String, idPicture: String) {
        val prefs: SharedPreferences = requireContext().getSharedPreferences(
            requireContext().getString(R.string.app_name),
            Context.MODE_PRIVATE
        )
        val image = ImagesModel(
            ima_tam_id = idTam,
            ima_tipo = prefs.getString(util.IDENTIFICACION, "") + "_" + idTam + idPicture,
            ima_ruta = prefs.getString(util.IDENTIFICACION, "") + "_" + idTam + idPicture
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
                    val file = File(path)
                    val contentResolver = requireContext().contentResolver
                    val requestFile = file.asRequestBody(
                        contentResolver.getType(Uri.fromFile(File(path)))?.toMediaTypeOrNull()
                    )
                    val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
                    val name = RequestBody.create(
                        "text/plain".toMediaTypeOrNull(),
                        prefs.getString(util.IDENTIFICACION, "") + "_" + idTam + idPicture
                    )

                    apiClient.getApiService(requireContext()).uploadImage(body, name)
                        .enqueue(object : Callback<ResponseBody> {
                            override fun onResponse(
                                call: Call<ResponseBody>,
                                response: Response<ResponseBody>
                            ) {
                                val responseCode = response.code()
                                if (responseCode == 200) {
                                    Toast.makeText(
                                        requireContext(),
                                        "Image uploaded",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    sizeUploadImages++
                                    loadNavigation("1")
                                }
                            }

                            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                Toast.makeText(
                                    requireContext(),
                                    "Failed due ${t.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                }
            })
    }


    private fun validarOpcion2(viewGroup: ViewGroup?): Boolean {
        var valido = false
        if (binding.root.radio_sin_vph.isChecked) {
            vph_user = "Sin VPH"
            valido = true
        }
        if (binding.root.radio_vph_positivo.isChecked) {
            vph_user = "Positivo"
            valido = true
        }
        if (binding.root.radio_vph_negativo.isChecked) {
            vph_user = "Negativo"
            valido = true
        }
        return valido
    }

    private fun uploadPictures2(path: String) {
        val prefs: SharedPreferences = requireContext().getSharedPreferences(
            requireContext().getString(R.string.app_name),
            Context.MODE_PRIVATE
        )
        sessionManager.fetchUsuIdentification()?.let {
            id_user = it
        }

        val sdf = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault())
        currentDateandTime = sdf.format(Date())
        if (vph) {
            if (validarOpcion2(binding.root.t2_vph)) {
                val screening = Tamizajes(
                    tam_id = 1,
                    tam_pac_per_identificacion = prefs.getString(util.IDENTIFICACION, "")
                        .toString(),
                    tam_usu_per_identificacion = id_user,
                    tam_fecha = currentDateandTime,
                    tam_contraste = contraste,
                    tam_vph = vph_user,
                    tam_vph_no_info = tam_vph_no_info,
                    tam_niv_id = getRiskLevelIdFromIA(resultIA)
                )
                vphDetalle = vph_user

                apiClient.getApiService(requireContext()).createScreening(screening)
                    .enqueue(object : Callback<InsertScreeningResponse> {
                        override fun onFailure(call: Call<InsertScreeningResponse>, t: Throwable) {
                            Toast.makeText(requireContext(), t.toString(), Toast.LENGTH_SHORT)
                                .show()
                        }

                        override fun onResponse(
                            call: Call<InsertScreeningResponse>,
                            response: Response<InsertScreeningResponse>
                        ) {
                            idTam = response.body()?.objetoRespuesta!!.insertId
                            //una
                            if (response.body()?.objetoRespuesta?.affectedRows == "1") {
                                val prefs: SharedPreferences =
                                    requireContext().getSharedPreferences(
                                        requireContext().getString(R.string.app_name),
                                        Context.MODE_PRIVATE
                                    )
                                val image = ImagesModel(
                                    ima_tam_id = idTam,
                                    ima_tipo = prefs.getString(
                                        util.IDENTIFICACION,
                                        ""
                                    ) + "_" + idTam + ".jpg",
                                    ima_ruta = prefs.getString(
                                        util.IDENTIFICACION,
                                        ""
                                    ) + "_" + idTam + ".jpg"
                                )

                                apiClient.getApiService(requireContext()).createImage(image)
                                    .enqueue(object : Callback<ImageResponse> {
                                        override fun onFailure(
                                            call: Call<ImageResponse>,
                                            t: Throwable
                                        ) {
                                            Toast.makeText(
                                                requireContext(),
                                                "create image $t",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }

                                        override fun onResponse(
                                            call: Call<ImageResponse>,
                                            response: Response<ImageResponse>
                                        ) {
                                            response.body()?.objetoRespuesta!!.insertId
                                            val file = File(path)
                                            val contentResolver = requireContext().contentResolver
                                            val requestFile = file.asRequestBody(
                                                contentResolver.getType(
                                                    Uri.fromFile(File(path))
                                                )?.toMediaTypeOrNull()
                                            )
                                            val body = MultipartBody.Part.createFormData(
                                                "file",
                                                file.name,
                                                requestFile
                                            )
                                            val name = RequestBody.create(
                                                "text/plain".toMediaTypeOrNull(),
                                                prefs.getString(
                                                    util.IDENTIFICACION,
                                                    ""
                                                ) + "_" + idTam + ".jpg"
                                            )

                                            apiClient.getApiService(requireContext())
                                                .uploadImage(body, name)
                                                .enqueue(object : Callback<ResponseBody> {
                                                    override fun onResponse(
                                                        call: Call<ResponseBody>,
                                                        response: Response<ResponseBody>
                                                    ) {
                                                        val responseCode = response.code()
                                                        if (responseCode == 200) {
                                                            Toast.makeText(
                                                                requireContext(),
                                                                "Image uploaded",
                                                                Toast.LENGTH_SHORT
                                                            )
                                                                .show()
                                                            sizeUploadImages2 = 1
                                                            loadNavigation("1")
                                                        }
                                                    }

                                                    override fun onFailure(
                                                        call: Call<ResponseBody>,
                                                        t: Throwable
                                                    ) {
                                                        Toast.makeText(
                                                            requireContext(),
                                                            "Failed due ${t.message}",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                })
                                        }
                                    })
                            }
                        }
                    })
            } else {
                val toast = Toast.makeText(context, "Diligencie el vph", Toast.LENGTH_LONG)
                toast.show()
            }
        } else {
            val screening = Tamizajes(
                tam_id = 1,
                tam_pac_per_identificacion = prefs.getString(util.IDENTIFICACION, "").toString(),
                tam_usu_per_identificacion = id_user,
                tam_fecha = currentDateandTime,
                tam_contraste = contraste,
                tam_vph = "Sin VPH",
                tam_vph_no_info = tam_vph_no_info,
                tam_niv_id = getRiskLevelIdFromIA(resultIA)
            )
            vphDetalle = "Sin VPH"

            apiClient.getApiService(requireContext()).createScreening(screening)
                .enqueue(object : Callback<InsertScreeningResponse> {
                    override fun onFailure(call: Call<InsertScreeningResponse>, t: Throwable) {
                        Toast.makeText(requireContext(), t.toString(), Toast.LENGTH_SHORT).show()
                    }

                    override fun onResponse(
                        call: Call<InsertScreeningResponse>,
                        response: Response<InsertScreeningResponse>
                    ) {
                        idTam = response.body()?.objetoRespuesta!!.insertId

                        if (response.body()?.objetoRespuesta?.affectedRows == "1") {
                            val prefs: SharedPreferences = requireContext().getSharedPreferences(
                                requireContext().getString(R.string.app_name),
                                Context.MODE_PRIVATE
                            )
                            val image = ImagesModel(
                                ima_tam_id = idTam,
                                ima_tipo = prefs.getString(
                                    util.IDENTIFICACION,
                                    ""
                                ) + "_" + idTam + ".jpg",
                                ima_ruta = prefs.getString(
                                    util.IDENTIFICACION,
                                    ""
                                ) + "_" + idTam + ".jpg"
                            )

                            apiClient.getApiService(requireContext()).createImage(image)
                                .enqueue(object : Callback<ImageResponse> {
                                    override fun onFailure(
                                        call: Call<ImageResponse>,
                                        t: Throwable
                                    ) {
                                        Toast.makeText(
                                            requireContext(),
                                            "create image $t",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }

                                    override fun onResponse(
                                        call: Call<ImageResponse>,
                                        response: Response<ImageResponse>
                                    ) {
                                        response.body()?.objetoRespuesta!!.insertId
                                        val file = File(path)
                                        val contentResolver = requireContext().contentResolver
                                        val requestFile = file.asRequestBody(
                                            contentResolver.getType(
                                                Uri.fromFile(File(path))
                                            )?.toMediaTypeOrNull()
                                        )
                                        val body = MultipartBody.Part.createFormData(
                                            "file",
                                            file.name,
                                            requestFile
                                        )
                                        val name = RequestBody.create(
                                            "text/plain".toMediaTypeOrNull(),
                                            prefs.getString(
                                                util.IDENTIFICACION,
                                                ""
                                            ) + "_" + idTam + ".jpg"
                                        )

                                        apiClient.getApiService(requireContext())
                                            .uploadImage(body, name)
                                            .enqueue(object : Callback<ResponseBody> {
                                                override fun onResponse(
                                                    call: Call<ResponseBody>,
                                                    response: Response<ResponseBody>
                                                ) {
                                                    val responseCode = response.code()
                                                    if (responseCode == 200) {
                                                        Toast.makeText(
                                                            requireContext(),
                                                            "Image uploaded",
                                                            Toast.LENGTH_SHORT
                                                        )
                                                            .show()
                                                        sizeUploadImages2=1
                                                        loadNavigation("1")
                                                    }
                                                }

                                                override fun onFailure(
                                                    call: Call<ResponseBody>,
                                                    t: Throwable
                                                ) {
                                                    Toast.makeText(
                                                        requireContext(),
                                                        "Failed due ${t.message}",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            })
                                    }
                                })
                        }

                    }
                })
        }
    }

    private fun loadNavigation(modoTamizaje: String) {
        if (idTam != "" && (sizeUploadImages == 5 || sizeUploadImages2 == 1)) {
            val prefs: SharedPreferences = requireContext().getSharedPreferences(
                requireContext().getString(R.string.app_name),
                Context.MODE_PRIVATE
            )
            val editor = prefs.edit()
            editor.putString(Preferences().TAM_ID, idTam)
            editor.putString(
                Preferences().TAM_PER_TIPO_ID,
                prefs.getString(util.TIPO_IDENTIFICACION, "")
            )
            editor.putString(
                Preferences().TAM_PER_IDENTIFICATION,
                prefs.getString(util.IDENTIFICACION, "")
            )
            editor.putString(Preferences().TAM_PER_NOMBRE, prefs.getString(util.NOMBRE, ""))
            editor.putString(Preferences().TAM_FECHA, currentDateandTime)
            editor.putString(Preferences().TAM_CONTRASTE, contraste)
            editor.putString(Preferences().TAM_VPH, vphDetalle)
            editor.putString(Preferences().TAM_NIV_ID, getRiskLevelIdFromIA(resultIA))
            editor.putString(Preferences().TAM_NIV_MENSAJE, resultIA)
            editor.apply()
            try {
                val navController = Navigation.findNavController(
                    context as Activity, R.id.nav_host_fragment_content_main
                )
                navController.navigate(R.id.action_nav_tamizaje_foto_to_nav_detalle_tamizaje)
            } catch (_: Exception) {
            }
        }
    }
}