package com.mds.sharedexpenses.data.repositories

// In Kotlin, a data class is used to represent data objects
data class Dept(
    val id: Int = 0,
    val group: String = "",
    val user: String = "",
    val expense: List<Int> = emptyList())
