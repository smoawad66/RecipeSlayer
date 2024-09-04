package com.example.recipeslayer.ui.recipe.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.recipeslayer.R
import com.example.recipeslayer.databinding.FragmentChangePasswordBinding
import com.example.recipeslayer.databinding.FragmentProfileBinding
import com.example.recipeslayer.local.LocalSource
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val repo = Repo()

        binding.btnUpdatePass.setOnClickListener {

            val id = Auth.id()
            val pass1: String = binding.edtPass1.text.toString()
            val pass2: String = binding.edtPass2.text.toString()
            val oldPassHashed = binding.edtPass0.text.toString()

            lifecycleScope.launch {
                val user = withContext(Dispatchers.IO) { repo.getUser(id) }
                if(Hash.verifyPassword(oldPassHashed, user.password)) {
                    if (pass1 == pass2) {
                        user.password = pass1
                        handleUpdate(user)
                        withContext(Dispatchers.Main) {
                            binding.edtPass1.text?.clear()
                            binding.edtPass2.text?.clear()
                            binding.edtPass1.clearFocus()
                            binding.edtPass2.clearFocus()
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Passwords are Not Matching!",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.edtPass1.text?.clear()
                        binding.edtPass1.requestFocus()
                        val imm =
                            context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
                        binding.edtPass2.text?.clear()
                    }
                }
                else {
                    Toast.makeText(requireContext(), "Wrong Password.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun handleUpdate(newUser: User) {

        if (!validateUserData(newUser))
            return

        val repo = Repo(localSource = LocalSource.getInstance())

        lifecycleScope.launch {

            val hash = Hash.hashPassword(newUser.password)
            newUser.password = hash
            withContext(IO) { repo.updateUser(newUser) }
            Toast.makeText(requireContext(), "Password Updated!", Toast.LENGTH_SHORT).show()
            Auth.logout()
            val intent = Intent(requireContext(), AuthActivity::class.java)
            intent.putExtra("splashTime", 0L)
            startActivity(intent)
            requireActivity().finishAffinity()

        }
    }

    private fun validateUserData(user: User): Boolean {

        with(user) {

            if (!Validator.validatePassword(password)) {
                Toast.makeText(requireContext(), "Enter stronger password.", Toast.LENGTH_SHORT).show()
                binding.edtPass1.text?.clear()
                binding.edtPass2.text?.clear()
                binding.edtPass1.clearFocus()
                binding.edtPass2.clearFocus()
                return false
            }
        }
        return true
    }

}