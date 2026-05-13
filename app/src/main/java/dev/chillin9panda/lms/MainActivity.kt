package dev.chillin9panda.lms

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val fab = findViewById<FloatingActionButton>(R.id.fab_add)

        if (savedInstanceState == null) {
            loadFragment(BookFragment())
        }

        bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_books -> {
                    loadFragment(BookFragment())
                    true
                }
                R.id.nav_authors -> {
                    loadFragment(AuthorFragment())
                    true
                }
                else -> false
            }
        }

        fab.setOnClickListener {
            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

            when (currentFragment) {
                is BookFragment -> {
                    val intent = Intent(this, AddBookActivity::class.java)
                    startActivity(intent)
                }
                is AuthorFragment -> {
                    val intent = Intent(this, AddAuthorActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}