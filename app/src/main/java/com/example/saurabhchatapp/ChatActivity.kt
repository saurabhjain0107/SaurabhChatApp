package com.example.saurabhchatapp

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.saurabhchatapp.adapter.MessageAdapter
import com.example.saurabhchatapp.databinding.ActivityChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import kotlin.collections.ArrayList


class ChatActivity : AppCompatActivity() {
    private lateinit var binding : ActivityChatBinding
    private lateinit var mDbRef: DatabaseReference
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var chatRecyclerView: RecyclerView
    private var storage : FirebaseStorage = FirebaseStorage.getInstance()
    lateinit var dialog : ProgressDialog
    var senderUid = FirebaseAuth.getInstance().currentUser?.uid
    var receiverRoom: String? = null
    var senderRoom: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        mDbRef = FirebaseDatabase.getInstance().reference

        val name = intent.getStringExtra("name")
        val receiverUid = intent.getStringExtra("uid")
//        senderUid = FirebaseAuth.getInstance().currentUser?.uid
        dialog = ProgressDialog(this)
        dialog.setMessage("Uploading image...")
        dialog.setCancelable(false)

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
        binding.attachment.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = ("image/*")
            startActivityForResult(intent,25)
        }

        setContentView(binding.root)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 25){
            if(data!!.data != null){
                val selectedImage  = data.data!!
                val calender = Calendar.getInstance()
                val reference = storage.reference.child("chats").child(calender.timeInMillis.toString())
                dialog.show()
                reference.putFile(selectedImage).addOnCompleteListener{
                    dialog.dismiss()
                    if(it.isSuccessful){
                        reference.downloadUrl.addOnSuccessListener {
                            val filePath = it.toString()
                            val message = binding.messageBox.text.toString()
                            val messageObject = Message(message,senderUid!!)
                            messageObject.message = "photo"
                            messageObject.image = filePath
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
                    }
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}