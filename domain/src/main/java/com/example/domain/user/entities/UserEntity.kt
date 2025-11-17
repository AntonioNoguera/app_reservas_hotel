package com.example.domain.user.entities

// Modelo para la capa de dominio del usuario!
// Modelo de usuario, independiente de todas las demás capas
// abstrae la lógica de negocios
data class UserEntity(
    val id: Int = 0,
    val username: String,
    val password: String
)