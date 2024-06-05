package com.yulagarces.citobot.ui.home

import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView
import com.jiangdg.demo.R
import com.jiangdg.demo.databinding.ActivityMainMenuBinding
import com.yulagarces.citobot.ui.config.PurchaseConfirmationDialogFragment
import com.yulagarces.citobot.utils.SessionManager
import kotlinx.android.synthetic.main.activity_main_menu.*


class MainMenuActivity : AppCompatActivity() {
    private var mAppBarConfiguration: AppBarConfiguration? = null
    private var binding: ActivityMainMenuBinding? = null

    private lateinit var sessionManager: SessionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainMenuBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        val toolbar=binding!!.appBarMain.toolbar
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_menu)
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        sessionManager = SessionManager(this)

        val drawer = binding!!.drawerLayout
        val navigationView = binding!!.navView
        mAppBarConfiguration = AppBarConfiguration.Builder(
            R.id.nav_menu_principal, R.id.nav_consultar_paciente_t,
            R.id.nav_crear_paciente, R.id.nav_configuracion,
            R.id.nav_logout
        )
            .setOpenableLayout(drawer)
            .build()
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main)
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration!!)
        NavigationUI.setupWithNavController(navigationView, navController)
        navigationView.itemIconTintList = null
        navigationView.setNavigationItemSelectedListener { menuItem ->
            drawer.closeDrawers()
            menuItem.isChecked = true
            when (menuItem.itemId) {
                R.id.nav_menu_principal -> navController.navigate(R.id.nav_menu_principal)
                R.id.nav_consultar_paciente_t -> navController.navigate(R.id.nav_consultar_paciente_t)
                R.id.nav_crear_paciente -> navController.navigate(R.id.nav_crear_paciente)
                R.id.nav_configuracion -> navController.navigate(R.id.nav_configuracion)
                R.id.nav_logout -> {
                    PurchaseConfirmationDialogFragment().show(
                        supportFragmentManager, "")
                }
            }
            true
        }


       loadTitlesDrawer(navigationView)
        loadHeader()
    }

    private fun loadTitlesDrawer(navigationView: NavigationView) {
        val tools: MenuItem = navigationView.menu.findItem(R.id.homeStr)
        val patiente: MenuItem = navigationView.menu.findItem(R.id.patientesStr)
        val config: MenuItem = navigationView.menu.findItem(R.id.configStr)
        val sesion: MenuItem = navigationView.menu.findItem(R.id.sesionStr)
        val spanString = SpannableString(tools.title)
        val patienteString = SpannableString(patiente.title)
        val configString = SpannableString(config.title)
        val sesionString = SpannableString(sesion.title)
        spanString.setSpan(ForegroundColorSpan(ContextCompat.getColor(this,R.color.principal)), 0, spanString.length, 0)
        patienteString.setSpan(ForegroundColorSpan(ContextCompat.getColor(this,R.color.principal)), 0, patienteString.length, 0)
        configString.setSpan(ForegroundColorSpan(ContextCompat.getColor(this,R.color.principal)), 0, configString.length, 0)
        sesionString.setSpan(ForegroundColorSpan(ContextCompat.getColor(this,R.color.principal)), 0, sesionString.length, 0)
        tools.title = spanString
        patiente.title = patienteString
        config.title = configString
        sesion.title = sesionString
    }

    private fun loadHeader() {
        val tvUser: TextView = nav_view.getHeaderView(0).findViewById(R.id.nombreUsuario)
        val tvRol: TextView = nav_view.getHeaderView(0).findViewById(R.id.cargoUsuario)
        sessionManager.fetchUser()?.let {
            tvUser.text = it
        }
        sessionManager.fetchRol()?.let {
            tvRol.text = it
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main)
        return (NavigationUI.navigateUp(navController, mAppBarConfiguration!!)
                || super.onSupportNavigateUp())
    }
}