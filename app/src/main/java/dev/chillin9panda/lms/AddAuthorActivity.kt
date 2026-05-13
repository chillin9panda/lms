package dev.chillin9panda.lms

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddAuthorActivity : AppCompatActivity() {

    private var isEditMode = false
    private var authorId: Int = -1

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_author)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val titleView = findViewById<TextView>(R.id.tvAddAuthorTitle)
        val btnSave = findViewById<Button>(R.id.btnSaveAuthor)
        val nameInput = findViewById<EditText>(R.id.etAuthorName)
        val emailInput = findViewById<EditText>(R.id.etAuthorEmail)
        val db = DatabaseHelper(this)

        authorId = intent.getIntExtra("AUTHOR_ID", -1)
        if (authorId != -1) {
            isEditMode = true
            titleView.text = "Edit Author"
            btnSave.text = "Update Author"

            nameInput.setText(intent.getStringExtra("AUTHOR_NAME"))
            emailInput.setText(intent.getStringExtra("AUTHOR_EMAIL"))
        }

        btnSave.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()

            if (name.isEmpty() || name.length > 50 || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Please check your inputs", Toast.LENGTH_SHORT).show()
            } else {
                val author = Models.Author(id = authorId, fullName = name, email = email)

                if (isEditMode) {
                    db.updateAuthor(author)
                    Toast.makeText(this, "Author Updated", Toast.LENGTH_SHORT).show()
                } else {
                    db.addAuthor(author)
                    Toast.makeText(this, "Author Added", Toast.LENGTH_SHORT).show()
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