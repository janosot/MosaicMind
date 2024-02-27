package com.example.mosaicmind

import android.content.Intent
import android.graphics.Color
import android.icu.lang.UCharacter.VerticalOrientation
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class GameActivity : AppCompatActivity() {

    private var difficulty: String? = null
    private var size: Int = 5
    private var lives: Int = 3
    private lateinit var board: Array<BooleanArray>
    private var remainingCells: Int = 0
    private lateinit var heart1: ImageView
    private lateinit var heart2: ImageView
    private lateinit var heart3: ImageView
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
        board = Array(size) { BooleanArray(size) }
        initializeGame()

        val backButton: ImageButton = findViewById(R.id.backButton)
        backButton.setOnClickListener { view -> goBack(view) }

        heart1 = findViewById(R.id.heart1)
        heart2 = findViewById(R.id.heart2)
        heart3 = findViewById(R.id.heart3)
    }

    private fun initializeGame() {

        loadBoard(size)

        val board: LinearLayout = findViewById(R.id.Board)
        for (rowNumber in 0..<size) {
            val row = LinearLayout(this)
            board.addView(row, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT,1F))
            for (columnNumber in 0..<size) {

                val button = Button(this)
                //button.text = "$rowNumber,$columnNumber"
                /*
                button.width = row.width / size
                button.height = row.height / size*/
                button.id = rowNumber * 100 + columnNumber
                button.setOnClickListener { checkCell(button) }
                row.addView(button, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT,1F))
            }

        }

    }

    private fun loadBoard(size: Int) {
        //TODO Load board from a file:
        // Initialize board array
        // Initialize NOF good cells and their caption to to View
        for(i in 0..<size) {
            for(j in 0..<size) {
                if((j % 2 == 0)) remainingCells++
                board[i][j] = (j % 2 == 0)
            }
        }
    }

    private fun checkCell(button: View) {
        button.isClickable = false
        val rowNumber: Int = button.id / 100
        val columnNumber: Int = button.id % 100
        if (board[rowNumber][columnNumber]) {
            remainingCells--
            button.setBackgroundColor(Color.DKGRAY)
        } else {
            button.setBackgroundColor(Color.RED)
            lives--
            updateHearts()
        }

        checkGameStatus()
    }


    fun checkGameStatus() {
        if(lives <= 0) {
            //TODO Show "You lose" message
            finish()
        }
        if(remainingCells == 0) {
            //TODO Show "You win" message
            finish()
        }
    }

    private fun updateHearts() {
        when (lives) {
            2 -> heart3.setImageResource(R.drawable.heart_missing)
            1 -> heart2.setImageResource(R.drawable.heart_missing)
            0 -> heart1.setImageResource(R.drawable.heart_missing)
        }
    }

    fun goBack(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}
