package dev.chillin9panda.lms

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
class GenericAdapter<T>(
    private var items: List<T>,
    private val onEdit: (T) -> Unit,
    private val onDelete: (T) -> Unit
) : RecyclerView.Adapter<GenericAdapter<T>.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.ivRowIcon)
        val title: TextView = view.findViewById(R.id.tvMainTitle)
        val sub: TextView = view.findViewById(R.id.tvSubTitle)
        val edit: ImageButton = view.findViewById(R.id.btnEdit)
        val delete: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_row, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        if (item is Models.Author) {
            holder.icon.setImageResource(android.R.drawable.ic_menu_myplaces)
            holder.title.text = item.fullName
            holder.sub.text = item.email
        } else if (item is Models.Book) {
            holder.icon.setImageResource(android.R.drawable.ic_menu_agenda)
            holder.title.text = item.title
            holder.sub.text = "ISBN: ${item.isbn} | Author: ${item.authorName ?: "Unknown"}"
        }

        holder.edit.setOnClickListener { onEdit(item) }
        holder.delete.setOnClickListener { onDelete(item) }
    }

    override fun getItemCount() = items.size
}