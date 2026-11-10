package com.example.proyecto_grupal

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.proyecto_grupal.model.Album
import com.example.proyecto_grupal.model.ITunesResponse
import com.example.proyecto_grupal.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {

    private lateinit var tvEstado: TextView
    private lateinit var contenedorIndie: LinearLayout
    private lateinit var contenedorCorridos: LinearLayout
    private lateinit var contendeorDannyOcean: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        // Manejo de barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvEstado = findViewById(R.id.tvEstado)
        contenedorIndie = findViewById(R.id.contenedorIndie)
        contenedorCorridos = findViewById(R.id.contenedorCorridos)
        contendeorDannyOcean = findViewById(R.id.contenedorDannyOcean)

        // Cargar cada seccion de albumes
        obtenerAlbum("indie", contenedorIndie)
        obtenerAlbum("Corridos", contenedorCorridos)
        obtenerAlbum("Danny Ocean", contendeorDannyOcean)
    }

    // Metodo para cargar los albumes desde la API
    private fun obtenerAlbum(genero: String, contenedor: LinearLayout) {
        RetrofitClient.api.obtenerAlbumGenero(
            genero = genero,
            pais = "MX",
            media = "music",
            entidad = "album",
            limite = 10
        ).enqueue(object : Callback<ITunesResponse> {
            override fun onResponse(
                call: Call<ITunesResponse>,
                response: Response<ITunesResponse>
            ) {
                val albumes = response.body()?.results ?: emptyList()

                // Si la lista de albumes no esta vacia se muestran los items
                if (albumes.isNotEmpty()) {
                    tvEstado.visibility = View.GONE
                    mostrarAlbums(albumes, contenedor)
                } else {
                    Toast.makeText(this@HomeActivity, "No hay resultados", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ITunesResponse>, t: Throwable) {
                Toast.makeText(
                    this@HomeActivity,
                    "Error en el API: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    // Metodo para mostrar dinamicamente el listado de albumes
    private fun mostrarAlbums(albumes: List<Album>, contenedor: LinearLayout) {
        // Evitar resultados duplicados
        contenedor.removeAllViews()

        // Ciclo para recorrer la informacion devuelta por la API
        for (album in albumes) {
            // Inflar la vista de cada item (item_album.xml)
            val vistaAlbum = layoutInflater.inflate(
                R.layout.item_album,
                contenedor,
                false
            )

            val imgPortada = vistaAlbum.findViewById<ImageView>(R.id.imgPortadaAlbum)
            val tvNombreAlbum = vistaAlbum.findViewById<TextView>(R.id.tvNombreAlbum)
            val tvNombreArtista = vistaAlbum.findViewById<TextView>(R.id.tvNombreArtista)

            // Asignar los textos con las propiedades reales del modelo Album
            tvNombreAlbum.text = album.collectionName ?: "Album sin nombre"
            tvNombreArtista.text = album.artistName ?: "Artista desconocido"

            // Cargar la portada con Glide usando el metodo de alta resolucion
            Glide.with(this)
                .load(album.obtenerImagenGrande())
                .centerCrop()
                .into(imgPortada)

            // Evento de clic para ir a los detalles del album
            vistaAlbum.setOnClickListener {
                val idAlbum = album.collectionId
                if (idAlbum != null) {
                    // SE CORRIGE AQUI: Album_DetailActivity con guion bajo
                    val intent = Intent(this@HomeActivity, Album_DetailActivity::class.java).apply {
                        putExtra("ID_ALBUM", idAlbum)
                        putExtra("NOMBRE_ALBUM", album.collectionName)
                        putExtra("ARTISTA_ALBUM", album.artistName)
                        putExtra("GENERO_ALBUM", album.primaryGenreName)
                        putExtra("CANCIONES_ALBUM", album.trackCount)
                        putExtra("FECHA_ALBUM", album.releaseDate)
                        putExtra("IMAGEN_ALBUM", album.obtenerImagenGrande())
                    }
                    startActivity(intent)
                } else {
                    Toast.makeText(this@HomeActivity, "El album no contiene ID", Toast.LENGTH_SHORT).show()
                }
            }

            // Agregar la vista inflada al contenedor correspondiente
            contenedor.addView(vistaAlbum)
        }
    }
}