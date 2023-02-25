package com.example.saurabhchatapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.saurabhchatapp.Message
import com.example.saurabhchatapp.R
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter(val context: Context, val messageList: ArrayList<Message>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val ITEM_RECEIVE = 1
    val ITEM_SENT = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == 1){
            val view : View = LayoutInflater.from(context).inflate(R.layout.receive_message,parent,false)
            return ReceiveViewHolder(view)
        }else{
            val view : View = LayoutInflater.from(context).inflate(R.layout.send_message,parent,false)
            return SentViewHolder(view)

        }
    }

    class SentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val sentMessage = view.findViewById<TextView>(R.id.txt_sent_message)
        val image = view.findViewById<ImageView>(R.id.image)


    }

    class ReceiveViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val receiveMessage = view.findViewById<TextView>(R.id.txt_receive_message)
        val rimage = view.findViewById<ImageView>(R.id.rimage)

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val currentMessage = messageList[position]
        if(holder.javaClass == SentViewHolder::class.java){
            //do stuff for sent view holder

            val viewHolder  = holder as SentViewHolder
            if(currentMessage.message.equals("photo")){
                holder.image.visibility = View.VISIBLE
                holder.sentMessage.visibility = View.GONE
                Glide.with(context).load(currentMessage.image).into(holder.image)
            }
            holder.sentMessage.text = currentMessage.message

        }else{
            //do stuff for Receive view holder

            val viewHolder  = holder as ReceiveViewHolder
            if(currentMessage.message.equals("photo")){
                holder.rimage.visibility = View.VISIBLE
                holder.receiveMessage.visibility = View.GONE
                Glide.with(context).load(currentMessage.image).into(holder.rimage)
            }
            holder.receiveMessage.text = currentMessage.message
        }

    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]
        if(FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderId)){
            return ITEM_SENT
        }else{
            return ITEM_RECEIVE
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }
}