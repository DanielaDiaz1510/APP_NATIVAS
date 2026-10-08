package com.example.parcialkotlinapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.parcialkotlinapp.api.ApiClient
import com.example.parcialkotlinapp.models.LoginRequest
import com.example.parcialkotlinapp.models.Usuario
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        // =====================================
        // COMPONENTES
        // =====================================

        val etCorreo =
            findViewById<EditText>(R.id.etCorreo)

        val etPassword =
            findViewById<EditText>(R.id.etPassword)

        val btnIngresar =
            findViewById<Button>(R.id.btnIngresar)

        val btnCrearCuenta =
            findViewById<Button>(R.id.btnCrearCuenta)

        val tvMensaje =
            findViewById<TextView>(R.id.tvMensaje)

        // =====================================
        // BOTÓN INGRESAR
        // =====================================

        btnIngresar.setOnClickListener {

            val correo =
                etCorreo.text.toString().trim()

            val password =
                etPassword.text.toString()

            // =====================================
            // VALIDACIONES
            // =====================================

            if (correo.isBlank()) {
                etCorreo.error = "Ingrese el correo"
                return@setOnClickListener
            }

            if (password.isBlank()) {
                etPassword.error = "Ingrese la contraseña"
                return@setOnClickListener
            }

            // =====================================
            // CREAR OBJETO PARA EL LOGIN
            // =====================================

            val loginRequest = LoginRequest(
                correo = correo,
                password = password
            )

            tvMensaje.text = "Validando usuario..."

            // Evitar varios clics mientras responde la API
            btnIngresar.isEnabled = false

            // =====================================
            // CONSUMIR API .NET
            // =====================================

            ApiClient.usuarioApi
                .login(loginRequest)
                .enqueue(object : Callback<Usuario> {

                    // =====================================
                    // RESPUESTA DEL SERVIDOR
                    // =====================================
                    override fun onResponse(
                        call: Call<Usuario>,
                        response: Response<Usuario>
                    ) {

                        btnIngresar.isEnabled = true

                        // LOGIN CORRECTO
                        if (response.isSuccessful) {
                            val usuario = response.body()
                            tvMensaje.text = "Bienvenido ${usuario?.nombre}"

                            // 🌟 Ir al menú principal enviando el nombre real de cualquier usuario
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)

                            // Captura el nombre real de la base de datos (Ej: Carlos, Laura, Juan, Majo)
                            // Si por algún error de red llega vacío, usa la palabra genérica "Usuario"
                            val nombreReal = usuario?.nombre ?: "Usuario"
                            intent.putExtra("nombreUsuario", usuario?.nombre)


                            startActivity(intent)
                            finish()

                        }
                        // CREDENCIALES INCORRECTAS
                        else if (response.code() == 401) {

                            tvMensaje.text =
                                "Correo o contraseña incorrectos"

                        }
                        // OTRO ERROR DEL BACKEND
                        else {

                            tvMensaje.text =
                                "Error del servidor: ${response.code()}"
                        }
                    }

                    // =====================================
                    // ERROR DE CONEXIÓN
                    // =====================================
                    override fun onFailure(
                        call: Call<Usuario>,
                        t: Throwable
                    ) {

                        btnIngresar.isEnabled = true

                        tvMensaje.text =
                            "No se pudo conectar con el servidor: ${t.message}"
                    }
                })
        }

        // =====================================
        // BOTÓN CREAR CUENTA
        // =====================================

        btnCrearCuenta.setOnClickListener {

            val intent = Intent(
                this,
                RegistroActivity::class.java
            )

            startActivity(intent)
        }
    }
}