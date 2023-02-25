package com.example.saurabhchatapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.saurabhchatapp.adapter.GroupMessageAdapter
import com.example.saurabhchatapp.adapter.MessageAdapter
import com.example.saurabhchatapp.databinding.ActivityGroupChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class GroupChat : AppCompatActivity() {
    private lateinit var binding : ActivityGroupChatBinding
    private lateinit var mDbRef: DatabaseReference
    private lateinit var messageAdapter: GroupMessageAdapter
    private lateinit var messageList: ArrayList<Message>
    lateinit var senderUid : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupChatBinding.inflate(layoutInflater)
        senderUid = FirebaseAuth.getInstance().uid.toString()
        mDbRef = FirebaseDatabase.getInstance().reference
        messageList = ArrayList()

        supportActionBar!!.title = "GroupChat"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this)
        messageAdapter = GroupMessageAdapter(this, messageList)
        binding.chatRecyclerView.adapter = messageAdapter


        mDbRef.child("Group").addValueEventListener(object : ValueEventListener {
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
        binding.sentButton.setOnClickListener {
            val message = binding.messageBox.text.toString()
            val messageObject = Message(message, senderUid!!)
            binding.messageBox.setText("")
            mDbRef.child("Group").push()
                .setValue(messageObject)
        }

        setContentView(binding.root)
    }
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}