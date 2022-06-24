package com.example.chattingapp.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.chattingapp.ChattingApp
import com.example.chattingapp.model.User
import com.example.chattingapp.R
import com.example.chattingapp.adapter.ListFriendAdapter
import com.example.chattingapp.adapter.UserAdapter
import com.example.chattingapp.databinding.FragmentHomeBinding
import com.example.chattingapp.databinding.UserLayoutBinding
import com.example.chattingapp.factory.HomeViewModelFactory
import com.example.chattingapp.viewModel.HomeViewModel
import com.example.chattingapp.viewModel.SharedViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HomeFragment : Fragment(), UserAdapter.OnItemClickListener,
    ListFriendAdapter.OnItemClickListener {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var userList: ArrayList<User>
    private lateinit var removeList: ArrayList<String>
    private lateinit var adapter: UserAdapter
    private lateinit var listFriendAdapter: ListFriendAdapter
    private lateinit var mDbRef: DatabaseReference
    private lateinit var viewModel: HomeViewModel
    private lateinit var listFriend: ArrayList<String>
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel =
            ViewModelProvider(this, HomeViewModelFactory(activity?.application as ChattingApp)).get(
                HomeViewModel::class.java
            )
        mAuth = FirebaseAuth.getInstance()
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        if (viewModel.isRememberMe()) {
            mAuth.signInWithEmailAndPassword(
                viewModel.getEmail().toString(),
                viewModel.getPassword().toString()
            )
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(requireContext(), "Login success", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Wrong email or password",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mDbRef = FirebaseDatabase.getInstance().getReference()
        userList = ArrayList()
        removeList = ArrayList()
        listFriend = ArrayList()
        (activity as AppCompatActivity).supportActionBar?.hide()
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        setUp()
        registerData()
        addListOfUser()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomNavigation()
        listFriend = loadListFriend(mAuth.currentUser?.uid.toString(), listFriend, "friendUid")
        removeList = loadListFriend(mAuth.currentUser?.uid.toString(), removeList, "removeUid")
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
                        ) && !removeList.contains(
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

    private fun loadListFriend(
        userUid: String,
        list: ArrayList<String>,
        typeOfList: String
    ): ArrayList<String> {
        if (typeOfList.equals("friendUid")) {
            removeList.removeAll(list)
        }
        mDbRef.child("user")
            .child(userUid)
            .child(typeOfList)
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

    override fun onAcceptClick(position: Int) {
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
                    var targetListFriend: ArrayList<String> = ArrayList()
                    targetListFriend =
                        loadListFriend(user.uid.toString(), targetListFriend, "friendUid")
                    targetListFriend.add(mAuth.currentUser?.uid.toString())
                    val targetList: List<String> = targetListFriend
                    mDbRef.child("user")
                        .child(user.uid.toString())
                        .child("friendUid")
                        .setValue(targetList)
                }
            }
    }

    override fun onCancelClick(position: Int) {
        val user = userList[position]
        removeList.add(user.uid.toString())
        val list: List<String> = removeList
        mDbRef.child("user")
            .child(mAuth.currentUser?.uid.toString())
            .child("removeUid")
            .setValue(list)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(
                        requireContext(),
                        "Remove ${user.name} successful",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun setUp() {
        adapter = UserAdapter(this)
        listFriendAdapter = ListFriendAdapter(this)
        val lm = LinearLayoutManager(context)
        binding.userRecycleView.layoutManager = lm
        binding.userRecycleView.adapter = adapter
        mDbRef.child("user").child(mAuth.currentUser!!.uid).get().addOnSuccessListener {
            if (it.exists()) {
                Glide.with(requireView()).load(it.child("imageUrl").value.toString())
                    .into(binding.imgProfile)
            }
        }
    }

    private fun addListOfFriend() {
        val currentUid = mAuth.currentUser?.uid!!
        var listFriend: ArrayList<String> = ArrayList()
        FirebaseDatabase
            .getInstance()
            .getReference("user")
            .child(currentUid)
            .child("friendUid")
            .get()
            .addOnSuccessListener {
                if (it.exists()) {
                    listFriend = it.value as ArrayList<String>
                }
            }
        mDbRef.child("user").addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (postSnapshot in snapshot.children) {
                    val currentUser = postSnapshot.getValue(User::class.java)
                    if (listFriend.contains(currentUser?.uid)) {
                        userList.add(currentUser!!)
                    }
                }
                listFriendAdapter.submitList(userList)
                listFriendAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun bottomNavigation() {
        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    binding.userRecycleView.adapter = adapter
                    addListOfUser()
                    true
                }
                R.id.list_friend -> {
                    binding.userRecycleView.adapter = listFriendAdapter
                    addListOfFriend()
                    true
                }
                else -> false
            }

        }
    }

    override fun onListFriendItemClick(position: Int) {
        sharedViewModel.sendData(userList[position])
        findNavController().navigate(R.id.action_homeFragment_to_chatFragment)
    }
}