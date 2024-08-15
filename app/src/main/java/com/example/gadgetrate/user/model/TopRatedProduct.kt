package com.example.gadgetrate.user.model

data class TopRatedProduct(
    val productId: String,
    val productName: String = "",
    val productCategory: String = "",
    val productDescription: String = "",
    val productImageUrl: String = "",
    val averageRating: Double
)
