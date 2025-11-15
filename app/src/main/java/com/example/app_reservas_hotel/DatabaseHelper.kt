package com.example.app_reservas_hotel

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, dataBaseName, null, databaseVersion) {
    companion object {
        private const val dataBaseName = "hotel_reservas.db"
        private const val databaseVersion = 2 // <- incrementado para forzar onUpgrade
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            """
                             CREATE TABLE IF NOT EXISTS usuarios (
                                id INTEGER PRIMARY KEY AUTOINCREMENT,
                                username TEXT NOT NULL UNIQUE,
                                password TEXT NOT NULL
                             );
                        """.trimIndent()
        )
        db?.execSQL(
            """
                            CREATE TABLE IF NOT EXISTS HOTELES (
                                id INTEGER PRIMARY KEY AUTOINCREMENT,
                                nombre TEXT NOT NULL,
                                direccion TEXT NOT NULL,
                                telefono TEXT NOT NULL
                            );
                        """.trimIndent()
        )
        db?.execSQL(
            """
                            CREATE TABLE IF NOT EXISTS habitaciones (
                                id INTEGER PRIMARY KEY AUTOINCREMENT,
                                id_hotel INTEGER NOT NULL,
                                numero_habitacion INTEGER NOT NULL,
                                tipo TEXT NOT NULL,
                                precio REAL NOT NULL
                            );
                        """.trimIndent()
        )
        db?.execSQL(
            """
                             CREATE TABLE IF NOT EXISTS reservas (
                                id INTEGER PRIMARY KEY AUTOINCREMENT,
                                id_hotel INTEGER NOT NULL,
                                id_habitacion INTEGER NOT NULL,
                                id_usuario INTEGER NOT NULL,
                                nombre TEXT NOT NULL,
                                fecha_entrada TEXT NOT NULL,
                                fecha_salida TEXT NOT NULL,
                                numero_habitacion INTEGER NOT NULL
                            );
                        """.trimIndent()
        )

        db?.let { insertTestData(it) }
    }

    // eliminar onOpen para no re-intentar insertar datos en cada apertura
    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS reservas")
        db?.execSQL("DROP TABLE IF EXISTS habitaciones")
        db?.execSQL("DROP TABLE IF EXISTS HOTELES")
        db?.execSQL("DROP TABLE IF EXISTS usuarios")
        onCreate(db)
    }

    private fun insertTestData(db: SQLiteDatabase) {
        if (hasData(db, "HOTELES")) return

        val hotelNames = listOf(
            "Hotel Sol y Mar",
            "Hotel La Rivera",
            "Hotel Montaña Azul",
            "Hotel Centro Plaza",
            "Hotel Jardín del Lago"
        )

        val hotelAddresses = listOf(
            "Av. del Mar 123",
            "Calle Rivera 45",
            "Camino Alto 78",
            "Plaza Central 1",
            "Paseo del Lago 9"
        )

        val hotelPhones = listOf(
            "600-111-222",
            "600-222-333",
            "600-333-444",
            "600-444-555",
            "600-555-666"
        )

        val usuarios = listOf(
            Pair("user1", "pass1"),
            Pair("user2", "pass2"),
            Pair("user3", "pass3")
        )

        val roomTypes = listOf("Individual", "Doble", "Suite", "Familiar", "Económica")
        var basePrice = 50.0

        db.beginTransaction()
        try {
            for (i in hotelNames.indices) {
                val hv = ContentValues().apply {
                    put("nombre", hotelNames[i])
                    put("direccion", hotelAddresses.getOrNull(i) ?: "Dirección desconocida")
                    put("telefono", hotelPhones.getOrNull(i) ?: "000-000-000")
                }

                val hotelId = db.insert("HOTELES", null, hv)
                if (hotelId == -1L) continue

                for (roomIndex in 1..5) {
                    val rv = ContentValues().apply {
                        put("id_hotel", hotelId)
                        put("numero_habitacion", roomIndex)
                        put("tipo", roomTypes[(roomIndex - 1) % roomTypes.size])
                        put("precio", basePrice + roomIndex * 10)
                    }
                    db.insert("habitaciones", null, rv)
                }
                basePrice += 20.0
            }

            for (user in usuarios) {
                val uv = ContentValues().apply {
                    put("username", user.first)
                    put("password", user.second)
                }
                db.insertWithOnConflict("usuarios", null, uv, SQLiteDatabase.CONFLICT_IGNORE)
            }

            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    private fun hasData(db: SQLiteDatabase, table: String): Boolean {
        val cursor: Cursor = db.rawQuery("SELECT COUNT(*) FROM $table", null)
        cursor.use {
            return if (it.moveToFirst()) it.getInt(0) > 0 else false
        }
    }


    fun mostrarDatosPrueba(db: SQLiteDatabase): String {
        val sb = StringBuilder()
        val usuariosCursor = db.rawQuery("SELECT username,password FROM usuarios", null)
        usuariosCursor.use {
            if (it.moveToFirst()) {
                sb.append("Usuarios registrados:\n")
                do {
                    val username = it.getString(0)
                    val password = it.getString(1)
                    sb.append(" - $username $password\n")
                } while (it.moveToNext())
            } else {
                sb.append("No hay usuarios registrados.\n")
            }
        }
        val hotelsCursor = db.rawQuery("SELECT id, nombre, direccion, telefono FROM HOTELES", null)
        hotelsCursor.use { hc ->
            if (hc.moveToFirst()) {
                do {
                    val hid = hc.getLong(0)
                    val hname = hc.getString(1)
                    val haddr = hc.getString(2)
                    val hphone = hc.getString(3)
                    sb.append("Hotel: $hname\n")
                    sb.append("  Dirección: $haddr\n")
                    sb.append("  Teléfono: $hphone\n")
                    val roomsCursor = db.rawQuery(
                        "SELECT numero_habitacion, tipo, precio FROM habitaciones WHERE id_hotel = ?",
                        arrayOf(hid.toString())
                    )
                    roomsCursor.use { rc ->
                        if (rc.moveToFirst()) {
                            sb.append("  Habitaciones:\n")
                            do {
                                val num = rc.getInt(0)
                                val tipo = rc.getString(1)
                                val precio = rc.getDouble(2)
                                sb.append("    #$num - $tipo - \$${"%.2f".format(precio)}\n")
                            } while (rc.moveToNext())
                        } else {
                            sb.append("  (Sin habitaciones)\n")
                        }
                    }
                    sb.append("\n")
                } while (hc.moveToNext())
            } else {
                sb.append("No hay hoteles en la base de datos.\n")
            }
        }
        return sb.toString()
    }

    fun mostrarDatosPrueba(): String {
        val db = this.readableDatabase
        try {
            return mostrarDatosPrueba(db)
        } finally {
            db.close()
        }
    }

    fun registrarUsuario(username: String, password: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("username", username)
            put("password", password)
        }
        val result = db.insertWithOnConflict("usuarios", null, values, SQLiteDatabase.CONFLICT_IGNORE)
        return result != -1L
    }

    fun iniciarSesion(username: String, password: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT id FROM usuarios WHERE username = ? AND password = ?",
            arrayOf(username, password)
        )
        cursor.use {
            return it.count > 0
        }
    }

    fun mostrarHoteles(db: SQLiteDatabase): Cursor {
        return db.rawQuery("SELECT id, nombre, direccion, telefono FROM HOTELES", null)
    }

    fun mostrarHabitacionesPorHotel(db: SQLiteDatabase, hotelId: Long): Cursor {
        return db.rawQuery(
            "SELECT id, numero_habitacion, tipo, precio FROM habitaciones WHERE id_hotel = ?",
            arrayOf(hotelId.toString())
        )
    }

    fun mostrarReservasPorUsuario(db: SQLiteDatabase, userId: Long): Cursor {
        return db.rawQuery(
            "SELECT id, id_hotel, id_habitacion, nombre, fecha_entrada, fecha_salida, numero_habitacion FROM reservas WHERE id_usuario = ?",
            arrayOf(userId.toString())
        )
    }

    fun crearReserva(idHotel: Long, idHabitacion: Long, idUsuario: Long, nombre: String, fechaEntrada: String, fechaSalida: String, numeroHabitacion: Int): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("id_hotel", idHotel)
            put("id_habitacion", idHabitacion)
            put("id_usuario", idUsuario)
            put("nombre", nombre)
            put("fecha_entrada", fechaEntrada)
            put("fecha_salida", fechaSalida)
            put("numero_habitacion", numeroHabitacion)
        }
        val result = db.insert("reservas", null, values)
        return result != -1L
    }

    fun cancelarReserva(reservaId: Long): Boolean {
        val db = this.writableDatabase
        val result = db.delete("reservas", "id = ?", arrayOf(reservaId.toString()))
        return result > 0
    }
}