package com.example.chattingapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.chattingapp.ChattingApp
import com.example.chattingapp.R
import com.example.chattingapp.databinding.FragmentLoginBinding
import com.example.chattingapp.factory.LoginViewModelFactory
import com.example.chattingapp.viewModel.LoginViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var viewModel: LoginViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(
            this,
            LoginViewModelFactory(activity?.application as ChattingApp)
        ).get(LoginViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mAuth = FirebaseAuth.getInstance()
        (activity as AppCompatActivity).supportActionBar?.hide()
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnLogin.setOnClickListener {
            viewModel.checkValidEmailAndPassword(
                binding.loginEmail.text.toString().trim(),
                binding.loginPassword.text.toString().trim()
            )
        }
        binding.btnSignup.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }
        listenerSuccessEvent()
        listenerErrorEvent()
    }

    private fun listenerSuccessEvent() {
        viewModel.isSuccessEvent.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.rememberMe(binding.cbRememberMe.isChecked)
                login(
                    binding.loginEmail.text.toString().trim(),
                    binding.loginPassword.text.toString().trim()
                )
            }
        }
    }

    private fun listenerErrorEvent() {
        viewModel.isErrorEvent.observe(viewLifecycleOwner) { errMess ->
            val dialog = AlertDialog.Builder(requireContext())
            dialog.setTitle("Error")
            dialog.setMessage(errMess)
            dialog.show()
        }
    }

    private fun login(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    if (viewModel.isRemember()) {
                        viewModel.saveUserInfo(
                            binding.loginEmail.text.toString().trim(),
                            binding.loginPassword.text.toString().trim()
                        )
                    }
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                } else {
                    Toast.makeText(requireContext(), "Wrong email or password", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }
}