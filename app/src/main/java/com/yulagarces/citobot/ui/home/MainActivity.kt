package com.yulagarces.citobot.ui.home

import android.Manifest.permission.*
import android.content.Intent
import android.os.Bundle
import android.os.PowerManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import com.gyf.barlibrary.ImmersionBar
import com.jiangdg.ausbc.utils.ToastUtils
import com.jiangdg.ausbc.utils.Utils
import com.jiangdg.demo.R
import com.jiangdg.demo.databinding.ActivityMainBinding
import com.yulagarces.citobot.ui.screening.capture.CaptureFragment

class MainActivity : AppCompatActivity(), CaptureFragment.ComunicadorFragment {
    private var mWakeLock: PowerManager.WakeLock? = null
    private var immersionBar: ImmersionBar? = null
    private lateinit var viewBinding: ActivityMainBinding
    private var globalPath : String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBar()
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        replaceDemoFragment(CaptureFragment())
    }

    override fun recibirDato(path: String, responseCode: Int, tipo: String) {
        globalPath = path
        val intent = Intent()
        intent.putExtra("PATH", path)
        intent.putExtra("TYPE", tipo)
        setResult(responseCode, intent)
        super.onBackPressed()
    }

    override fun onStart() {
        super.onStart()
        mWakeLock = Utils.wakeLock(this)
    }

    override fun onStop() {
        super.onStop()
        mWakeLock?.apply {
            Utils.wakeUnLock(this)
        }
    }

    private fun replaceDemoFragment(fragment: Fragment) {
        val hasCameraPermission = PermissionChecker.checkSelfPermission(this, CAMERA)
        val hasStoragePermission = PermissionChecker.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE)
        val hasReadPermission = PermissionChecker.checkSelfPermission(this, READ_EXTERNAL_STORAGE)
        if (hasCameraPermission != PermissionChecker.PERMISSION_GRANTED || hasStoragePermission != PermissionChecker.PERMISSION_GRANTED || hasReadPermission != PermissionChecker.PERMISSION_GRANTED ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, CAMERA)) {
                ToastUtils.show(R.string.permission_tip)
            }
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(CAMERA, WRITE_EXTERNAL_STORAGE, RECORD_AUDIO, READ_EXTERNAL_STORAGE),
                    REQUEST_CAMERA
            )
            return
        }
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commitAllowingStateLoss()
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CAMERA -> {
                val hasCameraPermission = PermissionChecker.checkSelfPermission(this, CAMERA)
                if (hasCameraPermission == PermissionChecker.PERMISSION_DENIED) {
                    ToastUtils.show(R.string.permission_tip)
                    return
                }
                replaceDemoFragment(CaptureFragment())
            }
            REQUEST_STORAGE -> {
                val hasCameraPermission =
                        PermissionChecker.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE)
                if (hasCameraPermission == PermissionChecker.PERMISSION_DENIED) {
                    ToastUtils.show(R.string.permission_tip)
                    return
                }
            }
            READ_STORAGE -> {
                val hasCameraPermission =
                        PermissionChecker.checkSelfPermission(this, READ_EXTERNAL_STORAGE)
                if (hasCameraPermission == PermissionChecker.PERMISSION_DENIED) {
                    ToastUtils.show(R.string.permission_tip)
                    return
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        immersionBar= null
    }

    private fun setStatusBar() {
        immersionBar = ImmersionBar.with(this)
                .statusBarDarkFont(false)
                .statusBarColor(R.color.black)
                .navigationBarColor(R.color.black)
                .fitsSystemWindows(true)
                .keyboardEnable(true)
        immersionBar?.init()
    }

    companion object {
        private const val REQUEST_CAMERA = 0
        private const val REQUEST_STORAGE = 1
        private const val READ_STORAGE = 2
    }
}