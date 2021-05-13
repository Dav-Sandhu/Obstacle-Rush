package com.example.obstaclerush

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import kotlin.system.exitProcess

class MainActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val playBtn: Button = findViewById(R.id.play_button)
        val scoresBtn: Button = findViewById(R.id.scores_button)
        val quitBtn: Button = findViewById(R.id.quit_button)

        val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val editor: SharedPreferences.Editor =  sharedPreferences.edit()
        editor.clear()
        editor.apply()

        playBtn.setOnClickListener{
            val startGame = Intent(this, GameActivity::class.java)
            startActivity(startGame)
        }

        scoresBtn.setOnClickListener{
            val viewScores = Intent(this, DisplayScoresActivity::class.java)
            startActivity(viewScores)
        }

        quitBtn.setOnClickListener{
            this.finishAffinity()
            exitProcess(0)
        }
    }
}