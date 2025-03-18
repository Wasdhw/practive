package com.example.practive.database

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.practive.R
import com.example.practive.database.book.Book

class MyListAdapter(private val onBookClick: (Book) -> Unit) : RecyclerView.Adapter<MyListAdapter.MyViewHolder>() {

    private var bookList = mutableListOf<Book>()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bookTitle: TextView = itemView.findViewById(R.id.booktitle)
        val bookAuthor: TextView = itemView.findViewById(R.id.bookauthor)
        val bookPublish: TextView = itemView.findViewById(R.id.bookpublish)
        val bookImage: ImageView = itemView.findViewById(R.id.BookImage)
        val copies: TextView = itemView.findViewById(R.id.copies)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.book_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = bookList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = bookList[position]
        holder.bookTitle.text = currentItem.bookname
        holder.bookAuthor.text = "Author: ${currentItem.author}"
        holder.bookPublish.text = "Published: ${currentItem.publish}"
        holder.copies.text = "Stock: ${currentItem.totalCopies.toString()}"

        if (currentItem.photo != null) {
            val bitmap = BitmapFactory.decodeByteArray(currentItem.photo, 0, currentItem.photo!!.size)
            holder.bookImage.setImageBitmap(bitmap)
        }

        currentItem.photo?.let {
            val bitmap: Bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
            holder.bookImage.setImageBitmap(bitmap)
        }


        holder.itemView.setOnClickListener {
            onBookClick(currentItem) // Pass the selected book
        }

        val layoutParams = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.setMargins(0, 0, 0, 0) // Ensure no extra spacing
        holder.itemView.layoutParams = layoutParams
    }




    @SuppressLint("NotifyDataSetChanged")
    fun setData(book: List<Book>) {
        bookList = book.toMutableList()
        notifyDataSetChanged()
    }
}
