package com.example.chatapplication

class User {
    var name: String? = null;
    var email: String? = null;
    var uid: String? = null;
    var state: Boolean? = null
    var password: String? = null
    constructor(){

    }

    constructor(name: String?, email: String?, uid: String?){
        this.name = name
        this.email = email
        this.uid = uid
    }

    constructor(name: String?, email: String?, uid: String?, state: Boolean){
        this.name = name
        this.email = email
        this.uid = uid
        this.state = state
    }
    constructor(name: String?, email: String?, password: String?, uid: String?, state: Boolean){
        this.name = name
        this.email = email
        this.password = password
        this.uid = uid
        this.state = state
    }
}