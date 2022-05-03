package com.example.chattingapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chattingapp.databinding.FragmentListFriendBinding
import com.example.chattingapp.viewModel.ListFriendViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ListFriendFragment : Fragment(), UserAdapter.OnItemClickListener {
    private lateinit var binding: FragmentListFriendBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var userList: ArrayList<User>
    private lateinit var adapter: UserAdapter
    private lateinit var mDbRef: DatabaseReference
    private lateinit var viewModel: ListFriendViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(ListFriendViewModel::class.java)
        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference()
        userList = ArrayList()
        (activity as AppCompatActivity).supportActionBar?.hide()
        binding = FragmentListFriendBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUp()
        registerData()
        addListOfFriend()
    }

    override fun onStart() {
        super.onStart()
        viewModel.loadData(userList)
    }

    private fun registerData() {
        viewModel.listOfData.observe(viewLifecycleOwner) {
            adapter.submitList(it)
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
                adapter.submitList(userList)
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun setUp() {
        adapter = UserAdapter(this)
        val lm = LinearLayoutManager(context)
        binding.friendRecycleView.layoutManager = lm
        binding.friendRecycleView.adapter = adapter
    }

    override fun onItemClick(position: Int) {
        TODO("Not yet implemented")
    }

}