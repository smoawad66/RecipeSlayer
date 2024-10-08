package com.example.recipeslayer.ui.recipe.fragments

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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.recipeslayer.utils.Toast.toast

class VerifyFragment2 : Fragment(R.layout.fragment_verify) {

    private lateinit var binding: FragmentVerifyBinding
    private val args: VerifyFragment2Args by navArgs()
    private lateinit var user: User
    private val staticPassword = "Password@123"


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentVerifyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        user = args.user

        verifyUserEmail()
    }

    private fun verifyUserEmail() {
        val firebaseAuth = FirebaseAuth.getInstance()

        firebaseAuth.signInWithEmailAndPassword(user.email, staticPassword).addOnCompleteListener { task->
            if (task.isSuccessful) {
                deleteAndCreateNewUser(firebaseAuth.currentUser)
            } else {
                createFirebaseUser()
            }
        }
    }

    private fun deleteAndCreateNewUser(firebaseUser: FirebaseUser?){
        firebaseUser?.delete()?.addOnCompleteListener { task->
            if (task.isSuccessful) {
                createFirebaseUser()
            } else {
                toast(requireContext(), getString(R.string.unexpected_error_happens_please_try_again))
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }

    private fun createFirebaseUser() {
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.createUserWithEmailAndPassword(user.email, staticPassword).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                sendVerificationEmail(firebaseAuth.currentUser!!)
            }
            else {
                toast(requireContext(), getString(R.string.unexpected_error_happens_please_try_again))
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }

    private fun sendVerificationEmail(firebaseUser: FirebaseUser) {
        firebaseUser.sendEmailVerification().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                loading(GONE)
                listenToVerify(firebaseUser)
            }
            else {
                toast(requireContext(), getString(R.string.failed_to_send_verification_email))
                requireActivity().supportFragmentManager.popBackStack()
            }
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
