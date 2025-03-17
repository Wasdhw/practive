package com.example.practive.database.borrow

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

class BorrowAdapter : ListAdapter<BorrowRecord, BorrowAdapter.BorrowViewHolder>(BorrowDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BorrowViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_borrow, parent, false)
        return BorrowViewHolder(view)
    }

    override fun onBindViewHolder(holder: BorrowViewHolder, position: Int) {
        val borrow: BorrowRecord = getItem(position)
        Log.d("BorrowAdapter", "Binding book: ${borrow.bookTitle}, Image Size: ${borrow.bookPhoto?.size ?: 0}")
        holder.bind(borrow)
    }

    class BorrowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val bookCover: ImageView = itemView.findViewById(R.id.borrow_book_cover)
        private val bookTitle: TextView = itemView.findViewById(R.id.borrow_book_title)
        private val borrowDate: TextView = itemView.findViewById(R.id.borrow_date)
        private val returnDate: TextView = itemView.findViewById(R.id.return_date)

        fun bind(borrow: BorrowRecord) {
            bookTitle.text = borrow.bookTitle
            borrowDate.text = "Borrowed: ${formatDate(borrow.borrowDate)}"
            returnDate.text = "Return by: ${formatDate(borrow.returnDate)}"

            // Load book cover from database BLOB
            if (borrow.bookPhoto != null && borrow.bookPhoto!!.isNotEmpty()) {
                val bitmap = BitmapFactory.decodeByteArray(borrow.bookPhoto, 0, borrow.bookPhoto!!.size)
                bookCover.setImageBitmap(bitmap)
            } else {
                Log.e("BorrowAdapter", "No image found for ${borrow.bookTitle}, using default image.")
                bookCover.setImageResource(R.drawable.user) // Default placeholder image
            }
        }

        private fun formatDate(timestamp: Long?): String {
            return if (timestamp != null) {
                SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(timestamp))
            } else {
                "Unknown Date"
            }
        }
    }

    class BorrowDiffCallback : DiffUtil.ItemCallback<BorrowRecord>() {
        override fun areItemsTheSame(oldItem: BorrowRecord, newItem: BorrowRecord): Boolean {
            return oldItem.bookId == newItem.bookId
        }

        override fun areContentsTheSame(oldItem: BorrowRecord, newItem: BorrowRecord): Boolean {
            return oldItem == newItem
        }
    }
}
