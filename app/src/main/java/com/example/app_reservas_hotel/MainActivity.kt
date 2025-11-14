package com.example.app_reservas_hotel

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private val DELAY_MS = 5000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val output = findViewById<TextView>(R.id.textView)

        // Programar la redirección en el hilo principal (se ejecutará después de 5s)
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this@MainActivity, Login::class.java))
            finish()
        }, DELAY_MS)

        // Lectura de BD en segundo plano (no bloquea la UI ni la redirección)
        Thread {
            try {
                val dbHelper = DatabaseHelper(this@MainActivity)
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