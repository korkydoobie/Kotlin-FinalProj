package com.example.quadtech

import android.app.Activity
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.widget.Toast
import com.example.quadtech.databinding.ActivityAddDiaryBinding

class AddDiaryActivity : Activity() {
    private lateinit var binding: ActivityAddDiaryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddDiaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = "testuser"

        val dbHelper = object : SQLiteOpenHelper(this, "diary.db", null, 1) {
            override fun onCreate(db: SQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS diaries (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "title TEXT, content TEXT, username TEXT)"
                )
            }

            override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}
        }

        val db = dbHelper.writableDatabase

        binding.btnSave.setOnClickListener {
            val title = binding.etTitle.text.toString()
            val content = binding.etContent.text.toString()

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val values = ContentValues().apply {
                put("title", title)
                put("content", content)
                put("username", username)
            }

            db.insert("diaries", null, values)
            Toast.makeText(this, "Diary saved", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
