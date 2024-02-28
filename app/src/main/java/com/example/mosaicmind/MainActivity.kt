package com.example.mosaicmind

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var difficultyRadioGroup: RadioGroup

    // Override function to handle the creation of the activity, with the savedInstanceState as a paramete
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        difficultyRadioGroup = findViewById(R.id.difficultyRadioGroup)
    }

    // Function to start the game with the selected difficulty level
    // @param view The view that was clicked
    fun startGame(view: View) {
        val selectedDifficulty: String = when (difficultyRadioGroup.checkedRadioButtonId) {
            R.id.easyRadioButton -> "Easy"
            R.id.mediumRadioButton -> "Medium"
            R.id.difficultRadioButton -> "Difficult"
            else -> "Default" // no button is selected
        }

        val intent = Intent(this, GameActivity::class.java)
        intent.putExtra("DIFFICULTY", selectedDifficulty)
        startActivity(intent)
    }
}
