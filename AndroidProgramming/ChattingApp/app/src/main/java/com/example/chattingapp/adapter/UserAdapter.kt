package com.example.chattingapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.chattingapp.Model.User
import com.example.chattingapp.databinding.UserLayoutBinding

class UserAdapter(val mListener: OnItemClickListener) : ListAdapter<User, UserAdapter.UserVH>(
    UserDiffUtilCallback()
) {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
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
        var binding: UserLayoutBinding,
        listener: OnItemClickListener
    ) :
        RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup, listener: OnItemClickListener): UserVH {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = UserLayoutBinding.inflate(layoutInflater, parent, false)
                return UserVH(binding, listener)
            }
        }

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }

        fun binding(item: User) {
            binding.name.text = item.name.toString()
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