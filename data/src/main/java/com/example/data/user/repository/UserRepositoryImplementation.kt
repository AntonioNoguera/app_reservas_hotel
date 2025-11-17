package com.example.data.user.repository

import com.example.data.user.UserDao
import com.example.data.user.room_models.UserRoom
import com.example.data.user.room_models.toDomain
import com.example.data.user.room_models.toRoom
import com.example.domain.user.entities.UserEntity
import com.example.domain.user.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRepositoryImpl(
    private val userDao: UserDao
) : UserRepository {

    override suspend fun insertUser(user: UserEntity): Long {
        return userDao.insertUser(user.toRoom())
    }

    override suspend fun login(username: String, password: String): UserEntity? {
        val user: UserRoom? = userDao.login(username, password)
        return user?.toDomain()
    }

    override suspend fun getUserByUsername(username: String): UserEntity? {
        return userDao.getUserByUsername(username)?.toDomain()
    }

    override fun getAllUsers(): Flow<List<UserEntity>> {
        return userDao.getAllUsers().map { roomList ->
            roomList.toDomain()
        }
    }

    override suspend fun updateUser(user: UserEntity) {
        userDao.updateUser(user.toRoom())
    }

    override suspend fun deleteUser(user: UserEntity) {
        userDao.deleteUser(user.toRoom())
    }

    override suspend fun userExists(username: String): Boolean {
        return userDao.userExists(username)
    }
}