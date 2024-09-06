package com.example.recipeslayer.ui.auth.fragments

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.recipeslayer.R
import com.example.recipeslayer.databinding.FragmentRegisterBinding
import com.example.recipeslayer.models.User
import com.example.recipeslayer.repo.Repo
import com.example.recipeslayer.utils.Config.isArabic
import com.example.recipeslayer.utils.Validator
import com.example.recipeslayer.utils.Hash
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.recipeslayer.utils.toast.toast

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
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

        binding.btnSignup.setOnClickListener {
            // ya 2alb a5ok (كلام حلو)
            binding.passInstructions.visibility = GONE
            binding.apply {
                val name = edtName.text.toString()
                val email = edtEmail.text.toString()
                val password = edtPassword.text.toString()
                val user = User(name, email, password)
                handleRegister(user)
            }
        }

        binding.alreadyHaveAnAccount.setOnClickListener { navigateToLogin() }
        binding.tvLogin.setOnClickListener { navigateToLogin() }
    }

    private fun handleRegister(newUser: User) {

        if (!validateUserData(newUser))
            return

        val repo = Repo()

        lifecycleScope.launch {
            val user = withContext(IO) { repo.getUser(newUser.email) }

            if (user != null)
                toast(requireContext(), getString(R.string.user_already_exists)).also { return@launch }

            val hash = Hash.hashPassword(newUser.password)
            newUser.password = hash

            navigateToVerify(newUser)
        }
    }

    private fun validateUserData(user: User): Boolean {

        with(user) {
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                toast(requireContext(), getString(R.string.missing_data))
                return false
            }

            if (!Validator.validateEmail(email)) {
                toast(requireContext(), getString(R.string.please_enter_a_valid_email))
                return false
            }

            if (!Validator.validatePassword(password)) {
                toast(requireContext(), getString(R.string.please_enter_a_stronger_password))
                binding.passInstructions.visibility = VISIBLE
                return false
            }
        }
        return true
    }

    private fun navigateToLogin() {
        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
    }

    private fun navigateToVerify(newUser: User) {
        val action = RegisterFragmentDirections.actionRegisterFragmentToVerifyFragment(newUser)
        findNavController().navigate(action)
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
