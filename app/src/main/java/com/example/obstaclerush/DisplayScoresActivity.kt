package com.example.obstaclerush

import android.content.ContentValues
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView

class DisplayScoresActivity : AppCompatActivity() {

    var data: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_scores)

        val score: Int = intent.getIntExtra("score", 0)
        val username: String? = intent.getStringExtra("username").toString()
        val insertScores: Boolean = intent.getBooleanExtra("insertScores", false)
        val outputScores: TextView = findViewById(R.id.outputScores)
        val menuBtn: Button = findViewById(R.id.gobackMenu)

        if (insertScores){
            var db = MyDBHelper(applicationContext).writableDatabase
            var q = db.rawQuery("SELECT * FROM SCORES", null)

            val contentValues = ContentValues()
            contentValues.put("username", username)
            contentValues.put("score", score)

            q.moveToFirst()
            var exists: Boolean = false

            while (!q.isAfterLast){
                if (q.getString(1).toString() == username){
                    if (score > q.getInt(2)){
                        db.update("SCORES", contentValues, "QID=${q.getInt(0)}", null)
                    }

                    exists = true
                }

                q.moveToNext()
            }

            if (!exists){db.insert("SCORES", null, contentValues)}

            db.close()
        }

        var db2 = MyDBHelper(applicationContext).readableDatabase
        var q2 = db2.rawQuery("SELECT * FROM SCORES", null)
        q2.moveToFirst()

        while(!q2.isAfterLast){
            data += q2.getString(1).toString() + "..."
            data += q2.getInt(2).toString() + " points" + "\n"
            q2.moveToNext()
        }
        db2.close()

        outputScores.text = data

        menuBtn.setOnClickListener {
            val viewMain = Intent(this, MainActivity::class.java)
            startActivity(viewMain)
        }
    }
}