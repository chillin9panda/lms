package dev.chillin9panda.lms

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "LibrarySystem.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_AUTHORS = "authors"
        const val COL_AUTHOR_ID = "id"
        const val COL_AUTHOR_NAME = "fullName"
        const val COL_AUTHOR_EMAIL = "email"

        const val TABLE_BOOKS = "books"
        const val COL_BOOK_ID = "id"
        const val COL_BOOK_TITLE = "title"
        const val COL_BOOK_ISBN = "isbn"
        const val COL_BOOK_YEAR = "publishedYear"
        const val COL_BOOK_GENRE = "genre"
        const val COL_BOOK_AUTHOR_ID = "authorId"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createAuthorTable = ("CREATE TABLE $TABLE_AUTHORS (" +
                "$COL_AUTHOR_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COL_AUTHOR_NAME TEXT NOT NULL," +
                "$COL_AUTHOR_EMAIL TEXT NOT NULL)")

        val createBookTable = ("CREATE TABLE $TABLE_BOOKS (" +
                "$COL_BOOK_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COL_BOOK_TITLE TEXT NOT NULL," +
                "$COL_BOOK_ISBN TEXT UNIQUE," +
                "$COL_BOOK_YEAR INTEGER," +
                "$COL_BOOK_GENRE TEXT," +
                "$COL_BOOK_AUTHOR_ID INTEGER," +
                "FOREIGN KEY($COL_BOOK_AUTHOR_ID) REFERENCES $TABLE_AUTHORS($COL_AUTHOR_ID) " +
                "ON DELETE CASCADE)")

        db.execSQL(createAuthorTable)
        db.execSQL(createBookTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_BOOKS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_AUTHORS")
        onCreate(db)
    }

    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    fun addAuthor(author: Models.Author): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COL_AUTHOR_NAME, author.fullName)
            put(COL_AUTHOR_EMAIL, author.email)
        }
        return db.insert(TABLE_AUTHORS, null, values)
    }

    fun getAllAuthors(): List<Models.Author> {
        val list = mutableListOf<Models.Author>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_AUTHORS", null)
        if (cursor.moveToFirst()) {
            do {
                list.add(
                    Models.Author(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2)
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun updateAuthor(author: Models.Author): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COL_AUTHOR_NAME, author.fullName)
            put(COL_AUTHOR_EMAIL, author.email)
        }
        return db.update(TABLE_AUTHORS, values, "$COL_AUTHOR_ID=?", arrayOf(author.id.toString()))
    }

    fun deleteAuthor(id: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_AUTHORS, "$COL_AUTHOR_ID=?", arrayOf(id.toString()))
    }


    fun addBook(book: Models.Book): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COL_BOOK_TITLE, book.title)
            put(COL_BOOK_ISBN, book.isbn)
            put(COL_BOOK_YEAR, book.publishedYear)
            put(COL_BOOK_GENRE, book.genre)
            put(COL_BOOK_AUTHOR_ID, book.authorId)
        }
        return db.insert(TABLE_BOOKS, null, values)
    }

    fun getAllBooks(): List<Models.Book> {
        val list = mutableListOf<Models.Book>()
        val db = this.readableDatabase
        val query = "SELECT b.*, a.$COL_AUTHOR_NAME FROM $TABLE_BOOKS b " +
                "INNER JOIN $TABLE_AUTHORS a ON b.$COL_BOOK_AUTHOR_ID = a.$COL_AUTHOR_ID"

        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                list.add(
                    Models.Book(
                        id = cursor.getInt(0),
                        title = cursor.getString(1),
                        isbn = cursor.getString(2),
                        publishedYear = cursor.getInt(3),
                        genre = cursor.getString(4),
                        authorId = cursor.getInt(5),
                        authorName = cursor.getString(6)
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun updateBook(book: Models.Book): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COL_BOOK_TITLE, book.title)
            put(COL_BOOK_ISBN, book.isbn)
            put(COL_BOOK_YEAR, book.publishedYear)
            put(COL_BOOK_GENRE, book.genre)
            put(COL_BOOK_AUTHOR_ID, book.authorId)
        }
        return db.update(TABLE_BOOKS, values, "$COL_BOOK_ID = ?", arrayOf(book.id.toString()))
    }

    fun deleteBook(id: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_BOOKS, "$COL_BOOK_ID=?", arrayOf(id.toString()))
    }

    fun isIsbnExists(isbn: String, excludeId: Int = -1): Boolean {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_BOOKS WHERE $COL_BOOK_ISBN = ? AND $COL_BOOK_ID != ?"
        val cursor = db.rawQuery(query, arrayOf(isbn, excludeId.toString()))
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }
}