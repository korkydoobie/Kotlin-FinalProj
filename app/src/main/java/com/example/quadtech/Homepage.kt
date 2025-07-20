package com.example.quadtech

import android.app.Activity
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.widget.ListView
import android.widget.ArrayAdapter
import android.widget.Button

class Homepage : Activity() {

    lateinit var dbHelper: SQLiteOpenHelper
    lateinit var db: SQLiteDatabase
    lateinit var listView: ListView
    lateinit var addButton: Button

    // Hardcoded username
    var username: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)

        username = intent.getStringExtra("username") ?: "defaultUser"

        dbHelper = object : SQLiteOpenHelper(this, "diary.db", null, 1) {
            override fun onCreate(db: SQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS diaries (id INTEGER PRIMARY KEY, title TEXT, content TEXT, username TEXT)")
            }

            override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}
        }

        db = dbHelper.readableDatabase

        listView = findViewById(R.id.listView)
        addButton = findViewById(R.id.buttonAdd)

        loadDiaryList()

        addButton.setOnClickListener {
            val intent = Intent(this, AddDiaryActivity::class.java)
            intent.putExtra("username", username)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadDiaryList()
    }

    private fun loadDiaryList() {
        val cursor = db.rawQuery("SELECT title, content FROM diaries WHERE username = ?", arrayOf(username))
        val items = mutableListOf<String>()

        while (cursor.moveToNext()) {
            val title = cursor.getString(0)
            val content = cursor.getString(1)
            items.add("📔 $title\n$content")
        }
        cursor.close()

        listView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
    }
}
