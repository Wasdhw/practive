package com.example.practive.database.book

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "book_data")
data class Book(
    @PrimaryKey(autoGenerate = true)
    val bookId: Int = 0,
    val bookname: String,
    val author: String,
    val publish: String,
    val photo: ByteArray?, // Store image as a BLOB
    val totalCopies: Int,
    val desc: String,
    val borrowCount: Int = 0,


) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Book) return false

        return bookId == other.bookId &&
                bookname == other.bookname &&
                author == other.author &&
                desc == other.desc &&
                publish == other.publish &&
                photo.contentEquals(other.photo)

    }

    override fun hashCode(): Int {
        return 31 * bookId.hashCode() + bookname.hashCode() + author.hashCode() +
                publish.hashCode() + (photo?.contentHashCode() ?: 0)
    }
}