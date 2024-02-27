package com.example.mosaicmind

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import org.json.JSONArray
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlin.random.Random

class GameActivity : AppCompatActivity() {

    private var difficulty: String? = null
    private var size: Int = 5
    private var lives: Int = 3
    private var remainingCells: Int = 0
    private var colorMode: Boolean = true
    private lateinit var currentBoard: Array<BooleanArray>
    private lateinit var finishedBoard: Array<BooleanArray>
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
        finishedBoard = Array(size) { BooleanArray(size) }
        currentBoard = Array(size) { BooleanArray(size) }
        initializeGame()

        val backButton: ImageButton = findViewById(R.id.backButton)
        backButton.setOnClickListener { view -> goBack(view) }

        val changeModeButton: Button = findViewById(R.id.changeModeButton)
        changeModeButton.setOnClickListener { view -> changeColorMode(view) }

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
        val gameResource = when(difficulty) {
            "Easy" -> R.raw.easy
            "Medium" -> R.raw.easy
            "Difficult" -> R.raw.easy
            else -> R.raw.easy
        }

        val boardArray = getBoardFromResource(gameResource)

        for(rowNumber in 0..<size) {
            val row = boardArray.getJSONArray(rowNumber)
            for (columnNumber in 0..<size) {
                val cell = row.getInt(columnNumber) == 1
                if (cell) remainingCells++
                finishedBoard[rowNumber][columnNumber] = cell
            }
        }

        setBoardCaptions()
    }

    private fun getBoardFromResource(resource: Int) : JSONArray {
        val jsonText = resources.openRawResource(resource)
            .bufferedReader().use { it.readText() }

        val boardsArray = JSONArray(jsonText)
        val boardObject = boardsArray.getJSONObject(Random.Default.nextInt(0, boardsArray.length()))

        return boardObject.getJSONArray("board")
    }

    private fun createCaption(caption:String) : TextView {
        val captionView = TextView(this)
        captionView.text = caption
        captionView.isSingleLine = true
        captionView.textSize = 22F
        captionView.setTypeface(null, Typeface.BOLD)
        captionView.gravity = Gravity.CENTER
        captionView.setTextColor(Color.LTGRAY)
        return captionView
    }
    private fun setBoardCaptions() {
        var captions = mutableListOf<String>()
        val rowCaptions: LinearLayout = findViewById(R.id.rowCaptions)
        val columnCaptions: LinearLayout = findViewById(R.id.columnsCaptions)

        //Count rows
        for(rowNumber in 0..<size) {
            var blockSize = 0
            for (columnNumber in 0..<size) {
                if(!finishedBoard[rowNumber][columnNumber]) {
                    if(blockSize != 0) {
                        captions.add(blockSize.toString())
                    }
                    blockSize = 0
                }
                else {
                    blockSize++
                }
            }

            if(blockSize != 0) {
                captions.add(blockSize.toString())
            }
            var text = ""
            for(i in 0..<captions.size) {
                text += captions[i]
                if(i != captions.size - 1) {
                    text += ","
                }
            }

            rowCaptions.addView(createCaption(text),
                LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT,1F))
            captions = mutableListOf()
        }

        //Count columns
        for(columnNumber in 0..<size) {
            var blockSize = 0
            for (rowNumber in 0..<size) {
                if(!finishedBoard[rowNumber][columnNumber]) {
                    if(blockSize != 0) {
                        captions.add(blockSize.toString())
                    }
                    blockSize = 0
                }
                else {
                    blockSize++
                }
            }

            if(blockSize != 0) {
                captions.add(blockSize.toString())
            }


            var text = ""
            for(i in 0..<captions.size) {
                text += captions[i]
                if(i != captions.size - 1) {
                    text += ","
                }
            }
            columnCaptions.addView(createCaption(text),
                LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT,1F))

            captions = mutableListOf()
        }
    }

    private fun checkCell(button: View) {
        button.isClickable = false
        val rowNumber: Int = button.id / 100
        val columnNumber: Int = button.id % 100
        if(colorMode) {
            if (finishedBoard[rowNumber][columnNumber]) {
                remainingCells--
                button.backgroundTintList = ContextCompat.getColorStateList(this, R.color.darkGrey)
            } else {
                button.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.holo_red_dark)
                lives--
                updateHearts()
            }
        } else {
            if (!finishedBoard[rowNumber][columnNumber]) {
                button.setBackgroundColor(Color.RED)
            } else {

                button.backgroundTintList = ContextCompat.getColorStateList(this, R.color.darkGrey)

                remainingCells--
                lives--
                updateHearts()
            }
        }


        checkGameStatus()

        fillCompletedRows()
        //fillCompletedColumns()

    }

    private fun fillCompletedRows() {

    }

    private fun changeColorMode(view: View) {
        colorMode = !colorMode
        val button : Button = view as Button
        if(colorMode) {
            button.text = "Color"
        } else {
            button.text = "Cross"
        }
    }

    private fun checkGameStatus() {
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
