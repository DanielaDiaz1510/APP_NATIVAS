package com.example.parcialkotlinapp

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.parcialkotlinapp.api.ApiClient
import com.example.parcialkotlinapp.models.CrearUsuarioRequest
import com.example.parcialkotlinapp.models.Usuario
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class RegistroActivity : AppCompatActivity() {

    companion object {
        // Edad máxima permitida para registrarse
        private const val EDAD_MAXIMA = 80
    }

    // Variable global para guardar la fecha en formato Date
    private var fechaSeleccionadaCalendar: Calendar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        // COMPONENTES DE TEXTO ESTÁNDAR
        val etNombre = findViewById<EditText>(R.id.etNombre)
        val etApellido = findViewById<EditText>(R.id.etApellido)
        val etCorreo = findViewById<EditText>(R.id.etCorreoRegistro)
        val etPasswordRegistro = findViewById<EditText>(R.id.etPasswordRegistro)

        // Nuevos componentes
        val btnFechaNacimiento = findViewById<Button>(R.id.btnFechaNacimiento)
        val tvFechaSeleccionada = findViewById<TextView>(R.id.tvFechaSeleccionada)
        val tvEdadCalculada = findViewById<TextView>(R.id.tvEdadCalculada)
        val etUniversidad = findViewById<EditText>(R.id.etUniversidad)
        val spSemestre = findViewById<Spinner>(R.id.spSemestre)

        // BOTONES DE ACCIÓN Y MENSAJE
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)
        val tvMensaje = findViewById<TextView>(R.id.tvMensajeRegistro)

        // SPINNER DE SEMESTRES
        val semestres = arrayOf(
            "Seleccione un semestre",
            "Semestre 1", "Semestre 2", "Semestre 3", "Semestre 4", "Semestre 5",
            "Semestre 6", "Semestre 7", "Semestre 8", "Semestre 9", "Semestre 10"
        )

        val adaptador = ArrayAdapter(this, android.R.layout.simple_spinner_item, semestres)
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spSemestre.adapter = adaptador

        // DATE PICKER
        btnFechaNacimiento.setOnClickListener {
            val calendarActual = Calendar.getInstance()
            val anoActual = calendarActual.get(Calendar.YEAR)
            val mesActual = calendarActual.get(Calendar.MONTH)
            val diaActual = calendarActual.get(Calendar.DAY_OF_MONTH)

            // Abrir ventana flotante del calendario
            val datePickerDialog = DatePickerDialog(this, { _, anoSeleccionado, mesSeleccionado, diaSeleccionado ->
                val seleccionado = Calendar.getInstance()
                seleccionado.set(anoSeleccionado, mesSeleccionado, diaSeleccionado)
                fechaSeleccionadaCalendar = seleccionado

                // Mostrar la fecha visualmente en pantalla en formato Día/Mes/Año
                val formatoVisual = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                tvFechaSeleccionada.text = formatoVisual.format(seleccionado.time)

                //  Obtener datos del DatePicker

                val hoy = Calendar.getInstance()

                var anos = hoy.get(Calendar.YEAR) - anoSeleccionado
                var meses = hoy.get(Calendar.MONTH) - mesSeleccionado
                var dias = hoy.get(Calendar.DAY_OF_MONTH) - diaSeleccionado

                // Ajustar si los días dan un número negativo
                if (dias < 0) {
                    val mesAnterior = (hoy.clone() as Calendar)
                    mesAnterior.add(Calendar.MONTH, -1)
                    dias += mesAnterior.getActualMaximum(Calendar.DAY_OF_MONTH)
                    meses--
                }

                // Ajustar si los meses dan un número negativo
                if (meses < 0) {
                    meses += 12
                    anos--
                }

                // Mostramos cuantos años,meses y dìas tiene el usuario
                tvEdadCalculada.text = "Edad: $anos años, $meses meses, $dias días"

            }, anoActual, mesActual, diaActual)

            // No permitir fechas de nacimiento futuras
            datePickerDialog.datePicker.maxDate = System.currentTimeMillis()

            // No permitir fechas que superen la edad máxima que es 80
            val fechaMinima = Calendar.getInstance()
            fechaMinima.add(Calendar.YEAR, -(EDAD_MAXIMA + 1))
            fechaMinima.add(Calendar.DAY_OF_MONTH, 1)
            datePickerDialog.datePicker.minDate = fechaMinima.timeInMillis

            datePickerDialog.show()
        }

        // BOTÓN REGISTRAR
        btnRegistrar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val apellido = etApellido.text.toString().trim()
            val correo = etCorreo.text.toString().trim()
            val password = etPasswordRegistro.text.toString()
            val universidad = etUniversidad.text.toString().trim()

            // VALIDACIONES DE FORMULARIO
            if (nombre.isBlank()) {
                etNombre.error = "Ingrese el nombre"
                return@setOnClickListener
            }

            if (apellido.isBlank()) {
                etApellido.error = "Ingrese el apellido"
                return@setOnClickListener
            }

            if (correo.isBlank()) {
                etCorreo.error = "Ingrese el correo"
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                etCorreo.error = "Correo no válido"
                return@setOnClickListener
            }

            if (password.isBlank()) {
                etPasswordRegistro.error = "Ingrese la contraseña"
                return@setOnClickListener
            }

            // Números minimos de caracteres para la contraseña es 6
            if (password.length < 6) {
                etPasswordRegistro.error = "La contraseña debe tener al menos 6 caracteres"
                return@setOnClickListener
            }

            // Validar que se haya escogido una fecha de nacimiento
            if (fechaSeleccionadaCalendar == null) {
                Toast.makeText(this, "Seleccione su fecha de nacimiento", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validar que la edad no supere el máximo permitido
            if (calcularEdadEnAnos(fechaSeleccionadaCalendar!!) > EDAD_MAXIMA) {
                tvMensaje.text = "La edad no puede ser mayor a $EDAD_MAXIMA años."
                Toast.makeText(this, "La edad no puede ser mayor a $EDAD_MAXIMA años", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // Validar que se haya escrito la universidad
            if (universidad.isBlank()) {
                etUniversidad.error = "Ingrese la universidad"
                return@setOnClickListener
            }

            // Validar que se haya elegido un semestre
            if (spSemestre.selectedItemPosition == 0) {
                Toast.makeText(this, "Seleccione un semestre académico", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Preparar la fecha de nacimiento en el formato que entiende el servidor (año-mes-día)
            val formatoFecha = SimpleDateFormat("yyyy-MM-dd'T'00:00:00", Locale.US)
            val fechaNacimientoTexto = formatoFecha.format(fechaSeleccionadaCalendar!!.time)

            // Capturar el semestre seleccionado
            val semestreSeleccionado = spSemestre.selectedItemPosition


            val nuevoUsuario = CrearUsuarioRequest(
                nombre = nombre,
                apellido = apellido,
                correo = correo,
                password = password,
                fechaNacimiento = fechaNacimientoTexto,
                universidad = universidad,
                semestre = semestreSeleccionado
            )

            tvMensaje.text = "Registrando usuario..."

            // LLAMADA AL BACKEND
            ApiClient.usuarioApi
                .crearUsuario(nuevoUsuario)
                .enqueue(object : Callback<Usuario> {

                    override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                        if (response.isSuccessful) {
                            // AlertDialog de Registro Exitoso
                            val builder = androidx.appcompat.app.AlertDialog.Builder(this@RegistroActivity)
                            builder.setTitle("Registro exitoso")
                            builder.setMessage("El usuario fue creado exitosamente.")

                            // Al seleccionar ACEPTAR, se cierra el cuadro y regresa al Login
                            builder.setPositiveButton("ACEPTAR") { dialog, _ ->
                                dialog.dismiss()
                                finish() // Finaliza esta pantalla y te devuelve automáticamente al Login
                            }

                            val dialog = builder.create()
                            dialog.setCancelable(false) // Bloquea la pantalla para que el usuario deba darle obligatoriamente a ACEPTAR
                            dialog.show()

                            // Limpiar formulario completo tras éxito
                            etNombre.text.clear()
                            etApellido.text.clear()
                            etCorreo.text.clear()
                            etPasswordRegistro.text.clear()
                            etUniversidad.text.clear()
                            tvFechaSeleccionada.text = "No seleccionada"
                            fechaSeleccionadaCalendar = null
                            spSemestre.setSelection(0)

                        } else {

                            val cuerpoError = try {
                                response.errorBody()?.string()
                            } catch (e: Exception) {
                                null
                            }

                            val mensajeError = extraerMensajeError(response.code(), cuerpoError)

                            // Mensaje en pantalla y emergente
                            tvMensaje.text = mensajeError
                            Toast.makeText(this@RegistroActivity, mensajeError, Toast.LENGTH_LONG).show()

                            // Si el error es del correo le muestra en el campo tambien
                            if (mensajeError.contains("correo", ignoreCase = true)) {
                                etCorreo.error = mensajeError
                                etCorreo.requestFocus()
                            }
                        }

                    }

                    override fun onFailure(call: Call<Usuario>, t: Throwable) {
                        tvMensaje.text = "No se pudo conectar con el servidor. Verifique su conexión a internet e intente de nuevo."
                    }
                })
        }

        // FLECHA DE NAVEGACIÓN
        val btnFlechaAtras = findViewById<TextView>(R.id.btnFlechaAtras)
        btnFlechaAtras.setOnClickListener {
            finish()
        }
    }

    private fun calcularEdadEnAnos(nacimiento: Calendar): Int {
        val hoy = Calendar.getInstance()
        var anos = hoy.get(Calendar.YEAR) - nacimiento.get(Calendar.YEAR)
        if (hoy.get(Calendar.DAY_OF_YEAR) < nacimiento.get(Calendar.DAY_OF_YEAR)) {
            anos--
        }
        return anos
    }

    private fun extraerMensajeError(codigo: Int, cuerpo: String?): String {
        val mensajeGenerico = when (codigo) {
            400 -> "Los datos ingresados no son válidos. Revise la información e intente de nuevo."
            401 -> "No tiene autorización para realizar esta acción."
            404 -> "No se encontró el servicio solicitado."
            409 -> "El registro ya existe."
            in 500..599 -> "Ocurrió un problema en el servidor. Intente de nuevo más tarde."
            else -> "Ocurrió un error inesperado. Intente de nuevo."
        }

        if (cuerpo.isNullOrBlank()) return mensajeGenerico

        return try {
            val json = org.json.JSONObject(cuerpo)

            // Buscar la clave mensaje o message sin importar mayúsculas/minúsculas
            val claveMensaje = json.keys().asSequence().firstOrNull {
                it.equals("mensaje", ignoreCase = true) || it.equals("message", ignoreCase = true)
            }

            when {
                claveMensaje != null && json.optString(claveMensaje).isNotBlank() ->
                    json.getString(claveMensaje)

                json.has("errors") -> {
                    val errores = json.getJSONObject("errors")
                    val mensajes = mutableListOf<String>()
                    for (campo in errores.keys()) {
                        val lista = errores.optJSONArray(campo) ?: continue
                        for (i in 0 until lista.length()) {
                            mensajes.add(lista.getString(i))
                        }
                    }
                    if (mensajes.isNotEmpty()) mensajes.joinToString("\n")
                    else mensajeGenerico
                }

                json.has("title") -> json.getString("title")

                else -> mensajeGenerico
            }
        } catch (e: Exception) {
            val texto = cuerpo.trim()
            if (!texto.startsWith("<") && texto.length <= 200) texto else mensajeGenerico
        }
    }
}