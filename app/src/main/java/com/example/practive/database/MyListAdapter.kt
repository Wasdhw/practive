package com.example.practive.database

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.practive.R

class MyListAdapter() : RecyclerView.Adapter<MyListAdapter.MyViewHolder>() {

    private var bookList = emptyList<Book>()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val bookTitle: TextView = itemView.findViewById(R.id.booktitle)
        val bookAuthor: TextView = itemView.findViewById(R.id.bookauthor)
        val bookPublish: TextView = itemView.findViewById(R.id.bookpublish)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.book_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
       return bookList.size

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = bookList[position]
        holder.bookTitle.text = currentItem.bookname
        holder.bookAuthor.text = currentItem.author
        holder.bookPublish.text = currentItem.publish

        val layoutParams = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.setMargins(0, 0, 0, 0)  // Ensure no extra spacing
        holder.itemView.layoutParams = layoutParams
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(book: List<Book>){
        this.bookList = book
        notifyDataSetChanged()

    }

}