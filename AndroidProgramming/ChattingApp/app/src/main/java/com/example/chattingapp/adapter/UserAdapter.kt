package com.example.chattingapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chattingapp.R
import com.example.chattingapp.model.User
import com.example.chattingapp.databinding.UserLayoutBinding
import java.util.*

class UserAdapter(val mListener: OnItemClickListener) : ListAdapter<User, UserAdapter.UserVH>(
    UserDiffUtilCallback()
) {

    interface OnItemClickListener {
        fun onAcceptClick(position: Int)
        fun onCancelClick(position: Int)
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
            itemView.findViewById<AppCompatButton>(R.id.btnAccept).setOnClickListener {
                listener.onAcceptClick(adapterPosition)
            }
            itemView.findViewById<AppCompatButton>(R.id.btnCancel).setOnClickListener {
                listener.onCancelClick(adapterPosition)
            }
        }

        fun binding(item: User) {
            val name = "${item.name.toString()} , ${Calendar.getInstance().get(Calendar.YEAR) - item.birthDay?.substring(item.birthDay!!.length -4)!!
                .toInt()}"
            binding.name.text = name
            binding.job.text = item.job
            Glide.with(itemView).load(item.imageUrl.toString()).into(binding.avatar)

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