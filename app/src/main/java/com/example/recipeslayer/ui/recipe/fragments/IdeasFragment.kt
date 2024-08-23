package com.example.recipeslayer.ui.recipe.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.lifecycleScope
import com.example.recipeslayer.R
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class IdeasFragment : Fragment() {

    private lateinit var promptEt: TextInputEditText
    private lateinit var response: GenerateContentResponse
    private lateinit var responseTv: TextView
    private lateinit var generateBtn: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ideas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        promptEt = view.findViewById(R.id.edt_prompt)
        responseTv = view.findViewById(R.id.prompt_result)
        generateBtn = view.findViewById(R.id.generate_login)

        generateBtn.setOnClickListener {
            responseTv.visibility = View.VISIBLE
            val generativeModel = GenerativeModel(
                modelName = "gemini-1.5-flash",
                apiKey = "AIzaSyBJDwCxczi8DQa6LY5ig0SZNOO-dUIyoYM"
            )
            val prompt = promptEt.text.toString()
            MainScope().launch {
                response = generativeModel.generateContent(prompt)
                responseTv.text = response.text
                Log.i("lol", "onCreate: ________________${response.text}")
            }
        }
    }
}
