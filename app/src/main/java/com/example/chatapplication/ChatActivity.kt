package com.example.chatapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChatActivity : AppCompatActivity() {

    //We must declare components globally that we use in our design, (also we can use 'Data Binding')
    private lateinit var chatRecylerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendButton: ImageView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var mDbRef: DatabaseReference
    private lateinit var chatBackground: RelativeLayout

    //Menu
    private lateinit var textUserName: TextView
    private lateinit var imageLogOut: ImageView


    //we declare two variables in which the names of the senders and receivers are kept to be kept on the database of the incoming and broadcasting
    var receiverRoom: String? = null
    var senderRoom: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val name = intent.getStringExtra("name")
        val receiverUid = intent.getStringExtra("uid")

        val senderUid = FirebaseAuth.getInstance().currentUser?.uid
        mDbRef = FirebaseDatabase.getInstance().getReference()

        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid  + receiverUid

        chatBackground = findViewById<RelativeLayout>(R.id.background)

        supportActionBar?.hide()

        //Setting state to online
        mDbRef.child("user").child(senderUid.toString()).child("state").setValue(true)

        //Let's initialize all of them above
        chatRecylerView = findViewById(R.id.chatRecylerView)
        messageBox = findViewById(R.id.messageBox)
        sendButton = findViewById(R.id.sentButton)

        //Menu
        textUserName = findViewById(R.id.txt_chat_name)
        imageLogOut = findViewById(R.id.image_options)
        //txtState = findViewById(R.id.txt_state)

        messageList = ArrayList()
        messageAdapter = MessageAdapter(this, messageList, senderRoom!!, receiverRoom!!)
        chatRecylerView.layoutManager = LinearLayoutManager(this)
        chatRecylerView.adapter =  messageAdapter

        //Menu
        textUserName.setText(name.toString())

        //Background
        val sharedPref = getSharedPreferences("background", Context.MODE_PRIVATE)
        val background = sharedPref.getInt("background", R.drawable.chatbackground7)
        chatBackground.setBackgroundResource(background)

        //Logic for adding data to recylerView
        mDbRef.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object: ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for (postSnapShot in snapshot.children)
                    {
                        val message = postSnapShot.getValue(Message::class.java)
                        messageList.add(message!!)
                    }

                    //Normally, in the other Chat Apps, if you entered the someones room, page automatically scroll to bottom
                    //for do this, we must add the codes below
                    chatRecylerView.smoothScrollToPosition(messageAdapter.itemCount)

                    messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })

        //For state
        /*mDbRef.child("user").child(receiverUid.toString()).child("state").addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val state = if (snapshot.getValue<Boolean>()!!) R.string.online else R.string.offline
                txtState.setText(state)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })*/

        //Adding the message to database
        sendButton.setOnClickListener {
            val message = messageBox.text.toString()
            //for now, we set the message's state false, we will set it false or true depends on the situation
            val senderMessageKey = mDbRef.child("chats").child(senderRoom!!).child("messages").push().key
            val messageObject = Message(senderMessageKey, message, senderUid, System.currentTimeMillis(), false)
            if (!message.isEmpty()){
                mDbRef.child("chats").child(senderRoom!!).child("messages").child(senderMessageKey!!)
                    .setValue(messageObject).addOnSuccessListener {
                        mDbRef.child("chats").child(receiverRoom!!).child("messages").child(senderMessageKey!!)
                            .setValue(messageObject)
                    }
                //After the message has been sent, we must scroll to bottom and clear message text like in other apps
                messageBox.setText("")
                //After the process ended successfully, a sound will start
                val mediaPlayer = MediaPlayer.create(this, R.raw.message_sent2)
                mediaPlayer.start()
            }
        }

        //For log out
        imageLogOut.setOnClickListener {
            finish()
        }
    }

}