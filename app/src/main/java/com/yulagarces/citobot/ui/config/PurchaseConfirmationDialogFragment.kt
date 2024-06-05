package com.yulagarces.citobot.ui.config

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.yulagarces.citobot.ui.login.LoginActivity

class PurchaseConfirmationDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setMessage("Cerrar sesión")
            .setPositiveButton("Aceptar") { _,_ ->
                val goHome = Intent(requireContext(), LoginActivity::class.java)
                startActivity(goHome)
                requireActivity().finish()
            }
            .setNegativeButton("Cancelar") { _,_ ->
                dismiss()
            }
            .create()

    companion object {
        const val TAG = "PurchaseConfirmationDialog"
    }
}