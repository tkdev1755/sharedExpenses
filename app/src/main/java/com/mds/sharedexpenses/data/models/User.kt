package com.mds.sharedexpenses.data.models

data class User(
    val id: String = "",
    var name: String = "",
    var email: String = "",
    var phone: String = "",
    val groups: MutableList<Group>) {

    fun toJson(): Map<String, Any> {
        return mapOf(
            "name" to name,
            "email" to email,
        )
    }
}
