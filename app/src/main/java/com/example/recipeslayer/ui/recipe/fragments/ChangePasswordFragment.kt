package com.example.recipeslayer.ui.recipe.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.recipeslayer.databinding.FragmentChangePasswordBinding
import com.example.recipeslayer.models.User
import com.example.recipeslayer.repo.Repo
import com.example.recipeslayer.ui.auth.AuthActivity
import com.example.recipeslayer.utils.Auth
import com.example.recipeslayer.utils.Hash
import com.example.recipeslayer.utils.Validator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.content.Intent
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.Gravity
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.TextView
import com.example.recipeslayer.R
import com.example.recipeslayer.utils.Config.isArabic
import com.google.android.material.textfield.TextInputEditText
import com.example.recipeslayer.utils.toast.toast


class ChangePasswordFragment : Fragment() {

    private lateinit var binding: FragmentChangePasswordBinding
    private lateinit var repo: Repo

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (isArabic()) {
            fun toDp(v: Int) = (v * resources.displayMetrics.density).toInt()
            binding.edtPass1.setPadding(toDp(55), toDp(17), toDp(17), toDp(17))
            binding.edtPass2.setPadding(toDp(55), toDp(17), toDp(17), toDp(17))
        }

        binding.passwordToggle.setOnClickListener {
            binding.edtPass1.togglePasswordVisibility(binding.passwordToggle)
        }

        binding.password2Toggle.setOnClickListener {
            binding.edtPass2.togglePasswordVisibility(binding.password2Toggle)
        }

        repo = Repo()

        binding.btnUpdatePass.setOnClickListener {

            binding.passInstructions.visibility = GONE
            val id = Auth.id()
            val oldPass = binding.edtPass0.text.toString()
            val pass1 = binding.edtPass1.text.toString()
            val pass2 = binding.edtPass2.text.toString()

            if (!validatePasswords(oldPass, pass1, pass2))
                return@setOnClickListener

            lifecycleScope.launch {
                val user = withContext(IO) { repo.getUser(id) }

                if (!Hash.verifyPassword(oldPass, user.password)) {
                    toast(requireContext(), getString(R.string.wrong_password))
                    return@launch
                }

                if (pass1 != pass2) {
                    toast(requireContext(), getString(R.string.passwords_don_t_match))
                    binding.edtPass1.text?.clear()
                    binding.edtPass1.requestFocus()
                    binding.edtPass2.text?.clear()
//                    val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                    imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
                    return@launch
                }


                user.password = Hash.hashPassword(pass1)
                updateUserPassword(user)
            }

        }
    }

    private fun updateUserPassword(user: User) {
        lifecycleScope.launch {
            withContext(IO) { repo.updateUser(user) }
            toast(requireContext(), getString(R.string.password_changed))
            Auth.logout()
            val intent = Intent(requireContext(), AuthActivity::class.java)
            intent.putExtra("splashTime", 0L)
            intent.putExtra("email", user.email)
            startActivity(intent)
            activity?.finishAffinity()
        }
    }

    private fun validatePasswords(oldPass: String, newPass: String, confirmPass: String): Boolean {

        if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            toast(requireContext(), getString(R.string.missing_data))
            return false
        }


        if (!Validator.validatePassword(newPass)) {
            toast(requireContext(), getString(R.string.please_enter_a_stronger_password))
            binding.passInstructions.visibility = VISIBLE
            binding.apply {
                edtPass1.text?.clear()
                edtPass1.clearFocus()
                edtPass2.text?.clear()
                edtPass2.clearFocus()
            }
            return false
        }
        return true
    }

    private fun TextInputEditText.togglePasswordVisibility(img: ImageView) {
        if (this.transformationMethod == PasswordTransformationMethod.getInstance()) {
            this.transformationMethod = HideReturnsTransformationMethod.getInstance()
            img.setImageResource(R.drawable.hide_pass)
        } else {
            this.transformationMethod = PasswordTransformationMethod.getInstance()
            img.setImageResource(R.drawable.show_pass)
        }
        this.setSelection(this.text?.length ?: 0)
    }

}