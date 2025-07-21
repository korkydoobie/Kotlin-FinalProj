package com.example.quadtech

import android.app.AlertDialog
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
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
    private val diaryIds = mutableListOf<Long>()
    private lateinit var binding: ActivityHomepageBinding
    lateinit var username: String
    private var selectedPosition = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomepageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listView = binding.listView.apply {
            choiceMode = ListView.CHOICE_MODE_SINGLE
            setSelector(R.drawable.list_item_selector)
        }
        addButton = binding.buttonAdd
        username = intent.getStringExtra("username") ?: "defaultUser"
        Log.d("HOMEPAGE", "Entered Homepage. Username: $username")

        dbHelper = object : SQLiteOpenHelper(this, "diary.db", null, 1) {
            override fun onCreate(db: SQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS diaries (id INTEGER PRIMARY KEY, title TEXT, content TEXT, username TEXT)")
            }
            override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}
        }

        db = dbHelper.writableDatabase
        listView.choiceMode = ListView.CHOICE_MODE_SINGLE

        loadDiaryList()

        binding.listView.setOnItemClickListener { _, _, position, _ ->
            viewFullContent(position)
            binding.listView.clearChoices()
        }

        binding.listView.setOnItemLongClickListener { _, view, position, _ ->
            selectedPosition = position
            view.isPressed = true
            view.postDelayed({ view.isPressed = false }, 200)
            true
        }

        binding.buttonAdd.setOnClickListener {
            val intent = Intent(this, AddDiaryActivity::class.java)
            intent.putExtra("username", username)
            startActivity(intent)
        }

        binding.buttonDelete.setOnClickListener {
            if (selectedPosition != -1) {
                showDeleteConfirmation(selectedPosition)
            } else {
                Toast.makeText(this, "Long-press an entry to select it", Toast.LENGTH_SHORT).show()
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
        diaryIds.clear()
        val cursor = db.rawQuery("SELECT id, title FROM diaries WHERE username = ?", arrayOf(username))
        val items = mutableListOf<String>()

        while (cursor.moveToNext()) {
            diaryIds.add(cursor.getLong(0))
            items.add(cursor.getString(1))
        }
        cursor.close()

        binding.listView.adapter = ArrayAdapter(this,
            android.R.layout.simple_list_item_1,
            items
        )
        binding.listView.clearChoices()
    }

    private fun viewFullContent(position: Int) {
        if (position < diaryIds.size) {
            val cursor = db.rawQuery("SELECT title, content FROM diaries WHERE id = ?",
                arrayOf(diaryIds[position].toString()))

            if (cursor.moveToFirst()) {
                AlertDialog.Builder(this)
                    .setTitle(cursor.getString(0))
                    .setMessage(cursor.getString(1))
                    .setPositiveButton("OK", null)
                    .show()
            }
            cursor.close()
        }
    }

    private fun showDeleteConfirmation(position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Delete Entry")
            .setMessage("Are you sure you want to delete this entry?")
            .setPositiveButton("Delete") { _, _ ->
                deleteEntry(position)
            }
            .setNegativeButton("Cancel") { _, _ ->
                selectedPosition = -1
                binding.listView.clearChoices()
            }
            .show()
    }

    private fun deleteEntry(position: Int) {
        if (position < diaryIds.size) {
            db.delete("diaries", "id=?", arrayOf(diaryIds[position].toString()))
            loadDiaryList()
            selectedPosition = -1
            Toast.makeText(this, "Entry deleted", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        db.close()
        dbHelper.close()
        super.onDestroy()
    }
}