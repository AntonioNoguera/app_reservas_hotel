// File: `app/src/main/java/com/example/app_reservas_hotel/Login.kt`
package com.example.app_reservas_hotel

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.app_reservas_hotel.databinding.ActivityLoginBinding
import com.example.data.database.AppDatabase
import com.example.data.user.repository.UserRepositoryImpl
import com.example.domain.user.use_case.LoginUseCase
import com.example.domain.user.use_case.RegisterUseCase
import com.example.domain.user.entities.UserEntity
import kotlinx.coroutines.launch

class Login : AppCompatActivity() {
    private lateinit var loginUseCase: LoginUseCase
    private lateinit var registerUserUseCase: RegisterUseCase

    private lateinit var binding: ActivityLoginBinding

    private var username: Editable? = null
    private var password: Editable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicializar Use Cases (Por que no hay DI - Data Injection)
        val database = AppDatabase.getDatabase(this)
        val userRepository = UserRepositoryImpl(database.userDao())

        loginUseCase = LoginUseCase(userRepository)
        registerUserUseCase = RegisterUseCase(userRepository)


        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.setUpBinding()
        this.setActions()
    }

    private fun setUpBinding() {
        binding.apply {
            username = TboxUser.text
            password = TboxUsuarioPassword.text
        }
    }

    private fun setActions() {
        // set login
        binding.apply {
            BtnLogin.setOnClickListener { login() }
            btnRedirectRegister.setOnClickListener { register() }
        }
    }

    private fun login() {
        lifecycleScope.launch {
            val result: Result<UserEntity> = loginUseCase(
                username.toStringTrimmed(),
                password.toStringTrimmed()
            )

            result.onSuccess { user ->
                Toast.makeText(
                    this@Login,
                    "Bienvenido ${user.username}",
                    Toast.LENGTH_SHORT
                ).show()

                val intent = Intent(this@Login, MainActivity::class.java)
                intent.putExtra("username", user.username)
                startActivity(intent)
                finish()
            }

            result.onFailure { exception ->
                Toast.makeText(
                    this@Login,
                    exception.message ?: "Error desconocido",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun register() {
        lifecycleScope.launch {
            val result: Result<Long> = registerUserUseCase(
                username.toStringTrimmed(),
                password.toStringTrimmed()
            )

            result.onSuccess { userID ->
                Toast.makeText(
                    this@Login,
                    "Creado usuario: ${username} con ID: ${userID}",
                    Toast.LENGTH_SHORT
                ).show()

            }

            result.onFailure { exception ->
                Toast.makeText(
                    this@Login,
                    exception.message ?: "Error desconocido",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun Editable?.toStringTrimmed(): String {
        return this?.toString()?.trim() ?: ""
    }
}