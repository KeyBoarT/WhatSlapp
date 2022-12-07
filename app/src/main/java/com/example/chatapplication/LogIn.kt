package com.example.chatapplication

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.content.Intent
import android.content.SharedPreferences
import android.speech.tts.TextToSpeech
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class LogIn : AppCompatActivity() {

    //We must declare components globally that we use in our design, (also we can use 'Data Binding')
    //But, in this program we used traditional methods, because 'Data Binding' is a new technology,
    // so it might will not use in old versions.

    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnLogIn: Button
    private lateinit var textForgotPassword: TextView
    private lateinit var textSignUp: TextView
    //private lateinit var sharedPreferences: SharedPreferences

    //Firebase Autentication variable
    private lateinit var mAuth: FirebaseAuth

    //This method runs when the program starts
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        //With 'findViewById' method, assign the values of variables which is declared above
        edtEmail = findViewById(R.id.edt_email)
        edtPassword = findViewById(R.id.edt_password)
        btnLogIn = findViewById(R.id.btn_login)
        textForgotPassword = findViewById(R.id.text_forgotpassword)
        textSignUp = findViewById(R.id.text_signup)

        //Getting saved data before
        val sharedPreferences = getSharedPreferences("user" ,Context.MODE_PRIVATE)
        edtEmail.setText(sharedPreferences.getString("email", ""))
        edtPassword.setText(sharedPreferences.getString("password", ""))

        //Assign the mAuth varible, so we can reach Firebase with this variable
        mAuth = FirebaseAuth.getInstance()

        //for masking password chars
        edtPassword.transformationMethod = PasswordTransformationMethod.getInstance()

        //SignUp Button's content
        textSignUp.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }

        //Forgot password's content
        textForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPassword::class.java)
            startActivity(intent)
        }

        btnLogIn.setOnClickListener{
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()

            login(email, password)
        }
    }

    private fun login(email: String, password: String) {

        if (email.isBlank()){
            Toast.makeText(this@LogIn, R.string.email_field_cannot_be_left_blank, Toast.LENGTH_SHORT).show()
            return
        }

        if (password.isBlank()){
            Toast.makeText(this@LogIn, R.string.password_field_cannot_be_left_blank, Toast.LENGTH_SHORT).show()
            return
        }
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    //Code for saving user to later logins
                    val sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE)
                    val edit = sharedPreferences.edit()
                    edit.putString("email", edtEmail.text.toString())
                    edit.putString("password", edtPassword.text.toString())
                    edit.apply()

                    //Code for jumping to homePage
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    finish()
                    startActivity(intent)
                } else {
                    Toast.makeText(applicationContext, "User does not exist !", Toast.LENGTH_SHORT).show()
                }
            }
    }
}