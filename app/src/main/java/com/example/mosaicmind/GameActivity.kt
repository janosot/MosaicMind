package com.example.mosaicmind

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import org.json.JSONArray
import kotlin.random.Random

class GameActivity : AppCompatActivity() {
    private var difficulty: String? = null
    private var size: Int = 5
    private var lives: Int = 3
    private var remainingCells: Int = 0
    private var colorMode: Boolean = true
    private var boardID: Int = -1
    private lateinit var currentBoard: Array<BooleanArray>
    private lateinit var finishedBoard: Array<BooleanArray>

    private lateinit var heart1: ImageView
    private lateinit var heart2: ImageView
    private lateinit var heart3: ImageView

    private lateinit var popupWindow: PopupWindow
    private lateinit var popupMessageTextView: TextView
    private lateinit var homeButton: Button
    private lateinit var restartButton: Button

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        //Get difficulty from main menu and initialize size based on the difficulty
        difficulty = intent.getStringExtra("DIFFICULTY")
        size = when (difficulty) {
            "Easy" -> 5
            "Medium" -> 10
            "Difficult" -> 15
            else -> 5
        }

        //Initialize board arrays (one for current board state and one for finished board) and the game
        finishedBoard = Array(size) { BooleanArray(size) }
        currentBoard = Array(size) { BooleanArray(size) }
        initializeGame()

        //Set button listeners
        val backButton: ImageButton = findViewById(R.id.backButton)
        backButton.setOnClickListener { _ -> goBack() }

        val changeModeButton: Button = findViewById(R.id.changeModeButton)
        changeModeButton.setOnClickListener { view -> changeColorMode(view) }

        heart1 = findViewById(R.id.heart1)
        heart2 = findViewById(R.id.heart2)
        heart3 = findViewById(R.id.heart3)

        // Popup window initialization
        val popupView = layoutInflater.inflate(R.layout.popup_window, null)
        popupMessageTextView = popupView.findViewById(R.id.popupMessageTextView)
        homeButton = popupView.findViewById(R.id.homeButton)
        restartButton = popupView.findViewById(R.id.restartButton)

        popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        )

        val backgroundColor = ContextCompat.getColor(this, R.color.colorPrimary)
        popupWindow.setBackgroundDrawable(ColorDrawable(backgroundColor))

        popupWindow.isTouchable = true
        popupWindow.isOutsideTouchable = false

        popupWindow.animationStyle = android.R.style.Animation_Dialog
    }

    private fun initializeGame() {
        val board: LinearLayout = findViewById(R.id.Board)
        board.removeAllViews()

        loadBoard(size)

        //Add and initialize buttons based on game size
        for (rowNumber in 0 until size) {
            val row = LinearLayout(this)
            board.addView(row, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1F))
            for (columnNumber in 0 until size) {
                val button = Button(this)
                button.id = rowNumber * 100 + columnNumber
                button.setOnClickListener { checkCell(button) }
                row.addView(button, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1F))
            }
        }
    }

    // Load and parse JSON with finished boards with captions based on difficulty
    private fun loadBoard(size: Int) {
        val gameResource = when(difficulty) {
            "Easy" -> R.raw.easy
            "Medium" -> R.raw.medium
            "Difficult" -> R.raw.hard
            else -> R.raw.easy
        }

        val boardArray = getBoardFromResource(gameResource)

        for (rowNumber in 0 until size) {
            val row = boardArray.getJSONArray(rowNumber)
            for (columnNumber in 0 until size) {
                val cell = row.getInt(columnNumber) == 1
                if (cell) remainingCells++
                finishedBoard[rowNumber][columnNumber] = cell
            }
        }

        createBoardCaptions()
    }

    private fun getBoardFromResource(resource: Int): JSONArray {
        val jsonText = resources.openRawResource(resource)
            .bufferedReader().use { it.readText() }

        val boardsArray = JSONArray(jsonText)
        if(boardID == -1) {
            boardID = Random.Default.nextInt(0, boardsArray.length())
        }
        val boardObject =
            boardsArray.getJSONObject(boardID)

        return boardObject.getJSONArray("board")
    }

    //Helper function for creating TextViews for captions
    private fun createCaption(caption: String, isRow: Boolean): TextView {
        val captionView = TextView(this)
        captionView.text = caption

        captionView.textSize = when(difficulty) {
            "Easy" -> 22F
            "Medium" -> 14F
            "Difficult" -> 12F
            else -> 22F
        }
        captionView.setTypeface(null, Typeface.BOLD)
        captionView.gravity = Gravity.CENTER
        if(!isRow) {
            captionView.gravity = Gravity.CENTER or Gravity.BOTTOM
        }
        captionView.setTextColor(Color.LTGRAY)
        return captionView
    }

    //Count the game captions based on selected board and put then to their layouts
    private fun createBoardCaptions() {
        createRowCaptions()
        createColumnCaptions()
    }

    private fun createRowCaptions() {
        var captions = mutableListOf<String>()
        val rowCaptions: LinearLayout = findViewById(R.id.rowCaptions)

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
                    text += " "
                }
            }

            rowCaptions.addView(createCaption(text, true),
                LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT,1F))
            captions = mutableListOf()
        }
    }

    private fun createColumnCaptions() {
        var captions = mutableListOf<String>()
        val columnCaptions: LinearLayout = findViewById(R.id.columnsCaptions)

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
                    text += "\n"
                }
            }
            columnCaptions.addView(createCaption(text, false),
                LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT,1F))

            captions = mutableListOf()
        }
    }

    //Check if the clicked button is correct and update game state
    private fun checkCell(button: View) {
        button.isClickable = false
        val rowNumber: Int = button.id / 100
        val columnNumber: Int = button.id % 100

        if(finishedBoard[rowNumber][columnNumber]) {
            currentBoard[rowNumber][columnNumber] = true
        }

        if (colorMode) {
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
                button.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.holo_red_dark)
            } else {

                button.backgroundTintList = ContextCompat.getColorStateList(this, R.color.darkGrey)

                remainingCells--
                lives--
                updateHearts()
            }
        }

        checkGameStatus()
        fillCompletedRows()
        fillCompletedColumns()
    }

    private fun fillCompletedRows() {
        for (rowNumber in 0 until size) {
            var completedRow = true
            for (columnNumber in 0 until size) {
                if(currentBoard[rowNumber][columnNumber] != finishedBoard[rowNumber][columnNumber]) {
                    completedRow = false
                }
            }

            if(completedRow) {
                for (columnNumber in 0 until size) {
                    if(!finishedBoard[rowNumber][columnNumber]) {
                        val button: Button = findViewById(rowNumber*100 + columnNumber)
                        button.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.holo_red_dark)
                    }
                }
            }
        }
    }
    private fun fillCompletedColumns() {
        for (columnNumber in 0 until size) {
            var completedRow = true
            for (rowNumber in 0 until size) {
                if(currentBoard[rowNumber][columnNumber] != finishedBoard[rowNumber][columnNumber]) {
                    completedRow = false
                }
            }

            if(completedRow) {
                for (rowNumber in 0 until size) {
                    if(!finishedBoard[rowNumber][columnNumber]) {
                        val button: Button = findViewById(rowNumber*100 + columnNumber)
                        button.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.holo_red_dark)
                    }
                }
            }
        }
    }
    private fun changeColorMode(view: View) {
        colorMode = !colorMode
        val button: Button = view as Button
        button.text = if (colorMode) "Color" else "Cross"
    }

    fun goHome() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun showPopup(message: String, showRestartButton: Boolean = true) {
        popupMessageTextView.text = message
        homeButton.setOnClickListener {
            goHome()
            dismissPopup()
        }

        if (showRestartButton) {
            restartButton.visibility = View.VISIBLE
            restartButton.setOnClickListener {
                restartGame()
                dismissPopup()
            }
        } else {
            restartButton.visibility = View.GONE
        }

        popupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.CENTER, 0, 0)
    }

    private fun dismissPopup() {
        popupWindow.dismiss()
    }

    private fun checkGameStatus() {
        if (lives <= 0) {
            showPopup("YOU LOSE", true)
            disableGrid()
        }
        if (remainingCells == 0) {
            showPopup("YOU WON", false)
            disableGrid()
        }
    }


    private fun disableGrid() {
        val board: LinearLayout = findViewById(R.id.Board)
        for (i in 0 until board.childCount) {
            val row = board.getChildAt(i) as LinearLayout
            for (j in 0 until row.childCount) {
                val button = row.getChildAt(j) as Button
                button.isClickable = false
            }
        }
    }

    fun restartGame() {
        lives = 3
        remainingCells = 0
        colorMode = true

        // Renew hearts
        heart1.setImageResource(R.drawable.heart_full)
        heart2.setImageResource(R.drawable.heart_full)
        heart3.setImageResource(R.drawable.heart_full)

        // Reset the game board to its initial state
        initializeGame()
        dismissPopup()
    }

    private fun updateHearts() {
        when (lives) {
            2 -> heart3.setImageResource(R.drawable.heart_missing)
            1 -> heart2.setImageResource(R.drawable.heart_missing)
            0 -> heart1.setImageResource(R.drawable.heart_missing)
        }
    }

    fun goBack() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
