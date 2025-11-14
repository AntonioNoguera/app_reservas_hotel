// kotlin
package com.example.app_reservas_hotel

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class Login : AppCompatActivity() {
    private val TAG = "login"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val BtnLogin = findViewById<Button>(R.id.BtnLogin)
        val TboxUser = findViewById<EditText>(R.id.TboxUser)
        val TboxPassword = findViewById<EditText>(R.id.TboxUsuarioPassword)

        BtnLogin.setOnClickListener {
            if (TboxUser.text.toString().isNotEmpty() && TboxPassword.text.toString().isNotEmpty()) {
                assignAttributesLogin(TboxUser, TboxPassword)
            }
        }
    }

    fun assignAttributesLogin(TboxUser: EditText, TboxPassword: EditText) {
        val user = Dao(TboxUser.text.toString(), TboxPassword.text.toString())
        Log.d(TAG, "Usuario creado: ${user.username}")
        // aquí continúa la lógica de autenticación...
    }
}