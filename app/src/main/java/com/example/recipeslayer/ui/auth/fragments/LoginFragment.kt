package com.example.recipeslayer.ui.auth.fragments

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
import com.example.recipeslayer.utils.Config.isArabic
import com.example.recipeslayer.utils.Hash
import com.example.recipeslayer.utils.Validator
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.recipeslayer.utils.toast.toast

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isArabic()) {
            fun toDp(v: Int) = (v * resources.displayMetrics.density).toInt()
            binding.edtPassword.setPadding(toDp(55), toDp(17), toDp(17), toDp(17))
        }

        binding.passwordToggle.setOnClickListener {
            binding.edtPassword.togglePasswordVisibility()
        }

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

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().moveTaskToBack(
                true
            )
        }


    }

    private fun handleLogin(email: String, password: String) {

        // Validation
        if (!validateUserData(email, password))
            return

        val repo = Repo()

        lifecycleScope.launch {
            val user = withContext(IO) { repo.getUser(email) as User? }

            if (user == null) {
                toast(requireContext(), getString(R.string.user_doesn_t_exist))
                return@launch
            }

            // Attempt to Login
            if (!Hash.verifyPassword(password, user.password)) {
                toast(requireContext(), getString(R.string.invalid_credentials))
                binding.apply {
                    edtPassword.text?.clear()
                    edtPassword.requestFocus()
                }
                return@launch
            }

            // Login and redirect user to home
            Auth.login(user.id).also {
                toast(requireContext(), getString(R.string.welcome_back).plus(" ").plus(user.name))
                navigateToHome()
            }
        }

    }


    private fun validateUserData(email: String, password: String): Boolean {

        if (email.isEmpty() || password.isEmpty()) {
            toast(requireContext(), getString(R.string.missing_data))
            return false
        }

        if (!Validator.validateEmail(email)) {
            toast(requireContext(), getString(R.string.please_enter_a_valid_email))
            return false
        }

        return true
    }

    private fun navigateToRegister() {
        findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
    }

    private fun navigateToHome() {
        val intent = Intent(activity, RecipeActivity::class.java)
        startActivity(intent)
        activity?.finishAffinity()
    }

    private fun TextInputEditText.togglePasswordVisibility() {
        if (this.transformationMethod == PasswordTransformationMethod.getInstance()) {
            this.transformationMethod = HideReturnsTransformationMethod.getInstance()
            binding.passwordToggle.setImageResource(R.drawable.hide_pass)
        } else {
            this.transformationMethod = PasswordTransformationMethod.getInstance()
            binding.passwordToggle.setImageResource(R.drawable.show_pass)
        }
        this.setSelection(this.text?.length ?: 0)
    }
}
