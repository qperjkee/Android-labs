package com.example.lab2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

class FragmentResult : Fragment() {

    private lateinit var tvResult: TextView
    private lateinit var btnCancel: Button

    var onCancelListener: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_result, container, false)

        tvResult = view.findViewById(R.id.tvResult)
        btnCancel = view.findViewById(R.id.btnCancel)

        btnCancel.setOnClickListener {
            onCancelListener?.invoke()
            tvResult.text = ""
        }

        return view
    }

    fun displayResult(product: String, company: String) {
        tvResult.text = "Selected: $product from $company"
    }
}
