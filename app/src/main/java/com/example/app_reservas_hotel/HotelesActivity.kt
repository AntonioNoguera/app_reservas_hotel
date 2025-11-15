// File: `app/src/main/java/com/example/app_reservas_hotel/HotelesActivity.kt`
package com.example.app_reservas_hotel

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HotelesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hoteles)

        val username = intent.getStringExtra("username")
        val txtWelcome = findViewById<TextView?>(R.id.txtWelcome)
        txtWelcome?.text = if (!username.isNullOrEmpty()) "Bienvenido, $username" else "Bienvenido"
    }
}