package com.example.chatapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.snapshots

class Profile : AppCompatActivity() {
    //Declaring the firebase authentication for use later
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDfRef: DatabaseReference

    //Let's declare the views on the profile page
    private lateinit var edt_email: EditText
    private lateinit var edt_name: EditText
    private lateinit var btn_update: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        mAuth = FirebaseAuth.getInstance()
        mDfRef = FirebaseDatabase.getInstance().getReference().child("user")

        //Get infos from currentUser to add to edittexts
        val currentUser = mAuth.currentUser

        //Assigning the views which are above
        edt_email = findViewById(R.id.edt_email)
        edt_name = findViewById(R.id.edt_name)
        btn_update = findViewById(R.id.btn_update)

        getData()

        btn_update.setOnClickListener {
            updateData(edt_name.text.toString(), edt_email.text.toString())
        }
    }

    private fun getData(){
        val currentUser = mAuth.currentUser
        mDfRef.child(currentUser!!.uid).child("name").get().addOnSuccessListener {
            if(it.exists()){
                edt_name.setText(it.value.toString())
            }
        }
        edt_email.setText(currentUser.email)
    }

    private fun updateData(name: String?, email: String?) {
        val currentUser = mAuth.currentUser
        if (name.isNullOrEmpty()) {
            Toast.makeText(this@Profile, R.string.name_field_cannot_be_left_blank, Toast.LENGTH_SHORT).show()
            return
        }
        if (email.isNullOrEmpty()) {
            Toast.makeText(this@Profile, R.string.email_field_cannot_be_left_blank, Toast.LENGTH_SHORT).show()
            return
        }
        mDfRef.child(currentUser!!.uid).child("name").setValue(name)
        mDfRef.child(currentUser.uid).child("email").setValue(email)
        mAuth.currentUser!!.updateEmail(email)
        Toast.makeText(this@Profile, R.string.profile_updated, Toast.LENGTH_SHORT).show()
        getData()
    }
}
