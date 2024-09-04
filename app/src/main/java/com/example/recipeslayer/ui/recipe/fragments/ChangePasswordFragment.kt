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

        repo = Repo()

        binding.btnUpdatePass.setOnClickListener {

            val id = Auth.id()
            val oldPass = binding.edtPass0.text.toString()
            val pass1 = binding.edtPass1.text.toString()
            val pass2 = binding.edtPass2.text.toString()

            if (!validatePasswords(oldPass, pass1, pass2))
                return@setOnClickListener

            lifecycleScope.launch {
                val user = withContext(IO) { repo.getUser(id) }

                if (!Hash.verifyPassword(oldPass, user.password)) {
                    toast("Wrong password!")
                    return@launch
                }

                if (pass1 != pass2) {
                    toast("Passwords don't match!")
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
            toast("Password Changed!")
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
            toast("Missing data")
            return false
        }


        if (!Validator.validatePassword(newPass)) {
            toast("Please enter a stronger password.")
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

    private fun toast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

}