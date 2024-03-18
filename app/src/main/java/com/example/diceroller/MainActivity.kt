package com.example.diceroller

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(localClassName, "onCreate")
        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.rollButton)

        button.setOnClickListener {
            rollDice()
        }
    }

    private fun rollDice() {
        val dice = Dice() // this is equivalent to Dice(6)
        val diceRoll1 = dice.roll() // first dice roll
        val diceRoll2 = dice.roll() // second dice roll
        val crash = findViewById<View>(R.id.crash_test)
        val screenH = crash?.height
        updateText(diceRoll1, diceRoll2) //update the text view with result
        updateImg(diceRoll1, diceRoll2) // update the image views of dices
    }

    private fun updateText(diceRoll1: Int, diceRoll2: Int) {
        // get the text view from the layout
        val rollResultText = findViewById<TextView>(R.id.rollResultText)
        // set the text of the text view
        rollResultText.text = "Rolled: $diceRoll1 & $diceRoll2"
        // creating a color (RGB) of the text view
        // Each color component can have a value between 0 and 255
        // Red component - random number between 0 and 255
        // Green component - based on the sum of the dices
        // Blue component - - based on the product of the dices
        val r = (0 .. 255).random() // red component - random number between 0 and 255
        val g = (((diceRoll1 + diceRoll2) / 12.0) * 255).toInt()
        val b = (((diceRoll1 * diceRoll2) / 36.0) * 255).toInt()
        // set the color of the text view
        rollResultText.setTextColor(Color.rgb(r, g, b))
        Log.i(localClassName, "Text color: R:$r, G:$g, B:$b")
    }

    private fun updateImg(diceRoll1: Int, diceRoll2: Int) {
        // get the image views from the layout
        val dice1Img = findViewById<ImageView>(R.id.dice1Img)
        val dice2Img = findViewById<ImageView>(R.id.dice2Img)
        // set the image of the image views
        dice1Img.setImageResource(resolveDrawable(diceRoll1))
        dice2Img.setImageResource(resolveDrawable(diceRoll2))
    }

    private fun resolveDrawable(diceValue: Int): Int {
        // return the drawable resource id based on the dice value
        return when(diceValue){
            1 -> R.drawable.dice_1
            2 -> R.drawable.dice_2
            3 -> R.drawable.dice_3
            4 -> R.drawable.dice_4
            5 -> R.drawable.dice_5
            else -> R.drawable.dice_6
        }
    }

    override fun onStart() {
        super.onStart()
        Log.i(localClassName, "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.i(localClassName, "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.i(localClassName, "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.i(localClassName, "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(localClassName, "onDestroy")
    }
}