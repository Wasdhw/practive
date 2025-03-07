package com.example.practive.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BookViewmodel(application: Application): AndroidViewModel(application) {

    val readAllData: LiveData<List<Book>>
    private val repository: BookRepository

    init {
        val bookDao = UserDatabase.getDatabase(application).bookDao()
        repository = BookRepository(bookDao)
        readAllData = repository.readAllData
    }
    fun addBook(book: Book){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addBook(book)

        }


    }
}