package com.example.chattingapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chattingapp.model.Message
import com.example.chattingapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import de.hdodenhof.circleimageview.CircleImageView

class MessageAdapter(val context: Context, val messageList: ArrayList<Message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ITEM_RECEIVE = 1;
    val ITEM_SENT = 2;

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 1) {
            // inflate receive
            val view : View = LayoutInflater.from(context).inflate(R.layout.receive,parent,false)
            return ReceiveViewHolder(view)
        } else {
            val view : View = LayoutInflater.from(context).inflate(R.layout.sent,parent,false)
            return SentViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList[position]
        if (holder.javaClass == SentViewHolder::class.java) {
            // do the stuff for sent view holder
            val viewHolder = holder as SentViewHolder
            FirebaseDatabase.getInstance().getReference().child("user").child(currentMessage.senderId.toString()).get().addOnSuccessListener {
                if(it.exists()){
                    Glide.with(holder.itemView).load(it.child("imageUrl").value.toString()).into(holder.image)
                }
            }
            holder.sentMessage.text = currentMessage.message
        } else {
            // do the stuff for receive view holder
            val viewHolder = holder as ReceiveViewHolder
            FirebaseDatabase.getInstance().getReference().child("user").child(currentMessage.receiverId.toString()).get().addOnSuccessListener {
                if(it.exists()){
                    Glide.with(holder.itemView).load(it.child("imageUrl").value.toString()).into(holder.image)
                }
            }
            holder.receiveMessage.text = currentMessage.message
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]
        if (FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderId)) {    // sender
            return ITEM_SENT
        } else {
            return ITEM_RECEIVE
        }
    }

    class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sentMessage = itemView.findViewById<TextView>(R.id.txtSentMessage)
        val image = itemView.findViewById<CircleImageView>(R.id.avatar)
//        val itemView = itemView
    }

    class ReceiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val receiveMessage = itemView.findViewById<TextView>(R.id.txtReceiveMessage)
        val image = itemView.findViewById<CircleImageView>(R.id.avatar)

    }


}