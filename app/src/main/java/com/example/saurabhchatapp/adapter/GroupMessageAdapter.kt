package com.example.saurabhchatapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.saurabhchatapp.Message
import com.example.saurabhchatapp.R
import com.example.saurabhchatapp.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class GroupMessageAdapter(val context: Context, val messageList: ArrayList<Message>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val ITEM_RECEIVE = 1
    val ITEM_SENT = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == 1){
            val view : View = LayoutInflater.from(context).inflate(R.layout.receive_grmessage,parent,false)
            return ReceiveViewHolder(view)
        }else{
            val view : View = LayoutInflater.from(context).inflate(R.layout.send_grmessage,parent,false)
            return SentViewHolder(view)

        }
    }

    class SentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val sentMessage = view.findViewById<TextView>(R.id.txt_sent_message)
        val sname = view.findViewById<TextView>(R.id.name)

    }

    class ReceiveViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val receiveMessage = view.findViewById<TextView>(R.id.txt_receive_message)
        val rname = view.findViewById<TextView>(R.id.txt_name)

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val currentMessage = messageList[position]
        if(holder.javaClass == SentViewHolder::class.java){
            //do stuff for sent view holder
            val viewHolder  = holder as SentViewHolder
            holder.sentMessage.text = currentMessage.message
            FirebaseDatabase.getInstance().reference.child("User").child(currentMessage.senderId)
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            val user = snapshot.getValue(User::class.java)
                            holder.sname.text = user!!.firstName
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })

        }
        else {
            //do stuff for Receive view holder

            val viewHolder  = holder as ReceiveViewHolder
            holder.receiveMessage.text = currentMessage.message
            FirebaseDatabase.getInstance().reference.child("User").child(currentMessage.senderId)
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            val user = snapshot.getValue(User::class.java)
                            holder.rname.text = user!!.firstName
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
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
