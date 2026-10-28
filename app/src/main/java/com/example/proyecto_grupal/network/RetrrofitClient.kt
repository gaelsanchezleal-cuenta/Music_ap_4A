package com.example.proyecto_grupal.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//archivo para contruir todo el enlace la api

object RetrrofitClient {

    //colocar la base URL
    private const val BASE_URL = "https://itunes.apple.com"

    //contruir la url completa
    //by lazy detiene el proceso si se tarda la api (y ddespues te murestra la respuesta)
    val api : ITunesApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesApi::class.java)

    }
}