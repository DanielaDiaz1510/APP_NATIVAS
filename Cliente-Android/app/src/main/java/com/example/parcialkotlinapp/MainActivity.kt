package com.example.parcialkotlinapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Manejo de los bordes de la pantalla
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Saludo personalizado


        // Recibimos el nombre del usuario autenticado
        val nombreUsuario = intent.getStringExtra("nombreUsuario") ?: "Usuario"

        // Obtenemos la hora actual del dispositivo en formato de 24 horas
        val horaActual = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)


        val saludo = when {
            horaActual in 5..11 -> "Buenos días, $nombreUsuario"
            horaActual in 12..17 -> "Buenas tardes, $nombreUsuario"
            else -> "Buenas noches, $nombreUsuario"
        }

        // Buscamos el título de la pantalla principal para cambiarle el texto dinámicamente
        val tvTituloPrincipal = findViewById<TextView>(R.id.tvTituloPrincipal)
        tvTituloPrincipal.text = saludo

        // =====================================
        // BOTÓN EJERCICIO PAR O IMPAR
        // =====================================
        val btnParImpar = findViewById<Button>(R.id.btnParImpar)
        btnParImpar.setOnClickListener {
            val intent = Intent(this, ParImparActivity::class.java)
            startActivity(intent)
        }

        // AlertDialog para preguntarle al usuario si esta seguro de salir

        val btnCerrarSesion = findViewById<ImageView>(R.id.btnCerrarSesion)

        btnCerrarSesion.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Cerrar sesión")
            builder.setMessage("¿Está seguro de que desea cerrar sesión?")

            // Opción SALIR: Finaliza la sesión actual y limpia el historial para no poder regresar con el botón Atrás
            builder.setPositiveButton("SALIR") { _, _ ->
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }


            builder.setNegativeButton("CANCELAR") { dialog, _ ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.setCancelable(false)
            dialog.show()

            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(resources.getColor(R.color.ucompensar_orange, theme))
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(resources.getColor(R.color.ucompensar_blue, theme))


        }
    }
}
