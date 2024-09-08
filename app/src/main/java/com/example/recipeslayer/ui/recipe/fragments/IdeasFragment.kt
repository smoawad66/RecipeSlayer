package com.example.recipeslayer.ui.recipe.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.recipeslayer.R
import com.example.recipeslayer.utils.Constants.AI_INSTRUCTIONS
import com.example.recipeslayer.utils.Constants.GEMINI_API_KEY
import com.example.recipeslayer.utils.Internet.isInternetAvailable
import com.example.recipeslayer.utils.Toast.toast
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.GoogleGenerativeAIException
import com.google.ai.client.generativeai.type.ResponseStoppedException
import com.google.ai.client.generativeai.type.ServerException
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
    private lateinit var newChat: TextView
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
        newChat = view.findViewById(R.id.tv_new_chat)
        generateBtn = view.findViewById(R.id.generate_login)


        val generativeModel = GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = GEMINI_API_KEY,
            systemInstruction = content { text(AI_INSTRUCTIONS) },
        )

        val chat = generativeModel.startChat()

        generateBtn.setOnClickListener {

            val userMessage = promptEt.text.toString().trim()
            if (userMessage.isBlank()) {
                toast(requireContext(), getString(R.string.missing_data))
                return@setOnClickListener
            }

            lifecycleScope.launch {
                if (!isInternetAvailable()) {
                    responseTv.text = getString(R.string.check_your_internet_connection)
                    return@launch
                }

                responseTv.visibility = VISIBLE
                responseTv.text = getString(R.string.generating_a_response_for_your_prompt)
                generateBtn.isEnabled = false
                generateBtn.alpha = 0.5f

                try {
                    val aiResponse = withContext(IO) { chat.sendMessage(userMessage).text!! }

                    chat.history.addAll(listOf(
                        content("user") { text(userMessage) },
                        content("model") { text(aiResponse) },
                    ))

                    markwon.setMarkdown(responseTv, aiResponse)
                    promptEt.apply { text?.clear(); requestFocus() }
                    generateBtn.isEnabled = true
                    generateBtn.alpha = 1.0f
                }
                catch (e: GoogleGenerativeAIException) {
                    generateBtn.isEnabled = false
                    responseTv.text = getString(R.string.you_have_reached_the_limit)
                    newChat.visibility = VISIBLE
                }
            }
        }


        newChat.setOnClickListener {
            findNavController().apply {
                popBackStack(R.id.ideasFragment, true)
                navigate(R.id.ideasFragment)
            }
        }
    }
}
