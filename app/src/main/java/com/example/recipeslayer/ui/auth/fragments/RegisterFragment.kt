package com.example.recipeslayer.ui.auth.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.recipeslayer.R
import com.example.recipeslayer.databinding.FragmentRegisterBinding
import com.example.recipeslayer.local.LocalSource
import com.example.recipeslayer.models.User
import com.example.recipeslayer.repo.Repo
import com.example.recipeslayer.utils.Validator
import com.example.recipeslayer.utils.Auth
import com.example.recipeslayer.utils.Hash
import com.example.recipeslayer.ui.recipe.RecipeActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSignup.setOnClickListener {
            // data to insert into room ya 2alb a5ok (كلام حلو)
            binding.apply {
                val email = edtEmail.text.toString()
                val name = edtName.text.toString()
                val password = edtPassword.text.toString()
                val user = User(name, email, password)
                handleRegister(user)
            }
        }

        binding.alreadyHaveAnAccount.setOnClickListener { navigateToLogin() }
        binding.tvLogin.setOnClickListener { navigateToLogin() }
    }

    private fun handleRegister(newUser: User) {
        // Validation
        if (!validateUserData(newUser))
            return

        val repo = Repo(localSource = LocalSource.getInstance())
        val auth = FirebaseAuth.getInstance()

        lifecycleScope.launch {
            val user = withContext(IO) { repo.getUser(newUser.email) as User? }

            if (user != null) {
                toast("User already exists!")
                return@launch
            }

            val (email, password) = newUser.email to newUser.password
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val firebaseUser = auth.currentUser
                        firebaseUser?.sendEmailVerification()?.addOnCompleteListener { verificationTask ->
                            if (verificationTask.isSuccessful) {
                                // Save user to local database
                                val hash = Hash.hashPassword(newUser.password)
                                newUser.password = hash
                                lifecycleScope.launch(IO) {
                                    repo.insertUser(newUser)
                                }
                                // Redirect user to home
                                toast("Verification email sent. Please check your email.")
                                navigateToHome()
                                activity?.finish()
                            } else {
                                toast("Failed to send verification email.")
                            }
                        }
                    } else {
                        toast("Account creation failed: ${task.exception?.message}")
                    }
                }
        }
    }

    private fun validateUserData(user: User): Boolean {
        with(user) {
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                toast("Missing data.")
                return false
            }

            if (!Validator.validateEmail(email)) {
                toast("Please enter a valid email.")
                return false
            }

            if (!Validator.validatePassword(password)) {
                toast("Password must be stronger.")
                return false
            }
        }
        return true
    }

    private fun navigateToLogin() {
        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
    }

    private fun toast(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToHome() {
        val intent = Intent(requireContext(), RecipeActivity::class.java)
        startActivity(intent)
    }
}
