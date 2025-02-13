package com.example.lab1

import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

            val rdgProduct = findViewById<RadioGroup>(R.id.rdbgProduct)
            val rdgCompany = findViewById<RadioGroup>(R.id.rdbgCompany)
            val btnOk = findViewById<Button>(R.id.btnOk)
            val btnCancel = findViewById<Button>(R.id.btnCancel)
            val tvResult = findViewById<TextView>(R.id.tvResult)

            btnOk.setOnClickListener {
                val selectedProductId = rdgProduct.checkedRadioButtonId
                val selectedCompanyId = rdgCompany.checkedRadioButtonId

                if (selectedProductId != -1 && selectedCompanyId != -1) {
                    val selectedProduct = findViewById<RadioButton>(selectedProductId)
                    val selectedCompany = findViewById<RadioButton>(selectedCompanyId)

                    val result = "Product: ${selectedProduct.text}, Company: ${selectedCompany.text}"
                    tvResult.text = result
                } else {
                    Toast.makeText(this, "Please select both a product type and a company.", Toast.LENGTH_SHORT).show()
                }
            }

            btnCancel.setOnClickListener {
                rdgProduct.clearCheck()
                rdgCompany.clearCheck()

                tvResult.text = ""
            }
        }
    }
