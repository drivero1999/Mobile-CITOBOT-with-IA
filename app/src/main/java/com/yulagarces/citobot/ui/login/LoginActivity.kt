package com.yulagarces.citobot.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.jiangdg.demo.databinding.ActivityLoginBinding
import com.yulagarces.citobot.ui.home.MainMenuActivity
import com.yulagarces.citobot.data.services.ApiClient
import com.yulagarces.citobot.data.responses.UserResponse
import com.yulagarces.citobot.utils.SessionManager
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    private lateinit var sessionManager: SessionManager

    private lateinit var apiClient: ApiClient
    private var perIdentification :String = ""
    private var uperTipoIdsu :String = ""
    private var perPrimerNombre :String = ""
    private var perOtrosNombres :String = ""
    private var perPrimerApellido :String = ""
    private var perSegundoApellido :String = ""
    private var genNombre :String = ""
    private var usuUsuario :String = ""
    private var usuEmail :String = ""
    private var proNombre :String = ""
    private var usuRol :String = ""
    private var usuEstado :String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        apiClient = ApiClient()
        sessionManager = SessionManager(this)

        val view = binding.root
        setContentView(view)
    //    et_user.setText("sa@sa.com")
    //    et_password.setText("123456")
        auth = FirebaseAuth.getInstance()
        btn_login.setOnClickListener {
           signIn(et_user.text.toString(), et_password.text.toString())
        }
    }

    public override fun onStart() {
        super.onStart()
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user!!.getIdToken(false).addOnSuccessListener { result ->
                        val token = result.token
                        sessionManager.saveAuthToken(token.toString())
                        apiClient.getApiService(this).getUserByEmail(email)
                              .enqueue(object : Callback<UserResponse> {
                                  override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                                      Toast.makeText(this@LoginActivity, "Error, intente de nuevo$t", Toast.LENGTH_SHORT).show()
                                      Log.d("Error", t.toString())
                                  }
                                  override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                                      response.body()?.objetoRespuesta?.forEach { user ->
                                          perIdentification = user.per_identificacion
                                          uperTipoIdsu = user.per_tip_id
                                          perPrimerNombre = user.per_primer_nombre
                                          perOtrosNombres = user.per_otros_nombres
                                          perPrimerApellido = user.per_primer_apellido
                                          perSegundoApellido = user.per_segundo_apellido
                                          genNombre = user.gen_nombre
                                          usuUsuario = user.usu_usuario
                                          usuEmail = user.usu_email
                                          proNombre = user.usu_estado
                                          usuEstado = user.usu_estado
                                          usuRol = user.usu_rol
                                      }
                                      sessionManager.saveUser(perIdentification, uperTipoIdsu,perPrimerNombre, perOtrosNombres,
                                          perPrimerApellido, perSegundoApellido, genNombre, usuUsuario, usuEmail,
                                          proNombre, usuRol, usuEstado)

                                      val goHome = Intent(this@LoginActivity, MainMenuActivity::class.java)
                                      startActivity(goHome)
                                      finish()
                                  }
                              })
                    }
                } else {
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }


}