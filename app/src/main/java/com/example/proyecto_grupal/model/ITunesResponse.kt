package com.example.proyecto_grupal.model

//archivo para verificar si obtenemos una respuesta de la api

data class ITunesResponse(
    val resultCount: Int,
    val results: List<Album>
)
