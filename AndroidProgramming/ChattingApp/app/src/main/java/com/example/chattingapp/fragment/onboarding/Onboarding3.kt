package com.example.chattingapp.fragment.onboarding

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.chattingapp.R
import com.example.chattingapp.databinding.FragmentOnboarding3Binding

class Onboarding3 : Fragment() {
    private lateinit var binding: FragmentOnboarding3Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOnboarding3Binding.inflate(inflater, container, false)
        return binding.root
    }

    private fun onBoardingFinished() {
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("Finished", true)
        editor.apply()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnFinished.setOnClickListener {
            onBoardingFinished()
            findNavController().navigate(R.id.action_viewPagerFragment_to_welcomeFragment)
        }
    }
}