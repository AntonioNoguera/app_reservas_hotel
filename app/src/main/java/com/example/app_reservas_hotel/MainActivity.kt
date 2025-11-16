package com.example.app_reservas_hotel

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val container = findViewById<LinearLayout>(R.id.container)

        fun normalize(path: String?): String? {
            if (path.isNullOrEmpty()) return null
            var p = path.replaceFirst("^images/images/".toRegex(), "images/")
            if (!p.startsWith("images/")) p = "images/" + p
            return p
        }

        fun assetExists(path: String): Boolean {
            return try {
                assets.open(path).close()
                true
            } catch (e: Exception) {
                Log.w(TAG, "Asset no encontrado: $path", e)
                false
            }
        }

        // Leer la BD en background y poblar la UI
        Thread {
            try {
                val dbHelper = DatabaseHelper(this@MainActivity)
                val db = dbHelper.readableDatabase
                val cursor = dbHelper.mostrarHoteles(db)

                cursor.use { c ->
                    if (c.moveToFirst()) {
                        do {
                            val hid = c.getLong(0)
                            val hname = c.getString(1)
                            val haddr = c.getString(2)
                            val hphone = c.getString(3)
                            val rawHfoto = c.getString(4)
                            val hfoto = normalize(rawHfoto)

                            // Inflar layout del hotel
                            val view = LayoutInflater.from(this@MainActivity).inflate(R.layout.hotel_item, container, false)
                            val img = view.findViewById<ImageView>(R.id.hotelImage)
                            val nameTv = view.findViewById<TextView>(R.id.hotelName)
                            val addrTv = view.findViewById<TextView>(R.id.hotelAddr)
                            val phoneTv = view.findViewById<TextView>(R.id.hotelPhone)
                            val roomsContainer = view.findViewById<LinearLayout>(R.id.roomsContainer)

                            nameTv.text = hname
                            addrTv.text = "Dirección: $haddr"
                            phoneTv.text = "Tel: $hphone"

                            // Seleccionar imagen: hotel o primera habitación disponible
                            var selectedPath: String? = null
                            if (!hfoto.isNullOrEmpty() && assetExists(hfoto)) selectedPath = hfoto

                            val roomsCursor = dbHelper.mostrarHabitacionesPorHotel(db, hid)
                            roomsCursor.use { rc ->
                                if (rc.moveToFirst()) {
                                    do {
                                        val num = rc.getInt(1)
                                        val tipo = rc.getString(2)
                                        val precio = rc.getDouble(3)
                                        val rfoto = normalize(rc.getString(4))

                                        // Si no hay foto seleccionada, usar la primera habitacion que tenga imagen
                                        if (selectedPath == null && !rfoto.isNullOrEmpty() && assetExists(rfoto)) {
                                            selectedPath = rfoto
                                        }

                                        // Añadir fila de habitación (texto simple)
                                        val roomTv = TextView(this@MainActivity)
                                        roomTv.text = "    #$num - $tipo - \$${"%.2f".format(precio)}"
                                        roomTv.textSize = 14f
                                        roomsContainer.addView(roomTv)
                                    } while (rc.moveToNext())
                                } else {
                                    val emptyTv = TextView(this@MainActivity)
                                    emptyTv.text = "    (Sin habitaciones)"
                                    roomsContainer.addView(emptyTv)
                                }
                            }

                            // Mostrar la imagen en el hilo UI usando Glide y URI assets
                            runOnUiThread {
                                if (selectedPath != null) {
                                    val uri = "file:///android_asset/$selectedPath"
                                    Glide.with(this@MainActivity)
                                        .load(uri)
                                        .centerCrop()
                                        .placeholder(android.R.drawable.progress_indeterminate_horizontal)
                                        .error(android.R.drawable.ic_menu_report_image)
                                        .into(img)
                                } else {
                                    img.setImageResource(android.R.drawable.ic_menu_report_image)
                                }

                                container.addView(view)
                            }

                        } while (c.moveToNext())
                    } else {
                        runOnUiThread {
                            val tv = TextView(this@MainActivity)
                            tv.text = "No hay hoteles en la base de datos."
                            container.addView(tv)
                        }
                    }
                }

                db.close()
            } catch (e: Exception) {
                Log.e(TAG, "Error al leer la base de datos", e)
                runOnUiThread {
                    val tv = TextView(this@MainActivity)
                    val msg = e.message ?: "Desconocido"
                    tv.text = "Error al acceder a la base de datos: $msg"
                    container.addView(tv)
                }
            }
        }.start()

//        // Mantener la redirección al login tras 5s
//        Handler(Looper.getMainLooper()).postDelayed({
//            startActivity(Intent(this@MainActivity, Login::class.java))
//            finish()
//        }, 5000L)
    }
}