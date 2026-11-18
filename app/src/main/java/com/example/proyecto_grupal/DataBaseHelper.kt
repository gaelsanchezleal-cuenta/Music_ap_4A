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
        private const val DATABASE_VERSION = 2

        // TABLA USUARIOS
        private const val TABLE_USUARIOS = "usuarios"
        private const val COL_ID = "id"
        private const val COL_NOMBRE = "nombre"
        private const val COL_CORREO = "correo"
        private const val COL_PASSWORD = "password"

        // TABLA FAVORITOS
        const val TABLE_FAVORITOS = "favoritos"
        const val COL_FAVORITO_ID = "id"
        const val COL_ALBUM_ID = "album_id"
        const val COL_NOMBRE_ALBUM = "nombre_album"
        const val COL_ARTISTA = "artista"
        const val COL_GENERO = "genero"
        const val COL_CANCIONES = "canciones"
        const val COL_FECHA = "fecha"
        const val COL_IMAGEN = "imagen"

        // Sentencia SQL para crear la tabla usuarios
        private const val CREATE_TABLE_USUARIOS = """
            CREATE TABLE $TABLE_USUARIOS (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_NOMBRE TEXT NOT NULL,
                $COL_CORREO TEXT NOT NULL UNIQUE,
                $COL_PASSWORD TEXT NOT NULL
            )
        """

        // Sentencia SQL para crear la tabla favoritos
        private const val CREATE_TABLE_FAVORITOS = """
            CREATE TABLE $TABLE_FAVORITOS (
                $COL_FAVORITO_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_ALBUM_ID INTEGER UNIQUE NOT NULL,
                $COL_NOMBRE_ALBUM TEXT NOT NULL,
                $COL_ARTISTA TEXT NOT NULL,
                $COL_GENERO TEXT,
                $COL_CANCIONES TEXT,
                $COL_FECHA TEXT,
                $COL_IMAGEN TEXT
            )
        """
    }

    // 1. METODO PARA CREAR LA BASE DE DATOS
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE_USUARIOS.trimIndent())
        db?.execSQL(CREATE_TABLE_FAVORITOS.trimIndent())
    }

    // 2. METODO PARA ACTUALIZAR LA BASE DE DATOS
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //verificar que la version sea menor a 2
        if (oldVersion <2 ){
            db?.execSQL(CREATE_TABLE_FAVORITOS)
        }
    }

    // 3. METODO PARA REGISTRAR USUARIO
    fun registrarUsuario(nombre: String, correo: String, password: String): Boolean {
        val db = writableDatabase

        val datos = ContentValues().apply {
            put(COL_NOMBRE, nombre)
            put(COL_CORREO, correo)
            put(COL_PASSWORD, password)
        }

        val resultado = db.insert(TABLE_USUARIOS, null, datos)

        return resultado != -1L
    }

    // 4. METODO PARA CONSULTAR A UN USUARIO (INICIAR SESION)
    fun iniciarSesion(correo: String, password: String): Boolean {
        val db = readableDatabase

        val querySql = """
            SELECT * FROM $TABLE_USUARIOS
            WHERE $COL_CORREO = ? AND $COL_PASSWORD = ?
        """.trimIndent()

        val cursor = db.rawQuery(querySql, arrayOf(correo, password))
        val usuarioEncontrado = cursor.count > 0

        cursor.close()

        return usuarioEncontrado
    }

    // 5. METODO PARA REGISTRAR INFORMACION PROVENIENTE DE LA API
    fun guardarAlbumFavorito(
        albumId: Long,
        nombreAlbum: String,
        artista: String,
        genero: String,
        canciones: String,
        fecha: String,
        imagen: String
    ): Boolean {
        val db = writableDatabase

        val datos = ContentValues().apply {
            put(COL_ALBUM_ID, albumId)
            put(COL_NOMBRE_ALBUM, nombreAlbum)
            put(COL_ARTISTA, artista)
            put(COL_GENERO, genero)
            put(COL_CANCIONES, canciones)
            put(COL_FECHA, fecha)
            put(COL_IMAGEN, imagen)
        }

        // Inserción en la tabla ignorando duplicados si album_id ya existe
        val resultado =
            db.insertWithOnConflict(TABLE_FAVORITOS, null, datos, SQLiteDatabase.CONFLICT_IGNORE)

        return resultado != -1L
    }

    // 6. METODO PARA ELIMINAR UN REGISTRO DE FAVORITOS
    fun eliminarAlbumFavorito(albumId: Long): Boolean {
        val db = writableDatabase

        // delete() devuelve el número de filas eliminadas
        val filasEliminadas = db.delete(
            TABLE_FAVORITOS,
            "$COL_ALBUM_ID = ?",
            arrayOf(albumId.toString())
        )

        return filasEliminadas > 0 // Devuelve true si se eliminó al menos 1 registro
    }

    // METODO PARA VERIFICAR QUE UN REGISTRO EXISTE
    fun existeAlbumFavorito(albumId: Long): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT $COL_FAVORITO_ID FROM $TABLE_FAVORITOS WHERE $COL_ALBUM_ID = ?",
            arrayOf(albumId.toString())

        )
        val existe = cursor.moveToFirst()
        cursor.close()

        return existe
    }
}