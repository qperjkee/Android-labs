package com.example.lab2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var fragmentInput: FragmentInput
    private lateinit var fragmentResult: FragmentResult

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fragmentInput = supportFragmentManager.findFragmentById(R.id.fragment_input) as FragmentInput
        fragmentResult = supportFragmentManager.findFragmentById(R.id.fragment_result) as FragmentResult

        fragmentInput.onSubmitListener = { product, company ->
            fragmentResult.displayResult(product, company)
        }

        fragmentResult.onCancelListener = {
            fragmentInput.clearSelection()
        }
    }
}
