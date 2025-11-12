package com.example.app_reservas_hotel

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val output = findViewById<TextView>(R.id.textView)

        Thread {
            try {
                val dbHelper = DatabaseHelper(this)

                // Usar la sobrecarga que abre/cierra la BD internamente
                val resultText = dbHelper.mostrarDatosPrueba()

                runOnUiThread {
                    output.text = resultText
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error al leer la base de datos", e)
                runOnUiThread {
                    val msg = e.message ?: "Desconocido"
                    output.text = getString(R.string.error_db_access, msg)
                }
            }
        }.start()
    }
}