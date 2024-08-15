package com.example.gadgetrate.user.model

data class LatestReviews(
    val productId: String = "",
    val productName: String = "",
    val productCategory: String = "",
    val productDescription: String = "",
    val productReview: String = "",
    var productRating: Any = 0.0,
    val userId: String = "",
    val userName: String = "",
    val productImageUrl: String = "",
    val reviewDate: String = "",
    val reviewNo: Long = 0L
)
