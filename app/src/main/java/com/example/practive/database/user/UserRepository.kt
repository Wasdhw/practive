package com.example.practive.database.user


import androidx.lifecycle.LiveData
import com.example.practive.database.user.UserDao

class UserRepository(private val userDao: UserDao) {

    // Function to update user password
    suspend fun updateUserPassword(userId: Int, newPassword: String) {
        userDao.updatePassword(userId, newPassword)
    }

    suspend fun getUserByIdSync(userId: Int): User? {  // Add this function
        return userDao.getUserByIdSync(userId)
    }

    fun getUserById(userId: Int): LiveData<User> {
        return userDao.getUserById(userId)
    }
}
