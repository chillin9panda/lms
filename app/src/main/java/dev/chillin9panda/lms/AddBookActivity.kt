package dev.chillin9panda.lms

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddBookActivity : AppCompatActivity() {

    private var isEditMode = false
    private var bookId: Int = -1

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_book)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val db = DatabaseHelper(this)

        val etTitle = findViewById<EditText>(R.id.etBookTitle)
        val etIsbn = findViewById<EditText>(R.id.etISBN)
        val etYear = findViewById<EditText>(R.id.etYear)
        val spinnerGenre = findViewById<Spinner>(R.id.spinnerGenre)
        val spinnerAuthors = findViewById<Spinner>(R.id.spinnerAuthors)
        val btnSave = findViewById<Button>(R.id.btnSaveBook)

        val authorsFromDb = db.getAllAuthors()
        val authorNames = mutableListOf("Select Author")
        authorNames.addAll(authorsFromDb.map { it.fullName })

        val authorAdapter = ArrayAdapter(this, R.layout.spinner_item, authorNames)
        authorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerAuthors.adapter = authorAdapter

        bookId = intent.getIntExtra("BOOK_ID", -1)
        if (bookId != -1) {
            isEditMode = true
            findViewById<TextView>(R.id.tvAddBookTitle).text = "Edit Book"
            findViewById<Button>(R.id.btnSaveBook).text = "Update Book"

            etTitle.setText(intent.getStringExtra("BOOK_TITLE"))
            etIsbn.setText(intent.getStringExtra("BOOK_ISBN"))
            etYear.setText(intent.getIntExtra("BOOK_YEAR", 2024).toString())

            val genreArray = resources.getStringArray(R.array.genre_options)
            val currentGenre = intent.getStringExtra("BOOK_GENRE")
            val genrePos = genreArray.indexOf(currentGenre)
            if (genrePos >= 0) spinnerGenre.setSelection(genrePos)

            val currentAuthorId = intent.getIntExtra("BOOK_AUTHOR_ID", -1)

            val authorPos = authorsFromDb.indexOfFirst { it.id == currentAuthorId }
            if (authorPos >= 0) spinnerAuthors.setSelection(authorPos + 1)
        }

        btnSave.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val isbn = etIsbn.text.toString().trim()
            val yearStr = etYear.text.toString().trim()
            val year = yearStr.toIntOrNull() ?: 0

            val genrePos = spinnerGenre.selectedItemPosition
            val authorPos = spinnerAuthors.selectedItemPosition

            if (genrePos == 0) {
                Toast.makeText(this, "Please select a genre", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (authorPos == 0) {
                Toast.makeText(this, "Please select an author", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val isbnExists = db.isIsbnExists(isbn, bookId)
            if (isbnExists) {
                Toast.makeText(this, "A book with this ISBN already exists!", Toast.LENGTH_LONG).show()
            } else if (title.isEmpty()) {
                Toast.makeText(this, "Title is required", Toast.LENGTH_SHORT).show()
            } else if (!isbn.matches(Regex("^ISBN-\\d{4}-\\d{4}$"))) {
                Toast.makeText(this, "ISBN must be ISBN-XXXX-XXXX", Toast.LENGTH_SHORT).show()
            } else if (year !in 1900..2026) {
                Toast.makeText(this, "Year must be 1900-2026", Toast.LENGTH_SHORT).show()
            } else {
                val selectedAuthorId = authorsFromDb[authorPos - 1].id
                val selectedGenre = spinnerGenre.selectedItem.toString()

                val book = Models.Book(
                    id = bookId,
                    title = title,
                    isbn = isbn,
                    publishedYear = year,
                    genre = selectedGenre,
                    authorId = selectedAuthorId
                )

                if (isEditMode) {
                    db.updateBook(book)
                    Toast.makeText(this, "Book updated", Toast.LENGTH_SHORT).show()
                } else {
                    db.addBook(book)
                    Toast.makeText(this, "Book saved", Toast.LENGTH_SHORT).show()
                }
                finish()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}