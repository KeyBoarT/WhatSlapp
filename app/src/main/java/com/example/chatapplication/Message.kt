package com.example.chatapplication

class Message {
    var messageId: String? = null
    var message: String? = null
    var senderId: String? = null
    var date: Long? = null
    var state: Boolean? = null
    constructor()

    //For adding date time in our message we must declare new constructor that includes date
    constructor(messageId: String? ,message: String?, senderId: String?, date: Long?, state: Boolean){
        this.messageId = messageId
        this.message = message
        this.senderId = senderId
        this.date = date
        this.state = state
    }
}