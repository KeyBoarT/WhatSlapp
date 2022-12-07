package com.example.chatapplication

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Year
import java.time.format.DateTimeFormatter
import java.util.Date

class MessageAdapter(val context: Context, val messageList: ArrayList<Message>, val senderRoom: String, val receiverRoom: String):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var mDbRef: DatabaseReference

    val ITEM_RECEIVE = 1
    val ITEM_SENT = 2
    val ITEM_RECEIVE_WITH_DATE = 3
    val ITEM_SENT_WITH_DATE = 4
    //for compare to current date
    var CURRENT_DATE: Long  = 0
    //we set the default value '0', so always first message's date's value will bigger than default value

    //date format value
    val DATE_FORMAT = "dd MMMM yyyy"
    val SDF: SimpleDateFormat = SimpleDateFormat(DATE_FORMAT)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 1){
            //inflate receive
            val view: View = LayoutInflater.from(context).inflate(R.layout.receive,parent, false)
            return ReceiveViewHolder(view)
        }
        else if (viewType == 2)
        {
            //inflate sent
            val view: View = LayoutInflater.from(context).inflate(R.layout.sent, parent, false)
            return SentViewHolder(view)
        }
        else if (viewType == 3){
            //inflate receive with date
            val view: View = LayoutInflater.from(context).inflate(R.layout.receive_with_date, parent, false)
            return ReceiveWithDateViewHolder(view)
        }
        else{
            //inflate sent with date
            val view: View = LayoutInflater.from(context).inflate(R.layout.sent_with_date, parent, false)
            return SentWithDateViewHolder(view)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        mDbRef = FirebaseDatabase.getInstance().getReference()
        val currentMessage = messageList[position]
        val format = SimpleDateFormat("HH:mm")
        val date = Date(currentMessage.date!!)
        val state = currentMessage.state
        if (holder.javaClass == SentViewHolder::class.java){
            // do the stuff for sent view holder
            val viewHolder = holder as SentViewHolder
            //holder.Date.text = date.toString()
            holder.sentMessage.text = currentMessage.message
            holder.sentTime.text = format.format(date)
            val id = if (state!!) R.drawable.ic_done else R.drawable.ic_not_done
            holder.state.setImageResource(id)
        }
        else if (holder.javaClass == ReceiveViewHolder::class.java){
            // do the stuff for receive view holder
            val viewHolder = holder as ReceiveViewHolder
            holder.receiveMessage.text = currentMessage.message
            mDbRef.child("chats").child(senderRoom).child("messages").child(currentMessage.messageId.toString()).child("state").setValue(true)
            mDbRef.child("chats").child(receiverRoom).child("messages").child(currentMessage.messageId.toString()).child("state").setValue(true)
            holder.receiveTime.text = format.format(date)
        }
        else if (holder.javaClass == SentWithDateViewHolder::class.java)
        {
            val viewHolder = holder as SentWithDateViewHolder
            holder.sentMessage.text = currentMessage.message
            holder.sentTime.text = format.format(date)
            holder.sentDate.text = SDF.format(CURRENT_DATE)
            val id = if (state!!) R.drawable.ic_done else R.drawable.ic_not_done
            holder.state.setImageResource(id)
        }
        else{
            val viewHolder = holder as ReceiveWithDateViewHolder
            holder.receiveMessage.text = currentMessage.message
            holder.receiveTime.text = format.format(date)
            mDbRef.child("chats").child(senderRoom).child("messages").child(currentMessage.messageId.toString()).child("state").setValue(true)
            mDbRef.child("chats").child(receiverRoom).child("messages").child(currentMessage.messageId.toString()).child("state").setValue(true)
            holder.receiveDate.text = SDF.format(CURRENT_DATE)
        }
    }

    var result = false
    //Gelen mesajların gönderilen mesaj mı yoksa gelen mesaj mı olduğunu anlamak için yazılan kod
    @RequiresApi(Build.VERSION_CODES.O)
    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]
        //comparing current message's date and current date
        var result = false
        if (position != 0)
            result = SDF.format(currentMessage.date!!).equals(SDF.format(messageList[position - 1].date!!))
        when {
            !result -> {
                CURRENT_DATE = currentMessage.date!!
                if (FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderId)) {
                    return ITEM_SENT_WITH_DATE
                }
                else{
                    return ITEM_RECEIVE_WITH_DATE
                }
            }
            else -> {
                if (FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderId)) {
                    return ITEM_SENT
                }
                else{
                    return ITEM_RECEIVE
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val sentMessage = itemView.findViewById<TextView>(R.id.txt_sent_message)
        val sentTime = itemView.findViewById<TextView>(R.id.txt_sent_time)
        val state = itemView.findViewById<ImageView>(R.id.image_sent_state)
    }

    class ReceiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val receiveMessage = itemView.findViewById<TextView>(R.id.txt_receive_message)
        val receiveTime = itemView.findViewById<TextView>(R.id.txt_receive_time)
    }

    class SentWithDateViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val sentDate = itemView.findViewById<TextView>(R.id.txt_sent_date)
        val sentMessage = itemView.findViewById<TextView>(R.id.txt_sent_message_with_date)
        val sentTime = itemView.findViewById<TextView>(R.id.txt_sent_time_with_date)
        val state = itemView.findViewById<ImageView>(R.id.image_sent_state_with_date)
    }

    class ReceiveWithDateViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val receiveDate = itemView.findViewById<TextView>(R.id.txt_receive_date)
        val receiveMessage = itemView.findViewById<TextView>(R.id.txt_receive_message_with_date)
        val receiveTime = itemView.findViewById<TextView>(R.id.txt_receive_time_with_date)
    }
}
