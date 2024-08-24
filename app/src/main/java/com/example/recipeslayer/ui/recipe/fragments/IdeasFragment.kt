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
import com.example.recipeslayer.utils.Constants.GEMINI_API_KEY
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class IdeasFragment : Fragment() {

    private lateinit var promptEt: TextInputEditText
    private lateinit var response: GenerateContentResponse
    private lateinit var responseTv: TextView
    private lateinit var generateBtn: AppCompatButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_ideas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        promptEt = view.findViewById(R.id.edt_prompt)
        responseTv = view.findViewById(R.id.prompt_result)
        generateBtn = view.findViewById(R.id.generate_login)

        generateBtn.setOnClickListener {
            responseTv.visibility = View.VISIBLE
            responseTv.text = getString(R.string.generating_a_response_for_your_prompt)
            val generativeModel = GenerativeModel(
                modelName = "gemini-1.5-flash",
                apiKey = GEMINI_API_KEY
            )
            val prompt = "I am using you for my android app. I am using you to answer questions in the context of cooking. The user will give you a prompt. If it is out of the context of cooking, just answer that you only know about cooking. Give me the response in plain text with no formatting (no stars '*'). Here is the prompt: ${promptEt.text.toString()}"
            lifecycleScope.launch(IO) {
                response = generativeModel.generateContent(prompt)
                withContext(Main){ responseTv.text = response.text }
                Log.i("lol", "onCreate: ________________${response.text}")
            }
        }
    }
}
