package com.example.chatapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ForgotPassword : AppCompatActivity() {

    private lateinit var btnSubmit: Button
    private lateinit var edtEmail: TextView
    private lateinit var textSignUp: TextView
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        //As always, we must assign the variables that are declared above
        btnSubmit = findViewById(R.id.btn_submit)
        edtEmail = findViewById<EditText>(R.id.edt_email)
        textSignUp = findViewById<EditText>(R.id.text_signup)
        mAuth = FirebaseAuth.getInstance()

        //When user click on the submit button, the method starts named 'sendEmail'
        btnSubmit.setOnClickListener {
            sendEmail(edtEmail.text.toString())
            val intent = Intent(this, LogIn::class.java)
            finish()
            startActivity(intent)
        }

        textSignUp.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            finish()
            startActivity(intent)
        }
    }


    //Let's write a method ,for send the password reseting email
    private fun sendEmail(email: String){
        if (email.isEmpty()){
            edtEmail.setError(getString(R.string.empty_field_error))
        }
        else{
            Firebase.auth.sendPasswordResetEmail(email).addOnCompleteListener{
                    task ->
                if (task.isSuccessful){
                    Toast.makeText(this@ForgotPassword, R.string.email_sent, Toast.LENGTH_SHORT).show()
                }
                else
                {
                    Toast.makeText(this@ForgotPassword, R.string.an_error_occured, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}