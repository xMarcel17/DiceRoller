package com.example.diceroller

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.diceroller.databinding.ActivityMainBinding
import com.example.diceroller.databinding.ActivityMainExtendedBinding
import com.google.android.material.snackbar.Snackbar

class MainActivityExtended : AppCompatActivity() {

    private lateinit var binding: ActivityMainExtendedBinding // Connection to the layout

    private var numDice: Int = 5 // number of dice used in the game
    private var isHoldEnabled: Boolean = true // flag: can user hold dice?

    // Array of dice images ids for easy manipulation via loops
    private val diceImgIdsArray =
        arrayOf(R.id.Dice1Img, R.id.Dice2Img, R.id.Dice3Img, R.id.Dice4Img, R.id.Dice5Img)
    // Array to keep track whether given dice is held or not
    private val diceStatesArray = arrayOf(false, false, false, false, false)
    // Array of values rolled.
    private val diceValuesArray = arrayOf(1, 1, 1, 1, 1)
    private var currentPlayer = 0 // Current player id. Only two players
    private val playerScores = arrayOf(0, 0) // scores of the players
    private var rollCount = 0 // variable holding the number of rolls for each player.
    // The turn of the player ends when rollCount == numDice

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(localClassName, "onCreate")
        // Inflate the layout and send it to screen.
        // With view binding there is no need to call findViewById
        binding = ActivityMainExtendedBinding.inflate(layoutInflater)

        setContentView(binding.root)
        applySettings()

        binding.settingsButton.setOnClickListener{
            startSettingsActivity();
        }

        // Set the click listener of the roll button
        binding.RollButton.setOnClickListener {
            // Check the state of the button by analysing its text.
            if (binding.RollButton.text != getString(R.string.button_enabled)) {
                // The text is not equal to "Roll".
                // Set the text to "Roll"
                binding.RollButton.text = getString(R.string.button_enabled)
                if (currentPlayer == 1 && rollCount == 0) {
                    // if this is the start for the second player, reset the turn
                    resetTurn()
                }
                else {
                    // initialise the label of the game
                    binding.playerLabel.apply {
                        text = getString(R.string.player_d, currentPlayer)
                        visibility = View.VISIBLE
                    }
                }
                // Check the rollResultText to "Click roll" to encourage player to press button
                binding.RollResultText.text = getString(R.string.click_roll)
                // set the dice image click event handler
                if(isHoldEnabled)
                    setDiceClick()
            } else {
                // The text is equal to "Roll" -> roll the dices
                rollDices()
            }
        }
    }
