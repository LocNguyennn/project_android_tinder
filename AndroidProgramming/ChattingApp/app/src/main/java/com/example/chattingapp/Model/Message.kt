package com.example.chattingapp.Model

class Message {
    var message : String? = null
    var senderId : String? = null

    constructor(){}

    constructor(message: String?, senderId: String?){
        this.message = message
        this.senderId = senderId
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Message

        if (message != other.message) return false
        if (senderId != other.senderId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = message?.hashCode() ?: 0
        result = 31 * result + (senderId?.hashCode() ?: 0)
        return result
    }

}