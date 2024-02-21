package com.example.mosaicmind

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GameActivity : AppCompatActivity() {

    private var difficulty: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        difficulty = intent.getStringExtra("DIFFICULTY")
        val difficultyTextView: TextView = findViewById(R.id.difficultyTextView)
        difficultyTextView.text = "Selected Difficulty: $difficulty"

        initializeGame()

        val backButton: Button = findViewById(R.id.backButton)
        backButton.setOnClickListener { goBack() }
    }

    private fun initializeGame() {
        // TODO: Implement game initialization based on the difficulty level
    }

    fun goBack() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
