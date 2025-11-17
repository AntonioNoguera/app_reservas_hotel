package com.example.domain.user.use_case

import com.example.domain.user.repository.UserRepository
import com.example.domain.user.entities.UserEntity

class RegisterUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(username: String, password: String): Result<Long> {
        return try {
            // Validaciones
            if (username.isBlank()) {
                return Result.failure(Exception("El nombre de usuario es requerido"))
            }

            if (password.isBlank()) {
                return Result.failure(Exception("La contraseña es requerida"))
            }

            if (username.length < 3) {
                return Result.failure(Exception("El usuario debe tener al menos 3 caracteres"))
            }

            if (password.length < 6) {
                return Result.failure(Exception("La contraseña debe tener al menos 6 caracteres"))
            }

            // Verificar si existe
            val exists = userRepository.userExists(username)
            if (exists) {
                return Result.failure(Exception("El usuario ya existe"))
            }

            // Crear usuario
            val newUser = UserEntity(
                username = username,
                password = password
            )

            val userId = userRepository.insertUser(newUser)
            Result.success(userId)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}