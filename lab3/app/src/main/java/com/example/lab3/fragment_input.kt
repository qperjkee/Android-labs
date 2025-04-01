package com.example.lab3

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import java.io.FileOutputStream

class FragmentInput : Fragment() {

    private lateinit var rdbgProduct: RadioGroup
    private lateinit var rdbgCompany: RadioGroup
    private lateinit var btnSubmit: Button
    private lateinit var btnOpen: Button

    private val FILE_NAME = "selection_data.txt"

    var onSubmitListener: ((String, String) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_input, container, false)

        rdbgProduct = view.findViewById(R.id.rdbgProduct)
        rdbgCompany = view.findViewById(R.id.rdbgCompany)
        btnSubmit = view.findViewById(R.id.btnOk)
        btnOpen = view.findViewById(R.id.btnOpen)

        btnSubmit.setOnClickListener {
            val selectedProductId = rdbgProduct.checkedRadioButtonId
            val selectedCompanyId = rdbgCompany.checkedRadioButtonId

            if (selectedProductId != -1 && selectedCompanyId != -1) {
                val selectedProduct = view.findViewById<RadioButton>(selectedProductId).text.toString()
                val selectedCompany = view.findViewById<RadioButton>(selectedCompanyId).text.toString()

                onSubmitListener?.invoke(selectedProduct, selectedCompany)

                saveToFile(selectedProduct, selectedCompany)
            } else {
                Toast.makeText(requireContext(), "Please select both a product type and a company.", Toast.LENGTH_SHORT).show()
            }
        }

        btnOpen.setOnClickListener {
            val intent = Intent(requireContext(), FileViewActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    private fun saveToFile(product: String, company: String) {
        try {
            val fileOutputStream: FileOutputStream = requireContext().openFileOutput(FILE_NAME, Context.MODE_APPEND)
            val data = "Selected: $product from $company\n"
            fileOutputStream.write(data.toByteArray())
            fileOutputStream.close()

            Toast.makeText(requireContext(), "Data successfully saved to file", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error saving data: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun clearSelection() {
        rdbgProduct.clearCheck()
        rdbgCompany.clearCheck()
    }
}