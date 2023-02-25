package com.example.saurabhchatapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.saurabhchatapp.R
import com.example.saurabhchatapp.UserStatus

class StatusAdapter(val context: Context, val statusList : ArrayList<UserStatus>) : RecyclerView.Adapter<StatusAdapter.StatusViewHolder>() {
    class StatusViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_status,parent,false)
        return StatusViewHolder(view)
    }

    override fun onBindViewHolder(holder: StatusViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
       return statusList.size
    }
}
