package com.example.quadtech

import android.app.AlertDialog
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quadtech.databinding.ActivityHomepageBinding

class Homepage : AppCompatActivity() {

    lateinit var dbHelper: SQLiteOpenHelper
    lateinit var db: SQLiteDatabase
    lateinit var listView: ListView
    lateinit var addButton: Button
    private val diaryIds = mutableListOf<Long>() // Store diary IDs for deletion
    private lateinit var binding: ActivityHomepageBinding
    lateinit var username: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomepageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize views from binding
        listView = binding.listView.apply {
            choiceMode = ListView.CHOICE_MODE_SINGLE
            setSelector(R.drawable.list_item_selector) }
        addButton = binding.buttonAdd
        username = intent.getStringExtra("username") ?: "defaultUser"
        Log.d("HOMEPAGE", "Entered Homepage. Username: $username")

        dbHelper = object : SQLiteOpenHelper(this, "diary.db", null, 1) {
            override fun onCreate(db: SQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS diaries (id INTEGER PRIMARY KEY, title TEXT, content TEXT, username TEXT)")
            }

            override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}
        }

        db = dbHelper.writableDatabase // Changed to writableDatabase
        listView.choiceMode = ListView.CHOICE_MODE_SINGLE // Enable single selection

        loadDiaryList()

        binding.buttonAdd.setOnClickListener {
            val intent = Intent(this, AddDiaryActivity::class.java)
            intent.putExtra("username", username)
            startActivity(intent)
        }

        // Add item click listener to handle selection
        listView.setOnItemClickListener { _, _, position, _ ->
            // This will highlight the selected item
            listView.setItemChecked(position, true)
        }

        binding.buttonDelete.setOnClickListener {
            val selectedPosition = listView.checkedItemPosition
            if (selectedPosition != ListView.INVALID_POSITION && selectedPosition < diaryIds.size) {
                // Show confirmation dialog
                AlertDialog.Builder(this)
                    .setTitle("Delete Entry")
                    .setMessage("Are you sure you want to delete this entry?")
                    .setPositiveButton("Delete") { _, _ ->
                        // Get the ID of the selected item
                        val idToDelete = diaryIds[selectedPosition]

                        // Delete from database
                        val rowsDeleted = db.delete("diaries", "id=?", arrayOf(idToDelete.toString()))

                        if (rowsDeleted > 0) {
                            loadDiaryList() // Refresh the list
                            Toast.makeText(this, "Entry deleted", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Failed to delete entry", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            } else {
                Toast.makeText(this, "Please select an entry first", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonLogout.setOnClickListener {
            val intent = Intent(this, Launcher::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        loadDiaryList()
    }

    private fun loadDiaryList() {
        diaryIds.clear() // Clear previous IDs
        val cursor = db.rawQuery("SELECT id, title, content FROM diaries WHERE username = ? ORDER BY id DESC",
            arrayOf(username))

        val items = mutableListOf<String>()
        while (cursor.moveToNext()) {
            diaryIds.add(cursor.getLong(0))
            val title = cursor.getString(1)
            val content = cursor.getString(2)
            items.add("📔 $title\n$content")
        }
        cursor.close()

        listView.adapter = ArrayAdapter(this,
            android.R.layout.simple_list_item_activated_1,
            items)
        listView.clearChoices()
    }

    override fun onDestroy() {
        db.close()
        dbHelper.close()
        super.onDestroy()
    }
}