//  The code below is the code before changes in 7.5.2.
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_main_extended)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_container)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

    private fun setDiceClick() {
        for ((diceIdx, id) in diceImgIdsArray.withIndex()) {
            // Iterate over the dices ImageViews and set listener of onClick event on each
            findViewById<ImageView>(id).setOnClickListener {
                // cast of the "it" argument of type View to ImageView -
                val img = it as ImageView
                // Toggle the state of the clicked dice
                diceStatesArray[diceIdx] = !diceStatesArray[diceIdx]
                // Change the tint of the clicked dice
                changeDiceTint(img, diceStatesArray[diceIdx])
            }
        }
    }

    private fun rollDices() {
        val dice = Dice()
        for (i in 0 until numDice) { // until so the range is not closed
            if (!diceStatesArray[i]) {
                diceValuesArray[i] = dice.roll() // get the value for this dice
                // Update the image of the dice ImageView based on the rolled value
                findViewById<ImageView>(diceImgIdsArray[i]).setImageResource(
                    resolveDrawable(
                        diceValuesArray[i]
                    )
                )
            }
        }
        updateTextAndImages()
        rollCount += 1 // increment the rollCount
        if (rollCount == numDice) {
            // If the rollCount == numDice -> change the player or end the game
            if (currentPlayer == 1) {
                // If the current player is 1 -> end the game
                endGame()
            } else {
                // Change the turn by changing the currentPlayer value
                // and the text displayed in rollButton
                currentPlayer = 1
                binding.RollButton.text = getString(R.string.second_start)
            }
            // reset roll count
            rollCount = 0
        }
    }

    private fun updateTextAndImages() {
        // Prepare the string to be displayed with StringBuilder with initial string "Rolled:"
        val rolledText = StringBuilder(getString(R.string.rolled_msg))
        var sum = 0
        for (i in 0 until numDice) { // until so the range is not closed
            val rollValue = diceValuesArray[i] // get the value for this dice
            sum += rollValue // add it to the sum
            rolledText.append("$rollValue") // append it to the result string
            if (i != numDice - 1)
                rolledText.append(", ") // append ", " to each value excluding the last one
            // Update the image of the dice ImageView based on the rolled value
            findViewById<ImageView>(diceImgIdsArray[i]).setImageResource(
                resolveDrawable(rollValue) )
        }
        // store the score of the player
        playerScores[currentPlayer] = sum
        rolledText.append("\nSum: $sum") // add the score to the result string
        binding.RollResultText.text = rolledText // send result string to the rollResultText
    }

    private fun endGame(){
        binding.RollButton.isEnabled = false // disable tollButton
        binding.playerLabel.visibility = View.INVISIBLE // Hide the label of the game
        // Determine the winner
        val winner = when {
            playerScores[0] == playerScores[1] -> -1 // draw is indicated by -1
            playerScores[0] > playerScores[1] -> 0 // player 0 wins
            else -> 1 // player 1 wins
        }
        // Display a snackbar with a result and an action that allows to start a new game
        Snackbar.make(
            binding.root,
            if (winner != -1) {
                val points = playerScores[winner]
                "Player $winner wins with $points points"
            } else {
                val points = playerScores[0]
                "Draw with $points points"
            },
            Snackbar.LENGTH_INDEFINITE
        ).setAction("Start over") {
            // Reset the game so we can start over
            resetGame()
            // enable the button so the user can click it again
            binding.RollButton.isEnabled = true
        }.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Create a menu with menuInflater and R.menu.menu resource
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle the selection of menu items
        when (item.itemId) {
            R.id.settings -> startSettingsActivity()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun startSettingsActivity() {
        // creates an Intent to start SettingActivity
        val intent: Intent = Intent(this, SettingsActivity::class.java).apply {
            // Add an extra value of type Int and a key stored in string resource
            // with name num_dice_key
            putExtra(getString(R.string.num_dice_key), numDice)
            // Add an extra value of type Boolean and a key stored in string resource
            // with name hold_enable_key
            putExtra(getString(R.string.hold_enable_key), isHoldEnabled)
            // The extra values can be retrieved in the destination activity with the keys
        }
        // Start the SettingsActivity with the launchSettingsActivity variable
        launchSettingsActivity.launch(intent)
    }

    // Register a callback for a SettingsActivity result.
    // This code will be called when we return from Settings Activity (after onStart)
    private val launchSettingsActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            Log.i(localClassName, "onActivityResult")
            if (result.resultCode == RESULT_OK) {
                // Perform operations only when the resulCode is RESULT_OK
                // Retrieve the data from the result.data Intent (only when it's not null)
                result.data?.let { data ->
                    // This code will be exectued only when result.data is not null,
                    // "data" is the argument of a lambda
                    // Get the numDice and isHoldEnabled settings
                    numDice = data.getIntExtra(getString(R.string.num_dice_key), 2)
                    isHoldEnabled = data.getBooleanExtra(getString(R.string.hold_enable_key), true)
                }
                // Apply the settings
                applySettings()
                // Reset the game - each time the user goes to settings activity
                // and returns back the game will be reset
                resetGame()
                // Display a snackbar pop-up to confirm the settings change
                Snackbar.make(
                    binding.root,
                    "Current settings: numDice: $numDice, isHoldEnabled: $isHoldEnabled",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

    private fun applySettings() {
        // Each time new settings are applied the game is reset
        binding.RollButton.isEnabled = true // enable the button so the user can click it again
        resetGame()
        val diceToHideBegin = numDice + 1
        // According to the numDice setting -> hide the remaining dice
        for (num in 1..5) {
            // Change the visibility of dices -> the view is found by its id
            if (num in diceToHideBegin..5)
                findViewById<ImageView>(diceImgIdsArray[num - 1]).apply {
                    // Making the visibility to GONE make it disappear and
                    // not take space in the layout
                    visibility = View.GONE
                    // Just in case disable any clicking on the image by disabling clickable
                    // and focusable attributes
                    isClickable = false
                    isFocusable = false
                }
            else
                findViewById<ImageView>(diceImgIdsArray[num - 1]).apply {
                    // Make the image visible
                    visibility = View.VISIBLE
                    // make the image clickable when isHoldEnabled == true
                    isClickable = isHoldEnabled
                    isFocusable = isHoldEnabled
                }
        }
    }

    private fun resetGame() {
        // Reset the variables of the game and initialize labels and buttons
        currentPlayer = 0
        playerScores[0] = 0
        playerScores[1] = 0
        rollCount = 0
        binding.RollResultText.text = getString(R.string.click_start)
        binding.playerLabel.visibility = View.INVISIBLE
        binding.RollButton.text = getString(R.string.button_initial)
        resetTurn()
    }

    private fun resetTurn() {
        // prepare the game for next turn
        for (num in 0..4) {
            diceValuesArray[num] = 1 // Reset the values displayed by each dice
            diceStatesArray[num] = false // Reset the "hold" state of each dice
            findViewById<ImageView>(diceImgIdsArray[num]).let {
                changeDiceTint(it, false) // reset the tint of each dice image view
                it.setImageResource(resolveDrawable(1)) // reset the image of each dice image view
            }
        }
        // Change the game label to show that user has changed
        binding.playerLabel.apply {
            text = getString(R.string.player_d, currentPlayer)
        }
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

    private fun changeDiceTint(img: ImageView, highlight: Boolean) {
        // Change the tint of the img. The getColor method is available for API >= M (API 23)
        img.imageTintList =
            ColorStateList.valueOf(getColor(if (highlight) R.color.yellow else R.color.white))
   }
}