package com.example.data.user

import androidx.room.*
import com.example.data.user.room_models.UserRoom
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserRoom): Long

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserRoom>>

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserRoom?

    @Query("SELECT * FROM users WHERE username = :username AND password = :password LIMIT 1")
    suspend fun login(username: String, password: String): UserRoom?

    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE username = :username)")
    suspend fun userExists(username: String): Boolean

    @Update
    suspend fun updateUser(user: UserRoom)

    @Delete
    suspend fun deleteUser(user: UserRoom)
}