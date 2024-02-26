package com.example.mosaicmind

import android.content.Intent
import android.icu.lang.UCharacter.VerticalOrientation
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class GameActivity : AppCompatActivity() {

    private var difficulty: String? = null
    private var size: Int = 5
    private var lives: Int = 3
    private var board: Array<Array<Boolean>> = Array(5){ Array(5) { false } }
    private var remainingCells: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        difficulty = intent.getStringExtra("DIFFICULTY")
        size = when(difficulty) {
            "Easy" -> 5
            "Medium" -> 10
            "Difficult" -> 15
            else -> 5
        }
        /*
        val difficultyTextView: TextView = findViewById(R.id.difficultyTextView)
        difficultyTextView.text = "Selected Difficulty: $difficulty"
        */
        initializeGame()

        val backButton: Button = findViewById(R.id.backButton)
        backButton.setOnClickListener { goBack() }
    }

    private fun initializeGame() {
        // TODO: Implement game initialization based on the difficulty level

        loadBoard(size)

        remainingCells = size * size

        val board: LinearLayout = findViewById(R.id.Board)
        for (rowNumber in 0..size) {
            val row = LinearLayout(this)

            for (columnNumber in 0..size) {

                val button = Button(this)
                //button.text = "$rowNumber,$columnNumber"
                button.id = rowNumber * 100 + columnNumber
                button.setOnClickListener { checkCell(button) }
                row.addView(button)
            }
            board.addView(row)
        }

    }

    private fun loadBoard(size: Int) {
        for(i in 0..size) {
            for(j in 0..size) {
                board[i][i] = (j % 2 == 0)
            }
        }
    }

    private fun checkCell(button: View) {

        remainingCells--
        button.isClickable = false
        val rowNumber: Int = button.id / 100
        val columnNumber: Int = button.id % 100
        if(board[rowNumber][columnNumber]) {
            button.setBackgroundColor(5)
        }
        else
        {
            button.setBackgroundColor(15)
            lives--
        }

    }

    fun goBack() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
