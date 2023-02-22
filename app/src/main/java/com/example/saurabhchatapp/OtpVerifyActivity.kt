package com.example.saurabhchatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.example.saurabhchatapp.databinding.ActivityOtpVerifyBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider

class OtpVerifyActivity : AppCompatActivity() {
    private lateinit var binding : ActivityOtpVerifyBinding
    lateinit var callbacks : PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var auth: FirebaseAuth
    private lateinit var verificationId : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpVerifyBinding.inflate(layoutInflater)

        auth = FirebaseAuth.getInstance()

        setContentView(binding.root)
        editTextInput()

        val phoneNumber = intent.getStringExtra("phoneNumber")
        binding.tvMobile.text = phoneNumber

        verificationId = intent.getStringExtra("verificationId").toString()
        binding.tvResendBtn.setOnClickListener {
            Toast.makeText(this, "Otp send Successfully", Toast.LENGTH_SHORT).show()
        }
        binding.btnVerify.setOnClickListener {
            if (binding.etC1.text.toString().trim().isEmpty() ||
                binding.etC2.text.toString().trim().isEmpty() ||
                binding.etC3.text.toString().trim().isEmpty() ||
                binding.etC4.text.toString().trim().isEmpty() ||
                binding.etC5.text.toString().trim().isEmpty() ||
                binding.etC6.text.toString().trim().isEmpty()
            ) {
                Toast.makeText(this, "Otp is not valid", Toast.LENGTH_SHORT).show()
            } else {
                if (verificationId != null) {
                    var code = binding.etC1.text.toString().trim() +
                            binding.etC2.text.toString().trim() +
                            binding.etC3.text.toString().trim() +
                            binding.etC4.text.toString().trim() +
                            binding.etC5.text.toString().trim() +
                            binding.etC6.text.toString().trim()

                    val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
                    FirebaseAuth.getInstance()
                        .signInWithCredential(credential)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(
                                   this,
                                    "Otp is  Verify",
                                    Toast.LENGTH_SHORT
                                ).show()
                              val intent = Intent(this,SetProfileActivity::class.java)
                                startActivity(intent)
                            } else {
                                Toast.makeText(
                                    this,
                                    "Otp is not valid",
                                    Toast.LENGTH_SHORT
                                ).show()

                            }
                        }

                }
            }
        }

    }

    private fun editTextInput() {
        binding.etC1.addTextChangedListener{
            binding.etC2.requestFocus()
        }
        binding.etC2.addTextChangedListener{
            binding.etC3.requestFocus()
        }
        binding.etC3.addTextChangedListener{
            binding.etC4.requestFocus()
        }
        binding.etC4.addTextChangedListener{
            binding.etC5.requestFocus()
        }
        binding.etC5.addTextChangedListener{
            binding.etC6.requestFocus()
        }
    }
}