package com.mds.sharedexpenses.data.models

data class User(
    val id: String = "",
    var name: String = "",
    var email: String = "",
    var phone: String = "",
    val groups: MutableList<Group> = mutableListOf()
)
