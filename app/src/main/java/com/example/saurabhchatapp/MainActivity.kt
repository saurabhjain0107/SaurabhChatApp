package com.example.saurabhchatapp

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.saurabhchatapp.adapter.StatusAdapter
import com.example.saurabhchatapp.adapter.UserAdapter
import com.example.saurabhchatapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import java.util.Locale.filter
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private var mDbRef : FirebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var statusRecyclerView: RecyclerView
    private lateinit var userList : ArrayList<User>
    private lateinit var statusList : ArrayList<UserStatus>
    private lateinit var adapter: UserAdapter
    private lateinit var statusAdapter: StatusAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()

        userList = ArrayList()
        statusList = ArrayList()
        adapter = UserAdapter(this,userList)
        statusAdapter = StatusAdapter(this,statusList)
        userRecyclerView = binding.userRecyclerview
        statusRecyclerView = binding.statusList
        val layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false)
        layoutManager.orientation
        binding.statusList.layoutManager = layoutManager
        statusRecyclerView.adapter = statusAdapter
        userRecyclerView.adapter = adapter

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
              R.id.status ->{
                  val intent = Intent()
                  intent.setType("image/*")
                  intent.setAction(Intent.ACTION_GET_CONTENT)
                  startActivityForResult(intent,75)
                 true
              }
              else ->{
                   false
              }
            }
        }


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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(data != null){
            if(data.data != null){
                val storage = FirebaseStorage.getInstance()

                val reference = storage.reference.child("status")
                reference.putFile(data.data!!).addOnCompleteListener{
                    if(it.isSuccessful){
                        reference.downloadUrl.addOnSuccessListener {

                        }
                    }
                }
            }
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.topmenu,menu)
        val item = menu.findItem(R.id.search)
        val searchView = item.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Toast.makeText(this@MainActivity, "You clicked on search button.", Toast.LENGTH_SHORT).show()
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
//
//          keText(this, "You clicked on search button text change.", Toast.LENGTH_SHORT).show()

                adapter.filter.filter(newText)
                var tempArr = ArrayList<User>()

                for (arr in userList) {
                    if (arr.firstName!!.toLowerCase(Locale.getDefault())
                            .contains(newText.toString())) {
                        tempArr.add(arr)
                    }
                }
                userRecyclerView.adapter = adapter
                return true
//                Toast.makeText(this@MainActivity, "search click", Toast.LENGTH_SHORT).show()
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.search) {
            val searchView = item.actionView as SearchView

        }
        if(item.itemId == R.id.logout){
            auth.signOut()
            val intent = Intent(this,PhoneNumberActivity::class.java)
            startActivity(intent)
        }
        if(item.itemId == R.id.group){
            startActivity(Intent(this,GroupChat::class.java))
        }
//        if(item.itemId == R.id.edtProfile){
//            val intent = Intent(this,SetProfileActivity::class.java)
//            startActivity(intent)
//        }
        return super.onOptionsItemSelected(item)
    }
        }



