package com.example.quadtech

import android.app.Activity
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.quadtech.R
import com.example.quadtech.databinding.ActivityAddDiaryBinding

class AddDiaryActivity : Activity() {
    lateinit var etTitle: EditText
    lateinit var etContent: EditText
    lateinit var btnSave: Button
    lateinit var username: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_diary)

        username = intent.getStringExtra("username") ?: "defaultUser"

        var dbHelper = object : SQLiteOpenHelper(this, "diary.db", null, 1) {
            override fun onCreate(db: SQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS diaries (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "title TEXT, content TEXT, username TEXT)"
                )
            }

            override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}
        }

        var db = dbHelper.writableDatabase

        etTitle = findViewById(R.id.etTitle)
        etContent = findViewById(R.id.etContent)
        btnSave = findViewById(R.id.btnSave)

        btnSave.setOnClickListener {
            val title = etTitle.text.toString()
            val content = etContent.text.toString()

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