package com.openclassrooms.realestatemanager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.openclassrooms.realestatemanager.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        configureTextViewMain(binding)
        configureTextViewQuantity(binding)
    }

    private fun configureTextViewMain(binding: ActivityMainBinding) {
        binding.activityMainActivityTextViewMain.textSize = 15f
        binding.activityMainActivityTextViewMain.text =
            "Le premier bien immobilier enregistré vaut "
    }

    private fun configureTextViewQuantity(binding: ActivityMainBinding) {
        val quantity = Utils.convertDollarToEuro(100)
        binding.activityMainActivityTextViewQuantity.textSize = 20f
        binding.activityMainActivityTextViewQuantity.text = quantity.toString()
    }
}