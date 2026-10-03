package com.grupo3.clubdeportivo.ui.components

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.grupo3.clubdeportivo.R
import com.grupo3.clubdeportivo.ui.menuPrincipal
import com.grupo3.clubdeportivo.ui.perfilAdministrador

class FooterView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    override fun onFinishInflate() {
        super.onFinishInflate()

        // Configuración automática del botón Inicio -> Menú Principal
        findViewById<View>(R.id.navInicio)?.setOnClickListener {
            if (context !is menuPrincipal) {
                val intent = Intent(context, menuPrincipal::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                context.startActivity(intent)
            }
        }

        // Configuración automática del botón Perfil -> Perfil Administrador
        findViewById<View>(R.id.navPerfil)?.setOnClickListener {
            if (context !is perfilAdministrador) {
                val intent = Intent(context, perfilAdministrador::class.java)
                context.startActivity(intent)
            }
        }
    }
}
