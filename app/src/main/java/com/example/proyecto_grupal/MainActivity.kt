package com.example.proyecto_grupal

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnIngresar: Button
    private lateinit var dbHelper: DataBaseHelper
    private lateinit var auth: FirebaseAuth
    private lateinit var credentialManager: CredentialManager
    private lateinit var btnGoogle: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnIngresar = findViewById(R.id.btnIngresar)
        btnGoogle = findViewById(R.id.btnGoogle)

        dbHelper = DataBaseHelper(this)
        auth = FirebaseAuth.getInstance()
        credentialManager = CredentialManager.create(this)

        val tvTitulo = findViewById<TextView>(R.id.tvTitulo)
        val tvCrearCuenta = findViewById<TextView>(R.id.tvCrearCuenta)

        // EVENTO PARA INICIAR SESION NORMAL
        btnIngresar.setOnClickListener {
            iniciarSesion()
        }

        // EVENTO PARA INICIAR SESION CON GOOGLE
        btnGoogle.setOnClickListener {
            iniciarSesionGoogle()
        }

        // EVENTO PARA REGISTRAR
        tvCrearCuenta.setOnClickListener {
            val intent = Intent(this, Registrar::class.java)
            startActivity(intent)
        }
    }

    // EVENTO PARA INICIAR SESION LOCAL
    private fun iniciarSesion() {
        val correo = etEmail.text.toString().trim()
        val pass = etPassword.text.toString().trim()

        if (correo.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val resultado = dbHelper.iniciarSesion(correo, pass)

        if (resultado) {
            Toast.makeText(this, "Bienvenido", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Datos incorrectos", Toast.LENGTH_SHORT).show()
        }
    }

    // 3. METODO PARA AUTENTICAR EN FIREBASE
    private fun autenticarFirebase(idToken: String) {
        val credencialFirebase = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credencialFirebase).addOnCompleteListener(this) { inicio ->
            if (inicio.isSuccessful) {
                // Corregido: currentUser en lugar de correntUser
                val usuario = auth.currentUser

                Toast.makeText(this, "Bienvenido ${usuario?.displayName}", Toast.LENGTH_SHORT)
                    .show()
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Error al validar con Firebase", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 4. METODO PARA PROCESAR LA CREDENCIAL
    private fun procesarCredencial(credential: Credential) {
        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            try {
                //variable para obtener la credencial de google
                val credencialGoogle = GoogleIdTokenCredential.createFrom(credential.data)
                autenticarFirebase(credencialGoogle.idToken)
            } catch (e: GoogleIdTokenParsingException) {
                Toast.makeText(this, "Error al procesar token de Google", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Tipo de credencial no reconocida", Toast.LENGTH_SHORT).show()
        }
    }

    // 5. METODO PARA INICIAR SESION CON GOOGLE
    private fun iniciarSesionGoogle() {
        val inicioGoogle = GetSignInWithGoogleOption.Builder(
            getString(R.string.default_web_client_id)
        ).build()

        val solicitud = GetCredentialRequest.Builder()
            .addCredentialOption(inicioGoogle)
            .build()

        lifecycleScope.launch {
            try {
                val resultado = credentialManager.getCredential(
                    request = solicitud,
                    context = this@MainActivity
                )
                procesarCredencial(resultado.credential)
            } catch (e: GetCredentialException) {
                Toast.makeText(this@MainActivity,"Inicio cancelado o fallido", Toast.LENGTH_SHORT).show()
            }
        }
    }
}