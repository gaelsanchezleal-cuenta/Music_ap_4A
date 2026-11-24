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
import com.example.proyecto_grupal.model.AlbumF
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class FavoritosActivity : AppCompatActivity() {

    private lateinit var contenedorFavoritos: LinearLayout
    private lateinit var tvSinFavoritos: TextView
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var dbHelper: DataBaseHelper
    private lateinit var auth: FirebaseAuth
    private lateinit var credentialManager: CredentialManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_favoritos)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        contenedorFavoritos = findViewById(R.id.contenedorFavoritos)
        tvSinFavoritos = findViewById(R.id.tvSinFavoritos)
        bottomNavigation = findViewById(R.id.bottomNavigation)

        dbHelper = DataBaseHelper(this)
        auth = FirebaseAuth.getInstance()
        credentialManager = CredentialManager.create(this)

        cargarFavoritos()
        menuNavegacion()
    }

    // Recargar la lista al volver de Album_DetailActivity por si se elimino un favorito
    override fun onResume() {
        super.onResume()
        cargarFavoritos()
        bottomNavigation.selectedItemId = R.id.nav_favoritos
    }

    // Metodo para consultar la BD y mostrar los favoritos
    private fun cargarFavoritos() {
        val listaFavoritos = dbHelper.obtenerAlbumFavoritos()

        if (listaFavoritos.isNotEmpty()) {
            tvSinFavoritos.visibility = View.GONE
            mostrarFavoritos(listaFavoritos)
        } else {
            contenedorFavoritos.removeAllViews()
            tvSinFavoritos.visibility = View.VISIBLE
        }
    }

    // Metodo para inflar dinamicamente los albumes guardados
    private fun mostrarFavoritos(albumes: List<AlbumF>) {
        contenedorFavoritos.removeAllViews()

        for (album in albumes) {
            val vistaAlbum = layoutInflater.inflate(
                R.layout.item_album,
                contenedorFavoritos,
                false
            )

            val imgPortada = vistaAlbum.findViewById<ImageView>(R.id.imgPortadaAlbum)
            val tvNombreAlbum = vistaAlbum.findViewById<TextView>(R.id.tvNombreAlbum)
            val tvNombreArtista = vistaAlbum.findViewById<TextView>(R.id.tvNombreArtista)

            tvNombreAlbum.text = album.nombreAlbum
            tvNombreArtista.text = album.artista

            Glide.with(this)
                .load(album.imagen)
                .centerCrop()
                .into(imgPortada)

            // Evento de clic para abrir los detalles del album
            vistaAlbum.setOnClickListener {
                val intent = Intent(this@FavoritosActivity, Album_DetailActivity::class.java)

                intent.putExtra("ID_ALBUM", album.albumId)
                intent.putExtra("NOMBRE_ALBUM", album.nombreAlbum)
                intent.putExtra("ARTISTA_ALBUM", album.artista)
                intent.putExtra("GENERO_ALBUM", album.genero)
                intent.putExtra("CANCIONES_ALBUM", album.canciones)
                intent.putExtra("FECHA_ALBUM", album.fecha)
                intent.putExtra("IMAGEN_ALBUM", album.imagen)

                startActivity(intent)
            }

            contenedorFavoritos.addView(vistaAlbum)
        }
    }

    // Metodo para el funcionamiento del menu
    private fun menuNavegacion() {
        bottomNavigation.selectedItemId = R.id.nav_favoritos

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_inicio -> {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }

                R.id.nav_favoritos -> {
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
                Toast.makeText(this@FavoritosActivity, "Error: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }

            val intent = Intent(this@FavoritosActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}