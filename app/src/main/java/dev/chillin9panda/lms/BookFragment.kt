package dev.chillin9panda.lms
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView

class BookFragment : Fragment(R.layout.fragment_book) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var db: DatabaseHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = DatabaseHelper(requireContext())
        recyclerView = view.findViewById(R.id.rvBooks)
        tvEmpty = view.findViewById(R.id.tvEmptyBook)

        loadBooks()
    }

    override fun onResume() {
        super.onResume()
        loadBooks()
    }

    private fun loadBooks() {
        val books = db.getAllBooks()

        if (books.isEmpty()) {
            tvEmpty.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            tvEmpty.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE

            recyclerView.adapter = GenericAdapter(
                items = books,
                onEdit = { book ->
                    val intent = Intent(requireContext(), AddBookActivity::class.java)
                    intent.putExtra("BOOK_ID", book.id)
                    intent.putExtra("BOOK_TITLE", book.title)
                    intent.putExtra("BOOK_ISBN", book.isbn)
                    intent.putExtra("BOOK_YEAR", book.publishedYear)
                    intent.putExtra("BOOK_GENRE", book.genre)
                    intent.putExtra("BOOK_AUTHOR_ID", book.authorId)
                    startActivity(intent)
                },
                onDelete = { book ->
                    showDeleteConfirmation(book)
                }
            )
        }
    }

    private fun showDeleteConfirmation(book: Models.Book) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Book")
            .setMessage("Are you sure you want to remove '${book.title}'?")
            .setPositiveButton("Delete") { _, _ ->
                db.deleteBook(book.id)
                loadBooks()
                Toast.makeText(context, "Book removed", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}