package com.example.diceroller

class Dice(private val numSides: Int = 6)
/*this is the main constructor definition
- default value for numSides is 6*/
{
    // This is a public function definition inside the class
    // this function returns a random integer number between 1 and numSides
    fun roll(): Int {
        return (1 .. numSides).random()
    }
}