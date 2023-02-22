package com.example.saurabhchatapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.saurabhchatapp.databinding.ActivitySetProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class SetProfileActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySetProfileBinding
    private lateinit var auth: FirebaseAuth
    private var mDbRef : FirebaseDatabase = FirebaseDatabase.getInstance()
    private var storage : FirebaseStorage = FirebaseStorage.getInstance()
    private lateinit var selectedImage : Uri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetProfileBinding.inflate(layoutInflater)

        auth = FirebaseAuth.getInstance()

        setContentView(binding.root)
        binding.profile.setOnClickListener {
            val intent = Intent()
            intent.setAction(Intent.ACTION_GET_CONTENT)
            intent.setType("image/*")
            startActivityForResult(intent,45)
        }
        binding.btnContinue.setOnClickListener {
            val name = binding.etName.text.toString()
            if(name.isEmpty()){
                binding.etName.error = "Please type your name"
            }
            if(selectedImage != null){
                val reference = storage.reference.child("Profile").child(auth.uid!!)
                reference.putFile(selectedImage).addOnCompleteListener{
                    if(it.isSuccessful){
                        reference.downloadUrl.addOnSuccessListener {
                            val imageUrl = it.toString()
                            val uid = auth.uid!!
                            val phone = auth.currentUser!!.phoneNumber.toString()
                            val name = binding.etName.text.toString()

                            val user = User(name,phone,imageUrl,uid)
                            mDbRef.getReference().child("Users").child(uid).setValue(user).addOnSuccessListener {
                                val intent = Intent(this,MainActivity::class.java)
                                startActivity(intent)
                            }
                        }
                    }
                }
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(data != null){
            if(data.data != null){
                binding.profile.setImageURI(data.data)
                selectedImage = data.data!!
            }
        }
    }
}