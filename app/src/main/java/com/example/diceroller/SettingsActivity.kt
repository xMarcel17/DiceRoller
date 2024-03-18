package com.example.diceroller

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.diceroller.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize binding
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Retrieve the current settings of the game that were passed with the Intent
        // to this Activity
        val numDice = intent.getIntExtra(getString(R.string.num_dice_key), 2)
        val isHoldEnabled = intent.getBooleanExtra(getString(R.string.hold_enable_key), true)
        // Apply the current isHoldEnabled setting
        binding.enableHoldSwitch.isChecked = isHoldEnabled
        // Select the current num dices value from the spinner.
        // We assume there are 5 dices max. and 2 dices min.
        if (numDice in 2..5)
            binding.numDiceSpinner.setSelection(numDice - 2)
        // Set the click listener for confirm button
        binding.confirmButton.setOnClickListener {
            // get current selection from the spinner and convert it to integer number
            // that will indicate the number of dices
            val spinnerSelection = binding.numDiceSpinner.selectedItem.toString().toInt()
            // get the current state of the hold enable switch
            val holdEnable = binding.enableHoldSwitch.isChecked
            // Create an empty intent that will be used to pass data to the calling Activity
            val result = Intent().apply {
                // Add the settings to the result intent
                putExtra(getString(R.string.num_dice_key), spinnerSelection)
                putExtra(getString(R.string.hold_enable_key), holdEnable)
            }
            // Set the resultCode to RESULT_OK and attach the data by the result Intent
            setResult(RESULT_OK, result)
            // Close this Activity and go back to the previous one
            finish()
        }
    }
}


//  The code below is the code before changes in 7.5.9.
//class SettingsActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_settings)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.ConstraintLayout)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//    }
//}