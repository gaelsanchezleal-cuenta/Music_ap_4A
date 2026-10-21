package com.example.proyecto_grupal

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnIngresar: Button
    private lateinit var dbHelper: DataBaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        //aqui inicia

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnIngresar = findViewById(R.id.btnIngresar)

        //se pone this porque jala de esta clase
        dbHelper = DataBaseHelper(this)

        val tvTitulo = findViewById<TextView>(R.id.tvTitulo)
        val tvCrearCuenta = findViewById<TextView>(R.id.tvCrearCuenta)

        //EVENTO PARA INICIAR SESION
        btnIngresar.setOnClickListener {
            iniciarSesion()
        }

        // EVENTO PARA REGISTRAR
        tvCrearCuenta.setOnClickListener {
            val intent = Intent(this, Registrar::class.java)
            startActivity(intent)
        }
        //aqui termina

    }

    // EVENTO PARA INICAR SESION
    private fun iniciarSesion() {

        //acceder a lo que el usaurio escrube
        val correo = etEmail.text.toString().trim()
        val pass = etPassword.text.toString().trim()

        if (correo.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Rellena todo los campos", Toast.LENGTH_SHORT).show()
            //return para salirse
            return
        }

        // LLAMAR AL METODO DE INICIO DE SESION EN LA BASE DE DATOS (devolvera un boleano)
        val resultado = dbHelper.iniciarSesion(correo, pass)

        if (resultado) {
            //si la sesion es correcta
            Toast.makeText(this, "Bienvenido", Toast.LENGTH_SHORT).show()

            // EVENTO PARA INICAR SESION
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)

            //CERRAR LA PANTALLA ACTUAL
            finish()
        } else {
            Toast.makeText(this, "Datos incoreectos", Toast.LENGTH_SHORT).show()
        }
    }
}