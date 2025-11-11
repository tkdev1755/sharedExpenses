package com.mds.sharedexpenses.data.models

data class User(
    val id: String = "",
    var name: String = "",
    var email: String = "",
    val groups: MutableList<Group>) {
}
