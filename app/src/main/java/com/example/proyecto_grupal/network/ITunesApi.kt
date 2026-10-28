package com.example.proyecto_grupal.network

import com.example.proyecto_grupal.model.ITunesResponse
import retrofit2.http.GET
import retrofit2.http.Query

//archivo que permite generar la peticion a la api
interface ITunesApi {

    //lo que va despues de la diagonal de la url
    @GET("search")

    //metodo para buscar el album por genero
    fun obtenerAlbumGenero(
        //query recibe un parametro (los campos a llenar en postman)
        @Query("term") genero: String,
        @Query("country") pais: String,
        @Query("media") media: String,
        @Query("entity") entidad: String,
        @Query("limit") limite: Int,
        //ponemos retorfit2 como referencia
        //ponemos el ITUnesresponde porque de ahi obtendra la informacion de la API
    ) : retrofit2.Call<ITunesResponse>

}