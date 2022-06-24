package com.example.chattingapp.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.chattingapp.R
import com.example.chattingapp.databinding.FragmentWelcomeBinding

class WelcomeFragment : Fragment() {
    private lateinit var binding: FragmentWelcomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        if (onLoginSuccess()) {
            findNavController().navigate(R.id.action_welcomeFragment_to_homeFragment)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            btnLogin.setOnClickListener {
                findNavController().navigate(R.id.action_welcomeFragment_to_loginFragment)
            }
            btnSignup.setOnClickListener {
                findNavController().navigate(R.id.action_welcomeFragment_to_signUpFragment)
            }
        }
    }

    private fun onLoginSuccess(): Boolean {
        val sharePreferences =
            requireActivity().getSharedPreferences("shared_preference", Context.MODE_PRIVATE)
        return sharePreferences.getBoolean("KEY_REMEMBER_ME", false)
    }
}