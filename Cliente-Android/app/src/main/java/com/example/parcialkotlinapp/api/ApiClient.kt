package com.example.parcialkotlinapp.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private const val BASE_URL = "http://192.168.0.4:62128/"

    private val retrofit: Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()

    val usuarioApi: UsuarioApi =
        retrofit.create(UsuarioApi::class.java)

}