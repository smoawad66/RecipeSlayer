package com.example.recipeslayer.ui.recipe.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.lifecycleScope
import com.example.recipeslayer.R
import com.example.recipeslayer.utils.Constants.AI_INSTRUCTIONS
import com.example.recipeslayer.utils.Constants.GEMINI_API_KEY
import com.example.recipeslayer.utils.Internet.isInternetAvailable
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.UnknownException
import com.google.ai.client.generativeai.type.content
import com.google.android.material.textfield.TextInputEditText
import io.noties.markwon.Markwon
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class IdeasFragment : Fragment() {

    private lateinit var promptEt: TextInputEditText
    private lateinit var responseTv: TextView
    private lateinit var generateBtn: AppCompatButton
    private lateinit var markwon: Markwon

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ideas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        markwon = Markwon.create(requireContext())
        promptEt = view.findViewById(R.id.edt_prompt)
        responseTv = view.findViewById(R.id.prompt_result)
        generateBtn = view.findViewById(R.id.generate_login)

        val generativeModel = GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = GEMINI_API_KEY,
            systemInstruction = content { text(AI_INSTRUCTIONS) },
        )

        val chat = generativeModel.startChat()

        generateBtn.setOnClickListener {
            responseTv.visibility = View.VISIBLE
            responseTv.text = getString(R.string.generating_a_response_for_your_prompt)

            lifecycleScope.launch(IO) {
                if (!isInternetAvailable()) {
                    withContext(Main) {
                        responseTv.text = getString(R.string.check_your_internet_connection)
                    }
                    return@launch
                }

                withContext(Main) { generateBtn.isEnabled = false; generateBtn.alpha = 0.5f }

                val userMessage = promptEt.text.toString()
                try {
                    val aiResponse = chat.sendMessage(userMessage).text ?: ""
                    chat.history.addAll(listOf(
                        content("user") { text(userMessage) },
                        content("model") { text(aiResponse) },
                    ))
                    withContext(Main) {
                        markwon.setMarkdown(responseTv, aiResponse)
                        promptEt.apply { text?.clear(); requestFocus() }
                        generateBtn.isEnabled = true
                        generateBtn.alpha = 1.0f
                    }
                }
                catch (_: UnknownException){}
            }
        }
    }
}
