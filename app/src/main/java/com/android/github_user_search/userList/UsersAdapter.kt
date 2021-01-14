package com.android.github_user_search.userList

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.github_user_search.R
import com.android.github_user_search.data.User
import com.bumptech.glide.Glide

private const val TAG = "UserAdapter"

class UsersAdapter(context: Context) : ListAdapter<User, UsersAdapter.UserViewHolder>(UserDiffCallback) {
    private val context: Context = context
    var position: Int = 0

    class UserViewHolder(context: Context, itemView: View) :
            RecyclerView.ViewHolder(itemView) {
        private val userImageView: ImageView = itemView.findViewById(R.id.user_image)
        private val userNameTextView: TextView = itemView.findViewById(R.id.user_text)
        private val context: Context = context
        private var currentUser: User? = null

        fun bind(user: User) {
            currentUser = user
            userNameTextView.text = user.name
            Glide.with(context)
                    .load(user.image)
                    .into(userImageView);
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.user_item, parent, false)
        return UserViewHolder(context,view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        this.position = position
        val user = getItem(position)
        holder.bind(user)
    }

}

object UserDiffCallback : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        //return oldItem.id == newItem.id
        return oldItem == newItem
    }

}