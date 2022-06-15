package com.example.chattingapp.fragment

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.chattingapp.ChattingApp
import com.example.chattingapp.R
import com.example.chattingapp.databinding.FragmentProfileBinding
import com.example.chattingapp.factory.ProfileViewModelFactory
import com.example.chattingapp.viewModel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private lateinit var imageUri: Uri
    private lateinit var viewModel: ProfileViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(
            this,
            ProfileViewModelFactory(activity?.application as ChattingApp)
        ).get(
            ProfileViewModel::class.java
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().reference
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        findUser()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setLogout()
        //upload avatar
        binding.btnUploadImage.setOnClickListener {
            selectImage()
        }
        //upload background

        //change info
        binding.apply {
            btnChangeInfo.setOnClickListener {
                val textChange = "Change informations";
                val textSave = "Save change";
                if(textChange.equals(btnChangeInfo.text)){
                    btnChangeInfo.text = textSave
                    txtUserName.isClickable = true
                    txtDescription.isClickable = true
                    txtLocation.isClickable = true
                }
                else if(textSave.equals(btnChangeInfo.text)){
                    val progressDialog = ProgressDialog(requireContext())
                    progressDialog.setMessage("Uploading change to database...")
                    progressDialog.setCancelable(false)
                    progressDialog.show()
                    mDbRef.child("user").child(mAuth.currentUser?.uid.toString()).child("name")
                        .setValue(txtUserName.text.toString())
                    mDbRef.child("user").child(mAuth.currentUser?.uid.toString()).child("location")
                        .setValue(txtLocation.text.toString())
                    mDbRef.child("user").child(mAuth.currentUser?.uid.toString()).child("description")
                        .setValue(txtDescription.text.toString())
                    btnChangeInfo.text = textChange
                    txtDescription.isClickable = false
                    txtUserName.isClickable = false
                    txtLocation.isClickable = false
                    if (progressDialog.isShowing)
                        progressDialog.dismiss()
                }
            }
        }
    }

    private fun selectImage() {
        CropImage
            .activity()
            .setGuidelines(CropImageView.Guidelines.ON)
            .start(requireContext(),this)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result : CropImage.ActivityResult = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.uri
                val bitmap =
                    MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, imageUri)
                binding.avatar.setImageBitmap(bitmap)
                uploadImageToFirebase()
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }

    private fun uploadImageToFirebase() {
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Uploading File...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        val storageReference =
            FirebaseStorage.getInstance().getReference("images/${mAuth.currentUser?.uid}.jpeg")
        storageReference.putFile(imageUri)
            .addOnSuccessListener {
                addImageToRealtimeDatabase()
                Toast.makeText(requireContext(), "Successful uploaded", Toast.LENGTH_SHORT).show()

                if (progressDialog.isShowing)
                    progressDialog.dismiss()
            }
            .addOnFailureListener {
                if (progressDialog.isShowing)
                    progressDialog.dismiss()
                Toast.makeText(requireContext(), "Failed to upload image", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun addImageToRealtimeDatabase() {
        FirebaseStorage.getInstance()
            .getReference("images/${mAuth.currentUser?.uid.toString()}.jpeg").downloadUrl.addOnSuccessListener {
            mDbRef.child("user").child(mAuth.currentUser?.uid.toString())
                .child("imageUrl").setValue(it.toString()).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(
                            requireContext(),
                            "Upload image successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setLogout() {
        binding.btnLogout.setOnClickListener {
            viewModel.rememberMe(false)
            findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
        }
    }


    @SuppressLint("CheckResult")
    private fun findUser() {
        mDbRef.child("user").child(mAuth.currentUser!!.uid).get().addOnSuccessListener {
            if (it.exists()) {
                Glide.with(requireView()).load(it.child("imageUrl").value.toString()).into(binding.avatar)
                binding.txtUserName.setText(it.child("name").value.toString())
                binding.txtLocation.setText(it.child("email").value.toString())
                binding.txtDescription.setText(it.child("description").value.toString())
            }
        }
    }
}