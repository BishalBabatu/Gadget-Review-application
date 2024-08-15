package com.example.gadgetrate.user.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gadgetrate.R
import com.example.gadgetrate.databinding.ItemUsersReviewRatingBinding
import com.example.gadgetrate.user.model.Review

class ReviewAdapter(private val reviews: List<Review>) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ItemUsersReviewRatingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]
        holder.bind(review)
    }

    override fun getItemCount(): Int {
        return reviews.size
    }

    class ReviewViewHolder(private val binding: ItemUsersReviewRatingBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(review: Review) {
            binding.tvUserName.text = review.userName
            binding.tvReviewDate.text = review.reviewDate
            binding.tvUserReview.text = review.productReview
            binding.tvProductName.text = review.productName
            binding.tvProductCategory.text = "Category: ${review.productCategory}"
            binding.tvProductRating.text = "Rating: ${review.productRating}"
            binding.tvProductDescription.text = "Description: ${review.productDescription}"

            Glide.with(binding.ivProductImage.context)
                .load(review.productImageUrl)
                .placeholder(R.drawable.placeholder)
                .into(binding.ivProductImage)
        }
    }
}
