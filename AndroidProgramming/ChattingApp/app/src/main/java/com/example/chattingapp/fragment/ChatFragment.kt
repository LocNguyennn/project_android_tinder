package com.example.chattingapp.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chattingapp.Model.Message
import com.example.chattingapp.adapter.MessageAdapter
import com.example.chattingapp.databinding.FragmentChatBinding
import com.example.chattingapp.viewModel.SharedViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChatFragment : Fragment() {
    private lateinit var binding: FragmentChatBinding
    private lateinit var adapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var mDbRef: DatabaseReference
    private lateinit var sharedViewModel: SharedViewModel
    private val senderUid = FirebaseAuth.getInstance().currentUser?.uid.toString()
    private lateinit var receiverUid: String
    private lateinit var receiverRoom : String
    private lateinit var senderRoom : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Log.e("chatFragment", "onCreateView()")

//        (activity as AppCompatActivity).supportActionBar?.show()
//        (activity as AppCompatActivity).supportActionBar?.title = name.toString()

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        mDbRef = FirebaseDatabase.getInstance().getReference()
        messageList = ArrayList()
        binding = FragmentChatBinding.inflate(inflater, container, false)
        adapter = MessageAdapter(requireContext(), messageList)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        receiverUid = sharedViewModel.receiveData().uid.toString()
        receiverRoom = receiverUid + senderUid
        senderRoom = senderUid + receiverUid
        setUp()
        loadChat()
        sendChat()

    }

    private fun sendChat() {
//        Log.e("senderUid", senderUid.toString())
//        Log.e("receiverUid", receiverUid.toString())
        binding.btnSend.setOnClickListener {
            // adding the message to db
            val message = binding.messageBox.text.toString()
            val messageObject = Message(message, senderUid.toString())
            mDbRef.child("chats").child(senderRoom!!).child("messages").push()
                .setValue(messageObject).addOnSuccessListener {
                    mDbRef.child("chats").child(receiverRoom!!).child("messages").push()
                        .setValue(messageObject)
                    binding.messageBox.setText("")
                }
        }
    }

    private fun setUp(){
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.chatRecyclerView.adapter = adapter
    }
    private fun loadChat(){
        //logic for adding data to recyclerview
        mDbRef.child("chats").child(senderRoom).child("messages").addValueEventListener(object: ValueEventListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                messageList.clear()
                for(postSnapshot in snapshot.children){
                    val message = postSnapshot.getValue(Message::class.java)
                    messageList.add(message!!)
                }
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}