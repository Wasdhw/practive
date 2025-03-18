package com.example.practive.database

import android.content.Context
import android.util.Log
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.practive.database.book.Book
import com.example.practive.database.book.BookDao
import com.example.practive.database.borrow.BorrowDao
import com.example.practive.database.borrow.Converter
import com.example.practive.database.borrow.BorrowRecord
import com.example.practive.database.user.User
import com.example.practive.database.user.UserDao

@Database(entities = [User::class, Book::class, BorrowRecord::class], version = 1, exportSchema = false)
@TypeConverters(Converter::class)
abstract class UserDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun bookDao(): BookDao
    abstract fun borrowDao(): BorrowDao

    companion object {
        @Volatile
        private var INSTANCE: UserDatabase? = null

        fun getDatabase(context: Context): UserDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    "user_database"
                ).build()

                Log.d("DatabaseCheck", "Database Created!")
                INSTANCE = instance
                instance
            }
        }
    }
}
