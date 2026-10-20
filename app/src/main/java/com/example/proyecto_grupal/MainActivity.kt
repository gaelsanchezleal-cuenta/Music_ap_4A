package com.example.proyecto_grupal

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
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
        etPassword = findViewById<EditText>(R.id.etPassword)
        btnIngresar = findViewById<Button>(R.id.btnIngresar)

        //se pone this porque jala de esta clase
        dbHelper = dataBaseHelper(this)

        val tvTitulo = findViewById<TextView>(R.id.tvTitulo)
        val tvCrearCuenta = findViewById<TextView>(R.id.tvCrearCuenta)

        // EVENTO PARA INICAR SESION
        btnIngresar.setOnClickListener {

        }

        // EVENTO PARA REGISTRAR
        tvCrearCuenta.setOnClickListener {
            val intent = Intent(this, Registrar:: class.java)
            startActivity(intent)
        }

        //aqui termina

        }
    }