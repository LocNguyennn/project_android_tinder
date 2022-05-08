package com.example.chattingapp

import android.app.Activity.RESULT_OK
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
import androidx.navigation.fragment.findNavController
import com.example.chattingapp.databinding.FragmentProfileBinding
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference
    private lateinit var imageUri : Uri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference()
//        imageUri = Uri.EMPTY
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        findUser()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setLogout()


        binding.btnUploadImage.setOnClickListener{
            selectImage()
        }
        binding.btnUploadFirebase.setOnClickListener{
            uploadImageToFirebase()
        }
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 100)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 100  && resultCode == RESULT_OK){
            imageUri = data?.data!!
            val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, imageUri)
            binding.firebaseImage.setImageBitmap(bitmap)
        }
    }

    private fun uploadImageToFirebase() {
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Uploading File...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        val storageReference = FirebaseStorage.getInstance().getReference("images/${mAuth.currentUser?.uid}.jpeg")
        storageReference.putFile(imageUri)
            .addOnSuccessListener{
                addImageToRealtimeDatabase()
                Toast.makeText(requireContext(),"Successful uploaded",Toast.LENGTH_SHORT).show()

                if(progressDialog.isShowing)
                    progressDialog.dismiss()
            }
            .addOnFailureListener{
                if(progressDialog.isShowing)
                    progressDialog.dismiss()
                Toast.makeText(requireContext(),"Failed to upload image",Toast.LENGTH_SHORT).show()
            }

    }

    private fun addImageToRealtimeDatabase() {
        FirebaseStorage.getInstance().getReference("images/${mAuth.currentUser?.uid}").downloadUrl.addOnSuccessListener {
            mDbRef.child("user").child(mAuth.currentUser?.uid.toString())
                .child("imageUrl").setValue(it.toString()).addOnCompleteListener{
                    if(it.isSuccessful){
                        Toast.makeText(requireContext(),"Upload image successfully",Toast.LENGTH_SHORT).show()
                    }
                }
        }.addOnFailureListener{
            Toast.makeText(requireContext(),"${it.message}",Toast.LENGTH_SHORT).show()
        }
    }

    private fun setLogout() {
        binding.btnLogout.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
        }
    }



    private fun findUser() {
        mDbRef.child("user").child(mAuth.currentUser!!.uid).get().addOnSuccessListener {
            if(it.exists()){
                binding.txtFullName.text = it.child("name").value.toString()
                binding.txtUserName.text = it.child("email").value.toString()
            }
        }
    }
}