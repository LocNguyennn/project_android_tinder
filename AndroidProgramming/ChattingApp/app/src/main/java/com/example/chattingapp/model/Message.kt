package com.example.chattingapp.model

data class Message(
    var message: String? = null,
    var senderId: String? = null,
    var receiverId: String? = null
) {
}