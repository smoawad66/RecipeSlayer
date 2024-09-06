package com.example.recipeslayer.ui.recipe.fragments

import android.content.Intent
import android.os.Bundle
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
import androidx.navigation.fragment.navArgs
import com.example.recipeslayer.R
import com.example.recipeslayer.databinding.FragmentVerifyBinding
import com.example.recipeslayer.models.User
import com.example.recipeslayer.repo.Repo
import com.example.recipeslayer.ui.recipe.RecipeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.recipeslayer.utils.toast.toast

class VerifyFragment2 : Fragment(R.layout.fragment_verify) {

    private lateinit var binding: FragmentVerifyBinding
    private val args: VerifyFragment2Args by navArgs()
    private lateinit var user: User

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentVerifyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        user = args.user
        sendVerificationEmail()
    }

    private fun sendVerificationEmail() {
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.createUserWithEmailAndPassword(user.email, user.password).addOnCompleteListener { task ->
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
                toast(requireContext(), getString(R.string.unexpected_error_happens_please_try_again))
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
                    lifecycleScope.launch(IO) {
                        Repo().updateUser(user)
                        firebaseUser.delete()
                        withContext(Main) {
                            toast(requireContext(), getString(R.string.profile_updated))
                            restart()
                        }
                    }

                } else {
                    loading(GONE)
                    toast(requireContext(), getString(R.string.email_is_not_verified_yet))
                }
            }
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

    private fun restart() {
        activity?.finish().also {
            requireActivity().startActivity(Intent(activity, RecipeActivity::class.java))
        }

    }

}
