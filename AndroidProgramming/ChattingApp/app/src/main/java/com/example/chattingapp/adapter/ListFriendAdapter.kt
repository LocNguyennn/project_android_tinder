package com.example.chattingapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chattingapp.databinding.ListFriendItemBinding
import com.example.chattingapp.model.User
import com.example.chattingapp.databinding.UserLayoutBinding
import java.util.*

class ListFriendAdapter(val mListener: OnItemClickListener) :
    ListAdapter<User, ListFriendAdapter.UserVH>(
        UserDiffUtilCallback()
    ) {

    interface OnItemClickListener {
        fun onListFriendItemClick(position: Int)
    }

    class UserDiffUtilCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }

    class UserVH private constructor(
        var binding: ListFriendItemBinding,
        listener: OnItemClickListener
    ) :
        RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup, listener: OnItemClickListener): UserVH {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListFriendItemBinding.inflate(layoutInflater, parent, false)
                return UserVH(binding, listener)
            }
        }

        init {
            itemView.setOnClickListener {
                listener.onListFriendItemClick(adapterPosition)
            }
        }

        fun binding(item: User) {
            Glide.with(itemView).load(item.imageUrl.toString()).into(binding.avatar)
            binding.txtUserName.text = item.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserVH {
        return UserVH.from(parent, mListener)
    }

    override fun onBindViewHolder(holder: UserVH, position: Int) {
        val user = getItem(position)
        holder.binding(user)
    }

}