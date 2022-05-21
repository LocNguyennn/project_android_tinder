package com.example.chattingapp.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chattingapp.Model.User
import com.example.chattingapp.R
import com.example.chattingapp.adapter.UserAdapter
import com.example.chattingapp.databinding.FragmentHomeBinding
import com.example.chattingapp.viewModel.HomeViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HomeFragment : Fragment(), UserAdapter.OnItemClickListener {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var userList: ArrayList<User>
    private lateinit var adapter: UserAdapter
    private lateinit var mDbRef: DatabaseReference
    private lateinit var viewModel: HomeViewModel
    private lateinit var listFriend: ArrayList<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference()
        userList = ArrayList()
        listFriend = ArrayList()
        (activity as AppCompatActivity).supportActionBar?.hide()
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUp()
//        viewModel.loadData(userList)
        registerData()
        listFriend = loadListFriend(mAuth.currentUser?.uid.toString(),listFriend)
        addListOfUser()
        binding.btnListFriend.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_listFriendFragment)
        }
        binding.imgProfile.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
        }
    }


    private fun registerData() {
        viewModel.listOfData.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    private fun addListOfUser() {
        mDbRef.child("user").addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (postSnapshot in snapshot.children) {
                    val currentUser = postSnapshot.getValue(User::class.java)
                    if (mAuth.currentUser?.uid != currentUser?.uid && !listFriend.contains(
                            currentUser?.uid
                        )
                    ) {
                        userList.add(currentUser!!)
                    }
                }
                viewModel.loadData(userList)
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


    private fun loadListFriend(userUid: String, list : ArrayList<String>) : ArrayList<String>{
        mDbRef.child("user")
            .child(userUid)
            .child("friendUid")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (postSnapshot in snapshot.children) {
                            val stringUid = postSnapshot.getValue(String::class.java)
                            list.add(stringUid.toString())
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        return list
    }

    override fun onItemClick(position: Int) {
        val user = userList[position]
        Toast.makeText(requireContext(), "Match ${user.name} successful", Toast.LENGTH_SHORT).show()
        listFriend.add(user.uid.toString())
        val list: List<String> = listFriend
        mDbRef.child("user")
            .child(mAuth.currentUser?.uid.toString())
            .child("friendUid")
            .setValue(list)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    var targetListFriend : ArrayList<String> = ArrayList()
                    targetListFriend = loadListFriend(user.uid.toString(),targetListFriend)
                    targetListFriend.add(mAuth.currentUser?.uid.toString())
                    val targetList : List<String> = targetListFriend
                    mDbRef.child("user")
                        .child(userList[position].uid.toString())
                        .child("friendUid")
                        .setValue(targetList)
                }
            }
//        Toast.makeText(requireContext(), "Match ${userList[position].name} successful", Toast.LENGTH_SHORT).show()
    }

    private fun setUp() {
        adapter = UserAdapter(this)
        val lm = LinearLayoutManager(context)
        binding.userRecycleView.layoutManager = lm
        binding.userRecycleView.adapter = adapter
    }

}