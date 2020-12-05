package com.dtf.lottery.model

data class User(
    val id: Int,
    val nickname: String,
    val dateCreated: Long,
    val image: String,
    val rating: Int
)

data class Comment(
    val id: Int,
    val timestamp: Long,
    val text: String,
    val userId: Int
)