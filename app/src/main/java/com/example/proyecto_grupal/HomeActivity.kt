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
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.proyecto_grupal.model.Album
import com.example.proyecto_grupal.model.ITunesResponse
import com.example.proyecto_grupal.network.RetrofitClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {

    private lateinit var tvEstado: TextView
    private lateinit var contenedorIndie: LinearLayout
    private lateinit var contenedorCorridos: LinearLayout
    private lateinit var contendeorDannyOcean: LinearLayout
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var auth: FirebaseAuth
    private lateinit var credentialManager: CredentialManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvEstado = findViewById(R.id.tvEstado)
        contenedorIndie = findViewById(R.id.contenedorIndie)
        contenedorCorridos = findViewById(R.id.contenedorCorridos)
        contendeorDannyOcean = findViewById(R.id.contenedorDannyOcean)
        bottomNavigation = findViewById(R.id.bottomNavigation)

        // Credenciales para cerrar sesion
        auth = FirebaseAuth.getInstance()
        credentialManager = CredentialManager.create(this)

        obtenerAlbum("Luis Miguel", contenedorIndie)
        obtenerAlbum("The Weekend", contenedorCorridos)
        obtenerAlbum("Danny Ocean", contendeorDannyOcean)

        menuNavegacion()
    }

    // Metodo para cargar los albumes
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

                if (albumes.isNotEmpty()) {
                    tvEstado.visibility = View.GONE
                    mostrarAlbumes(albumes, contenedor)
                } else {
                    Toast.makeText(this@HomeActivity, "No hay Resultados", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<ITunesResponse>, t: Throwable) {
                Toast.makeText(this@HomeActivity, "Error en el Api", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Metodo para mostrar un listado de Albumes
    private fun mostrarAlbumes(albumes: List<Album>, contenedor: LinearLayout) {
        contenedor.removeAllViews()

        for (album in albumes) {
            val vistaAlbum = layoutInflater.inflate(
                R.layout.item_album,
                contenedor,
                false
            )

            val imgPortada = vistaAlbum.findViewById<ImageView>(R.id.imgPortadaAlbum)
            val tvNombreAlbum = vistaAlbum.findViewById<TextView>(R.id.tvNombreAlbum)
            val tvNombreArtista = vistaAlbum.findViewById<TextView>(R.id.tvNombreArtista)

            tvNombreAlbum.text = album.collectionName ?: "Album sin nombre"
            tvNombreArtista.text = album.artistName ?: "Artista desconocido"

            Glide.with(this)
                .load(album.obtenerImagenGrande())
                .centerCrop()
                .into(imgPortada)

            vistaAlbum.setOnClickListener {
                val idAlbum = album.collectionId

                if (idAlbum != null) {
                    val intent = Intent(this@HomeActivity, Album_DetailActivity::class.java)

                    intent.putExtra("ID_ALBUM", idAlbum)
                    intent.putExtra("NOMBRE_ALBUM", album.collectionName)
                    intent.putExtra("ARTISTA_ALBUM", album.artistName)
                    intent.putExtra("GENERO_ALBUM", album.primaryGenreName)
                    intent.putExtra("CANCIONES_ALBUM", album.trackCount)
                    intent.putExtra("FECHA_ALBUM", album.releaseDate)
                    intent.putExtra("IMAGEN_ALBUM", album.obtenerImagenGrande())

                    startActivity(intent)
                }
            }

            contenedor.addView(vistaAlbum)
        }
    }

    // Metodo para el funcionamiento del menu
    private fun menuNavegacion() {
        bottomNavigation.selectedItemId = R.id.nav_inicio

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_inicio -> {
                    true
                }
                R.id.nav_favoritos -> {
                    val intent = Intent(this, FavoritosActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_salir -> {
                    cerrarSesionGoogle()
                    true
                }
                else -> false
            }
        }
    }

    // Metodo para cerrar sesion
    private fun cerrarSesionGoogle() {
        auth.signOut()

        lifecycleScope.launch {
            try {
                credentialManager.clearCredentialState(
                    ClearCredentialStateRequest()
                )
            } catch (e: Exception) {
                Toast.makeText(this@HomeActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }

            val intent = Intent(this@HomeActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}