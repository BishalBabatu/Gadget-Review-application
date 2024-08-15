package com.example.gadgetrate.user.model

import com.google.firebase.firestore.PropertyName

data class Review(
    val productId: String = "",
    val productName: String = "",
    val productCategory: String = "",
    val productDescription: String = "",
    val productImageUrl: String = "",
    val productReview: String = "",
    @get:PropertyName("productRating") @set:PropertyName("productRating") var productRating: Double = 0.0,
    val reviewDate: String = "",
    val userId: String = "",
    val userName: String = ""
)
