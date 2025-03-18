package com.example.practive.database.user


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practive.database.user.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    suspend fun getUserByIdSync(userId: Int): User? {  // Add this function
        return repository.getUserByIdSync(userId)
    }

    fun getUserById(userId: Int): LiveData<User> {
        return repository.getUserById(userId)
    }

    fun updatePassword(userId: Int, newPassword: String) {
        viewModelScope.launch {
            repository.updateUserPassword(userId, newPassword)
        }
    }
}
