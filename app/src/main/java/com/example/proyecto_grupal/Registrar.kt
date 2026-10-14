package com.example.proyecto_grupal

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Registrar : AppCompatActivity() {

    //creacion de los objetos a rellenar
    //lateinit var sigifica que en algun momento tendra un valor
    //despues de poner los objetos ponemos el tipo que seria edi text en este caso
    private lateinit var etNombre: EditText
    private lateinit var etCorreo: EditText
    private lateinit var etPassword: EditText

    //objeto del boton
    private lateinit var btnRegistrar: Button

    //objeto de la base de datos par aingresar a todos sus metodos
    private lateinit var dbHelper: DataBaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registrar)

        //AQUI INICIAMOS

        etNombre = findViewById(R.id.etnombreUsuario)
        etCorreo = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnRegistrar = findViewById(R.id.btnRegistrar)

        //iniciar el objeto de la base de datos
        dbHelper = DataBaseHelper(this)

        //EVENTOS PARA REGISTRAR A UN USUARIO
        btnRegistrar.setOnClickListener {
            //llamar al metodo de registrar de sesion
            registrarUsuario()
        }


    }

    //fun es equivalente al void en java
    //METODO PARA REGOSTRAR A UN USUARIO
    private fun registrarUsuario() {
        //OBTENER LA INFORMACION DEL USUARIO
        //el trim es borrar el espacio que colocamos sin intencion
        val nombre = etNombre.text.toString().trim()
        val correo = etCorreo.text.toString().trim()
        val password = etPassword.text.toString().trim()

        //VALIDAR QUE LOS CAMPOS TENGAN INFORMACION
        if (nombre.isEmpty() || correo.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            //con return nos salimos del if
            return
        }

        //LLAMAR LA BASE DE DATOS Y GUARDAR EL RESULTADO DE LA INSERSION

        val resultado = dbHelper.registrarUsuario(nombre, correo, password)

        if (resultado){
            //si el resultado es verdadero
            Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
            //regresar al usuario al inicio de sesion
            //finish lo que hace es devolvernos a la pantalla anterior
            finish()
        } else {
            Toast.makeText(this, "Registro fallido, correo duplicado", Toast.LENGTH_SHORT).show()
        }
    }

}