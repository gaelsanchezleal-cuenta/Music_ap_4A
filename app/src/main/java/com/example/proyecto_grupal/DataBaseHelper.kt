package com.example.proyecto_grupal

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DataBaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    companion object {
        private const val DATABASE_NAME = "musica.db"
        private const val DATABASE_VERSION = 1

        // CREACIÓN DE TABLA Y COLUMNAS
        private const val TABLE_USUARIOS = "usuarios"
        private const val COL_ID = "id"
        private const val COL_NOMBRE = "nombre"
        private const val COL_CORREO = "correo"
        private const val COL_PASSWORD = "password"
    }

    // 1. Metodo que se ejecuta al crear la Base de Datos por primera vez
    override fun onCreate(db: SQLiteDatabase?) {
        val crearTablaUsuarios = """
            CREATE TABLE $TABLE_USUARIOS (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_NOMBRE TEXT NOT NULL,
                $COL_CORREO TEXT NOT NULL UNIQUE,
                $COL_PASSWORD TEXT NOT NULL
            )
        """.trimIndent()

        db?.execSQL(crearTablaUsuarios)
    }

    // Metodo para actualizar la base de datos
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USUARIOS")
        onCreate(db)
    }

    // METODO PARA REGISTRAR
    fun registrarUsuario(nombre: String, correo: String, password: String): Boolean {
        // 1. Obtener la base de datos en modo escritura
        val db = writableDatabase

        // 2. Empaquetar los datos recibidos
        val datos = ContentValues().apply {
            put(COL_NOMBRE, nombre)
            put(COL_CORREO, correo)
            put(COL_PASSWORD, password)
        }

        //aqui vienen los 1L o -1L
        val resultado = db.insert(TABLE_USUARIOS, null, datos)

        db.close()

        //comprara el valor del resultado de la insersion y regresarlo como boolean
        // Verificamos con un if convencional
        if (resultado == -1L) {
            return false // Falló el registro (por ejemplo, correo duplicado)
        } else {
            return true  // Se registró con éxito
        }

        // METODO PPARA CONSULTAR A UN USARIO
        fun iniciarSesion(correo: String, password: String): Boolean {

            //readableDatabase se ocupa para leer en la base de datos
            val db = readableDatabase

            val query_sql = """
                SELECT * FROM $TABLE_USUARIOS
                
                WHERE $COL_CORREO = ? AND $COL_PASSWORD = ?
            """.trimIndent()

            // CREAR CURSOR PARA RECORRER LA INFORMACION

            //rawquery le pasa la informacion (los parametros)
            //el cursor devolvera la cantidad de registros que encontro
            val cursor = db.rawQuery(
                query_sql, arrayOf(correo, password)
            )

            //crear variable que convierte el cursor a boolean
            val usuarioEncontrar = cursor.count > 0

            //cerramos para evitar complicaciones
            cursor.close()
            db.close()

            //retornamos lo que nos dio el convirtor
            return  usuarioEncontrar
        }

    }
}