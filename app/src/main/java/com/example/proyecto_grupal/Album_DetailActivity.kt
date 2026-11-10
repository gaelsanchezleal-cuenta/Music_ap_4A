package com.example.proyecto_grupal

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Album_DetailActivity : AppCompatActivity() {

    private lateinit var tvDetalleAlbum: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_album_detail)

        tvDetalleAlbum = findViewById(R.id.tvDetalleAlbum)

        mostrarInformacion()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun mostrarInformacion() {
        val idAlbum = intent.getLongExtra("ID_ALBUM", -1)
        val nombreAlbum = intent.getStringExtra("NOMBRE_ALBUM") ?: "Sin nombre"
        val artista = intent.getStringExtra("ARTISTA_ALBUM") ?: "Artista desconocido"
        val genero = intent.getStringExtra("GENERO_ALBUM") ?: "Sin genero"
        val numeroCanciones = intent.getIntExtra("CANCIONES_ALBUM", 0)
        val fecha = intent.getStringExtra("FECHA_ALBUM") ?: "Sin fecha"
        val imagen = intent.getStringExtra("IMAGEN_ALBUM")

        // Concatenamos toda la informacion recibida dentro del unico TextView que tienes
        tvDetalleAlbum.text = "Album: $nombreAlbum\nArtista: $artista\nGenero: $genero\nCanciones: $numeroCanciones"
    }
}