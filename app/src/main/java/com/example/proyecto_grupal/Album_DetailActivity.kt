package com.example.proyecto_grupal

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide

class Album_DetailActivity : AppCompatActivity() {

    private lateinit var imgPortadaDetalle: ImageView
    private lateinit var tvNombreAlbumDetalle: TextView
    private lateinit var tvArtistaAlbum: TextView
    private lateinit var tvDetallesInfoAlbum: TextView
    private lateinit var btnFavorito: ImageButton

    private lateinit var dbHelper: DataBaseHelper
    private var albumFavorito = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_album_detail)

        dbHelper = DataBaseHelper(this)

        imgPortadaDetalle = findViewById(R.id.imgPortadaDetalle)
        tvNombreAlbumDetalle = findViewById(R.id.tvNombreAlbumDetalle)
        tvArtistaAlbum = findViewById(R.id.tvArtistaAlbumDetalle)
        tvDetallesInfoAlbum = findViewById(R.id.tvDetallesInfoAlbum)
        btnFavorito = findViewById(R.id.btnFavorito)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mostrarInformacion()
    }

    private fun mostrarInformacion() {
        val idAlbum = intent.getLongExtra("ID_ALBUM", -1)
        val nombreAlbum = intent.getStringExtra("NOMBRE_ALBUM") ?: "Sin nombre"
        val artista = intent.getStringExtra("ARTISTA_ALBUM") ?: "Artista desconocido"
        val genero = intent.getStringExtra("GENERO_ALBUM") ?: "Sin genero"
        val numeroCanciones = intent.getIntExtra("CANCIONES_ALBUM", 0)
        val fecha = intent.getStringExtra("FECHA_ALBUM") ?: "Sin fecha"
        val imagen = intent.getStringExtra("IMAGEN_ALBUM") ?: ""

        val anio = if (fecha.length >= 4) fecha.substring(0, 4) else fecha

        tvNombreAlbumDetalle.text = nombreAlbum
        tvArtistaAlbum.text = artista
        tvDetallesInfoAlbum.text = "Genero: $genero • $numeroCanciones canciones • año $anio"

        Glide.with(this)
            .load(imagen)
            .centerCrop()
            .into(imgPortadaDetalle)

        // Validacion para verificar si el album ya es favorito en la BD y actualizar el icono
        if (idAlbum != -1L) {
            albumFavorito = dbHelper.existeAlbumFavorito(idAlbum)
            if (albumFavorito) {
                btnFavorito.setImageResource(R.drawable.baseline_favorite_24)
            } else {
                btnFavorito.setImageResource(R.drawable.baseline_favorite_border_24)
            }
        }

        btnFavorito.setOnClickListener {
            if (albumFavorito) {
                val eliminado = dbHelper.eliminarAlbumFavorito(idAlbum)
                if (eliminado) {
                    albumFavorito = false
                    btnFavorito.setImageResource(R.drawable.baseline_favorite_border_24)
                    Toast.makeText(
                        this,
                        "Album $nombreAlbum eliminado de favoritos",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                val guardado = dbHelper.guardarAlbumFavorito(
                    albumId = idAlbum,
                    nombreAlbum = nombreAlbum,
                    artista = artista,
                    genero = genero,
                    canciones = numeroCanciones,
                    fecha = fecha,
                    imagen = imagen
                )
                if (guardado) {
                    albumFavorito = true
                    btnFavorito.setImageResource(R.drawable.baseline_favorite_24)
                    Toast.makeText(
                        this,
                        "Album $nombreAlbum agregado a favoritos",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}