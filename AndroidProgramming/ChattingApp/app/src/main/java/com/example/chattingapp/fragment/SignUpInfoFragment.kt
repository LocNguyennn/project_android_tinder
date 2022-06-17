package com.example.chattingapp.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
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
import com.example.chattingapp.R
import com.example.chattingapp.databinding.FragmentSignUpInfoBinding
import com.example.chattingapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.text.SimpleDateFormat
import java.util.*

class SignUpInfoFragment : Fragment() {
    private lateinit var binding: FragmentSignUpInfoBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private lateinit var imageUri: Uri
    private var gender: String = ""
    private var birthDay: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mAuth = FirebaseAuth.getInstance()
        (activity as AppCompatActivity).supportActionBar?.hide()
        binding = FragmentSignUpInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chooseBirthDay()
        chooseGender()
        uploadImage()
        addToDatabase()
    }

    @SuppressLint("SimpleDateFormat")
    private fun addToDatabase() {
        binding.btnUploadFirebase.setOnClickListener {
            if (!"".equals(gender) && !"".equals(birthDay) && imageUri != null) {
                mDbRef = FirebaseDatabase.getInstance().reference
                // save details to database
                mDbRef.child("user").child(mAuth.currentUser?.uid.toString()).child("birthDay")
                    .setValue(birthDay)
                mDbRef.child("user").child(mAuth.currentUser?.uid.toString()).child("gender")
                    .setValue(gender)
                uploadImageToFirebase()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Please fill all the information on screen",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun uploadImage() {
        binding.btnUploadImage.setOnClickListener {
            chooseImage()
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

    private fun chooseGender() {
        binding.apply {
            cbMale.setOnClickListener {
                if (cbMale.isChecked) {
                    gender = "Male"
                    cbFemale.isEnabled = false
                } else {
                    cbFemale.isEnabled = true
                }
            }
            cbFemale.setOnClickListener {
                if (cbFemale.isChecked) {
                    gender = "Female"
                    cbMale.isEnabled = false
                } else {
                    cbMale.isEnabled = true
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun chooseBirthDay() {
        val calender = Calendar.getInstance()
        val year = calender.get(Calendar.YEAR)
        val month = calender.get(Calendar.MONTH)
        val day = calender.get(Calendar.DAY_OF_MONTH)

        // button click to show DatePickerDialog
        binding.btnPickDate.setOnClickListener {
            val dpd = DatePickerDialog(
                requireContext(),
                DatePickerDialog.OnDateSetListener { view, mYear, mMonth, mDay ->
                    // set to textview
                    birthDay = "${mDay}/${mMonth + 1}/${mYear}"
                    binding.txtBirthDay.text = birthDay
                }, year, month, day
            )
            //show dialog
            dpd.show()
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
                if (progressDialog.isShowing)
                    progressDialog.dismiss()
            }
            .addOnFailureListener {
                if (progressDialog.isShowing)
                    progressDialog.dismiss()
            }
    }

    private fun addImageToRealtimeDatabase() {
        FirebaseStorage.getInstance()
            .getReference("images/${mAuth.currentUser?.uid.toString()}.jpeg").downloadUrl.addOnSuccessListener {
                mDbRef.child("user").child(mAuth.currentUser?.uid.toString())
                    .child("imageUrl").setValue(it.toString()).addOnCompleteListener {
                        if (it.isSuccessful) {
                            findNavController().navigate(R.id.action_signUpInfoFragment_to_signUpInfo2Fragment)
                        }
                    }
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}