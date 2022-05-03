package com.example.chattingapp

class User {
    var name: String? = null
    var email: String? = null
    var uid: String? = null
    var friendUid : ArrayList<String>? = null

    constructor(){}

    constructor(name: String?, email: String?, uid: String?){
        this.name = name
        this.email = email
        this.uid = uid
//        this.friendUid = friendUid
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (name != other.name) return false
        if (email != other.email) return false
        if (uid != other.uid) return false
        if (friendUid != other.friendUid) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name?.hashCode() ?: 0
        result = 31 * result + (email?.hashCode() ?: 0)
        result = 31 * result + (uid?.hashCode() ?: 0)
        result = 31 * result + (friendUid?.hashCode() ?: 0)
        return result
    }

}