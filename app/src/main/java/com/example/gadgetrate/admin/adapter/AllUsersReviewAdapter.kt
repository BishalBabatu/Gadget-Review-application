package com.example.gadgetrate.admin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gadgetrate.R
import com.example.gadgetrate.admin.model.Review
import com.example.gadgetrate.databinding.ItemUsersReviewRatingBinding

class AllUsersReviewAdapter(
    private val reviews: List<Review>
) : RecyclerView.Adapter<AllUsersReviewAdapter.AllUsersReviewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllUsersReviewViewHolder {
        val binding = ItemUsersReviewRatingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AllUsersReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AllUsersReviewViewHolder, position: Int) {
        holder.bind(reviews[position])
    }

    override fun getItemCount() = reviews.size

    class AllUsersReviewViewHolder(private val binding: ItemUsersReviewRatingBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(review: Review) {
            binding.tvUserName.text = review.userName
            binding.tvReviewDate.text = review.reviewDate
            binding.tvUserReview.text = review.productReview
            binding.tvProductName.text = review.productName
            binding.tvProductCategory.text = review.productCategory
            binding.tvProductDescription.text = review.productDescription
            binding.tvProductRating.text = review.productRating.toString()

            Glide.with(binding.ivProductImage.context)
                .load(review.productImageUrl)
                .placeholder(R.drawable.placeholder)
                .into(binding.ivProductImage)
        }
    }
}
