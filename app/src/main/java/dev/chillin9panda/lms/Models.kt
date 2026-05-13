package dev.chillin9panda.lms

class Models {
    data class Author(
        val id: Int = 0,
        val fullName: String,
        val email: String
    )

    data class Book(
        val id: Int = 0,
        val title: String,
        val isbn: String,
        val publishedYear: Int,
        val genre: String,
        val authorId: Int,
        val authorName: String? = null
    )
}