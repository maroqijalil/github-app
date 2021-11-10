package com.beginner.myapplication.model

data class SearchUsersList(
    val incomplete_results: Boolean = false,
    val items: List<Item> = listOf(),
    val total_count: Int = 0
)