package com.example.recipeslayer.ui.recipe.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.recipeslayer.R
import com.example.recipeslayer.databinding.FragmentProfileBinding
import com.example.recipeslayer.local.LocalSource
import com.example.recipeslayer.models.User
import com.example.recipeslayer.repo.Repo
import com.example.recipeslayer.ui.recipe.RecipeActivity
import com.example.recipeslayer.utils.Auth
import com.example.recipeslayer.utils.Validator
import com.github.dhaval2404.imagepicker.ImagePicker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private var imageUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val repo = Repo()
        val id = Auth.id()
        var profilePicPath: String? = null
        lifecycleScope.launch {
            val user = withContext(Dispatchers.IO) { repo.getUser(id) }

            withContext(Dispatchers.Main) {
                binding.edtName.setText(user.name)
                binding.edtEmail.setText(user.email)
                profilePicPath = user.picture

                if (!profilePicPath.isNullOrEmpty()) {
                    binding.profilePicIv.setImageURI(profilePicPath?.toUri())
                }
                else {
                    binding.profilePicIv.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.no_profile_pic))
                }
            }
        }



        binding.floatingActionButton.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start()
        }

        binding.btnUpdate.setOnClickListener {

            val email = binding.edtEmail.text.toString().trim()
            val name = binding.edtName.text.toString().trim()

            val id = Auth.id()

            lifecycleScope.launch {
                val user = withContext(Dispatchers.IO) { repo.getUser(id) }

                user.name = name
                user.email = email
                handleUpdate(user)

                withContext(Dispatchers.Main) {
                    binding.edtName.setText(user.name)
                    binding.edtEmail.setText(user.email)
                    binding.edtName.clearFocus()
                    binding.edtEmail.clearFocus()
                    Toast.makeText(requireContext(), "User Updated!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnChangePassword.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_changePasswordFragment)
        }

    }

    private fun handleUpdate(newUser: User) {
        Log.i("lol", "onViewCreated: _________________In handleUpdate")

        // Validation
        if (!validateUserData(newUser))
            return

        val repo = Repo(localSource = LocalSource.getInstance())

        lifecycleScope.launch {
            withContext(IO) {
                repo.updateUser(newUser)
                Log.i("lol", "onViewCreated: _________________In updateUser")
            }
        }
    }

    private fun validateUserData(user: User): Boolean {

        with(user) {
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                return false
            }

            if (!Validator.validateEmail(email)) {
                return false
            }

        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            imageUri = data?.data
            Log.i("lol", "onActivityResult: _____________$imageUri")
            binding.profilePicIv.setImageURI(imageUri)

            imageUri?.let {
//                val picturePath = getPathFromUri(it)
                updateUserProfilePicture(imageUri.toString())
            }
        }
    }

    private fun updateUserProfilePicture(picturePath: String) {
        val repo = Repo()
        val id = Auth.id()

        lifecycleScope.launch {
            val user = withContext(Dispatchers.IO) { repo.getUser(id) }

            withContext(Dispatchers.Main) {
                user.picture = picturePath
                handleUpdate(user)
                restart()
            }
        }
    }

    private fun restart() {
        activity?.finish().also {
            requireActivity().startActivity(Intent(activity, RecipeActivity::class.java))
        }
    }

}