package com.example.data.user.room_models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.user.entities.UserEntity

@Entity(tableName = "users")
data class UserRoom(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val username: String,
    val password: String // En producción, NUNCA guardes contraseñas en texto plano
)

fun UserRoom.toDomain(): UserEntity {
    return UserEntity(
        id = this.id,
        username = this.username,
        password = this.password
    )
}

// De Entity de Domain a Entity de Room
fun UserEntity.toRoom(): UserRoom {
    return UserRoom(
        id = this.id,
        username = this.username,
        password = this.password
    )
}

// Para listas
fun List<UserRoom>.toDomain(): List<UserEntity> {
    return this.map { it.toDomain() }
}