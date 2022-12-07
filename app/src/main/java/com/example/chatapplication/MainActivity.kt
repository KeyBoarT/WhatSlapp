package com.example.chatapplication

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.LocationManager
import android.net.nsd.NsdManager.DiscoveryListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.ActionBar.DisplayOptions
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.lang.reflect.GenericArrayType
import java.util.*
import java.util.jar.Manifest
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    //For listing users, we declare this variables
    private lateinit var userRecylerView: RecyclerView
    private lateinit var userList: ArrayList<User>
    private lateinit var adapter: UserAdapter
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDfRef: DatabaseReference

    //For searching, we must declare a temp list
    private lateinit var tempList: ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()
        mDfRef = FirebaseDatabase.getInstance().getReference()

        userList = ArrayList()
        tempList = ArrayList()
        adapter = UserAdapter(this, tempList)

        userRecylerView = findViewById(R.id.userRecylerView)

        userRecylerView.layoutManager = LinearLayoutManager(this)
        userRecylerView.adapter = adapter

        mDfRef.child("user").addValueEventListener(object: ValueEventListener {

            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                tempList.clear()
                //Soo, here we have two way for listing users
                //First one, create a token for all users to communicate with each other
                //Second one, Create database for all users to take their datas

                //if storage is more important, we can use first way
                //else, we must use second way

                //today, storage is less important than RAM, therefore we will use second one

                for (postSnapShot in snapshot.children){
                    val currentUser = postSnapShot.getValue(User::class.java)
                    if (!mAuth.currentUser!!.uid.equals(currentUser!!.uid))
                    {
                        userList.add(currentUser)
                    }
                }
                tempList.addAll(userList)
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    //For assigning our 'log out' menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_page_menu, menu)
        val item = menu?.findItem(R.id.search)
        val searchView = item?.actionView as SearchView
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onQueryTextChange(p0: String?): Boolean {
                tempList.clear()
                val searchText = p0!!.toLowerCase(Locale.getDefault())
                if(searchText.isNotEmpty()){
                    userList.forEach{
                        if (it.name!!.toLowerCase(Locale.getDefault()).contains(searchText)){
                            tempList.add(it)
                        }
                    }
                    userRecylerView.adapter!!.notifyDataSetChanged()
                }
                else{
                    tempList.clear()
                    tempList.addAll(userList)
                    userRecylerView.adapter!!.notifyDataSetChanged()
                }
                return false
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    //below method is run, when the menu items are clicked
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //we created if condition for every menu items to identify
        if (item.itemId == R.id.menu_logout){
            //write the login and logout
            mAuth.signOut()
            val intent = Intent(this@MainActivity, LogIn::class.java)
            finish()
            startActivity(intent)
        }

        if(item.itemId==R.id.menu_profile){
            val intent = Intent(this@MainActivity, Profile::class.java)
            startActivity(intent)
        }

        if(item.itemId==R.id.menu_background){
            val intent = Intent(this@MainActivity, Background::class.java)
            startActivity(intent)
        }

        if (item.itemId==R.id.menu_delete_account){
            val intent = Intent(this@MainActivity, LogIn::class.java)
            mDfRef.child("user").child(mAuth.currentUser!!.uid).setValue(null)
            mAuth.currentUser!!.delete()
            startActivity(intent)
            finish()
            Toast.makeText(this@MainActivity, R.string.profile_deleted, Toast.LENGTH_SHORT).show()
        }
        return true
    }
}