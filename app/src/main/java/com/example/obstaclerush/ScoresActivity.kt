package com.example.obstaclerush

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class ScoresActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scores)

        val yourScore: Int = intent.getIntExtra("score", 0)

        val score: TextView = findViewById(R.id.scoreView)
        val username: TextView = findViewById(R.id.enterUsername)
        val submitBtn: Button = findViewById(R.id.submitBtn)
        val menuBtn: Button = findViewById(R.id.menuBtn)

        score.text = yourScore.toString()

        menuBtn.setOnClickListener(){
            val viewMain = Intent(this, MainActivity::class.java)
            startActivity(viewMain)
        }

        submitBtn.setOnClickListener(){
            if (username.text != null && username.text.isNotEmpty()){
                val displayScores = Intent(this, DisplayScoresActivity::class.java)
                displayScores.putExtra("username", username.text.toString())
                displayScores.putExtra("score", yourScore)
                displayScores.putExtra("insertScores", true)
                startActivity(displayScores)
            }
        }

    }
}