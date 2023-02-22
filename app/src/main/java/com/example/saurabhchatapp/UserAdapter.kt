package com.example.saurabhchatapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class UserAdapter(val context : Context, val userList: ArrayList<User>) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName = itemView.findViewById<TextView>(R.id.name)
        val lastmsg = itemView.findViewById<TextView>(R.id.lastMsg)
        val image = itemView.findViewById<ImageView>(R.id.userProfile)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.home_card,parent,false)
        return UserViewHolder(view)
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
     val currentUser = userList[position]
        holder.itemView.setOnClickListener {
            val intent = Intent(context,ChatActivity::class.java)
            intent.putExtra("name",currentUser.firstName)
            intent.putExtra("uid",currentUser.id)
            context.startActivity(intent)
        }
        holder.userName.text = currentUser.firstName
        Glide.with(context).load(currentUser.image).into(holder.image)
    }

    override fun getItemCount(): Int {
      return userList.size
    }
}