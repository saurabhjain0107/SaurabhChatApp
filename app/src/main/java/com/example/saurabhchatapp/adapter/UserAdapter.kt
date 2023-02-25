package com.example.saurabhchatapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.saurabhchatapp.ChatActivity
import com.example.saurabhchatapp.R
import com.example.saurabhchatapp.User

class UserAdapter(val context : Context, val userList: ArrayList<User>) : RecyclerView.Adapter<UserAdapter.UserViewHolder>(),Filterable {
  val userListAll = userList
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
            val intent = Intent(context, ChatActivity::class.java)
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

    override fun getFilter(): Filter {
        return filteration
    }

    val filteration: Filter = object : Filter() {
        // Runs on a background thread.
        override fun performFiltering(charSequence: CharSequence?): FilterResults {
            val filteredUser = mutableListOf<User>()

            if (charSequence.toString().isEmpty())
                filteredUser.addAll(userListAll)
            else {
                userListAll.forEach {
                    val name = it.firstName
                    if (name.lowercase().contains(charSequence.toString().lowercase()))
                        filteredUser.add(it)
                }
            }
            val filterResults = FilterResults()
            filterResults.values = filteredUser
            return filterResults
        }

        // Runs on a UI thread.
        @SuppressLint("NotifyDataSetChanged")
        override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults?) {
            userList.clear()
            userList.addAll(filterResults!!.values as Collection<User>)
            notifyDataSetChanged()
        }
    }

}