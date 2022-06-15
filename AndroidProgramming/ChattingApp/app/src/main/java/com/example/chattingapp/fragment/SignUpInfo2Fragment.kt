package com.example.chattingapp.fragment

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.chattingapp.R
import com.example.chattingapp.databinding.FragmentSignUpInfo2Binding
import com.example.chattingapp.databinding.FragmentSignUpInfoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView

class SignUpInfo2Fragment : Fragment() {
    private lateinit var binding: FragmentSignUpInfo2Binding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private lateinit var imageUri: Uri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mAuth = FirebaseAuth.getInstance()
        (activity as AppCompatActivity).supportActionBar?.hide()
        binding = FragmentSignUpInfo2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mDbRef = FirebaseDatabase.getInstance().reference
        uploadImage()
        loadData()
        submitData()

    }

    private fun loadData() {
        mDbRef.child("user").child(mAuth.currentUser!!.uid).get().addOnSuccessListener {
            if (it.exists()) {
                Glide.with(requireView()).load(it.child("imageUrl").value.toString())
                    .into(binding.avatar)
            }
        }
    }

    private fun submitData() {
        binding.btnUploadFirebase.setOnClickListener {
            if (!"".equals(binding.txtJob.text.toString()) && !"".equals(binding.txtAboutMe.text.toString())) {
                mDbRef.child("user").child(mAuth.currentUser?.uid.toString()).child("job")
                    .setValue(binding.txtJob.text.toString())
                mDbRef.child("user").child(mAuth.currentUser?.uid.toString()).child("description")
                    .setValue(binding.txtAboutMe.text.toString())
                findNavController().navigate(R.id.action_signUpInfo2Fragment_to_homeFragment)
            }
        }

    }

    private fun uploadImage() {
        binding.btnUploadImage.setOnClickListener {
            chooseImage()
            uploadImageToFirebase()
        }
    }

    private fun chooseImage() {
        CropImage
            .activity()
            .setGuidelines(CropImageView.Guidelines.ON)
            .start(requireContext(), this)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                imageUri = result.uri
                val bitmap =
                    MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, imageUri)
                binding.avatar.setImageBitmap(bitmap)
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
            FirebaseStorage.getInstance()
                .getReference("images/${mAuth.currentUser?.uid}.jpeg")
        storageReference.putFile(imageUri)
            .addOnSuccessListener {
                addImageToRealtimeDatabase()
                Toast.makeText(requireContext(), "Successful uploaded", Toast.LENGTH_SHORT)
                    .show()

                if (progressDialog.isShowing)
                    progressDialog.dismiss()
            }
            .addOnFailureListener {
                if (progressDialog.isShowing)
                    progressDialog.dismiss()
                Toast.makeText(
                    requireContext(),
                    "Failed to upload image",
                    Toast.LENGTH_SHORT
                )
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
}