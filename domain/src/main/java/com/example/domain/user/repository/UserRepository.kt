package com.example.domain.user.repository

import com.example.domain.user.entities.UserEntity
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun insertUser(user: UserEntity): Long
    suspend fun login(username: String, password: String): UserEntity?
    suspend fun getUserByUsername(username: String): UserEntity?
    fun getAllUsers(): Flow<List<UserEntity>>
    suspend fun updateUser(user: UserEntity)
    suspend fun deleteUser(user: UserEntity)
    suspend fun userExists(username: String): Boolean
}