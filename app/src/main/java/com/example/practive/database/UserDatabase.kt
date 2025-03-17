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

@Database(entities = [User::class, Book::class, BorrowRecord::class], version = 8, exportSchema = false)
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
                )
                    .addMigrations(MIGRATION_7_8,) // Added book_data migration
                    .build()
                Log.d("DatabaseCheck", "Database Created!")
                INSTANCE = instance
                instance
            }
        }

        // 🛠 Migration for borrow_table
        val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS borrow_table_new (
                borrowId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                userId INTEGER NOT NULL, 
                bookId INTEGER NOT NULL, 
                borrowDate INTEGER NOT NULL, 
                returnDate INTEGER NOT NULL, 
                isReturned INTEGER NOT NULL DEFAULT 0, 
                bookTitle TEXT NOT NULL DEFAULT '',
                bookCoverPath TEXT NOT NULL DEFAULT ''
            )
            """
                )

                database.execSQL(
                    """
            INSERT INTO borrow_table_new (borrowId, userId, bookId, borrowDate, returnDate, isReturned, bookTitle, bookCoverPath)
            SELECT borrowId, userId, bookId, borrowDate, returnDate, 
                   COALESCE(isReturned, 0), COALESCE(bookTitle, ''), COALESCE(bookCoverPath, '') 
            FROM borrow_table
            """
                )

                database.execSQL("DROP TABLE borrow_table")
                database.execSQL("ALTER TABLE borrow_table_new RENAME TO borrow_table")
            }
        }

    }
}
