package com.example.recipeslayer.ui.auth.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.recipeslayer.R
import com.example.recipeslayer.databinding.FragmentLoginBinding
import com.example.recipeslayer.models.User
import com.example.recipeslayer.repo.Repo
import com.example.recipeslayer.utils.Auth
import com.example.recipeslayer.ui.recipe.RecipeActivity
import com.example.recipeslayer.utils.Hash
import com.example.recipeslayer.utils.Validator
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val email0 = activity?.intent?.extras?.getString("email")
        if (email0 != null) {
            binding.apply {
                edtEmail.setText(email0)
                edtPassword.requestFocus()
            }
        }

        binding.btnLogin.setOnClickListener {
            binding.apply {
                val email = edtEmail.text.toString()
                val password = edtPassword.text.toString()
                handleLogin(email, password)
            }
        }
        binding.dontHaveAccount.setOnClickListener { navigateToRegister() }
        binding.createNow.setOnClickListener { navigateToRegister() }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) { requireActivity().moveTaskToBack(true) }


    }

    private fun handleLogin(email: String, password: String) {

        // Validation
        if (!validateUserData(email, password))
            return

        val repo = Repo()

        lifecycleScope.launch {
            val user = withContext(IO) { repo.getUser(email) as User? }

            if (user == null) {
                toast("User doesn't exist!")
                return@launch
            }

            // Attempt to Login
            if (!Hash.verifyPassword(password, user.password)) {
                toast("Invalid Credentials!")
                binding.apply {
                    edtPassword.text?.clear()
                    edtPassword.requestFocus()
                }
                return@launch
            }

            // Login and redirect user to home
            Auth.login(user.id).also {
                navigateToHome()
                toast("Welcome back ${user.name}.")
            }
        }

    }


    private fun validateUserData(email: String, password: String): Boolean {

        if (email.isEmpty() || password.isEmpty()) {
            toast("Missing data.")
            return false
        }

        if (!Validator.validateEmail(email)) {
            toast("Please enter a valid email.")
            return false
        }

        return true
    }

    private fun toast(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToRegister() {
        findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
    }

    private fun navigateToHome() {
        val intent = Intent(activity, RecipeActivity::class.java)
        startActivity(intent)
        activity?.finishAffinity()
    }
}
