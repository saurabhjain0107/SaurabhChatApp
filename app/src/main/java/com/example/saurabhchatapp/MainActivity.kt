package com.example.saurabhchatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Adapter
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.saurabhchatapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private var mDbRef : FirebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userList : ArrayList<User>
    private lateinit var adapter: UserAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()

        userList = ArrayList()
        adapter = UserAdapter(this,userList)
        userRecyclerView = binding.userRecyclerview
        userRecyclerView.adapter = adapter
        mDbRef.reference.child("Users").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for(postsnapShot in snapshot.children){
                    val currentUser = postsnapShot.getValue(User::class.java)
                    if(auth.currentUser!!.uid != currentUser!!.id){
                        userList.add(currentUser!!)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        setContentView(binding.root)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.topmenu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.search){
            Toast.makeText(this,"search click", Toast.LENGTH_SHORT).show()
        }
        if(item.itemId == R.id.logout){
            auth.signOut()
            val intent = Intent(this,PhoneNumberActivity::class.java)
            startActivity(intent)
        }
//        if(item.itemId == R.id.edtProfile){
//            val intent = Intent(this,SetProfileActivity::class.java)
//            startActivity(intent)
//        }
        return super.onOptionsItemSelected(item)
    }
}