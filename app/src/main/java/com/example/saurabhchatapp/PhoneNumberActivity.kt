package com.example.saurabhchatapp

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.saurabhchatapp.databinding.ActivityPhoneNumberBinding
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

class PhoneNumberActivity : AppCompatActivity() {
    private lateinit var binding :ActivityPhoneNumberBinding
    lateinit var callbacks : PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhoneNumberBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        if(auth.currentUser != null){
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        setContentView(binding.root)
        
        binding.btnSend.setOnClickListener { 
            val intent = Intent(this,OtpVerifyActivity::class.java)
            intent.putExtra("phoneNumber",binding.etPhone.text.toString())
            startActivity(intent)
        }


        binding.btnSend.setOnClickListener {
            if (binding.etPhone.text.toString().trim().isEmpty()) {
                Toast.makeText(this, "Invalid Phone Number", Toast.LENGTH_SHORT).show()
            } else if (binding.etPhone.text.toString().trim().length != 10) {
                Toast.makeText(this, "Type valid Phone Number", Toast.LENGTH_SHORT).show()
            }else{
                otpSend()
            }
        }
    }

    private fun otpSend() {


        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d(ContentValues.TAG, "onVerificationCompleted:$credential")
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {

                Log.w(ContentValues.TAG, "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                }

                // Show a message and update the UI
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d(ContentValues.TAG, "onCodeSent:$verificationId")

                val intent = Intent(this@PhoneNumberActivity,OtpVerifyActivity::class.java)
                intent.putExtra("phone",binding.etPhone.text.toString().trim())
                intent.putExtra("verificationId",verificationId)
                startActivity(intent)
                // Save verification ID and resending token so we can use them later
//                storedVerificationId = verificationId
//                resendToken = token
            }
        }

        val options = PhoneAuthOptions.Builder(auth)
            .setPhoneNumber("+91" + binding.etPhone.text.toString().trim())       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {

        auth.signInWithCredential(credential)
            .addOnCompleteListener{ task ->
                if (task.isSuccessful) {


                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(ContentValues.TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }

    }
}