package com.example.recipeslayer.ui.auth.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.example.recipeslayer.R
import com.example.recipeslayer.databinding.FragmentVerifyBinding
import com.example.recipeslayer.models.User
import com.example.recipeslayer.repo.Repo
import com.example.recipeslayer.ui.recipe.RecipeActivity
import com.example.recipeslayer.utils.Auth
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.recipeslayer.utils.Toast.toast

class VerifyFragment : Fragment(R.layout.fragment_verify) {

    private lateinit var binding: FragmentVerifyBinding
    private val args: VerifyFragmentArgs by navArgs()
    private lateinit var newUser: User

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentVerifyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newUser = args.newUser
        sendVerificationEmail()
    }

    private fun sendVerificationEmail() {
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.createUserWithEmailAndPassword(newUser.email, newUser.password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val firebaseUser = firebaseAuth.currentUser
                firebaseUser?.sendEmailVerification()
                    ?.addOnCompleteListener { verificationTask ->
                        if (verificationTask.isSuccessful)
                            listenToVerify(firebaseUser)
                        else
                            toast(requireContext(), getString(R.string.failed_to_send_verification_email))
                    }

            } else {
                toast(requireContext(), getString(R.string.account_creation_failed))
                requireActivity().supportFragmentManager.popBackStack()
            }

            loading(GONE)
        }
    }

    private fun listenToVerify(firebaseUser: FirebaseUser) {
        binding.btnVerify.setOnClickListener {
            loading(VISIBLE)
            firebaseUser.reload().addOnCompleteListener {
                if (it.isSuccessful && firebaseUser.isEmailVerified) {
                    registerNewUser()
                    lifecycleScope.launch(IO) {
                        firebaseUser.delete()
                    }

                } else {
                    toast(requireContext(), getString(R.string.email_is_not_verified_yet))
                    loading(GONE)
                }
            }
        }
    }

    private fun registerNewUser() = lifecycleScope.launch {
        val repo = Repo()
        val userId = withContext(IO) { repo.insertUser(newUser) }
        Auth.login(userId).also {
            toast(requireContext(), getString(R.string.account_created_successfully))
            navigateToHome()
            activity?.finish()
        }
    }

    private fun loading(flag: Int) {
        val flag2 = if (flag == VISIBLE) GONE else VISIBLE
        binding.apply {
            progressBar.visibility = flag
            tvVerify.visibility = flag2
            tvVerificationSent.visibility = flag2
            btnVerify.visibility = flag2
        }
    }

    private fun navigateToHome() {
        val intent = Intent(requireContext(), RecipeActivity::class.java)
        startActivity(intent)
        activity?.finishAffinity()
    }

}
