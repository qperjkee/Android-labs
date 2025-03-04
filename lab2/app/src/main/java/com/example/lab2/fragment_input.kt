package com.example.lab2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast


class FragmentInput : Fragment() {

    private lateinit var rdbgProduct: RadioGroup
    private lateinit var rdbgCompany: RadioGroup
    private lateinit var btnSubmit: Button

    var onSubmitListener: ((String, String) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_input, container, false)

        rdbgProduct = view.findViewById(R.id.rdbgProduct)
        rdbgCompany = view.findViewById(R.id.rdbgCompany)
        btnSubmit = view.findViewById(R.id.btnOk)

        btnSubmit.setOnClickListener {
            val selectedProductId = rdbgProduct.checkedRadioButtonId
            val selectedCompanyId = rdbgCompany.checkedRadioButtonId

            if (selectedProductId != -1 && selectedCompanyId != -1) {
                val selectedProduct = view.findViewById<RadioButton>(selectedProductId).text.toString()
                val selectedCompany = view.findViewById<RadioButton>(selectedCompanyId).text.toString()

                onSubmitListener?.invoke(selectedProduct, selectedCompany)
            } else {
                Toast.makeText(requireContext(), "Please select both a product type and a company.", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    fun clearSelection() {
        rdbgProduct.clearCheck()
        rdbgCompany.clearCheck()
    }
}
