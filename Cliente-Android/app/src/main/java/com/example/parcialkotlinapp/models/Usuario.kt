package com.example.parcialkotlinapp.models

data class Usuario(
    val id: Int,
    val nombre: String,
    val apellido: String,
    val correo: String,
    val edad: String,
    val activo: Boolean,
    val fechaCreacion: String
)