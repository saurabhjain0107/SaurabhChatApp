package com.example.saurabhchatapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.saurabhchatapp.databinding.ActivityChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList


class ChatActivity : AppCompatActivity() {
    private lateinit var binding : ActivityChatBinding
    private lateinit var mDbRef: DatabaseReference
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var chatRecyclerView: RecyclerView
    var receiverRoom: String? = null
    var senderRoom: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        mDbRef = FirebaseDatabase.getInstance().reference

        val name = intent.getStringExtra("name")
        val receiverUid = intent.getStringExtra("uid")
        val senderUid = FirebaseAuth.getInstance().currentUser?.uid

        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid


        supportActionBar!!.title = name
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        messageList = ArrayList()
        chatRecyclerView = binding.chatRecyclerView
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        messageAdapter = MessageAdapter(this, messageList)
        chatRecyclerView.adapter = messageAdapter

        mDbRef.child("Chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    messageList.clear()
                    for (postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(Message::class.java)
                        messageList.add(message!!)
                    }
                    messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        //send message to data base
        binding.sentButton.setOnClickListener {

            val message = binding.messageBox.text.toString()
            val messageObject = Message(message,senderUid!!)
            mDbRef.child("Chats").child(senderRoom!!).child("messages").push()
                .setValue(messageObject).addOnCompleteListener {
                    mDbRef.child("Chats").child(receiverRoom!!).child("messages").push()
                        .setValue(messageObject)
                }
            var lastmsg : HashMap<String, Any> = HashMap<String, Any> ()
            lastmsg.put("lastmsg",messageObject.message)
            mDbRef.child("Chats").child(senderRoom!!).updateChildren(lastmsg)
            mDbRef.child("Chats").child(receiverRoom!!).updateChildren(lastmsg)


            binding.messageBox.setText("")
        }

        setContentView(binding.root)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}