package com.mds.sharedexpenses.data.repositories


data class Dept(
    val id: Int = 0,
    val group: String = "",
    val user: String = "",
    val expense: List<Int> = emptyList())
