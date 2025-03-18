package com.example.practive.database.borrow

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.practive.R
import java.text.SimpleDateFormat
import java.util.*

class BorrowAdapter(
    private val isAdminPage: Boolean, // Admin mode flag
    private val onBookRetrieved: (BorrowWithUser) -> Unit // Callback when book is retrieved
) : ListAdapter<BorrowWithUser, BorrowAdapter.BorrowViewHolder>(BorrowDiffCallback()) {

    private val retrievedBooks = mutableSetOf<Int>() // Store retrieved book IDs

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BorrowViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_borrow, parent, false)
        return BorrowViewHolder(view)
    }

    override fun onBindViewHolder(holder: BorrowViewHolder, position: Int) {
        val borrow: BorrowWithUser = getItem(position)
        holder.bind(borrow, retrievedBooks.contains(borrow.borrowId))

        // Only allow clicking in admin mode
        if (isAdminPage) {
            holder.itemView.setOnClickListener {
                onBookRetrieved(borrow)
            }
        }
    }


    class BorrowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val bookCover: ImageView = itemView.findViewById(R.id.borrow_book_cover)
        private val bookTitle: TextView = itemView.findViewById(R.id.borrow_book_title)
        private val borrowDate: TextView = itemView.findViewById(R.id.borrow_date)
        private val returnDate: TextView = itemView.findViewById(R.id.return_date)
        private val username: TextView = itemView.findViewById(R.id.borrow_username)
        private val checkMark: ImageView = itemView.findViewById(R.id.check_mark) // ✅ Green check

        fun bind(borrowWithUser: BorrowWithUser, isRetrieved: Boolean) {
            bookTitle.text = borrowWithUser.bookTitle
            borrowDate.text = "Borrowed: ${formatDate(borrowWithUser.borrowDate)}"
            returnDate.text = "Return by: ${formatDate(borrowWithUser.returnDate)}"
            username.text = "Borrowed by: ${borrowWithUser.username}"

            val bitmap = borrowWithUser.bookPhoto?.let { decodeBitmap(it) }
            if (bitmap != null) {
                bookCover.setImageBitmap(bitmap)
            } else {
                bookCover.setImageResource(R.drawable.user) // Default placeholder image
            }

            // Show or hide green check ✅
            checkMark.visibility = if (borrowWithUser.isReturned) View.VISIBLE else View.GONE
        }

        private fun formatDate(timestamp: Long?): String {
            return timestamp?.let {
                SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(it))
            } ?: "Unknown Date"
        }

        private fun decodeBitmap(imageData: ByteArray): Bitmap? {
            return try {
                BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
            } catch (e: Exception) {
                Log.e("BorrowAdapter", "Error decoding bitmap: ${e.message}")
                null
            }
        }
    }

    class BorrowDiffCallback : DiffUtil.ItemCallback<BorrowWithUser>() {
        override fun areItemsTheSame(oldItem: BorrowWithUser, newItem: BorrowWithUser): Boolean {
            return oldItem.borrowId == newItem.borrowId
        }

        override fun areContentsTheSame(oldItem: BorrowWithUser, newItem: BorrowWithUser): Boolean {
            return oldItem == newItem
        }
    }
}
