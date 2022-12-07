package com.example.chatapplication

import android.content.Context
import android.content.SharedPreferences
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView.FindListener
import android.widget.ImageView
import android.widget.Toast

class Background : AppCompatActivity() {

    private lateinit var btnGoBack: ImageView
    private lateinit var background1: ImageView
    private lateinit var background2: ImageView
    private lateinit var background3: ImageView
    private lateinit var background4: ImageView
    private lateinit var background5: ImageView
    private lateinit var background6: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_background)

        //To hide support action bar
        supportActionBar?.hide()

        btnGoBack = findViewById(R.id.image_goback)
        background1 = findViewById(R.id.image_background1)
        background2 = findViewById(R.id.image_background2)
        background3 = findViewById(R.id.image_background3)
        background4 = findViewById(R.id.image_background4)
        background5 = findViewById(R.id.image_background5)
        background6 = findViewById(R.id.image_background6)

        btnGoBack.setOnClickListener {finish()}
        background1.setOnClickListener {setBackground(R.drawable.chatbackground1)}
        background2.setOnClickListener {setBackground(R.drawable.chatbackground2)}
        background3.setOnClickListener {setBackground(R.drawable.chatbackground3)}
        background4.setOnClickListener {setBackground(R.drawable.chatbackground7)}
        background5.setOnClickListener {setBackground(R.drawable.chatbackground5)}
        background6.setOnClickListener {setBackground(R.drawable.chatbackground6)}
    }

    private fun setBackground(id: Int){
        val sharedPref = getSharedPreferences("background", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putInt("background", id)
        editor.apply()
        Toast.makeText(this@Background, R.string.background_changed, Toast.LENGTH_SHORT).show()
        finish()
    }
}