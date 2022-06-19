package com.example.chattingapp.fragment

import android.content.Context
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.chattingapp.R
import com.example.chattingapp.databinding.FragmentSplashBinding
import android.view.animation.AnimationUtils

class SplashFragment : Fragment() {
    private lateinit var binding: FragmentSplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSplashBinding.inflate(inflater,container,false)
        binding.splashScreenAppLogoBackIv.startAnimation(AnimationUtils.loadAnimation(context,R.anim.scale_decrease_anim))
        binding.splashScreenAppLogoIv.startAnimation(AnimationUtils.loadAnimation(context,R.anim.scale_decrease_anim))
        Handler().postDelayed({
            if(onBoardingFinished()){
                findNavController().navigate(R.id.action_splashFragment_to_welcomeFragment)

            }
            else{
                findNavController().navigate(R.id.action_splashFragment_to_viewPagerFragment)
            }
        },2000)
        return binding.root
    }
    private fun onBoardingFinished() : Boolean {
        val sharePref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        return sharePref.getBoolean("Finished",false)
    }
}