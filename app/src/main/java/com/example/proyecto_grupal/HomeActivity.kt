package com.example.proyecto_grupal

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.proyecto_grupal.model.ITunesResponse
import com.example.proyecto_grupal.network.RetrrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {

    private lateinit var tvResultado: TextView
    private lateinit var imgAlbum: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        tvResultado = findViewById(R.id.tvResultado)
        imgAlbum = findViewById(R.id.imgAlbum)
        obtenerAlbum()
    }


    //metodo para cargar los albumnes
    private fun obtenerAlbum() {

        RetrrofitClient.api.obtenerAlbumGenero(
            genero = "fuerza regida",
            pais = "MX",
            media = "music",
            entidad = "album",
            limite = 1,
        ).enqueue(object : Callback<ITunesResponse> {
            //metodo para verificar que la respuesta sea correcta
            override fun onResponse(
                call: Call<ITunesResponse?>,
                response: Response<ITunesResponse?>
            ) {

                //variable que obtenga los resultados de la API
                //el body dice que al obtener se entra a la infroamcion principal
                val albumnes = response.body()?.results

                if (!(albumnes.isNullOrEmpty())){

                    //crear una variable que guarde la informacion de cada respuesta obtenida de la API
                    val album = albumnes[0]

                    //mostramos informacion obtenida de la api
                    tvResultado.text = "NombreAlbum: ${album.collectionName} \n\n" +
                            "Artista ${album.artistName}"

                    //mostrar la imagen de la api
                    Glide.with(this@HomeActivity)
                        .load(album.obtenerImagenGrande())
                        .into(imgAlbum)

                } else {
                    Toast.makeText(this@HomeActivity, "No hay resultados", Toast.LENGTH_SHORT).show()
                }
            }

            //metodo para verificar si no hay respuesta
            override fun onFailure(call: Call<ITunesResponse?>, t: Throwable) {
                Toast.makeText(this@HomeActivity, "Error en el API", Toast.LENGTH_SHORT).show()
            }
        })

    }
}