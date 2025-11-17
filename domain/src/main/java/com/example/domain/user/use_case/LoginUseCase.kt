package com.example.domain.user.use_case

import com.example.domain.user.repository.UserRepository
import com.example.domain.user.entities.UserEntity

class LoginUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(username: String, password: String): Result<UserEntity> {
        return try {
            // Validaciones
            if (username.isBlank()) {
                return Result.failure(Exception("El nombre de usuario es requerido"))
            }

            if (password.isBlank()) {
                return Result.failure(Exception("La contraseña es requerida"))
            }

            // Intentar login
            val user = userRepository.login(username, password)

            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Credenciales inválidas"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}