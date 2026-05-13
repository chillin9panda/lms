package dev.chillin9panda.lms

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
class AuthorFragment : Fragment(R.layout.fragment_author) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var db: DatabaseHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = DatabaseHelper(requireContext())
        recyclerView = view.findViewById(R.id.rvAuthors)
        tvEmpty = view.findViewById(R.id.tvEmptyAuthor)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        loadAuthors()
    }

    override fun onResume() {
        super.onResume()
        loadAuthors()
    }

    private fun loadAuthors() {
        val authors = db.getAllAuthors()

        if (authors.isEmpty()) {
            tvEmpty.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            tvEmpty.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE

            recyclerView.adapter = GenericAdapter(
                items = authors,
                onEdit = { author ->
                    val intent = Intent(requireContext(), AddAuthorActivity::class.java)
                    intent.putExtra("AUTHOR_ID", author.id)
                    intent.putExtra("AUTHOR_NAME", author.fullName)
                    intent.putExtra("AUTHOR_EMAIL", author.email)
                    startActivity(intent)
                },
                onDelete = { author ->
                    showDeleteConfirmation(author)
                }
            )
        }
    }

    private fun showDeleteConfirmation(author: Models.Author) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Author")
            .setMessage("Are you sure you want to delete ${author.fullName}? This will also delete all their books.")
            .setPositiveButton("Delete") { _, _ ->
                val result = db.deleteAuthor(author.id)
                if (result > 0) {
                    Toast.makeText(context, "Author deleted", Toast.LENGTH_SHORT).show()
                    loadAuthors()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}