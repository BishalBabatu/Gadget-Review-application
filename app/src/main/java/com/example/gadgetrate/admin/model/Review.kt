package com.example.gadgetrate.admin.model

data class Review(
    val productCategory: String = "",
    val productDescription: String = "",
    val productId: String = "",
    val productImageUrl: String = "",
    val productName: String = "",
    val productRating: Double = 0.0,
    val productReview: String = "",
    val reviewDate: String = "",
    val reviewNo: Long = 0,
    val userId: String = "",
    val userName: String = ""
)
